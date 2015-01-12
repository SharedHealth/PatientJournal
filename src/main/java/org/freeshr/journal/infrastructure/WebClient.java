package org.freeshr.journal.infrastructure;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebClient {

    private String baseUrl;
    private Map<String, String> headers;

    public WebClient(String baseUrl, Map<String, String> headers) {
        this.baseUrl = baseUrl;
        this.headers = headers;
    }

    public String get(String path) throws IOException {
        String url = getUrl(path);
            HttpGet request = new HttpGet(URI.create(url));
            return execute(request);
    }


    private String execute(final HttpRequestBase request) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            addHeaders(request);

            ResponseHandler<String> responseHandler = new
                    ResponseHandler<String>() {
                public String handleResponse(final HttpResponse response)
                        throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? parseContentInputAsString(entity) : null;
                    } else if (status == HttpStatus.SC_NOT_FOUND) {
                        return null;
                    } else if (status == HttpStatus.SC_UNAUTHORIZED) {
                        throw new IdentityUnauthorizedException("Identity not authorized");
                    } else {
                        throw new HttpResponseException(status, "Unexpected response status: " + status);
                    }
                }
            };
            return httpClient.execute(request, responseHandler);
        }
    }


    private String parseContentInputAsString(HttpEntity entity) throws
            IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        String inputLine;
        StringBuilder responseString = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            responseString.append(inputLine);
        }
        reader.close();
        return responseString.toString();
    }

    private void addHeaders(HttpRequestBase request) {
        Map<String, String> requestHeaders = getCommonHeaders();
        if (headers != null) {
            requestHeaders.putAll(headers);
        }

        for (String key : requestHeaders.keySet()) {
            request.addHeader(key, requestHeaders.get(key));
        }
    }

    private Map<String, String> getCommonHeaders() {
        Map<String, String> commonHeaders = new HashMap<>();
        commonHeaders.put("accept", "application/atom+xml");
        return commonHeaders;
    }

    private String getUrl(String path) {
        return baseUrl + path;
    }
}
