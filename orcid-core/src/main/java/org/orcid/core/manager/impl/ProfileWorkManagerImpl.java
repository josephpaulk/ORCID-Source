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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;

public class ProfileWorkManagerImpl implements ProfileWorkManager {

    @Resource
    private ProfileWorkDao profileWorkDao;

    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param clientOrcid
     *            The client orcid
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @return true if the relationship was deleted
     * */
    @Override
    public boolean removeWork(String clientOrcid, String workId) {
        return profileWorkDao.removeWork(clientOrcid, workId);
    }

    /**
     * Updates the visibility of an existing profile work relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param workId
     *            The id of the work that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile work relationship
     * 
     * @return true if the relationship was updated
     * */
    public boolean updateWork(String clientOrcid, String workId, Visibility visibility) {
        return profileWorkDao.updateWork(clientOrcid, workId, visibility);
    }

    /**
     * Get the profile work associated with the client orcid and the workId
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param workId
     *            The id of the work that will be updated
     * 
     * @return the profileWork object
     * */
    public ProfileWorkEntity getProfileWork(String clientOrcid, String workId) {
        return profileWorkDao.getProfileWork(clientOrcid, workId);
    }

    /**
     * Creates a new profile entity relationship between the provided work and
     * the given profile.
     * 
     * @param orcid
     *            The profile id
     * 
     * @param workId
     *            The work id
     * 
     * @param visibility
     *            The work visibility
     * 
     * @return true if the profile work relationship was created
     * */
    public boolean addProfileWork(String orcid, long workId, Visibility visibility, String sourceOrcid) {
        return profileWorkDao.addProfileWork(orcid, workId, visibility, sourceOrcid);
    }
}
