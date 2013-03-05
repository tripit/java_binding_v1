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
 * @package    com.tripit.api
 * @copyright  Copyright &copy; 2008-2012, TripIt, Inc.
 */

package com.tripit.api;

import com.tripit.auth.*;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;

public class Request {

    private HttpRequestBase request = null;
	
    public Request(String baseUrl, boolean isPost, Map<String, String> requestParameterMap) throws Exception {
        // construct request url
        String urlStr = baseUrl;

        // add request parameters to url (for GET)
        if (!isPost && requestParameterMap != null) {
            for (String key: requestParameterMap.keySet()) {
                urlStr += "/" + URLEncoder.encode(key, "UTF-8") + "/" + URLEncoder.encode(requestParameterMap.get(key), "UTF-8");
            }
        }

        // create the request
        this.request = isPost ? new HttpPost(urlStr) : new HttpGet(urlStr);

        // add request parameters to body (for POST)
        if (isPost && requestParameterMap != null) {
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            for (String key: requestParameterMap.keySet()) {
                nvps.add(new BasicNameValuePair(key, requestParameterMap.get(key)));
            }
            ((HttpPost) this.request).setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        }
    }

    public Response execute(Credential credential) throws Exception {
        // authorize the request (with either webauth or oauth)
        credential.authorize(this.request);

        // execute and time the request
        HttpClient httpClient = new DefaultHttpClient();
        long start = System.currentTimeMillis();
        HttpResponse response = (HttpResponse) httpClient.execute(request);
        long responseTime = System.currentTimeMillis() - start;

        // parse the response
        StatusLine statusLine = response.getStatusLine();
        InputStream is = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        
        return new Response(statusLine.getStatusCode(), statusLine.getReasonPhrase(), responseTime, sb.toString());
    }
}
