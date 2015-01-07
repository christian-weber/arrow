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

package org.arrow.runtime.scope;

import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.util.Assert;
import org.arrow.runtime.support.EngineSynchronizationManager;

/**
 * Custom {@link Scope} implementation used for process scope bean definitions.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class ProcessScope implements Scope {
	
	public static boolean isSet() {
		return EngineSynchronizationManager.getProcessScope() != null;
	}

    /**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(String key, ObjectFactory<?> beanFactory) {

		Map<String, Object> map = EngineSynchronizationManager.getProcessScope();
		Assert.notNull(map, "map is null, key: " + key);
		if (map.containsKey(key)) {
			return map.get(key);
		}
		map.put(key, beanFactory.getObject());
		return map.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConversationId() {
		return "process";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerDestructionCallback(String name, Runnable runnable) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object remove(String key) {
		Map<String, Object> map = EngineSynchronizationManager.getProcessScope();
		return map.remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> resolveContextualObject(String key) {
		if ("process".equals(key)) {
			return EngineSynchronizationManager.getProcessScope();
		}
		return null;
	}

}
