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

package org.arrow.service.engine.concurrent.dispatch.onsuccess;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.dispatch.OnSuccess;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.arrow.runtime.execution.ProcessInstance;

/**
 * {@link akka.dispatch.OnSuccess} implementation which is used to release the lock and to
 * notify all waiting threads in order to release the blocking state.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class StopActorOnSuccess extends OnSuccess<Iterable<Object>> {

	private final ActorContext context;
    private final ActorRef actorRef;

	public StopActorOnSuccess(ActorContext context, ActorRef actorRef) {
		Assert.notNull(context);
        Assert.notNull(actorRef);

        this.context = context;
        this.actorRef = actorRef;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(Iterable<Object> nodes) {
//		context.stop(actorRef);
        actorRef.tell(PoisonPill.getInstance(), actorRef);
	}

}