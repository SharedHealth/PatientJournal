package org.freeshr.journal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.freeshr.journal.launch.Application;
import org.freeshr.journal.model.UserInfo;
import org.freeshr.journal.utils.FileUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.Assert.assertTrue;
import static org.freeshr.journal.controller.PatientJournalController.DETAILS_URI;
import static org.freeshr.journal.controller.PatientJournalController.LOGIN_URI;
import static org.freeshr.journal.controller.PatientJournalController.LOGOUT_URI;
import static org.freeshr.journal.controller.PatientJournalController.SESSION_KEY;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;
import static org.freeshr.journal.utils.HttpUtil.FROM_KEY;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
                        .withBody(FileUtil.asString("encounters/stu3/shrEncounterResponse.xml"))));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        initMocks(this);
    }

    @Test
    public void shouldGetALoginForm() throws Exception {
        mockMvc.perform(get(LOGIN_URI)).andExpect(view().name("loginForm"));
    }

    @Test
    public void shouldRedirectToDetailsWhenSessionIsValid() throws Exception {
        MockHttpSession validSession = new MockHttpSession();
        validSession.setAttribute(SESSION_KEY, getUserInfo("patientUserInfo.json"));

        mockMvc.perform(get(LOGIN_URI).session(validSession)).andExpect(redirectedUrl(DETAILS_URI));
    }

    @Test
    public void shouldRedirectToLogInPageWhenSessionIsExpired() throws Exception {
        mockMvc.perform(post(LOGIN_URI)).andExpect(redirectedUrl(LOGIN_URI));
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
                        .withBody(FileUtil.asString("encounters/stu3/shrEncounterResponse.xml"))));


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

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_KEY, getUserInfo("patientUserInfo.json"));

        mockMvc.perform(post(LOGIN_URI).session(session)).andExpect(redirectedUrl(DETAILS_URI));
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

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_KEY, getUserInfo("userInfo.json"));

        mockMvc.perform(post(LOGIN_URI).session(session)).andExpect(view().name("loginForm"))
                .andExpect(model().attributeExists("error"));
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

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_KEY, getUserInfo("userInfo.json"));
        mockMvc.perform(post(LOGIN_URI).session(session)).andExpect(view().name("loginForm"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void shouldGetPatientEncounters() throws Exception {
        String clientId = "12345";
        String email = "utsab@gmail.com";
        String authToken = "012345abcd6789";
        String token = "00f452a2-2925-4e03-b772-971fd14982b2";
        String response = "{\"access_token\" : \"" + token + "\"}";

        givenThat(WireMock.get(urlEqualTo("/api/default/patients/123123123123"))
                .withHeader("accept", equalTo("application/json"))
                .withHeader(FROM_KEY, equalTo(email))
                .withHeader(CLIENT_ID_KEY, equalTo(clientId))
                .withHeader(AUTH_TOKEN_KEY, equalTo(token))
                .willReturn(aResponse().withStatus(HttpStatus.SC_OK)
                        .withBody(asString("patient.json"))));


        givenThat(WireMock.get(urlEqualTo("/patients/123123123123/encounters"))
                .withHeader("accept", equalTo("application/atom+xml"))
                .withHeader(CLIENT_ID_KEY, matching(clientId))
                .withHeader(AUTH_TOKEN_KEY, matching(token))
                .withHeader(FROM_KEY, matching(email))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/atom+xml")
                        .withBody(FileUtil.asString("encounters/stu3/shrEncounterResponse.xml"))));


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

        MockHttpSession validSession = new MockHttpSession();
        validSession.setAttribute(SESSION_KEY, getUserInfo("patientUserInfo.json"));

        mockMvc.perform(get(DETAILS_URI).session(validSession)).andExpect(view().name("index"));
    }

    @Test
    public void shouldInvalidateTheSession() throws Exception {
        MockHttpSession validSession = new MockHttpSession();
        validSession.setAttribute(SESSION_KEY, getUserInfo("patientUserInfo.json"));
        mockMvc.perform(get(LOGOUT_URI).session(validSession)).andExpect(redirectedUrl(LOGIN_URI));
        assertTrue(validSession.isInvalid());
    }

    @Test
    public void shouldRedirectToLoginWhenSessionIsExpired() throws Exception {
        mockMvc.perform(get(DETAILS_URI)).andExpect(redirectedUrl(LOGIN_URI));
    }

    @Test
    public void shouldRedirectAnyRandomRequestToLogin() throws Exception {
        mockMvc.perform(get("/randomRequest")).andExpect(redirectedUrl(LOGIN_URI));
    }

    private UserInfo getUserInfo(String path) throws IOException {
        String response = asString(path);
        return new ObjectMapper().readValue(response, UserInfo.class);
    }
}
