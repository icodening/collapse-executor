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
package cn.icodening.collapse.web.server;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author icodening
 * @date 2023.05.20
 */
public class RecordableServletOutputStream extends ServletOutputStream {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);

    private final ServletOutputStream outputStream;

    public RecordableServletOutputStream(ServletOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        this.outputStream.setWriteListener(listener);
    }

    @Override
    public void write(int b) throws IOException {
        byteArrayOutputStream.write(b);
        outputStream.write(b);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        super.close();
        outputStream.close();
    }

    public byte[] getRecordBytes() {
        return this.byteArrayOutputStream.toByteArray();
    }
}
