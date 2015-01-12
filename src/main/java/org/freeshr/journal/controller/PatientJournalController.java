package org.freeshr.journal.controller;

import com.sun.syndication.io.FeedException;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundle;
import org.freeshr.journal.model.EncounterBundles;
import org.freeshr.journal.service.PatientEncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PatientJournalController {
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

    @RequestMapping(value = "/journal/{healthId}", method = RequestMethod.GET)
    public
    @ResponseBody
    String journal(@PathVariable("healthId")
                   String healthId, HttpServletRequest request, HttpServletResponse response) throws IOException, FeedException {
        if (!isIdentifiable(request)) {
            String identityServerUrl = applicationProperties.getIdentityServerUrl(request.getRequestURL());
            response.sendRedirect(identityServerUrl);
            return "";
        }
        EncounterBundles encounters = patientEncounterService.getEncountersForPatient(healthId,
                createSecurityHeaders(request));

        return encounters.toString();
    }

    private Map<String, String> createSecurityHeaders(HttpServletRequest request) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(X_AUTH_TOKEN, findIdentityToken(request));
        return headers;
    }

    private String findIdentityToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        cookies = cookies != null ? cookies : new Cookie[0];
        if(cookies.length < 1) return null;
        for (Cookie cookie : cookies) {
            if (cookie != null && IDENTITY_TOKEN_NAME.equals(cookie.getName()))
                return cookie.getValue();
        }
        return null;
    }

    private Boolean isIdentifiable(HttpServletRequest request) {
        return  findIdentityToken(request) != null;
    }
}
