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
package cn.icodening.collapse.core;

import java.util.HashMap;
import java.util.Map;

/**
 * input holder
 *
 * @author icodening
 * @date 2023.05.14
 */
public class Input<E> {

    private final E input;

    private final Bundle<E, Object> bundle;

    private Map<String, Object> attachments;

    Input(E input, Bundle<E, Object> bundle) {
        this.input = input;
        this.bundle = bundle;
    }

    @SuppressWarnings("unchecked")
    <OUTPUT> Bundle<E, OUTPUT> getBundle() {
        return (Bundle<E, OUTPUT>) bundle;
    }

    public E value() {
        return input;
    }

    private void initAttachmentMap() {
        this.attachments = new HashMap<>(8);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttachment(String key) {
        if (this.attachments == null) {
            initAttachmentMap();
            return null;
        }
        Object value = this.attachments.get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public void setAttachment(String key, Object value) {
        if (this.attachments == null) {
            initAttachmentMap();
        }
        this.attachments.put(key, value);
    }

    public void removeAttachment(String key) {
        if (this.attachments == null) {
            return;
        }
        this.attachments.remove(key);
    }

    public void putAll(Map<String, Object> map) {
        if (this.attachments == null) {
            initAttachmentMap();
        }
        if (map == null) {
            this.attachments.clear();
            return;
        }
        this.attachments.putAll(map);
    }
}
