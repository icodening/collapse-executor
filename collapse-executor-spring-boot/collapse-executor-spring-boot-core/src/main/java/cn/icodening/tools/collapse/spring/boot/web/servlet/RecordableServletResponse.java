package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.ServletOutputStream;
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
    public ServletOutputStream getOutputStream() throws IOException {
        return recordableServletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }
}
