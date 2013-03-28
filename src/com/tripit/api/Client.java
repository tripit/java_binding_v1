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
import java.util.*;

/**
 * TripIt API binding.
 *
 * @version 1.0
 * @author Thor Denmark <thor@tripit.com>
 */
public class Client {

    public static final String DEFAULT_API_URI_PREFIX = "https://api.tripit.com";
    public static final String DEFAULT_WEB_URI_PREFIX = "https://www.tripit.com";
    
    ////////////////////////////////////////////////////////////////
    // ATTRIBUTES

    private String apiUriPrefix = null;
    private Credential credential = null;
    
    ////////////////////////////////////////////////////////////////
    // CONSTRUCTORS

    /**
     * Creates a Client object, which is used to make calls to the TripIt API.
     * @param credential   the credentials to use when invoking an API method
     */
    public Client(Credential credential) {
        // Provide the usual URL as a default.
        this(credential, DEFAULT_API_URI_PREFIX);
    }
    
    /**
     * Creates a Client object which is used to make calls to the TripIt API.
     * @param credential   the credentials to use when invoking an API method
     * @param apiUriPrefix the uri prefix of the API server (ie: "https://api.tripit.com")
     */
    public Client(Credential credential, String apiUriPrefix) {
        this.apiUriPrefix = apiUriPrefix;
        this.credential = credential;
    }

    ////////////////////////////////////////////////////////////////
    // PUBLIC METHODS

    /**
     * Invokes the API 'get' method.
     *
     * @param  type                the type of object being retrieved
     * @param  requestParameterMap a map of request parameter key/value pairs
     * @return                     an API response object
     */
    public Response get(Type type, Map<String, String> requestParameterMap) throws Exception {
        return this.genRequest(Action.GET, type, requestParameterMap).execute(this.credential);
    }

    /**
     * Invokes the API 'list' method.
     *
     * @param  type                the type of object being listed
     * @param  requestParameterMap a map of request parameter key/value pairs
     * @return                     an API response object
     */
    public Response list(Type type, Map<String, String> requestParameterMap) throws Exception  {
        return this.genRequest(Action.LIST, type, requestParameterMap).execute(this.credential);
    }

    /**
     * Invokes the API 'delete' method.
     *
     * @param  type                the type of object being deleted
     * @param  requestParameterMap a map of request parameter key/value pairs
     * @return                     an API response object
     */
    public Response delete(Type type, Map<String, String> requestParameterMap) throws Exception {
        return this.genRequest(Action.DELETE, type, requestParameterMap).execute(this.credential);
    }

    /**
     * Invokes the API 'create' method.
     *
     * @param  requestParameterMap a map of request parameter key/value pairs
     * @return                     an API response object
     */
    public Response create(Map<String, String> requestParameterMap) throws Exception {
        return this.genRequest(Action.CREATE, null, requestParameterMap).execute(this.credential);
    }
    
    /**
     * Invokes the API 'replace' method.
     * 
     * @param type                the type of object being replaced
     * @param requestParameterMap a map of request parameter key/value pairs
     * @return                    an API response object
     * @throws Exception
     */
    public Response replace(Type type, Map<String, String> requestParameterMap) throws Exception {
        return this.genRequest(Action.REPLACE, type, requestParameterMap).execute(this.credential);
    }

    public Response subscribe(Type type, Map<String, String> requestParameterMap) throws Exception {
        requestParameterMap.put("type", type.toString().toLowerCase());
        return this.genRequest(Action.SUBSCRIBE, null, requestParameterMap).execute(this.credential);
    }

    public Response unsubscribe(Type type, Map<String, String> requestParameterMap) throws Exception {
        requestParameterMap.put("type", type.toString().toLowerCase());
        return this.genRequest(Action.UNSUBSCRIBE, null, requestParameterMap).execute(this.credential);
    }

    public Response crsLoadReservations(Map<String, String> requestParameterMap) throws Exception {
        return this.genRequest(Action.CRS_LOAD_RESERVATIONS, null, requestParameterMap).execute(this.credential);
    }
    
    public Response crsDeleteReservations(Map<String, String> requestParameterMap) throws Exception {
        return this.genRequest(Action.CRS_DELETE_RESERVATIONS, null, requestParameterMap).execute(this.credential);
    }

    ////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    
    private Request genRequest(Action a, Type t, Map<String, String> requestParameterMap) throws Exception {
        String url = this.apiUriPrefix;
        url += "/v1/" + a.getActionString();
        if (t != null) {
            url += "/" + t.toString().toLowerCase();
        }

        return new Request(url, a.isPost(), requestParameterMap);
    }
}
