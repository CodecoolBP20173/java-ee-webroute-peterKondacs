package com.codecool.webroute;

import com.codecool.webroute.handlers.Handler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.List;

class Server {

    private static final int PORT = 8000;

    public static void main(String[] args) throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        Handler handler = new Handler();
        List<String> handledWebRoutes = handler.getWebRoutes();

        for (String webRoute: handledWebRoutes) {
            httpServer.createContext(webRoute, handler);
        }

        httpServer.setExecutor(null);
        httpServer.start();
    }

}
