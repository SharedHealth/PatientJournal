package org.freeshr.journal.launch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.lang.System.getenv;

@Component("patientJournalProperties")
public class ApplicationProperties {

    @Value("${SHR_SERVER_BASE_URL}")
    private String shrBaseUrl;

    @Value("${IDENTITY_SERVER_URL}")
    private String identityServerUrl;

    public ApplicationProperties() {
        Map<String, String> env = getenv();
        shrBaseUrl = env.get("SHR_SERVER_BASE_URL");
        identityServerUrl = env.get("IDENTITY_SERVER_URL");
    }


    public String getIdentityServerUrl(StringBuffer requestURL) {
        return identityServerUrl + "?redirectTo=" + requestURL;
    }

    public String getSHRBaseUrl() {
        return shrBaseUrl;
    }

}
