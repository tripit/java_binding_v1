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

import com.tripit.api.*;
import java.io.*;
import oauth.signpost.*;
import oauth.signpost.commonshttp.*;
import oauth.signpost.basic.*;
import oauth.signpost.signature.*;

public class OAuthAccessTokenGetter {
    public static void main(String[] args) throws Exception {
    	String consumerKey = "";
    	String consumerSecret = "";
        String apiUriPrefix = Client.DEFAULT_API_URI_PREFIX;
        String webUriPrefix = Client.DEFAULT_WEB_URI_PREFIX;

        for (int i = 0; i < args.length; ++i) {
            String arg = args[i++];
            
            if ("-key".equals(arg)) {
            	consumerKey = args[i];
            }
            else if ("-secret".equals(arg)) {
            	consumerSecret = args[i];
            }
            else if ("-apiurl".equals(arg)) {
            	apiUriPrefix = args[i];
            }
            else if ("-weburl".equals(arg)) {
            	webUriPrefix = args[i];
            }
        }
        
        if (consumerKey.length() == 0 || consumerSecret.length() == 0 || apiUriPrefix.length() == 0 || webUriPrefix.length() == 0) {
            System.out.println("Usage: java com.tripit.api.OAuthAccessTokenGetter -key CONSUMER_KEY");
            System.out.println("                                                  -secret CONSUMER_SECRET");
            System.out.println("                                                  [ -apiurl API_SERVER_URI_PREFIX ]");
            System.out.println("                                                  [ -weburl WEB_SERVER_URI_PREFIX ]");
            System.exit(0);
        }
    	
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setMessageSigner(new HmacSha1MessageSigner());

        String requestTokenUrl = apiUriPrefix + "/oauth/request_token";
        String accessTokenUrl = apiUriPrefix + "/oauth/access_token";
        String authorizeUrl = webUriPrefix + "/oauth/authorize";

        OAuthProvider provider = new CommonsHttpOAuthProvider(requestTokenUrl, accessTokenUrl, authorizeUrl);

        System.out.println("\nfetching unauthorized request token...");

        String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);

        System.out.println("request token key: " + consumer.getToken());
        System.out.println("request token secret: " + consumer.getTokenSecret());

        System.out.println("\nnow visit the following url and grant access to this application:\n" + authUrl + "\n");
        
        System.out.println("enter the oauth_token value returned and hit ENTER when you're done:");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String verificationCode = br.readLine();

        System.out.println("\nfetching access token...");

        provider.retrieveAccessToken(consumer, verificationCode.trim());

        System.out.println("access token key: " + consumer.getToken());
        System.out.println("access token secret: " + consumer.getTokenSecret());
    }
}
