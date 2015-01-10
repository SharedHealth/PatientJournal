package org.freeshr.journal.launch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ApplicationProperties {
    @Value("${IDENTITY_SERVER_URL}")
    private String identityServerUrl;

    public String getIdentityServerUrl(StringBuffer requestURL) {
        return identityServerUrl + "?redirectTo=" + requestURL;
    }
}
