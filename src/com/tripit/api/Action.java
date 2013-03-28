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

import java.lang.reflect.*;
import java.util.Map;

public enum Action {
    GET("get", false) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.get(type, requestParameterMap);
        }
    },
    LIST("list", false) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.get(type, requestParameterMap);
        }
    },
    DELETE("delete", false) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.delete(type, requestParameterMap);
        }
    },
    CREATE("create", true) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.create(requestParameterMap);
        }
    },
    REPLACE("replace", true) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.replace(type, requestParameterMap);
        }
    },
    SUBSCRIBE("subscribe", true) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.subscribe(type, requestParameterMap);
        }
    },
    UNSUBSCRIBE("unsubscribe", true) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.unsubscribe(type, requestParameterMap);
        }
    },
    CRS_LOAD_RESERVATIONS("crsLoadReservations", true) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.crsLoadReservations(requestParameterMap);
        }
    },
    CRS_DELETE_RESERVATIONS("crsDeleteReservations", false) {
        public Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception {
            return client.crsDeleteReservations(requestParameterMap);
        }
    };
    
    private String actionString;
    private boolean isPost;
    
    private Action(String actionString, boolean isPost) {
        this.actionString = actionString;
        this.isPost = isPost;
    }
    
    public String getActionString() {
        return actionString;
    }
    
    public boolean isPost() {
        return isPost;
    }
    
    public static Action get(String s) throws Exception {
        Field[] fields = Action.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isEnumConstant() && field.getName().equalsIgnoreCase(s)) {
                return (Action) field.get(Action.class);
            }
        }
        throw new Exception("invalid action: " + s);
    }
    
    public abstract Response execute(Client client, Type type, Map<String, String> requestParameterMap) throws Exception;
}
