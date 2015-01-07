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

package org.arrow.service.rule;

import com.thoughtworks.xstream.XStream;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.rule.*;

import java.util.Map;

/**
 * Drools {@link }RuleEvaluator} implementation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@Component
public class DroolsRuleEvaluator implements RuleEvaluator {

    private final transient static LoggerFacade LOGGER = new LoggerFacade(DroolsRuleEvaluator.class);

    @Autowired
    private ConversionService conversionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(RuleSource ruleSource, RuleEvaluationContext context) throws RuleCompilationException {


        // KieServices is the factory for all KIE services
        KieServices ks = KieServices.Factory.get();
        // From the kie services, a container is created from the classpath
        KieContainer kc = ks.getKieClasspathContainer();
        // From the container, a session is created based on
        // its definition and configuration in the META-INF/kmodule.xml file
        KieSession ksession = kc.newKieSession(ruleSource.getSourceAsString());

        // setup global arguments
        for (RuleData data : context.getDataList()) {
            switch (data.getType()) {
                case GLOBAL: ksession.setGlobal(data.getName(), getVariable(context, data.getName()));
                case LOCAL: ksession.insert(getVariable(context, data.getName()));
            }
        }

        // register listeners
        if (LOGGER.isDebugEnabled()) {
            ksession.addEventListener(new DebugAgendaEventListener());
            ksession.addEventListener(new DebugRuleRuntimeEventListener());
        }

        ksession.fireAllRules();
        ksession.dispose();

        return context.getVariables();
    }

    private Object getVariable(RuleEvaluationContext context, String key) {
        try {
            Map<String, Object> map = context.getVariables();

            Object value = map.get(key);
            if (map.containsKey(key + "-type")) {
                String type = (String) map.get(key + "-type");
                Class<?> cls = Class.forName(type);

                if (conversionService.canConvert(String.class, cls)) {
                    return conversionService.convert(value, cls);
                } else {
                    XStream xstream = new XStream();
                    xstream.processAnnotations(cls);

                    return xstream.fromXML(value.toString());
                }
            }

            return value;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
