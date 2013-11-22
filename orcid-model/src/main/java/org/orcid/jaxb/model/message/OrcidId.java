package org.orcid.jaxb.model.message;

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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "orcid-id")
public class OrcidId extends OrcidIdBase {

    private static final long serialVersionUID = 1L;

    public OrcidId() {
        super();
    }

    public OrcidId(String path) {
        super(path);
    }

    public OrcidId(OrcidIdBase other) {
        super(other);
    }

}