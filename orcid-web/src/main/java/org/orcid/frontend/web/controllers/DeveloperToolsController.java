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
package org.orcid.frontend.web.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.SSOCredentials;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("developerToolsController")
@RequestMapping(value = { "/developer-tools" })
public class DeveloperToolsController extends BaseWorkspaceController {

    @Resource
    private OrcidSSOManager orcidSSOManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @RequestMapping
    public ModelAndView manageDeveloperTools() {
        ModelAndView mav = new ModelAndView("developer_tools");
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_AND_INTERNAL_ONLY);
        mav.addObject("profile", profile);
        try {
            if (!profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled().isValue()) {
                mav = new ModelAndView("manage");
                if (OrcidType.USER.equals(profile.getType())) {
                    mav.addObject("error", getMessage("manage.developer_tools.user.error.enable_developer_tools"));
                } else {
                    mav.addObject("error", getMessage("manage.developer_tools.user.error.invalid_user_type"));
                }
            }
        } catch (NullPointerException npe) {

        }
        return mav;
    }

    @RequestMapping(value = "/get-empty-sso-credential.json", method = RequestMethod.GET)
    public @ResponseBody
    SSOCredentials getEmptySSOCredentials(HttpServletRequest request) {
        SSOCredentials emptyObject = new SSOCredentials();
        emptyObject.setClientSecret(new Text());

        RedirectUri redirectUri = new RedirectUri();
        redirectUri.setValue(new Text());
        redirectUri.setType(Text.valueOf("default"));

        Set<RedirectUri> set = new HashSet<RedirectUri>();
        set.add(redirectUri);
        emptyObject.setRedirectUris(set);
        return emptyObject;
    }

    @RequestMapping(value = "/generate-sso-credentials.json", method = RequestMethod.POST)
    public @ResponseBody
    SSOCredentials generateSSOCredentialsJson(HttpServletRequest request, @RequestBody SSOCredentials ssoCredentials) {
        boolean hasErrors = validateSSoCredentials(ssoCredentials);

        if (!hasErrors) {
            OrcidProfile profile = getEffectiveProfile();
            String orcid = profile.getOrcidIdentifier().getPath();
            Set<String> redirectUriStrings = new HashSet<String>();
            for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                redirectUriStrings.add(redirectUri.getValue().getValue());
            }
            ClientDetailsEntity clientDetails = orcidSSOManager.grantSSOAccess(orcid, redirectUriStrings);
            ssoCredentials = SSOCredentials.toSSOCredentials(clientDetails);
        } else {
            List<String> errors = ssoCredentials.getErrors();
            if (errors == null)
                errors = new ArrayList<String>();
            for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                if (redirectUri.getErrors() != null && !redirectUri.getErrors().isEmpty())
                    errors.addAll(redirectUri.getErrors());
            }
            ssoCredentials.setErrors(errors);
        }

        return ssoCredentials;
    }

    @RequestMapping(value = "/update-redirect-uris.json", method = RequestMethod.POST)
    public @ResponseBody
    SSOCredentials updateRedirectUris(HttpServletRequest request, @RequestBody SSOCredentials ssoCredentials) {
        boolean hasErrors = validateSSoCredentials(ssoCredentials);

        if (!hasErrors) {
            OrcidProfile profile = getEffectiveProfile();
            String orcid = profile.getOrcidIdentifier().getPath();
            Set<String> redirectUriStrings = new HashSet<String>();
            for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                redirectUriStrings.add(redirectUri.getValue().getValue());
            }
            ClientDetailsEntity clientDetails = orcidSSOManager.updateRedirectUris(orcid, redirectUriStrings);
            ssoCredentials = SSOCredentials.toSSOCredentials(clientDetails);
        } else {
            List<String> errors = ssoCredentials.getErrors();
            if (errors == null)
                errors = new ArrayList<String>();
            for (RedirectUri redirectUri : ssoCredentials.getRedirectUris()) {
                if (redirectUri.getErrors() != null && !redirectUri.getErrors().isEmpty())
                    errors.addAll(redirectUri.getErrors());
            }
            ssoCredentials.setErrors(errors);
        }
        return ssoCredentials;
    }

    @RequestMapping(value = "/get-sso-credentials.json", method = RequestMethod.POST)
    public @ResponseBody
    SSOCredentials getSSOCredentialsJson(HttpServletRequest request) {
        SSOCredentials credentials = new SSOCredentials();
        String userOrcid = getEffectiveUserOrcid();
        ClientDetailsEntity existingClientDetails = orcidSSOManager.getUserCredentials(userOrcid);
        if (existingClientDetails != null) {
            credentials = SSOCredentials.toSSOCredentials(existingClientDetails);
        }
        return credentials;
    }

    @RequestMapping(value = "/revoke-sso-credentials.json", method = RequestMethod.POST)
    public @ResponseBody
    SSOCredentials revokeSSOCredentials(HttpServletRequest request) {
        String userOrcid = getEffectiveUserOrcid();
        orcidSSOManager.revokeSSOAccess(userOrcid);
        return this.getEmptySSOCredentials(request);
    }

    /**
     * Validates the ssoCredentials object
     * 
     * @param ssoCredentials
     * @return true if any error is found in the ssoCredentials object
     * */
    private boolean validateSSoCredentials(SSOCredentials ssoCredentials) {
        boolean hasErrors = false;
        Set<RedirectUri> redirectUris = ssoCredentials.getRedirectUris();

        if (redirectUris == null || redirectUris.isEmpty()) {
            List<String> errors = new ArrayList<String>();
            errors.add(getMessage("manage.developer_tools.at_least_one"));
            ssoCredentials.setErrors(errors);
            hasErrors = true;
        } else {
            for (RedirectUri redirectUri : redirectUris) {
                List<String> errors = validateRedirectUri(redirectUri);
                if (errors != null) {
                    redirectUri.setErrors(errors);
                    hasErrors = true;
                }
            }
        }
        return hasErrors;
    }

    /**
     * Checks if a redirect uri contains a valid URI associated to it
     * 
     * @param redirectUri
     * @return null if there are no errors, an List of strings containing error
     *         messages if any error happens
     * */
    private List<String> validateRedirectUri(RedirectUri redirectUri) {
        List<String> errors = null;
        try {
            URI.create(redirectUri.getValue().getValue());
        } catch (NullPointerException npe) {
            errors = new ArrayList<String>();
            errors.add(getMessage("manage.developer_tools.empty_redirect_uri"));
        } catch (IllegalArgumentException iae) {
            errors = new ArrayList<String>();
            errors.add(getMessage("manage.developer_tools.invalid_redirect_uri"));
        }
        return errors;
    }

    
    /**
     * TODO
     * */
    @RequestMapping(value = "/enable-developer-tools.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean enableDeveloperTools(HttpServletRequest request) {
        OrcidProfile profile = getEffectiveProfile();
        boolean updated = true;
        if (profile.getOrcidInternal() != null && profile.getOrcidInternal().getPreferences() != null
                && profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled() != null
                && !profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled().isValue()) {
            updated = profileEntityManager.enableDeveloperTools(profile);
        }
        return updated;
    }

    /**
     * TODO
     * */
    @RequestMapping(value = "/disable-developer-tools.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean disableDeveloperTools(HttpServletRequest request) {
        OrcidProfile profile = getEffectiveProfile();
        boolean updated = true;
        if (profile.getOrcidInternal() != null && profile.getOrcidInternal().getPreferences() != null
                && profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled() != null
                && profile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled().isValue()) {
            //Disable the developer tools
            updated = profileEntityManager.disableDeveloperTools(profile);
            //Disable the sso access
            orcidSSOManager.revokeSSOAccess(profile.getOrcidIdentifier().getPath());
        }
        return updated;
    }

}
