package org.javawebstack.http.router.testing;

import org.javawebstack.http.router.HTTPMethod;
import org.javawebstack.http.router.HTTPRouter;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class HTTPTest {

    private final HTTPRouter router;
    private final Map<String, String> defaultHeaders = new HashMap<>();

    protected HTTPTest(HTTPRouter router) {
        this.router = router;
    }

    public HTTPRouter getRouter() {
        return router;
    }

    public void setDefaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }

    public void setBearerToken(String token) {
        setDefaultHeader("Authorization", "Bearer " + token);
    }

    public TestExchange httpGet(String url) {
        return httpRequest(HTTPMethod.GET, url, null);
    }

    public TestExchange httpPost(String url) {
        return httpPost(url, null);
    }

    public TestExchange httpPost(String url, Object content) {
        return httpRequest(HTTPMethod.POST, url, content);
    }

    public TestExchange httpPut(String url) {
        return httpPut(url, null);
    }

    public TestExchange httpPut(String url, Object content) {
        return httpRequest(HTTPMethod.PUT, url, content);
    }

    public TestExchange httpDelete(String url) {
        return httpDelete(url, null);
    }

    public TestExchange httpDelete(String url, Object content) {
        return httpRequest(HTTPMethod.DELETE, url, content);
    }

    public TestExchange httpRequest(HTTPMethod method, String url, Object content) {
        TestHTTPSocket socket = new TestHTTPSocket(method, url);
        defaultHeaders.forEach((k, v) -> socket.getRequestHeaders().put(k.toLowerCase(Locale.ROOT), Collections.singletonList(v)));
        if (content != null) {
            if (content instanceof String) {
                socket.setInputStream(new ByteArrayInputStream(((String) content).getBytes(StandardCharsets.UTF_8)));
            } else if (content instanceof byte[]) {
                socket.setInputStream(new ByteArrayInputStream((byte[]) content));
            } else {
                socket.setInputStream(new ByteArrayInputStream(router.getMapper().map(content).toJsonString().getBytes(StandardCharsets.UTF_8)));
            }
        }
        TestExchange exchange = new TestExchange(router, socket);
        router.execute(exchange);
        return exchange;
    }

}
