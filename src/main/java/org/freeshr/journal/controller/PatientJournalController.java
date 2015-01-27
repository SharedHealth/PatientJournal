package org.freeshr.journal.controller;

import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundleData;
import org.freeshr.journal.model.EncounterBundlesData;
import org.freeshr.journal.service.FacilityService;
import org.freeshr.journal.service.PatientEncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PatientJournalController extends WebMvcConfigurerAdapter {
    public static final String IDENTITY_TOKEN_NAME = "SHR_IDENTITY_TOKEN";
    public static final String X_AUTH_TOKEN = "X-Auth-Token";

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    PatientEncounterService patientEncounterService;

    @Autowired
    FacilityService facilityService;

    public PatientJournalController() {
    }

    public PatientJournalController(ApplicationProperties applicationProperties, PatientEncounterService
            patientEncounterService) {
        this.applicationProperties = applicationProperties;
        this.patientEncounterService = patientEncounterService;
    }


    private Map<String, String> createSecurityHeaders(HttpServletRequest request) {
        HashMap<String, String> headers = new HashMap<>();
//        headers.put(X_AUTH_TOKEN, "8dad0c07-caf8-48a9-ac2a-1815a9aa11a1");
        headers.put(X_AUTH_TOKEN, findIdentityToken(request));
        return headers;
    }

    private String findIdentityToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        cookies = cookies != null ? cookies : new Cookie[0];
        if (cookies.length < 1) return null;
        for (Cookie cookie : cookies) {
            if (cookie != null && IDENTITY_TOKEN_NAME.equals(cookie.getName()))
                return cookie.getValue();
        }
        return null;
    }

    private Boolean isIdentifiable(HttpServletRequest request) {
        return findIdentityToken(request) != null;
//        return true;
    }
    
    @RequestMapping(value = "/journal/{healthId}", method = RequestMethod.GET)
    public ModelAndView loginForm(@PathVariable("healthId") String healthId,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isIdentifiable(request)) {
            String identityServerUrl = applicationProperties.getIdentityServerUrl(request.getRequestURL());
            response.sendRedirect(identityServerUrl);
            return null;
        }
        EncounterBundlesData encountersForPatient = patientEncounterService.getEncountersForPatient(healthId,
                createSecurityHeaders(request));
        List<EncounterBundleData> encounterBundles = encountersForPatient.getEncounterBundleDataList();
        return new ModelAndView("index", "encounterBundlesData", reverseEncounterBundles(encounterBundles));
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
