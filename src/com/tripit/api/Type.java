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

public enum Type {
    AIR (new Action[] {Action.GET, Action.DELETE}),
    ACTIVITY (new Action[] {Action.GET, Action.DELETE}),
    CAR (new Action[] {Action.GET, Action.DELETE}),
    CRUISE (new Action[] {Action.GET, Action.DELETE}),
    DIRECTIONS (new Action[] {Action.GET, Action.DELETE}),
    LODGING (new Action[] {Action.GET, Action.DELETE}),
    MAP (new Action[] {Action.GET, Action.DELETE}),
    NOTE (new Action[] {Action.GET, Action.DELETE}),
    PROFILE (new Action[] {Action.GET}),
    RAIL (new Action[] {Action.GET, Action.DELETE}),
    RESTAURANT (new Action[] {Action.GET, Action.DELETE}),
    TRANSPORT (new Action[] {Action.GET, Action.DELETE}),
    TRIP (new Action[] {Action.GET, Action.LIST, Action.DELETE, Action.SUBSCRIBE, Action.UNSUBSCRIBE}),
    WEATHER (new Action[] {Action.GET}),
    OBJECT (new Action[] {Action.LIST}),
    POINTS_PROGRAM (new Action[] {Action.GET, Action.LIST});
    
    private Action[] actions;
    
    Type(Action[] actions) {
        this.actions = actions;
    }
    
    public static Type get(Action a, String s) throws Exception {
        Field[] fields = Type.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isEnumConstant() && field.getName().equalsIgnoreCase(s)) {
                 Type t = (Type) field.get(Type.class);
                if (t.isEligible(a)) {
                    return t;
                }
                else {
                    throw new Exception("you cannot " + a + " a " + t);
                }
            }
        }
        throw new Exception("invalid type: " + s);
    }

    public boolean isEligible(Action a) {
        for (Action action : this.actions) {
            if (a == action) {
                return true;
            }
        }
        return false;
    }
}
