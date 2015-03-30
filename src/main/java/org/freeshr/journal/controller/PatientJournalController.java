package org.freeshr.journal.controller;

import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundleData;
import org.freeshr.journal.model.EncounterBundlesData;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;
import org.freeshr.journal.service.FacilityService;
import org.freeshr.journal.service.EncounterService;
import org.freeshr.journal.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.*;

@Controller
public class PatientJournalController extends WebMvcConfigurerAdapter {

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private PatientService patientService;

    public PatientJournalController() {
    }

    public PatientJournalController(ApplicationProperties applicationProperties, EncounterService
            encounterService) {
        this.applicationProperties = applicationProperties;
        this.encounterService = encounterService;
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginForm() {
        ModelAndView loginForm = new ModelAndView("loginForm");
        loginForm.addObject("userCredentials", new UserCredentials());
        return loginForm;
    }

    private List<EncounterBundleData> reverseEncounterBundles(List<EncounterBundleData> list) {
        List<EncounterBundleData> revereList = new ArrayList<>();
        for (int index = list.size() - 1; index >= 0; index--) {
            revereList.add(list.get(index));
        }
        return revereList;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/journal/{healthId}").setViewName("index");
    }

    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ModelAndView signin(@ModelAttribute UserCredentials userCredentials) {
        try {
            UserInfo userInfo = patientService.verifyPatient(userCredentials);
            String healthId = userInfo.getPatientProfile().getId();

            EncounterBundlesData encountersForPatient = encounterService.getEncountersForPatient(healthId,
                    createSecurityHeaders(userInfo));
            List<EncounterBundleData> encounterBundles = encountersForPatient.getEncounterBundleDataList();
            return new ModelAndView("index", "encounterBundlesData", reverseEncounterBundles(encounterBundles));
        } catch (Exception e) {
            return new ModelAndView("error", "errorMessage", e.getMessage());
        }
    }


    private Map<String, String> createSecurityHeaders(UserInfo userInfo) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CLIENT_ID_KEY, userInfo.getId());
        headers.put(AUTH_TOKEN_KEY, userInfo.getAccessToken());
        headers.put(FROM_KEY, userInfo.getEmail());
        return headers;
    }

    @RequestMapping(value = "/external")
    public ModelAndView fetchReference(@RequestParam("ref") String externalRefUrl) {
        try {
            ExternalRef externalContent = fetchExternalContent(UriUtils.decode(externalRefUrl, "UTF-8"));
            return new ModelAndView(externalContent.getTemplateName(), externalContent.getModelName(), externalContent.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ExternalRef fetchExternalContent(String decodedRef) throws IOException {
        if (decodedRef.startsWith(applicationProperties.getFacilityServerUrlPrefix())) {
            return new ExternalRef("facility", "facility", facilityService.getFacility(decodedRef));
        }
        return null;
    }
}
