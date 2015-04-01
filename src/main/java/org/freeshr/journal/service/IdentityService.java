package org.freeshr.journal.service;


import org.apache.log4j.Logger;
import org.freeshr.journal.infrastructure.IdentityServiceClient;
import org.freeshr.journal.model.IdentityToken;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IdentityService {

    public static final String NOT_A_PATIENT_MESSAGE = "Not Registered as Patient";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid Email or Password";
    private IdentityServiceClient identityServiceClient;

    private Logger logger = Logger.getLogger(IdentityService.class);

    @Autowired
    public IdentityService(IdentityServiceClient identityServiceClient) {
        this.identityServiceClient = identityServiceClient;
    }

    public UserInfo verifyPatient(UserCredentials userCredentials) throws RuntimeException {
        try {
            logger.debug(String.format("Signing In with credentials of user %s", userCredentials.getEmail()));
            IdentityToken identityToken = identityServiceClient.signin(userCredentials);
            logger.debug(String.format("Got access token for user %s", userCredentials.getEmail()));
            UserInfo userInfo = identityServiceClient.getUserInfo(identityToken);
            if (userInfo.getPatientProfile() == null) {
                throw new RuntimeException(NOT_A_PATIENT_MESSAGE);
            }
            return userInfo;

        } catch (IOException e) {
            String message = String.format("Not able to login for user %s", userCredentials.getEmail());
            logger.error(message);
            throw new RuntimeException(INVALID_CREDENTIALS_MESSAGE);
        }
    }
}
