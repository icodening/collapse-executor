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
package com.icodening.collapse.core;

import java.util.List;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class BlockingSameOutputCollapseExecutor<INPUT, OUTPUT> extends CollapseExecutorBlockingSupport<INPUT, OUTPUT, OUTPUT> {

    public BlockingSameOutputCollapseExecutor() {
        super();
    }

    public BlockingSameOutputCollapseExecutor(ListeningCollector collector) {
        super(collector);
    }

    @Override
    protected void bindingOutput(OUTPUT batchOutput, List<Bundle<INPUT, OUTPUT>> bundles) {
        for (Bundle<INPUT, OUTPUT> bundle : bundles) {
            bundle.bindOutput(batchOutput);
        }
    }
}
