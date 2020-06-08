package com.codecool.krk;

import com.codecool.krk.helpers.CookieHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.List;
import java.util.Optional;

public class Cookie implements HttpHandler {
    int counter = 0;
    private static final String SESSION_COOKIE_NAME = "sesionId";
    CookieHelper cookieHelper = new CookieHelper();

//    @Override
//    public void handle(HttpExchange httpExchange) throws IOException {
//        counter++;
//        String response = "Page was visited: " + counter + " times!";
//
//
//        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
//        HttpCookie cookie;
//        boolean isNewSession;
//        if (cookieStr != null) {  // Cookie already exists
//            cookie = HttpCookie.parse(cookieStr).get(0);
//            isNewSession = false;
//        } else { // Create a new cookie
//            cookie = new HttpCookie("sessionId", String.valueOf(counter)); // This isn't a good way to create sessionId. Find out better!
//            isNewSession = true;
//            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
//        }
//
//        response += "\n isNewSession: " + isNewSession;
//        response += "\n session id: " + cookie.getValue();
//
//
//        sendResponse(httpExchange, response);
////        httpExchange.sendResponseHeaders(200, response.length());
////        OutputStream os = httpExchange.getResponseBody();
////        os.write(response.getBytes());
////        os.close();
//    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        counter++;
        String response = "Page was visited: " + counter + " times!";

        Optional<HttpCookie> cookie = getSessionIdCookie(httpExchange);

        boolean isNewSession;
        if (cookie.isPresent()) {
            isNewSession = false;
        } else {
            isNewSession = true;
            String sessionId = String.valueOf(counter);
            cookie = Optional.of(new HttpCookie(SESSION_COOKIE_NAME, sessionId));
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
        }
        response += "\n isNewSession: " + isNewSession;
        response += "\n session id: " + cookie.get().getValue();

        sendResponse(httpExchange, response);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Optional<HttpCookie> getSessionIdCookie(HttpExchange httpExchange) {
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        return cookieHelper.findCookieByName(SESSION_COOKIE_NAME, cookies);
    }
}
