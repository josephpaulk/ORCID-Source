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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.Email;

public class Emails implements ErrorsInterface, Serializable {
    private List<Email> emails = null;

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public static Emails valueOf(List<org.orcid.jaxb.model.message.Email> emailMgss) {
        Emails emails =  new Emails();
        emails.setEmails(new ArrayList<Email>());
        for (org.orcid.jaxb.model.message.Email emailMgs: emailMgss)
            emails.emails.add(Email.valueOf(emailMgs));
        return emails;
    }
    
    public List<org.orcid.jaxb.model.message.Email> toEmails() {
        List<org.orcid.jaxb.model.message.Email> emailsMgss = new ArrayList<org.orcid.jaxb.model.message.Email>(); 
        for(Email email:this.emails)
            emailsMgss.add(email.toEmail());
        return emailsMgss;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

}
