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

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Component;

/**
 * {@link BeanPostProcessor} implementation used to register the query post
 * processing infrastructure.
 * 
 * @author christian.weber
 * @since 1.0.0
 * @see QueryPostProcessorAdvice
 */
@Component
public class GraphRepositoryBeanPostProcessor implements BeanPostProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {

		if (bean instanceof GraphRepository<?>) {
			ProxyFactory factory = new ProxyFactory(bean);
			factory.addAdvice(new QueryPostProcessorAdvice());
			return factory.getProxy();
		}
		return bean;
	}

}
