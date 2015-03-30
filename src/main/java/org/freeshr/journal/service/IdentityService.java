package org.freeshr.journal.service;


import org.freeshr.journal.infrastructure.IdentityServiceClient;
import org.freeshr.journal.model.IdentityToken;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IdentityService {

    private IdentityServiceClient identityServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(IdentityService.class);

    @Autowired
    public IdentityService(IdentityServiceClient identityServiceClient) {
        this.identityServiceClient = identityServiceClient;
    }

    public UserInfo verifyPatient(UserCredentials userCredentials) throws RuntimeException{
        try {
            logger.debug(String.format("Signing In with credentials of user %s", userCredentials.getEmail()));
            IdentityToken identityToken = identityServiceClient.signin(userCredentials);
            logger.debug(String.format("Got access token for user %s", userCredentials.getEmail()));
            UserInfo userInfo = identityServiceClient.getUserInfo(identityToken);
            if (userInfo.getPatientProfile() == null) {
                throw new RuntimeException("Not Registered as Patient");
            }
            return userInfo;

        } catch (IOException e) {
            String message = String.format("Not able to login for user %s", userCredentials.getEmail());
            logger.error(message);
            throw new RuntimeException("Not Authorized");
        }
    }
}
