/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.BlockingSameOutputCollapseExecutor;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.util.ThrowableCallable;

import java.util.Collection;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class BlockingCallableGroupCollapseExecutor {

    private final InternalBlockingCallableGroupCollapseExecutor<Object> collapseExecutor;

    public BlockingCallableGroupCollapseExecutor() {
        this.collapseExecutor = new InternalBlockingCallableGroupCollapseExecutor<>();
        this.collapseExecutor.setName(this.getClass().getSimpleName());
    }

    public BlockingCallableGroupCollapseExecutor(ListeningCollector collector) {
        this.collapseExecutor = new InternalBlockingCallableGroupCollapseExecutor<>(collector);
        this.collapseExecutor.setName(this.getClass().getSimpleName());
    }

    public void setName(String name) {
        this.collapseExecutor.setName(name);
    }

    @SuppressWarnings("unchecked")
    public <R> R execute(Object group, ThrowableCallable<R> callable) throws Throwable {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (R) collapseExecutor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalBlockingCallableGroupCollapseExecutor<R> extends BlockingSameOutputCollapseExecutor<CallableGroup<R>, R> {

        public InternalBlockingCallableGroupCollapseExecutor() {
            super();
        }

        private InternalBlockingCallableGroupCollapseExecutor(ListeningCollector collector) {
            super(collector);
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
