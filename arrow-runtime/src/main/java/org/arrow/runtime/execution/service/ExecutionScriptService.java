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

import org.springframework.scripting.ScriptSource;
import org.arrow.runtime.rule.RuleEvaluationContext;
import org.arrow.runtime.rule.RuleSource;

import java.util.Map;

/**
 * Script execution service definition used to evaluate scripts.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface ExecutionScriptService {

    /**
     * Evaluates the given script source with the given script evaluation context.
     *
     * @param source  the script source
     * @param context the script evaluation context
     * @return Object
     */
    Object evaluate(ScriptSource source, ScriptEvaluationContext context);

    /**
     * Evaluates the given groovy script source with the given script evaluation context.
     *
     * @param source  the script source
     * @param context the script evaluation context
     * @return Object
     */
    Object evaluateGroovy(ScriptSource source, ScriptEvaluationContext context);

    Object evaluateCompiledGroovy(ScriptSource source, ScriptEvaluationContext context);

    /**
     * The context for a script evaluation.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class ScriptEvaluationContext {

        private final String scriptLanguage;
        private final Map<String, Object> arguments;

        public ScriptEvaluationContext(String scriptLanguage, Map<String, Object> arguments) {
            this.scriptLanguage = scriptLanguage;
            this.arguments = arguments;
        }

        /**
         * Returns the script language.
         *
         * @return String
         */
        public String getScriptLanguage() {
            return scriptLanguage;
        }

        /**
         * Returns the script arguments
         *
         * @return Map
         */
        public Map<String, Object> getArguments() {
            return arguments;
        }
    }

}
