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
package com.icodening.collapse.web.server;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author icodening
 * @date 2023.05.20
 */
class RecordableServletResponse extends HttpServletResponseWrapper {

    private final RecordableServletOutputStream recordableServletOutputStream;
    private final PrintWriter writer;

    public RecordableServletResponse(HttpServletResponse response) throws IOException {
        super(response);
        this.recordableServletOutputStream = new RecordableServletOutputStream(response.getOutputStream());
        this.writer = new PrintWriter(recordableServletOutputStream, true);
    }

    @Override
    public RecordableServletOutputStream getOutputStream() throws IOException {
        return recordableServletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }
}
