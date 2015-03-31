package org.freeshr.journal.service;

import org.freeshr.journal.infrastructure.IdentityServiceClient;
import org.freeshr.journal.model.IdentityToken;
import org.freeshr.journal.model.UserCredentials;
import org.freeshr.journal.model.UserInfo;
import org.freeshr.journal.model.UserProfile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.io.IOException;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.freeshr.journal.service.IdentityService.INVALID_CREDENTIALS_MESSAGE;
import static org.freeshr.journal.service.IdentityService.NOT_A_PATIENT_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdentityServiceTest {
    @Mock
    private IdentityServiceClient identityServiceClient;

    private IdentityService identityService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        identityService = new IdentityService(identityServiceClient);
    }

    @Test
    public void shouldGetUserInfoForGivenCredentials() throws Exception {
        UserCredentials credentials = new UserCredentials("email@gmail.com", "password");

        IdentityToken identityToken = new IdentityToken(UUID.randomUUID().toString());

        when(identityServiceClient.signin(credentials)).thenReturn(identityToken);
        when(identityServiceClient.getUserInfo(identityToken)).thenReturn(getPatientUserInfo(identityToken.toString()));

        UserInfo userInfo = identityService.verifyPatient(credentials);

        verify(identityServiceClient, times(1)).signin(credentials);
        verify(identityServiceClient, times(1)).getUserInfo(identityToken);

        assertEquals("some_user", userInfo.getName());
        assertEquals("email@gmail.com", userInfo.getEmail());
        assertEquals(identityToken.toString(), userInfo.getAccessToken());
    }

    @Test
    public void shouldValidateGivenCredentialsForPatient() throws Exception {
        UserCredentials credentials = new UserCredentials("email@gmail.com", "password");
        IdentityToken identityToken = new IdentityToken(UUID.randomUUID().toString());

        when(identityServiceClient.signin(credentials)).thenReturn(identityToken);
        when(identityServiceClient.getUserInfo(identityToken)).thenReturn(getUserInfo(identityToken.toString()));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(NOT_A_PATIENT_MESSAGE);

        identityService.verifyPatient(credentials);
    }

    @Test
    public void shouldThrowErrorWhenCanNotSignin() throws Exception {
        UserCredentials credentials = new UserCredentials("email@gmail.com", "password");
        when(identityServiceClient.signin(credentials)).thenThrow(new IOException());

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(INVALID_CREDENTIALS_MESSAGE);

        identityService.verifyPatient(credentials);
    }

    @Test
    public void shouldThrowErrorWhenCanNotGetUserDetails() throws Exception {
        UserCredentials credentials = new UserCredentials("email@gmail.com", "password");
        IdentityToken identityToken = new IdentityToken(UUID.randomUUID().toString());

        when(identityServiceClient.signin(credentials)).thenReturn(identityToken);
        when(identityServiceClient.getUserInfo(identityToken)).thenThrow(new IOException());

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(INVALID_CREDENTIALS_MESSAGE);

        identityService.verifyPatient(credentials);
    }

    private UserInfo getUserInfo(String token) {
        return new UserInfo("123", "some_user", "email@gmail.com", 1, true,
                token, asList("MCI_ADMIN", "SHR_USER"), asList(new UserProfile("facility", "10000069", asList("3026"))));
    }

    private UserInfo getPatientUserInfo(String token) {
        return new UserInfo("123", "some_user", "email@gmail.com", 1, true,
                token, asList("MCI_ADMIN", "SHR_USER"), asList(new UserProfile("patient", "11198745245", null)));
    }

}