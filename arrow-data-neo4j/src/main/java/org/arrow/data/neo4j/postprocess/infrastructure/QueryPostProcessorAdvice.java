/*
 * Copyright 2014 Christian Weber
 *
 * This file is build on Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.arrow.data.neo4j.postprocess.infrastructure;

import java.lang.reflect.AccessibleObject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.arrow.data.neo4j.postprocess.QueryPostProcessor;

/**
 * {@link MethodInterceptor} implementation used to enable query post
 * processing.
 * 
 * @author christian.weber
 * @since 1.0.0
 * @see QueryPostProcess
 * @see QueryPostProcessor
 */
public class QueryPostProcessorAdvice implements MethodInterceptor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(MethodInvocation invocation) throws Throwable {

		AccessibleObject ao = invocation.getStaticPart();
		QueryPostProcess pp = ao.getAnnotation(QueryPostProcess.class);

		if (pp != null) {
			Class<? extends QueryPostProcessor<?>> clazz = pp.value();

			@SuppressWarnings("rawtypes")
			QueryPostProcessor qpp = clazz.newInstance();

			return qpp.postProcess(invocation.proceed());
		}

		return invocation.proceed();
	}

}