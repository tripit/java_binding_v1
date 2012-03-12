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
import java.util.*;

public class CommandLineInterface {
    public static void main(String[] args) throws Exception {
        String url = null;
        Credential cred = null;
        Action a = null;
        Type type = null;
        Map<String, String> requestParameterMap = new HashMap<String, String>();

        if (args.length == 0 || "-help".equals(args[0]) || "-?".equals(args[0])) {
            System.out.println("Usage: java com.tripit.api.CommandLineInterface -webauth USERNAME:PASSWORD | -oauth CONSUMER_KEY:CONSUMER_SECRET:TOKEN_KEY:TOKEN_SECRET | -oauth CONSUMER_KEY:CONSUMER_SECRET:REQUESTOR_ID");
            System.out.println("                                                -action ACTION[:TYPE]");
            System.out.println("                                                [ -url API_SERVER_URI_PREFIX ]");
            System.out.println("                                                [ -postfile FILENAME ]");
            System.out.println("                                                [ -REQUEST_PARAMETER_KEY_1 REQUEST_PARAMETER_VALUE_1 ]...");
            System.out.println("Notes: authenticate either with -webauth or -oauth");
            System.out.println("       ACTION is 'get', 'list', 'delete', or 'create'");
            System.out.println("       TYPE (when necessary) is 'air', 'lodging', 'car', etc...");
            System.out.println("       API_SERVER_URI_PREFIX defaults to " + Client.DEFAULT_API_URI_PREFIX);
            System.out.println("       a postfile FILENAME is necessary when ACTION is 'create'");
            System.out.println("       more info on REQUEST_PARAMETER_KEY's is at \"http://groups.google.com/group/api_tripit/web/tripit-api-documentation---v1\"");
            System.exit(0);
        }
        
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i++];
            
            if ("-webauth".equals(arg)) {
                StringTokenizer st = new StringTokenizer(args[i], ":");
                cred = new WebAuthCredential(st.nextToken(), st.nextToken());
            }
            else if ("-oauth".equals(arg)) {
                StringTokenizer st = new StringTokenizer(args[i], ":");
                String key = st.nextToken();
                String secret = st.nextToken();
                String token = st.nextToken();
                if (st.hasMoreTokens()) {
                    cred = new OAuthCredential(key, secret, token, st.nextToken());
                }
                else {
                    cred = new OAuthCredential(key, secret, token); 
                }
            }
            else if ("-url".equals(arg)) {
                url = args[i];
            }
            else if ("-action".equals(arg)) {
                StringTokenizer st = new StringTokenizer(args[i], ":");
                a = Action.get(st.nextToken());
                if (st.hasMoreTokens()) {
                    type = Type.get(a, st.nextToken());
                }
            }
            else if ("-postfile".equals(arg)) {
                StringBuilder content = new StringBuilder();
                BufferedReader in = new BufferedReader(new FileReader(args[i]));
                String line = null;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                String format = requestParameterMap.get("format") != null ? requestParameterMap.get("format") : "xml"; 
                requestParameterMap.put(format, content.toString());
            }
            else if (arg.charAt(0) == '-') {
                // interpret remaining arguments as request parameter key/values
                requestParameterMap.put(arg.substring(1), args[i]);
            }
            else {
                throw new Exception("invalid argument: " + arg);
            }
        }

        if (url == null) {
            url = Client.DEFAULT_API_URI_PREFIX;
        }
        if (a == null) {
            throw new Exception("you must specify an action");
        }
        
        Client client = new Client(cred, url);

        Response response = a.execute(client, type, requestParameterMap);
        
        System.out.println(response);
    }
}
