package cn.icodening.collapse.sample.simple;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2023.06.20
 */
public class SimpleHttpServer {

    static final String HTTP_PATH = "/example";

    private final AtomicInteger counter = new AtomicInteger(0);

    private HttpServer httpServer;

    private final int port;

    public SimpleHttpServer() {
        this(8080);
    }

    public SimpleHttpServer(int port) {
        this.port = port;
    }

    public synchronized void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext(HTTP_PATH, exchange -> {
            counter.incrementAndGet();
            String resp = "hello world. collapse executor";
            byte[] respData = resp.getBytes();
            exchange.sendResponseHeaders(200, resp.getBytes().length);
            try (OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(respData);
                responseBody.flush();
            }
        });
        httpServer.start();
        this.httpServer = httpServer;
    }

    public int getCounter() {
        return this.counter.get();
    }

    public void resetCounter() {
        this.counter.set(0);
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }
}
