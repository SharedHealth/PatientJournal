package org.freeshr.journal.launch;

import org.springframework.stereotype.Component;

import java.util.Map;

import static java.lang.System.getenv;

@Component("patientJournalProperties")
public class ApplicationProperties {

    private String shrBaseUrl;

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
