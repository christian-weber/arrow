package org.arrow.runtime.execution.service.data;

import org.arrow.runtime.api.process.ProcessSpecification;

public interface SubProcessRepository {

    Iterable<? extends ProcessSpecification> findAllBySignalEvent(String pid, String signalRef);

    Iterable<? extends ProcessSpecification> findAllByMessageEvent(String pid, String messageRef);

    Iterable<? extends ProcessSpecification> findAllByConditionalEvent(String pid, String beanName);

}
