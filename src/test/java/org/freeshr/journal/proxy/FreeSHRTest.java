package org.freeshr.journal.proxy;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.syndication.io.FeedException;
import org.apache.http.HttpStatus;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundle;
import org.freeshr.journal.model.EncounterBundlesData;
import org.freeshr.journal.utils.FileUtil;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertTrue;
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
                .withHeader("X-Auth-Token", matching("xxx"))
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
        assertEquals(1, encounters.getEncounterBundleDataList().size());
        EncounterBundle encounterBundle = encounters.getEncounterBundleDataList().get(0).getEncounterBundle();
        assertEquals(2, encounterBundle.getResources().size());
        assertTrue(isEncounterOrComposition(encounterBundle.getResources().get(0)));
        assertTrue(isEncounterOrComposition(encounterBundle.getResources().get(1)));
    }

    private boolean isEncounterOrComposition(Resource resource) {
        return resource.getResourceType() == ResourceType.Encounter
                ||
                resource.getResourceType() == ResourceType.Composition;
    }

    private HashMap<String, String> getSecurityHeaders() {
        HashMap<String, String> map = new HashMap<>();
        map.put("X-Auth-Token", "xxx");
        return map;
    }


}