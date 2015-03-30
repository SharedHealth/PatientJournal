package org.freeshr.journal.controller;

import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundleData;
import org.freeshr.journal.model.EncounterBundlesData;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;
import org.freeshr.journal.service.EncounterService;
import org.freeshr.journal.service.FacilityService;
import org.freeshr.journal.service.IdentityService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;
import static org.freeshr.journal.utils.HttpUtil.FROM_KEY;

@Controller
public class PatientJournalController extends WebMvcConfigurerAdapter {

    public static final String SESSION_KEY = "userinfo";
    public static final String DETAILS_URI = "/details";
    public static final String LOGIN_URI = "/login";
    public static final String SIGNIN_URI = "/signin";

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private IdentityService identityService;

    public PatientJournalController() {
    }

    public PatientJournalController(ApplicationProperties applicationProperties, EncounterService
            encounterService, FacilityService facilityService, IdentityService identityService) {
        this.applicationProperties = applicationProperties;
        this.encounterService = encounterService;
        this.facilityService = facilityService;
        this.identityService = identityService;
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

    @RequestMapping(value = SIGNIN_URI, method = RequestMethod.POST)
    public ModelAndView signin(@ModelAttribute UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (null == session) {
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
            return new ModelAndView("error", "errorMessage", e.getMessage());
        }
    }

    @RequestMapping(value = DETAILS_URI, method = RequestMethod.GET)
    public ModelAndView showPatientEncounters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (null == session || null == session.getAttribute(SESSION_KEY)) {
            response.sendRedirect(LOGIN_URI);
            return null;
        }
        UserInfo userInfo = (UserInfo) session.getAttribute(SESSION_KEY);
        String healthId = userInfo.getPatientProfile().getId();
        EncounterBundlesData encountersForPatient = null;
        try {
            encountersForPatient = encounterService.getEncountersForPatient(healthId,
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
