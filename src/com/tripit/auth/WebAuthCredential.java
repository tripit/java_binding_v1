/**
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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.*;

public class WebAuthCredential extends Credential {
    protected String username;
    protected String password;
    
    public WebAuthCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void authorize(HttpRequestBase request) throws Exception {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64(new String(this.username + ":" + this.password).getBytes())));
    }
}
