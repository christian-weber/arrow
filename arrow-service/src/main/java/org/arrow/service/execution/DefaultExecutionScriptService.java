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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.groovy.GroovyScriptEvaluator;
import org.springframework.scripting.support.StandardScriptEvaluator;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.arrow.runtime.execution.service.ExecutionScriptService;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default execution script service implementation based on spring dynamic language support features.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Service
@SuppressWarnings("unused") // spring bean
public class DefaultExecutionScriptService implements ExecutionScriptService {

    private final static Map<Integer, Script> GROOVY_SCRIPT_CACHE = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(ScriptSource source, ScriptEvaluationContext context) {
        StandardScriptEvaluator scriptEvaluator = new StandardScriptEvaluator();
        scriptEvaluator.setLanguage(context.getScriptLanguage());
        return scriptEvaluator.evaluate(source, context.getArguments());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluateGroovy(ScriptSource source, ScriptEvaluationContext context) {
        GroovyScriptEvaluator scriptEvaluator = new GroovyScriptEvaluator();
        return scriptEvaluator.evaluate(source, context.getArguments());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluateCompiledGroovy(ScriptSource source, ScriptEvaluationContext context) {

        try {
            final String script = source.getScriptAsString();

            if (GROOVY_SCRIPT_CACHE.containsKey(script.hashCode())) {
                return GROOVY_SCRIPT_CACHE.get(script.hashCode()).run();
            }

            GroovyShell shell = new GroovyShell();
            context.getArguments().forEach((k, v) -> shell.setVariable((String) k, v));
            Script gs = shell.parse(new StringReader(source.getScriptAsString()));

            GROOVY_SCRIPT_CACHE.put(script.hashCode(), gs);
            return gs.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
