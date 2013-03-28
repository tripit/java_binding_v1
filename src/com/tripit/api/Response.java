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

public class Response {
    protected int httpStatusCode = 0;
    protected String httpStatusString = null;
    protected long responseTimeMillis = 0;
    protected String content = null;
    
    public Response(int httpStatusCode, String httpStatusString, long responseTimeMillis, String content) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatusString = httpStatusString;
        this.responseTimeMillis = responseTimeMillis;
        this.content = content;
    }
    
    public String toString() {
        return "RESPONSE CODE: " + this.httpStatusCode + " (" + this.httpStatusString + ")\n" +
               "RESPONSE TIME: " + this.responseTimeMillis + "ms\n" +
               "RESPONSE BODY: " + this.content;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public String getHttpStatusString() {
        return this.httpStatusString;
    }

    public long getResponseTimeMillis() {
        return this.responseTimeMillis;
    }

    public String getContent() {
        return this.content;
    }
}
