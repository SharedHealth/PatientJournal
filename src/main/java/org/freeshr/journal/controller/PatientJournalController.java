package org.freeshr.journal.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.*;
import org.freeshr.journal.service.EncounterService;
import org.freeshr.journal.service.FacilityService;
import org.freeshr.journal.service.IdentityService;
import org.freeshr.journal.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.freeshr.journal.utils.HttpUtil.createSecurityHeaders;

@Controller
public class PatientJournalController extends WebMvcConfigurerAdapter {

    public static final String SESSION_KEY = "userinfo";
    public static final String DETAILS_URI = "/details";
    public static final String LOGIN_URI = "/login";
    public static final String LOGOUT_URI = "/logout";

    private Logger logger = Logger.getLogger(PatientJournalController.class);

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private PatientService patientService;

    public PatientJournalController() {
    }

    public PatientJournalController(ApplicationProperties applicationProperties, EncounterService
            encounterService, FacilityService facilityService, IdentityService identityService, PatientService patientService) {
        this.applicationProperties = applicationProperties;
        this.encounterService = encounterService;
        this.facilityService = facilityService;
        this.identityService = identityService;
        this.patientService = patientService;
    }

    @RequestMapping(value = LOGIN_URI, method = RequestMethod.GET)
    public ModelAndView loginForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object userInfo = request.getSession().getAttribute(SESSION_KEY);
        if (null != userInfo) {
            response.sendRedirect(DETAILS_URI);
            return null;
        }
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

    @RequestMapping(value = LOGIN_URI, method = RequestMethod.POST)
    public ModelAndView login(@ModelAttribute UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (null == session) {
            logger.debug("Session Expired");
            response.sendRedirect(LOGIN_URI);
            return null;
        }
        try {
            UserInfo userInfo = identityService.verifyPatient(userCredentials);
            session = request.getSession(true);
            session.setAttribute(SESSION_KEY, userInfo);
            session.setMaxInactiveInterval(applicationProperties.getSessionTimeoutInSeconds());
            response.sendRedirect(DETAILS_URI);
            return null;
        } catch (Exception e) {
            logger.error(String.format("Unable to Login for user %s", userCredentials.getEmail()));
            ModelAndView loginForm = new ModelAndView("loginForm");
            loginForm.addObject("userCredentials", new UserCredentials());
            if (StringUtils.isBlank(e.getMessage()))
                loginForm.addObject("error", new Exception("Can not identify user"));
            else
                loginForm.addObject("error", e);
            return loginForm;
        }
    }

    @RequestMapping(value = DETAILS_URI, method = RequestMethod.GET)
    public ModelAndView showPatientEncounters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (null == session || null == session.getAttribute(SESSION_KEY)) {
            logger.debug("Session Expired");
            response.sendRedirect(LOGIN_URI);
            return null;
        }
        UserInfo userInfo = (UserInfo) session.getAttribute(SESSION_KEY);
        String healthId = userInfo.getPatientProfile().getId();
        String message = String.format("Show Encounter Request for patient[%s]", healthId);
        logger.info(String.format("ACCESS: USER=%s EMAIL=%s ACTION=%s", userInfo.getId(), userInfo.getEmail(), message));
        try {
            Patient patient = patientService.getPatient(userInfo);
            EncounterBundlesData encountersForPatient = encounterService.getEncountersForPatient(healthId, createSecurityHeaders(userInfo));
            List<EncounterBundleData> encounterBundles = encountersForPatient.getEncounterBundleDataList();
            ModelAndView indexView = new ModelAndView("index");
            indexView.addObject("encounterBundlesData", reverseEncounterBundles(encounterBundles));
            indexView.addObject("patient", patient);
            return indexView;

        } catch (Exception e) {
            logger.error(String.format("Unable to fetch details for patient [%s]", healthId), e);
            return new ModelAndView("error", "errorMessage", "No health record found.");
        }
    }

    @RequestMapping(value = LOGOUT_URI, method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (null == session || null == session.getAttribute(SESSION_KEY)) {
            logger.debug("Session Expired before logout");
            response.sendRedirect(LOGIN_URI);
            return null;
        }
        UserInfo userInfo = (UserInfo) session.getAttribute(SESSION_KEY);
        logger.debug(String.format("User %s Logged out", userInfo.getEmail()));
        session.invalidate();
        response.sendRedirect(LOGIN_URI);
        return null;
    }

    @RequestMapping(value = "/external")
    public ModelAndView fetchReference(@RequestParam("ref") String externalRefUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (null == session || null == session.getAttribute(SESSION_KEY)) {
            logger.debug("Session Expired");
            response.sendError(401, "Session Expired");
            return null;
        }
        try {
            UserInfo userInfo = (UserInfo) session.getAttribute(SESSION_KEY);
            ExternalRef externalContent = fetchExternalContent(UriUtils.decode(externalRefUrl, "UTF-8"), userInfo);
            return new ModelAndView(externalContent.getTemplateName(), externalContent.getModelName(), externalContent.getData());
        } catch (IOException e) {
            logger.error(String.format("Unable to fetch request [%s]", externalRefUrl), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ModelAndView("notFound");
    }

    @RequestMapping(value = "/*", method = RequestMethod.GET)
    public void handleDefaultRequests(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        logger.debug(String.format("Got request for unknown path %s redirecting to %s", requestURI, LOGIN_URI));
        response.sendRedirect(LOGIN_URI);
    }

    private ExternalRef fetchExternalContent(String decodedRef, UserInfo userInfo) {
        if (decodedRef.startsWith(applicationProperties.getFacilityRegistryUrl())) {
            String message = String.format("Fetch Facility request for url [%s]", decodedRef);
            logger.info(String.format("ACCESS: USER=%s EMAIL=%s ACTION=%s", userInfo.getId(), userInfo.getEmail(), message));
            return new ExternalRef("facility", "facility", facilityService.getFacility(decodedRef));
        }
        throw new RuntimeException(String.format("Can not handle external reference %s", decodedRef));
    }
}
