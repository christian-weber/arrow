package org.arrow.service.engine.concurrent.dispatch.onsuccess;

import akka.actor.ActorContext;
import akka.dispatch.OnSuccess;
import org.arrow.runtime.message.infrastructure.PersistEventMessage;
import org.arrow.service.engine.actor.template.MasterTemplate;

/**
 * OnSuccess implementation used to persist a message instance.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class PersistOnSuccess extends OnSuccess<Iterable<Object>> {

    private final PersistEventMessage message;
    private final ActorContext actorContext;

    public PersistOnSuccess(PersistEventMessage message, ActorContext context) {
        this.message = message;
        this.actorContext = context;
    }

    @Override
    public void onSuccess(Iterable<Object> objects) {
        if (message.getMessage() != null) {
            actorContext.self().tell(message.getMessage(), actorContext.self());
        }
    }

}
