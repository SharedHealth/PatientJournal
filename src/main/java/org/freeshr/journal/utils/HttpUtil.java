package org.freeshr.journal.utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "password";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String AUTH_TOKEN_KEY = "X-Auth-Token";
    public static final String FROM_KEY = "From";

    public static Map<String, String> getIDPHeaders(ApplicationProperties properties) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CLIENT_ID_KEY, properties.getIdpClientId());
        headers.put(AUTH_TOKEN_KEY, properties.getIdpAuthToken());
        return headers;
    }

    public static List<? extends NameValuePair> getNameValuePairs(UserCredentials credentials) {
        ArrayList<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair(EMAIL_KEY, credentials.getEmail()));
        valuePairs.add(new BasicNameValuePair(PASSWORD_KEY, credentials.getPassword()));
        return valuePairs;
    }

    public static Map<String, String> createSecurityHeaders(UserInfo userInfo) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CLIENT_ID_KEY, userInfo.getId());
        headers.put(AUTH_TOKEN_KEY, userInfo.getAccessToken());
        headers.put(FROM_KEY, userInfo.getEmail());
        return headers;
    }

}
