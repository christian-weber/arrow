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

package org.arrow.service.impl;

import org.arrow.runtime.api.event.BusinessCondition;
import org.arrow.runtime.api.event.EventTrigger;
import org.arrow.runtime.api.task.JavaDelegate;
import org.arrow.runtime.execution.Execution;
import org.springframework.beans.factory.BeanNameAware;

public class JavaDelegateBean implements JavaDelegate {

    @Override
    public void execute(Execution execution) {
        System.out.println("execute java delegate");
    }
}