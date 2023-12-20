import Handlers.GetProductHandler;
import Handlers.PostProductHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private final int PORT = 8000;
    private final int THREADS = 3;
    private HttpServer httpServer;

    public Server() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), THREADS);
            httpServer.createContext("/product/getProduct", new GetProductHandler());
            httpServer.createContext("/product/postProduct", new PostProductHandler());
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
