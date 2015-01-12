package org.freeshr.journal.controller;

import org.freeshr.journal.launch.ApplicationProperties;
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

@Controller
public class PatientJournalController {
    public static final String IDENTITY_TOKEN_NAME = "SHR_IDENTITY_TOKEN";
    @Autowired
    ApplicationProperties applicationProperties;

    public PatientJournalController() {
    }

    public PatientJournalController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @RequestMapping(value = "/journal/{healthId}", method = RequestMethod.GET)
    public
    @ResponseBody
    String journal(@PathVariable("healthId")
                   String healthId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isIdentifiable(request)) {
            String identityServerUrl = applicationProperties.getIdentityServerUrl(request.getRequestURL());
            response.sendRedirect(identityServerUrl);
        }
        return "hello world " + healthId;
    }

    private Boolean isIdentifiable(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return false;
        for (Cookie cookie : cookies) {
            if (cookie!= null && IDENTITY_TOKEN_NAME.equals(cookie.getName()))
                return true;
        }
        return false;
    }
}
