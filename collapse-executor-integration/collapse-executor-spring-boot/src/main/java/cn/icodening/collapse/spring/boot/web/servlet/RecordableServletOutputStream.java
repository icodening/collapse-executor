package cn.icodening.collapse.spring.boot.web.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author icodening
 * @date 2023.05.20
 */
class RecordableServletOutputStream extends ServletOutputStream {

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
