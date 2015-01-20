package org.freeshr.journal.controller;

import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundle;
import org.freeshr.journal.model.EncounterBundles;
import org.freeshr.journal.service.PatientEncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public PatientJournalController() {
    }

    public PatientJournalController(ApplicationProperties applicationProperties, PatientEncounterService
            patientEncounterService) {
        this.applicationProperties = applicationProperties;
        this.patientEncounterService = patientEncounterService;
    }


    private Map<String, String> createSecurityHeaders(HttpServletRequest request) {
        HashMap<String, String> headers = new HashMap<>();
//        headers.put(X_AUTH_TOKEN, "c7159526-ac9d-42ba-b950-8a8e91561ab8");
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

//    private Boolean isIdentifiable(HttpServletRequest request) {
//        return true;
//    }
    private Boolean isIdentifiable(HttpServletRequest request) {
        return findIdentityToken(request) != null;
    }

    @RequestMapping(value = "/journal/{healthId}", method = RequestMethod.GET)
    public ModelAndView loginForm(@PathVariable("healthId") String healthId,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isIdentifiable(request)) {
            String identityServerUrl = applicationProperties.getIdentityServerUrl(request.getRequestURL());
            response.sendRedirect(identityServerUrl);
            return null;
        }
        EncounterBundles encountersForPatient = patientEncounterService.getEncountersForPatient(healthId,
                createSecurityHeaders(request));
        List<EncounterBundle> encounterBundles = encountersForPatient.getEncounterBundles();
        return new ModelAndView("index", "encounterBundles", reverseEncounterBundles(encounterBundles));
    }

    private List<EncounterBundle> reverseEncounterBundles(List<EncounterBundle> list) {
        List<EncounterBundle> revereList = new ArrayList<>();
        for (int index = list.size() - 1; index >= 0; index--) {
            revereList.add(list.get(index));
        }
        return revereList;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/journal/{healthId}").setViewName("index");
    }
}
