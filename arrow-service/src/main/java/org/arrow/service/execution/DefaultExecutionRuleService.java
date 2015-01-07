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

package org.arrow.service.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.arrow.runtime.execution.service.ExecutionRuleService;
import org.arrow.runtime.rule.RuleEvaluationContext;
import org.arrow.runtime.rule.RuleSource;
import org.arrow.service.rule.DroolsRuleEvaluator;

/**
 * Default {@link ExecutionRuleService} implementation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@Service
public class DefaultExecutionRuleService implements ExecutionRuleService {

    @Autowired
    private DroolsRuleEvaluator droolsRuleEvaluator;

    /**
     * {@inheritDoc}
     */
    @Override
    public void evaluate(RuleSource source, RuleEvaluationContext context) {
        droolsRuleEvaluator.evaluate(source, context);
    }

}
