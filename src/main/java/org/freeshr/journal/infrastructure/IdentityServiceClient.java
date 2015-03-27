package org.freeshr.journal.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.IdentityToken;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.getIDPHeaders;
import static org.freeshr.journal.utils.HttpUtil.getNameValuePairs;

@Component
public class IdentityServiceClient {

    private ApplicationProperties properties;

    @Autowired
    public IdentityServiceClient(ApplicationProperties properties) {
        this.properties = properties;
    }

    public IdentityToken signin(UserCredentials credentials) throws IOException {
        Map<String, String> headers = getIDPHeaders(properties);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(getNameValuePairs(credentials));
        String response = new WebClient().post(properties.getIdentityServerSigninUrl(), headers, entity);
        if (StringUtils.isNotEmpty(response)) {
            return new ObjectMapper().readValue(response, IdentityToken.class);
        }
        return null;
    }

    public UserInfo getUserInfo(IdentityToken identityToken) throws IOException {
        Map<String, String> headers = getIDPHeaders(properties);
        String response = new WebClient().get(getUserInfoUrl(identityToken), headers);
        if (StringUtils.isNotEmpty(response)) {
            return new ObjectMapper().readValue(response, UserInfo.class);
        }
        return null;


    }

    private String getUserInfoUrl(IdentityToken identityToken) {
        return properties.getIdentityServerUserInfoUrl() + identityToken.toString();
    }
}
