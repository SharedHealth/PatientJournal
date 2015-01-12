package org.freeshr.journal.infrastructure;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

public class WebClientTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    @Before
    public void setUp() throws Exception {
        givenThat(get(urlEqualTo("/patients/1/encounters"))
                .withHeader("accept", equalTo("application/atom+xml"))
                .withHeader("X-Auth-Token", equalTo("xxx"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/atom+xml")
                        .withBody("foo")));

        givenThat(get(urlEqualTo("/patients/NOT_FOUND/encounters"))
                .withHeader("accept", equalTo("application/atom+xml"))
                .withHeader("X-Auth-Token", equalTo("xxx"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_NOT_FOUND)));

        givenThat(get(urlEqualTo("/patients/UNAUTHORIZED/encounters"))
                .withHeader("accept", equalTo("application/atom+xml"))
                .withHeader("X-Auth-Token", equalTo("xxx"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_UNAUTHORIZED)));

        initMocks(this);
    }

    @Test
    public void shouldMakeGETWebRequest() throws Exception {
        HashMap<String, String> headers = new HashMap<String, String>() {{
            put("X-Auth-Token", "xxx");
        }};
        String response = new WebClient("http://localhost:9997", headers).get("/patients/1/encounters");
        assertEquals("foo", response);
    }

    @Test
    public void shouldHandleNOT_FOUND() throws Exception {
        HashMap<String, String> headers = new HashMap<String, String>() {{
            put("X-Auth-Token", "xxx");
        }};
        String response = new WebClient("http://localhost:9997", headers).get("/patients/NOT_FOUND/encounters");
        assertNull(response);
    }

    @Test(expected = IdentityUnauthorizedException.class)
    public void shouldHandleUNAUTHORIZED() throws Exception {
        HashMap<String, String> headers = new HashMap<String, String>() {{
            put("X-Auth-Token", "xxx");
        }};
        new WebClient("http://localhost:9997", headers).get("/patients/UNAUTHORIZED/encounters");
    }
}