package org.freeshr.journal.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.freeshr.journal.launch.Application;
import org.freeshr.journal.utils.FileUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;
import static org.freeshr.journal.utils.HttpUtil.FROM_KEY;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(initializers = EnvironmentMock.class, classes = {Application.class})
public class PatientJournalControllerTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    private MockMvc mockMvc;

    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        givenThat(WireMock.get(urlEqualTo("/patients/123123123123/encounters"))
                .withHeader("accept", equalTo("application/atom+xml"))
                .withHeader("X-Auth-Token", matching("012345abcd6789"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/atom+xml")
                        .withBody(FileUtil.asString("encounters/shrEncounterResponse.xml"))));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        initMocks(this);
    }

    @Test
    public void shouldGetALoginForm() throws Exception {
        mockMvc.perform(get("/login")).andExpect(view().name("loginForm"));
    }

    @Test
    public void shouldGetPatientDetails() throws Exception {
        String clientId = "12345";
        String authToken = "012345abcd6789";
        String token = "00f452a2-2925-4e03-b772-971fd14982b2";
        String response = "{\"access_token\" : \"" + token + "\"}";

        givenThat(WireMock.get(urlEqualTo("/patients/123123123123/encounters"))
                .withHeader("accept", equalTo("application/atom+xml"))
                .withHeader(CLIENT_ID_KEY, matching(clientId))
                .withHeader(AUTH_TOKEN_KEY, matching(token))
                .withHeader(FROM_KEY, matching("utsab@gmail.com"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/atom+xml")
                        .withBody(FileUtil.asString("encounters/shrEncounterResponse.xml"))));


        givenThat(WireMock.post(urlEqualTo("/signin"))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse().withStatus(HttpStatus.SC_OK)
                        .withBody(response)));

        givenThat(WireMock.get(urlMatching("/token/" + token))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(asString("patientUserInfo.json"))));

        mockMvc.perform(post("/details")).andExpect(view().name("index"));
    }

    @Test
    public void shouldGetAnErrorViewCanNotVerifyUser() throws Exception {
        String clientId = "12345";
        String authToken = "012345abcd6789";
        String token = "d8ef70d0-0045-4323-82ef-db718f96007d";
        String response = "{\"access_token\" : \"" + token + "\"}";

        givenThat(WireMock.post(urlEqualTo("/signin"))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse().withStatus(HttpStatus.SC_OK)
                        .withBody(response)));

        givenThat(WireMock.get(urlMatching("/token/" + token))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_UNAUTHORIZED)));

        mockMvc.perform(post("/details")).andExpect(view().name("error"));
    }

    @Test
    public void shouldGetAnErrorViewWhenUserIsNotPatient() throws Exception {
        String clientId = "12345";
        String authToken = "012345abcd6789";
        String token = "d8ef70d0-0045-4323-82ef-db718f96007d";
        String response = "{\"access_token\" : \"" + token + "\"}";

        givenThat(WireMock.post(urlEqualTo("/signin"))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse().withStatus(HttpStatus.SC_OK)
                        .withBody(response)));

        givenThat(WireMock.get(urlMatching("/token/" + token))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(authToken))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(asString("userInfo.json"))));

        mockMvc.perform(post("/details")).andExpect(view().name("error"));
    }
}