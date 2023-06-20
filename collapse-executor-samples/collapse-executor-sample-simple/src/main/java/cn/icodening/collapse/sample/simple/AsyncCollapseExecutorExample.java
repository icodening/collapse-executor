package cn.icodening.collapse.sample.simple;

import cn.icodening.collapse.core.SingleThreadExecutor;
import cn.icodening.collapse.core.SuspendableListenableCollector;
import cn.icodening.collapse.core.support.AsyncCallableGroupCollapseExecutor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author icodening
 * @date 2023.06.20
 */
public class AsyncCollapseExecutorExample {

    private static final int PORT = 8080;

    private static final String REMOTE_ENDPOINT = "http://127.0.0.1:" + PORT + SimpleHttpServer.HTTP_PATH;

    public static void main(String[] args) throws Throwable {
        //1. build a simple server
        SimpleHttpServer simpleHttpServer = startSimpleServer();
        //2. build a AsyncCallableGroupCollapseExecutor
        AsyncCallableGroupCollapseExecutor asyncCollapseExecutor = buildCollapseExecutor();

        ThreadPoolExecutor bizThreadPool = new ThreadPoolExecutor(100, 100, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        //3. collapse execute
        OkHttpClient okHttpClient = buildHttpClient();
        for (int i = 0; i < 100; i++) {
            bizThreadPool.execute(() -> {
                Request request = new Request.Builder().url(REMOTE_ENDPOINT).get().build();
                asyncCollapseExecutor.execute("example group " + SimpleHttpServer.HTTP_PATH, () -> {
                    try (Response response = okHttpClient.newCall(request).execute()) {
                        Objects.requireNonNull(response);
                        ResponseBody body = response.body();
                        Objects.requireNonNull(body);
                        return body.string();
                    }
                }).thenAccept(System.out::println);
            });
        }
        Thread.sleep(1000);
        System.out.println("execute times: " + simpleHttpServer.getCounter());
        simpleHttpServer.stop();
        bizThreadPool.shutdown();
    }

    private static OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
                .build();
    }


    private static AsyncCallableGroupCollapseExecutor buildCollapseExecutor() {
        SingleThreadExecutor singleThreadExecutor = new SingleThreadExecutor();
        SuspendableListenableCollector suspendableListeningBundleCollector = new SuspendableListenableCollector(singleThreadExecutor);
        AsyncCallableGroupCollapseExecutor asyncCallableGroupCollapseExecutor = new AsyncCallableGroupCollapseExecutor(suspendableListeningBundleCollector);
        asyncCallableGroupCollapseExecutor.setExecutor(new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }));
        return asyncCallableGroupCollapseExecutor;
    }

    private static SimpleHttpServer startSimpleServer() throws IOException {
        SimpleHttpServer simpleHttpServer = new SimpleHttpServer(PORT);
        simpleHttpServer.start();
        return simpleHttpServer;
    }
}
