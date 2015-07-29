package org.freeshr.journal.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.Facility;
import org.freeshr.journal.model.Provider;
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

public class ProviderServiceTest {
    @Mock
    private ApplicationProperties applicationProperties;

    private ProviderService providerService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        providerService = new ProviderService(applicationProperties);
    }

    @Test
    public void shouldFetchAProvider() throws Exception {
        String authToken = "xyz";
        String clientId = "12345";

        when(applicationProperties.getIdpAuthToken()).thenReturn(authToken);
        when(applicationProperties.getIdpClientId()).thenReturn(clientId);

        givenThat(get(urlEqualTo("/provider"))
                .withHeader("accept", equalTo("application/json"))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(FileUtil.asString("provider.json"))));

        Provider provider = providerService.getProvider("http://localhost:9997/provider");

        assertEquals("Md. Al-Amin", provider.getName());
        assertEquals("113024", provider.getId());
        assertEquals("10002085", provider.getFacilityId());
    }
}