package org.freeshr.journal.launch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("patientJournalProperties")
public class ApplicationProperties {

    @Value("${SHR_SERVER_BASE_URL}")
    private String shrBaseUrl;

    @Value("${SHR_VERSION}")
    private String shrVersion;

    @Value("${IDENTITY_SERVER_LOGIN_URL}")
    private String identityServerLoginUrl;

    @Value("${IDENTITY_SERVER_USER_INFO_URL}")
    private String identityServerUserInfoUrl;

    @Value("${IDP_CLIENT_ID}")
    private String idpClientId;

    @Value("${IDP_AUTH_TOKEN}")
    private String idpAuthToken;

    @Value("${FACILITY_SERVER_URL}")
    private String facilityServerUrlPrefix;

    @Value("${SESSION_TIMEOUT_SECONDS}")
    private String sessionTimeoutInSeconds;

    @Value("${MCI_SERVER_BASE_URL}")
    private String mciServerBaseUrl;

    public String getSHRBaseUrl() {
        return StringUtils.isEmpty(shrVersion) ? shrBaseUrl : String.format("%s/%s", shrBaseUrl, shrVersion);
    }

    public String getIdpAuthToken() {
        return idpAuthToken;
    }

    public String getFacilityServerUrlPrefix() {
        return facilityServerUrlPrefix;
    }

    public String getIdentityServerLoginUrl() {
        return identityServerLoginUrl;
    }

    public String getIdentityServerUserInfoUrl() {
        return ensureUrlEndsWithSlash(identityServerUserInfoUrl);
    }

    public String getIdpClientId() {
        return idpClientId;
    }

    public int getSessionTimeoutInSeconds() {
        return Integer.parseInt(sessionTimeoutInSeconds);
    }

    public String getMciServerBaseUrl() {
        return ensureUrlEndsWithSlash(mciServerBaseUrl);
    }

    private String ensureUrlEndsWithSlash(String url) {
        url = StringUtils.trim(url);
        return url.endsWith("/") ? url : url + "/";
    }

}
