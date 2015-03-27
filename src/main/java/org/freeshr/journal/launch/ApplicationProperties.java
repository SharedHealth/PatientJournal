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

    @Value("${IDENTITY_SERVER_BASE_URL}")
    private String identityServerBaseUrl;

    @Value("${IDENTITY_SERVER_SIGNIN_PATH}")
    private String identityServerSigninPath;

    @Value("${IDENTITY_SERVER_USER_INFO_PATH}")
    private String identityServerUserInfoPath;

    @Value("${IDP_CLIENT_ID}")
    private String idpClientId;

    @Value("${IDP_AUTH_TOKEN}")
    private String idpAuthToken;

    @Value("${FACILITY_SERVER_URL}")
    private String facilityServerUrlPrefix;

    public String getSHRBaseUrl() {
        return StringUtils.isEmpty(shrVersion) ? shrBaseUrl : String.format("%s/%s", shrBaseUrl, shrVersion);
    }

    public String getIdpAuthToken() {
        return idpAuthToken;
    }

    public String getFacilityServerUrlPrefix() {
        return facilityServerUrlPrefix;
    }

    public String getIdentityServerBaseUrl() {
        return ensureUrlEndsWithSlash(identityServerBaseUrl);
    }

    public String getIdentityServerSigninUrl() {
        return getIdentityServerBaseUrl()+ identityServerSigninPath;
    }

    public String getIdentityServerUserInfoUrl() {
        return getIdentityServerBaseUrl() + ensureUrlEndsWithSlash(identityServerUserInfoPath);
    }

    public String getIdpClientId() {
        return idpClientId;
    }

    private String ensureUrlEndsWithSlash(String url) {
        url = StringUtils.trim(url);
        return url.endsWith("/") ? url : url + "/";
    }

}
