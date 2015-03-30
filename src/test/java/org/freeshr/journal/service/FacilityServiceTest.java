package org.freeshr.journal.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.utils.FileUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.Assert.assertEquals;
import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacilityServiceTest {
    @Mock
    private ApplicationProperties applicationProperties;

    private FacilityService facilityService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        facilityService = new FacilityService(applicationProperties);
    }

    @Test
    public void shouldFetchAFacility() throws Exception {
        String authToken = "xyz";
        String clientId = "12345";

        when(applicationProperties.getIdpAuthToken()).thenReturn(authToken);
        when(applicationProperties.getIdpClientId()).thenReturn(clientId);

        givenThat(get(urlEqualTo("/facility"))
                .withHeader("accept", equalTo("application/json"))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(FileUtil.asString("facility.json"))));

        Facility facility = facilityService.getFacility("http://localhost:9997/facility");

        assertEquals("Dohar Upazila Health Complex", facility.getFacilityName());
        assertEquals("10000069", facility.getFacilityId());
        assertEquals("Upazila Health Complex", facility.getFacilityType());
        assertEquals("302618", facility.getCatchments().get(0));
    }
}