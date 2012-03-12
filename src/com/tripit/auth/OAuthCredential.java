/**
 * Copyright 2008-2012 Concur Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * 
 * @package    com.tripit.auth
 * @copyright  Copyright &copy; 2008-2012, TripIt, Inc.
 */

package com.tripit.auth;

import oauth.signpost.*;
import oauth.signpost.commonshttp.*;
import oauth.signpost.signature.*;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OAuthCredential extends Credential {
    private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String OAUTH_VERSION = "1.0";
    
    protected String consumerKey;
    protected String consumerSecret;
    protected String userKey;
    protected String userSecret;
    protected String requestorId;
    
    public OAuthCredential(String consumerKey, String consumerSecret, String userKey, String userSecret) {
    	this.consumerKey = consumerKey;
    	this.consumerSecret = consumerSecret;
    	this.userKey = userKey;
    	this.userSecret = userSecret;
    	this.requestorId = null;
    }

    public OAuthCredential(String consumerKey, String consumerSecret, String requestorId) {
    	this.consumerKey = consumerKey;
    	this.consumerSecret = consumerSecret;
    	this.userKey = this.userSecret = null;
    	this.requestorId = requestorId;
    }

    public void authorize(HttpRequestBase request) throws Exception {
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        consumer.setMessageSigner(new HmacSha1MessageSigner());

        if (this.userKey != null && this.userSecret != null) {
            consumer.setTokenWithSecret(this.userKey, this.userSecret);
        }
        
        if (this.requestorId != null) {
            HttpParameters params = new HttpParameters();
            params.put("xoauth_requestor_id", OAuth.percentEncode(this.requestorId));
            consumer.setAdditionalParameters(params);
            
            consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy() {
                private static final long serialVersionUID = 1L;
                public String writeSignature(String signature, HttpRequest request, HttpParameters parameters) {
                    String header = super.writeSignature(signature, request, parameters) + ", " + parameters.getAsHeaderElement("xoauth_requestor_id");
                    request.setHeader(OAuth.HTTP_AUTHORIZATION_HEADER, header);
                    return header;
                }
            });
        }

        consumer.sign(request);
    }
    
    public boolean validateSignature(URI requestUri)
        throws URISyntaxException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException 
    {
        List<NameValuePair> argList = URLEncodedUtils.parse(requestUri, "UTF-8");
        SortedMap<String, String> args = new TreeMap<String, String>();
        for (NameValuePair arg : argList) {
            args.put(arg.getName(), arg.getValue());
        }
        
        String signature = args.remove("oauth_signature");
        
        String baseUrl = new URI(requestUri.getScheme(),
            requestUri.getUserInfo(), requestUri.getAuthority(),
            requestUri.getPort(), requestUri.getPath(), null,
            requestUri.getFragment()).toString();
        
        return (signature != null && signature.equals(generateSignature(baseUrl, args)));
    }
    
    public String getSessionParameters(String redirectUrl, String action)
        throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException
    {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("redirect_url", redirectUrl);
        params = generateOAuthParameters(action, params);
        params.put("redirect_url", redirectUrl);
        params.put("action", action);
        
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean isFirst = true;
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append(",");
            }
            
            jsonEncodeString(param.getKey(), sb);
            sb.append(": ");
            jsonEncodeString(param.getValue(), sb);
        }
        sb.append('}');
        
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    private SortedMap<String, String> generateOAuthParameters(String url, SortedMap<String, String> params)
        throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException
    {
        TreeMap<String, String> oauthParameters = new TreeMap<String, String>();
        oauthParameters.put("oauth_consumer_key", consumerKey);
        oauthParameters.put("oauth_nonce", generateNonce());
        oauthParameters.put("oauth_timestamp", generateTimestamp());
        oauthParameters.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
        oauthParameters.put("oauth_version", OAUTH_VERSION);
        
        if (userKey != null) {
            oauthParameters.put("oauth_token", userKey);
        }
        
        if (requestorId != null) {
            oauthParameters.put("xoauth_requestor_id", requestorId);
        }
        
        SortedMap<String, String> oauthParametersForBaseString = (SortedMap<String, String>)oauthParameters.clone();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                oauthParametersForBaseString.put(param.getKey(), param.getValue());
            }
        }
        
        oauthParameters.put("oauth_signature", generateSignature(url, oauthParametersForBaseString));
        
        return oauthParameters;
    }
    
    private static String generateNonce() {
        return Long.toString(new Random().nextLong());
    }
    
    private static String generateTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000L);
    }

    private static void jsonEncodeString(String string, StringBuilder sb) {
        CharacterIterator ci = new StringCharacterIterator(string);
        sb.append('"');
        for (char c = ci.first(); c != CharacterIterator.DONE; c = ci.next()) {
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        sb.append('"');
    }

    private String generateSignature(String baseUrl, SortedMap<String, String> args)
        throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException
    {
        String encoding = "UTF-8";
        
        baseUrl = URLEncoder.encode(baseUrl, encoding);
        
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, String> arg : args.entrySet()) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(arg.getKey(), encoding));
            sb.append('=');
            sb.append(URLEncoder.encode(arg.getValue(), encoding));
        }
        String parameters = URLEncoder.encode(sb.toString(), encoding);
        
        String signatureBaseString = "GET&" + baseUrl + "&" + parameters;
        
        String key = (consumerSecret != null ? consumerSecret : "") + "&" + (userSecret != null ? userSecret : "");
        
        String macName = "HmacSHA1";
        Mac mac = Mac.getInstance(macName);
        mac.init(new SecretKeySpec(key.getBytes(encoding), macName));
        byte[] signature = mac.doFinal(signatureBaseString.getBytes(encoding));
        
        return new Base64().encodeToString(signature).trim();
    }
}
