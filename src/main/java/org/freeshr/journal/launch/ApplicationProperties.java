package org.freeshr.journal.launch;

import org.apache.commons.lang3.StringUtils;
import org.freeshr.journal.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("patientJournalProperties")
public class ApplicationProperties {
    public static final String URL_SEPERATOR = "/";


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

    @Value("${FACILITY_REGISTRY_URL}")
    private String facilityRegistryUrl;

    @Value("${PROVIDER_REGISTRY_URL}")
    private String providerRegistryUrl;

    @Value("${SESSION_TIMEOUT_SECONDS}")
    private String sessionTimeoutInSeconds;

    @Value("${MCI_SERVER_PATIENTS_URL}")
    private String mciServerPatientsUrl;

    public String getSHRBaseUrl() {
        return StringUtils.isEmpty(shrVersion) ? shrBaseUrl : String.format("%s/%s", shrBaseUrl, shrVersion);
    }

    public String getIdpAuthToken() {
        return idpAuthToken;
    }

    public String getFacilityRegistryUrl() {
        return StringUtil.ensureSuffix(facilityRegistryUrl, URL_SEPERATOR);
    }

    public String getProviderRegistryUrl() {
        return StringUtil.ensureSuffix(providerRegistryUrl, URL_SEPERATOR);
    }

    public String getIdentityServerLoginUrl() {
        return StringUtil.removeSuffix(identityServerLoginUrl, URL_SEPERATOR);
    }

    public String getIdentityServerUserInfoUrl() {
        return StringUtil.ensureSuffix(identityServerUserInfoUrl, URL_SEPERATOR);
    }

    public String getIdpClientId() {
        return idpClientId;
    }

    public int getSessionTimeoutInSeconds() {
        return Integer.parseInt(sessionTimeoutInSeconds);
    }

    public String getMciServerPatientsUrl() {
        return StringUtil.ensureSuffix(mciServerPatientsUrl, URL_SEPERATOR);
    }

}
