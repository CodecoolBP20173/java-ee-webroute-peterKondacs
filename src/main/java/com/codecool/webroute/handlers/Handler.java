package com.codecool.webroute.handlers;

import com.codecool.webroute.annotations.WebRoute;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) {
        String requestURIString = httpExchange.getRequestURI().toString();
        Method handlerMethod = null;

        try {
            handlerMethod = getHandlerMethodByRequestURI(requestURIString);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (handlerMethod != null) {
            try {
                handlerMethod.invoke(this, httpExchange);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @WebRoute("/test")
    public void onTest(HttpExchange httpExchange) throws IOException {
        String response = "Test";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    @WebRoute("/user/<userName>")
    public void onUser(HttpExchange httpExchange) throws IOException {
        String response = "Welcome " + httpExchange.getRequestURI().toString().replace("/user/", "");
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    public void returnContextNotFound(HttpExchange httpExchange) throws IOException {
        String response = "<p><h1>404 Not Found</h1></p><p>No context found for request</p>";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    public List<String> getWebRoutes() {
        List<String> webRoutes = new ArrayList<>();
        Method[] methods = this.getClass().getDeclaredMethods();
        Class<WebRoute> webRouteClass = WebRoute.class;

        for (Method method: methods) {
            if (method.isAnnotationPresent(webRouteClass)) {
                webRoutes.add(method.getAnnotation(webRouteClass).value().replaceAll("<.*>", ""));
            }
        }

        return webRoutes;
    }

    private Method getHandlerMethodByRequestURI(String requestURIString) throws NoSuchMethodException {
        Method[] methods = this.getClass().getDeclaredMethods();
        Class<WebRoute> webRouteClass = WebRoute.class;

        for (Method method: methods) {
            if (method.isAnnotationPresent(webRouteClass)) {
                String annotationRoute = method.getAnnotation(webRouteClass).value().replaceAll("<.*>", "");
                String lastCharacter = annotationRoute.substring(annotationRoute.length() - 1);

                if (lastCharacter.equals("/")) {
                    if (requestURIString.contains(annotationRoute)) {
                        return method;
                    }

                } else {
                    if (requestURIString.equals(annotationRoute)) {
                        return method;
                    }
                }
            }
        }

        return this.getClass().getDeclaredMethod("returnContextNotFound", HttpExchange.class);
    }
}
