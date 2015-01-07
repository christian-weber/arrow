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

package org.arrow.runtime.message.infrastructure;

import org.arrow.runtime.message.EntityEventMessage;
import scala.Function1;
import scala.Option;
import scala.PartialFunction;
import scala.Tuple2;
import scala.concurrent.Awaitable;
import scala.concurrent.CanAwait;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.reflect.ClassTag;
import scala.util.Try;

import java.util.concurrent.TimeoutException;

/**
 * Created by christian.weber on 30.09.2014.
 */
@SuppressWarnings("unchecked")
public class FutureAdapter implements Future {

    private final Future future;
    private final EntityEventMessage entity;

    public FutureAdapter(Future future, EntityEventMessage entity) {
        this.future = future;
        this.entity = entity;
    }

    @Override
    public void onSuccess(PartialFunction partialFunction, ExecutionContext executionContext) {
        future.onSuccess(partialFunction, executionContext);
    }

    @Override
    public void onFailure(PartialFunction partialFunction, ExecutionContext executionContext) {
        future.onFailure(partialFunction, executionContext);
    }

    @Override
    public void onComplete(Function1 function1, ExecutionContext executionContext) {
        future.onComplete(function1, executionContext);
    }

    @Override
    public boolean isCompleted() {
        return future.isCompleted();
    }

    @Override
    public Option<Try> value() {
        return future.value();
    }

    @Override
    public Future<Throwable> failed() {
        return future.failed();
    }

    @Override
    public Future andThen(PartialFunction partialFunction, ExecutionContext executionContext) {
        return future.andThen(partialFunction, executionContext);
    }

    @Override
    public Future mapTo(ClassTag classTag) {
        return future.mapTo(classTag);
    }

    @Override
    public Future fallbackTo(Future future) {
        return future.fallbackTo(future);
    }

    @Override
    public Future<Tuple2> zip(Future future) {
        return future.zip(future);
    }

    @Override
    public Future recoverWith(PartialFunction partialFunction, ExecutionContext executionContext) {
        return future.recoverWith(partialFunction, executionContext);
    }

    @Override
    public Future recover(PartialFunction partialFunction, ExecutionContext executionContext) {
        return future.recover(partialFunction, executionContext);
    }

    @Override
    public Future collect(PartialFunction partialFunction, ExecutionContext executionContext) {
        return future.collect(partialFunction, executionContext);
    }

    @Override
    public Future withFilter(Function1 function1, ExecutionContext executionContext) {
        return future.withFilter(function1, executionContext);
    }

    @Override
    public Future filter(Function1 function1, ExecutionContext executionContext) {
        return future.filter(function1, executionContext);
    }

    @Override
    public Future flatMap(Function1 function1, ExecutionContext executionContext) {
        return future.flatMap(function1, executionContext);
    }

    @Override
    public Future map(Function1 function1, ExecutionContext executionContext) {
        return future.map(function1, executionContext);
    }

    @Override
    public Future transform(Function1 function1, Function1 function12, ExecutionContext executionContext) {
        return future.transform(function1, function12, executionContext);
    }

    @Override
    public void foreach(Function1 function1, ExecutionContext executionContext) {
        future.foreach(function1, executionContext);
    }

    @Override
    public Awaitable ready(Duration duration, CanAwait canAwait) throws TimeoutException, InterruptedException {
        return future.ready(duration, canAwait);
    }

    @Override
    public Object result(Duration duration, CanAwait canAwait) throws Exception {
        return future.result(duration, canAwait);
    }

    @Override
    public String toString() {
        return "FutureAdapter[" + entity + ']' + entity.getExecution().getState();
    }
}
