package org.freeshr.journal.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.freeshr.journal.launch.Application;
import org.freeshr.journal.utils.FileUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
                .withHeader("X-Auth-Token", matching("foo"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/atom+xml")
                        .withBody(FileUtil.asString("encounters/shrEncounterResponse.xml"))));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        initMocks(this);
    }

    @Test
    public void shouldRedirectUnidentifiableRequests() throws Exception {
        mockMvc.perform(get("/journal/1234")).andExpect(status().is(HttpStatus.SC_MOVED_TEMPORARILY));
    }

    @Test
    public void shouldGetEncountersForPatient() throws Exception {
        MvcResult result = mockMvc.perform(get("/journal/123123123123").cookie(new Cookie(PatientJournalController
                .IDENTITY_TOKEN_NAME,
                "foo"))).
                andExpect(status().is(HttpStatus.SC_OK)).andReturn();
        String responseAsString = result.getResponse().getContentAsString();

    }
}