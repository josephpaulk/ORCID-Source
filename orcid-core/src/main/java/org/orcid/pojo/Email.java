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
package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class Email implements ErrorsInterface {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private boolean current;
    
    private boolean primary;
    
    private String source;
    
    private String value;
    
    private boolean verified;
    
    private org.orcid.jaxb.model.message.Visibility visibility;

    public Email() {

    }
    
    public static Email valueOf(org.orcid.jaxb.model.message.Email emailMgs) {
        Email email = new Email();
        email.setCurrent(emailMgs.isCurrent());
        email.setPrimary(emailMgs.isPrimary());
        email.setSource(emailMgs.getSource());
        email.setValue(emailMgs.getValue());
        email.setVerified(emailMgs.isVerified());
        email.setVisibility(emailMgs.getVisibility());    
        return email;
    }
    
    public org.orcid.jaxb.model.message.Email toEmail() {
        org.orcid.jaxb.model.message.Email emailMgs = new org.orcid.jaxb.model.message.Email();
        emailMgs.setCurrent(this.isCurrent());
        emailMgs.setPrimary(this.isPrimary());
        emailMgs.setSource(this.getSource());
        emailMgs.setValue(this.getValue());
        emailMgs.setVerified(this.isVerified());
        emailMgs.setVisibility(this.getVisibility());
        return emailMgs;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }


    public boolean isCurrent() {
        return current;
    }


    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getSource() {
        return source;
    }


    public void setSource(String source) {
        this.source = source;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public org.orcid.jaxb.model.message.Visibility getVisibility() {
        return visibility;
    }


    public void setVisibility(org.orcid.jaxb.model.message.Visibility visibility) {
        this.visibility = visibility;
    }


    public boolean isPrimary() {
        return primary;
    }


    public void setPrimary(boolean primary) {
        this.primary = primary;
    }


    public boolean isVerified() {
        return verified;
    }


    public void setVerified(boolean verified) {
        this.verified = verified;
    }

}
