package org.freeshr.journal.infrastructure;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.IdentityToken;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdentityServiceClientTest {

    @Mock
    private ApplicationProperties properties;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    private IdentityServiceClient identityServiceClient;

    @Before
    public void setUp() {
        initMocks(this);
        identityServiceClient = new IdentityServiceClient(properties);
    }

    @Test
    public void shouldGetIdentityTokenUsingCredentials() throws Exception {
        String clientId = "12345";
        String authToken = "xyz";
        UUID token = UUID.randomUUID();
        String response = "{\"access_token\" : \"" + token.toString() + "\"}";

        when(properties.getIdpClientId()).thenReturn(clientId);
        when(properties.getIdpAuthToken()).thenReturn(authToken);
        when(properties.getIdentityServerLoginUrl()).thenReturn("http://localhost:9997/signin");

        givenThat(post(urlEqualTo("/signin"))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(response)));

        IdentityToken identityToken = identityServiceClient.signin(new UserCredentials("email@gmail.com", "password"));

        assertEquals(token.toString(), identityToken.toString());
    }

    @Test
    public void shouldGetUserDetailsUsingIdentityToken() throws Exception {
        String clientId = "12345";
        String authToken = "xyz";
        IdentityToken identityToken = new IdentityToken("d8ef70d0-0045-4323-82ef-db718f96007d");

        when(properties.getIdpClientId()).thenReturn(clientId);
        when(properties.getIdpAuthToken()).thenReturn(authToken);
        when(properties.getIdentityServerUserInfoUrl()).thenReturn("http://localhost:9997/token/");

        givenThat(get(urlEqualTo("/token/" + identityToken.toString()))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(asString("userInfo.json"))));

        UserInfo userInfo = identityServiceClient.getUserInfo(identityToken);
        assertNotNull(userInfo);
        assertEquals("email@gmail.com", userInfo.getEmail());
        assertEquals(identityToken.toString(), userInfo.getAccessToken());
        assertTrue(userInfo.isActivated());
    }

}