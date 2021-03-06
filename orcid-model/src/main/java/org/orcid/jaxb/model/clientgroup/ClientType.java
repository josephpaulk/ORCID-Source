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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.23 at 12:45:35 PM BST 
//

package org.orcid.jaxb.model.clientgroup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for client-type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="client-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="creator"/>
 *     &lt;enumeration value="premium-creator"/>
 *     &lt;enumeration value="updater"/>
 *     &lt;enumeration value="premium-updater"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "client-type")
@XmlEnum
public enum ClientType {

    //@formatter:off
    @XmlEnumValue("creator")
    CREATOR("creator"), 
    @XmlEnumValue("premium-creator")
    PREMIUM_CREATOR("premium-creator"), 
    @XmlEnumValue("updater")
    UPDATER("updater"), 
    @XmlEnumValue("premium-updater")
    PREMIUM_UPDATER("premium-updater");
    //@formatter:on

    private final String value;

    ClientType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClientType fromValue(String v) {
        for (ClientType c : ClientType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
