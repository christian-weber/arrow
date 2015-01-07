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
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.StaticScriptSource;
import org.arrow.model.task.AbstractTask;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;

import static org.arrow.runtime.execution.service.ExecutionScriptService.ScriptEvaluationContext;

import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

import java.util.HashMap;
import java.util.Map;

/**
 * BPMN 2 Service Task implementation used to execute Java methods.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class ScriptTask extends AbstractTask {

    private String scriptLanguage;
    private String script;

    public String getScriptLanguage() {
        return scriptLanguage;
    }

    public void setScriptLanguage(String scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> executeTask(Execution execution, ExecutionService service) {

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("execution", execution);
        arguments.put("executionService", service);

        ScriptSource source = new StaticScriptSource(script);
        ScriptEvaluationContext context = new ScriptEvaluationContext(scriptLanguage, arguments);

        if ("groovy".equals(scriptLanguage)) {
            service.script().evaluateCompiledGroovy(source, context);
        } else {
            service.script().evaluate(source, context);
        }

        execution.setState(State.SUCCESS);
        finish(execution, service);
        return FutureUtil.result();
    }

}
