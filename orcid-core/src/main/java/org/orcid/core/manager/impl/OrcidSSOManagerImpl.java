package org.orcid.core.manager.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public class OrcidSSOManagerImpl implements OrcidSSOManager {

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    private final static String SSO_SCOPE = ScopePathType.AUTHENTICATE.value();

    @Override
    public ClientDetailsEntity generateUserCredentials(String orcid, Set<String> redirectUris) {
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        } else {
            // If it already have SSO client credentials, just return them
            if (profileEntity.getClientDetails() != null) {
                ClientDetailsEntity existingClientDetails = profileEntity.getClientDetails();
                Set<ClientScopeEntity> existingScopes = existingClientDetails.getClientScopes();
                boolean alreadyHaveAuthScope = false;
                // Check if it already have the SSO scope
                for (ClientScopeEntity clientScope : existingScopes) {
                    if (SSO_SCOPE.equals(clientScope.getScopeType())) {
                        alreadyHaveAuthScope = true;
                        break;
                    }
                }

                // If it doesn't add it and persist it
                if (!alreadyHaveAuthScope) {
                    ClientScopeEntity ssoScope = getClientScopeEntity(SSO_SCOPE, existingClientDetails);
                    existingScopes.add(ssoScope);
                    existingClientDetails.setClientScopes(existingScopes);
                    clientDetailsDao.merge(existingClientDetails);
                }
                return clientDetailsDao.findByClientId(existingClientDetails.getId());
            } else {
                String clientSecret = encryptionManager.encryptForInternalUse(UUID.randomUUID().toString());
                String clientId = profileEntity.getId();
                Set<String> redirectUrisSet = new HashSet<String>();
                for (String uri : redirectUris) {
                    redirectUrisSet.add(uri);
                }
                ClientDetailsEntity clientDetailsEntity = populateClientDetailsEntity(clientId, clientSecret, redirectUrisSet);
                clientDetailsDao.persist(clientDetailsEntity);
                return clientDetailsDao.findByClientId(clientDetailsEntity.getId());
            }
        }
    }

    @Override
    public ClientDetailsEntity getUserCredentials(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    private ClientDetailsEntity populateClientDetailsEntity(String clientId, String clientSecret, Set<String> clientRegisteredRedirectUris) {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId(clientId);
        clientDetailsEntity.setClientSecretForJpa(clientSecret);
        clientDetailsEntity.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(clientSecret));
        Set<ClientScopeEntity> clientScopes = new HashSet<ClientScopeEntity>();
        clientScopes.add(getClientScopeEntity(SSO_SCOPE, clientDetailsEntity));
        clientDetailsEntity.setClientScopes(clientScopes);
        clientDetailsEntity.setClientRegisteredRedirectUris(getClientRegisteredRedirectUris(clientRegisteredRedirectUris, clientDetailsEntity));
        return clientDetailsEntity;
    }

    private ClientScopeEntity getClientScopeEntity(String clientScope, ClientDetailsEntity clientDetailsEntity) {
        ClientScopeEntity clientScopeEntity = new ClientScopeEntity();
        clientScopeEntity.setClientDetailsEntity(clientDetailsEntity);
        clientScopeEntity.setScopeType(clientScope);
        return clientScopeEntity;
    }

    private SortedSet<ClientRedirectUriEntity> getClientRegisteredRedirectUris(Set<String> redirectUris, ClientDetailsEntity clientDetailsEntity) {
        SortedSet<ClientRedirectUriEntity> clientRedirectUriEntities = new TreeSet<ClientRedirectUriEntity>();
        for (String redirectUri : redirectUris) {
            ClientRedirectUriEntity clientRedirectUriEntity = new ClientRedirectUriEntity();
            clientRedirectUriEntity.setClientDetailsEntity(clientDetailsEntity);
            clientRedirectUriEntity.setRedirectUri(redirectUri);
            clientRedirectUriEntity.setRedirectUriType(RedirectUriType.SSO_AUTHENTICATION.value());
            clientRedirectUriEntities.add(clientRedirectUriEntity);
        }
        return clientRedirectUriEntities;
    }
}