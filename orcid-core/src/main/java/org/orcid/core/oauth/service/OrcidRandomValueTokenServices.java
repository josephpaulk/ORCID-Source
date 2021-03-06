/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.oauth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.oauth.OrcidOauth2AuthInfo;
import org.orcid.jaxb.model.message.ScopePathType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 11/05/2012
 */
public class OrcidRandomValueTokenServices extends DefaultTokenServices {

    private final int writeValiditySeconds;
    private final int readValiditySeconds;

    @Resource(name = "orcidTokenStore")
    private TokenStore tokenStore;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidRandomValueTokenServices.class);

    public OrcidRandomValueTokenServices(ClientDetailsService clientDetailsService, int writeValiditySeconds, int readValiditySeconds) {
        this.writeValiditySeconds = writeValiditySeconds;
        this.readValiditySeconds = readValiditySeconds;
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OrcidOauth2AuthInfo authInfo = new OrcidOauth2AuthInfo(authentication);
        OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
        if (existingAccessToken != null) {
            if (existingAccessToken.isExpired()) {
                tokenStore.removeAccessToken(existingAccessToken);
                LOGGER.info("Existing but expired access token found: clientId={}, scopes={}, userOrcid={}", new Object[] { authInfo.getClientId(), authInfo.getScopes(),
                        authInfo.getUserOrcid() });
            } else {
                DefaultOAuth2AccessToken updatedAccessToken = new DefaultOAuth2AccessToken(existingAccessToken);
                int validitySeconds = getAccessTokenValiditySeconds(authentication.getAuthorizationRequest());
                if (validitySeconds > 0) {
                    updatedAccessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
                }
                tokenStore.storeAccessToken(existingAccessToken, authentication);
                LOGGER.info("Existing reusable access token found: clientId={}, scopes={}, userOrcid={}", new Object[] { authInfo.getClientId(), authInfo.getScopes(),
                        authInfo.getUserOrcid() });
                return existingAccessToken;
            }
        }

        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(super.createAccessToken(authentication));
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("orcid", authInfo.getUserOrcid());
        accessToken.setAdditionalInformation(additionalInfo);
        tokenStore.storeAccessToken(accessToken, authentication);
        LOGGER.info("Creating new access token: clientId={}, scopes={}, userOrcid={}",
                new Object[] { authInfo.getClientId(), authInfo.getScopes(), authInfo.getUserOrcid() });
        return accessToken;
    }

    /**
     * The access token validity period in seconds
     * 
     * @param authorizationRequest
     *            the current authorization request
     * @return the access token validity period in seconds
     */
    @Override
    protected int getAccessTokenValiditySeconds(AuthorizationRequest authorizationRequest) {
        Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromStrings(authorizationRequest.getScope());
        if (requestedScopes.size() == 1 && ScopePathType.ORCID_PROFILE_CREATE.equals(requestedScopes.iterator().next())) {
            return readValiditySeconds;
        }
        /* 
         * Tokens should last for the longest life span,
         * DefaultPermissionChecker will strip scopes that
         * are past their lifetimes
         */
        for (ScopePathType scope : requestedScopes) {
            if (!scope.isUserGrantWriteScope()) {
                return readValiditySeconds;
            }
        }
        return writeValiditySeconds;
    }

    public int getWriteValiditySeconds() {
        return writeValiditySeconds;
    }

    public int getReadValiditySeconds() {
        return readValiditySeconds;
    }
}
