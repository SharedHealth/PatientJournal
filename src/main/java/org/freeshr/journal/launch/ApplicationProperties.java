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

    @Value("${FACILITY_AUTH_TOKEN}")
    private String facilityAuthToken;

    @Value("${FACILITY_SERVER_URL}")
    private String facilityServerUrlPrefix;

    public String getIdentityServerUrl(StringBuffer requestURL) {
        return identityServerUrl + "?redirectTo=" + requestURL;
    }

    public String getSHRBaseUrl() {
        return shrBaseUrl;
    }

    public String getFacilityAuthToken() {
        return facilityAuthToken;
    }

    public String getFacilityServerUrlPrefix() {
        return facilityServerUrlPrefix;
    }
}
