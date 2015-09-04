package org.freeshr.journal.proxy;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.syndication.io.FeedException;
import org.apache.http.HttpStatus;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundle;
import org.freeshr.journal.model.EncounterBundlesData;
import org.freeshr.journal.utils.FileUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertTrue;
import static org.freeshr.journal.utils.HttpUtil.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FreeSHRTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    @Mock
    WebClient webClient;

    @Mock
    ApplicationProperties applicationProperties;

    @Before
    public void setUp() throws Exception {
        givenThat(get(urlEqualTo("/patients/123123123123/encounters"))
                .withHeader("accept", equalTo("application/atom+xml"))
                .withHeader(AUTH_TOKEN_KEY, equalTo("xxx"))
                .withHeader(CLIENT_ID_KEY, equalTo("12345"))
                .withHeader(FROM_KEY, equalTo("email@gmail.com"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/atom+xml")
                        .withBody(FileUtil.asString("encounters/shrEncounterResponse.xml"))));
        initMocks(this);
    }

    @Test
    public void testGetEncountersForPatient() throws IOException, FeedException {
        when(applicationProperties.getSHRBaseUrl()).thenReturn("http://localhost:9997");
        FreeSHR freeSHR = new FreeSHR(new AtomFeed(), applicationProperties);
        EncounterBundlesData encounters = freeSHR.getEncountersForPatient("123123123123",
                getSecurityHeaders());
        assertEquals(3, encounters.getEncounterBundleDataList().size());
        EncounterBundle encounterBundle = encounters.getEncounterBundleDataList().get(0).getEncounterBundle();
        assertEquals(2, encounterBundle.getResources().size());
        assertTrue(isEncounterOrComposition(encounterBundle.getResources().get(0)));
        assertTrue(isEncounterOrComposition(encounterBundle.getResources().get(1)));
    }

    private boolean isEncounterOrComposition(IResource resource) {
        return resource instanceof Encounter || resource instanceof Composition;
    }

    private HashMap<String, String> getSecurityHeaders() {
        HashMap<String, String> map = new HashMap<>();
        map.put(AUTH_TOKEN_KEY, "xxx");
        map.put(CLIENT_ID_KEY, "12345");
        map.put(FROM_KEY, "email@gmail.com");
        return map;
    }
}