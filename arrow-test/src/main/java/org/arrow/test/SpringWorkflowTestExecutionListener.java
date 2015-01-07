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

package org.arrow.test;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.StopWatch;
import org.arrow.model.process.Definitions;
import org.arrow.parser.xml.bpmn.BpmnParser;
import org.arrow.parser.xml.bpmn.XStreamBpmnParser;
import org.arrow.runtime.service.RepositoryService;

public class SpringWorkflowTestExecutionListener extends
		AbstractTestExecutionListener {

    public final static ThreadLocal<ApplicationContext> CONTEXT_HOLDER = new ThreadLocal<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {

		ApplicationContext context = testContext.getApplicationContext();
        CONTEXT_HOLDER.set(context);

		RepositoryService rs = context.getBean(RepositoryService.class);

		Class<?> testClass = testContext.getTestClass();

		Method method = testContext.getTestMethod();
		Given given = findAnnotation(method, Given.class);

		if (given != null) {
			final String[] fileNames = given.value();

			for (String fileName : fileNames) {
				BpmnParser driver = new XStreamBpmnParser();
				InputStream stream = testClass.getResourceAsStream(fileName);
				Definitions definitions = driver.parse(stream);

				rs.deploy(definitions);
			}
		}

		StopWatch stopWatch = new StopWatch();
		stopWatch.start("START TEST");
		testContext.setAttribute("stopWatch", stopWatch);
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
        CONTEXT_HOLDER.set(null);

		StopWatch stopWatch = (StopWatch) testContext.getAttribute("stopWatch");
		if (stopWatch != null && stopWatch.isRunning()) {
			stopWatch.stop();
			System.out.println(stopWatch);
		}
	}

}
