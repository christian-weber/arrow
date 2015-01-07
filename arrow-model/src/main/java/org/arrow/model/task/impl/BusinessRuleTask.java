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

package org.arrow.model.task.impl;

import akka.dispatch.Futures;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.model.task.AbstractTask;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.rule.RuleData;
import org.arrow.runtime.rule.RuleEvaluationContext;
import org.arrow.runtime.rule.RuleSource;
import org.arrow.runtime.rule.StaticRuleSource;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.List;

/**
 * BPMN 2 Business Rule Task implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class BusinessRuleTask extends AbstractTask {

    private String ruleSource;
    private String ruleFormat;
    private List<RuleData> dataList;

    {
        dataList = new ArrayList<>();
    }

    public void setRuleSource(String ruleSource) {
        this.ruleSource = ruleSource;
    }

    public void setRuleFormat(String ruleFormat) {
        this.ruleFormat = ruleFormat;
    }

    public List<RuleData> getDataList() {
        return dataList;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeTask(Execution execution, ExecutionService service) {

        RuleSource source = new StaticRuleSource(ruleSource);
        RuleEvaluationContext context = new RuleEvaluationContext(ruleFormat, dataList, execution.getVariables());

        service.rule().evaluate(source, context);

		// mark the task as finished
        execution.setState(State.SUCCESS);
		finish(execution, service);

        return Futures.successful(iterableOf());
    }


}
