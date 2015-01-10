package org.freeshr.journal.controller;

import org.apache.commons.lang3.StringUtils;
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

@Controller
public class PatientJournalController {
    @Autowired
    ApplicationProperties applicationProperties;

    public PatientJournalController() {
    }

    public PatientJournalController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @RequestMapping(value="/journal/{healthId}", method = RequestMethod.GET)
    public @ResponseBody
    String journal(@PathVariable("healthId")
                   String healthId, HttpServletRequest request, HttpServletResponse response) {
        String identityToken = getIdentityToken(request);
        if(StringUtils.isEmpty(identityToken))
            applicationProperties.getIdentityServerUrl(request.getRequestURL());
        return "hello world " + healthId;
    }

    private String getIdentityToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName()))
                return cookie.getValue();
        }
        return null;
    }
}
