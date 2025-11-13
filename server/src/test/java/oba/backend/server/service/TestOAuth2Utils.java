package oba.backend.server.service;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class TestOAuth2Utils {

    public static ClientRegistration createClientRegistration(String registrationId) {
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/login/oauth2/code/" + registrationId)
                .scope("email", "profile")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .build();
    }
}
