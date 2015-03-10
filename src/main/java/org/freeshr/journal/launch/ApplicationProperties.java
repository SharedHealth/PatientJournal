package org.freeshr.journal.launch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("patientJournalProperties")
public class ApplicationProperties {

    @Value("${SHR_SERVER_BASE_URL}")
    private String shrBaseUrl;

    @Value("${SHR_VERSION}")
    private String shrVersion;

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
        return String.format("%s/%s",shrBaseUrl,shrVersion);
    }

    public String getFacilityAuthToken() {
        return facilityAuthToken;
    }

    public String getFacilityServerUrlPrefix() {
        return facilityServerUrlPrefix;
    }
}
