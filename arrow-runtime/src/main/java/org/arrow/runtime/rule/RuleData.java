/*
 * Copyright 2014 Christian Weber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arrow.runtime.rule;

/**
 * Created by christian.weber on 08.11.2014.
 */
public class RuleData {

    private final RuleDataType type;
    private final String name;

    public RuleData(RuleDataType type, String name) {
        this.type = type;
        this.name = name;
    }

    public RuleData(String type, String name) {
        this.type = RuleDataType.valueOf(type.toUpperCase());
        this.name = name;
    }

    public RuleDataType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static enum RuleDataType {
        GLOBAL, LOCAL
    }

}
