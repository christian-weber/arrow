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

package org.arrow.runtime.execution.listener;

import java.util.Comparator;

import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.arrow.runtime.config.Infrastructure;

/**
 * {@link Comparator} implementation for bpmn node listener instances. The
 * comparator is aware of the {@link Infrastructure} annotation which is used to
 * priors infrastructure listeners in order to sort them to the begin of the
 * bpmn node listener list.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public final class BpmnNodeListenerComparator implements
		Comparator<Object> {

    public final static BpmnNodeListenerComparator INSTANCE = new BpmnNodeListenerComparator();

    private BpmnNodeListenerComparator() {
        super();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Object o1, Object o2) {

		boolean expr1 = isInfrastructureListener(o1);
		boolean expr2 = isInfrastructureListener(o2);

		if (expr1 && expr2) {
			return compareOrder(o1, o2);
		}

		if (expr1) {
			return -1;
		}

		return 1;
	}

	/**
	 * Indicates if the given {@link ExecutionListener} is a infrastructure
	 * execution listener.
	 * 
	 * @param listener the execution listener instance
	 * @return boolean
	 */
	private boolean isInfrastructureListener(Object listener) {
		Class<?> cls = listener.getClass();
		return AnnotationUtils.findAnnotation(cls, Infrastructure.class) != null;
	}

	/**
	 * Compares the order of the given {@link ExecutionListener} instances.
	 * 
	 * @param o1 the first execution listener instance
	 * @param o2 the second execution listener instance
	 * @return int
	 */
	private int compareOrder(Object o1, Object o2) {
		return OrderComparator.INSTANCE.compare(o1, o2);
	}

}
