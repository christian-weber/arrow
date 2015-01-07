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

package org.arrow.service.engine.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.arrow.service.engine.actor.MasterActor;
import org.arrow.service.engine.actor.NodeActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorContext;
import akka.japi.Creator;
import akka.routing.SmallestMailboxPool;

/**
 * Spring Akka java context configuration.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@Configuration
public class AkkaConfiguration {

	@Autowired
	private ActorSystem system;
	@Autowired
	private ApplicationContext context;

	/**
	 * Creates the {@link org.arrow.service.engine.actor.MasterActor} instance.
	 * 
	 * @return ActorRef
	 */
	@Bean(name="master")
	@Scope("prototype")
	public ActorRef master() {
		Props props = Props.create(new MasterCreator(context));
		return system.actorOf(props);
	}

	/**
	 * Instantiates the {@link ActorSystem} instance.
	 * 
	 * @return ActorSystem
	 */
	@Bean
	public ActorSystem actorSystem() {
		return ActorSystem.create("RuntimeService");
	}

	@SuppressWarnings("serial")
	public static class MasterCreator implements Creator<MasterActor> {

		private final ApplicationContext context;
		
		public MasterCreator(ApplicationContext context) {
			this.context = context;
		}
		
		@Override
		public MasterActor create() throws Exception {
			// provide the scope map
			Map<String, Object> scopeMap = new HashMap<>();

			// create the MASTER actor
			MasterActor masterActor = new MasterActor(context, scopeMap);

			// create the NODE WORKER actor
			ActorRef nodeWorker = nodeWorker(masterActor.getContext(), scopeMap);
			masterActor.setNodeWorker(nodeWorker);

			return masterActor;
		}
		
		/**
		 * Creates the {@link org.arrow.service.engine.actor.NodeActor} instance.
		 * 
		 * @param actorContext the actor context instance
		 * @param scopeMap the scope map
		 * @return ActorRef
		 */
		private ActorRef nodeWorker(final UntypedActorContext actorContext,
				final Map<String, Object> scopeMap) {

			Props props = Props.create(new NodeWorkerCreator(context, scopeMap));
			props = props.withRouter(new SmallestMailboxPool(4));

			return actorContext.actorOf(props, "node");
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class NodeWorkerCreator implements Creator<NodeActor> {
		
		private final ApplicationContext context;
		private final Map<String, Object> scopeMap;
		
		public NodeWorkerCreator(ApplicationContext context, Map<String, Object> scopeMap) {
			this.context = context;
			this.scopeMap = scopeMap;
		}
		
		@Override
		public NodeActor create() throws Exception {
			return new NodeActor(context, scopeMap);
		}
		
	}
	
}
