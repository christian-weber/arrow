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

package org.arrow.runtime.execution.service;

import org.arrow.runtime.rule.RuleEvaluationContext;
import org.arrow.runtime.rule.RuleSource;

/**
 * Execution service facade for rule engine services.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public interface ExecutionRuleService {

    /**
     * Evaluates the given rule engine input data.
     *
     * @param source the rule source instance
     * @param context the rule evaluation context
     */
    void evaluate(RuleSource source, RuleEvaluationContext context);

}
