<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@base>
<div class="colorbox-content manage-bio" id="manage-bio">
    <form id="bio-settings-form" class="" action="<@spring.url '/account/save-bio-settings'/>" method="post" autocomplete="off">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h1 class="lightbox-title pull-left">${springMacroRequestContext.getMessage("manage_bio_settings.editpersonalinformation")}</h1>
            </div>            
        </div>
        <@spring.bind "changePersonalInfoForm.*" /> 
        <#if spring.status.error>
            <div class="alert alert-error">
                <ul class="validationerrors">
                    <#list spring.status.errors.fieldErrors?sort as error> <li>${springMacroRequestContext.getMessage(error, false)}</li> </#list>
                </ul>
            </div>
        </#if>
         <#if changesSaved?? && changesSaved>
            <div class="alert alert-success">
                <strong><@spring.message "orcid.frontend.web.details_saved"/></strong>
            </div>
        </#if>
        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
            	    <h3>${springMacroRequestContext.getMessage("manage_bio_settings.h3names")}</h3>		
                    <div class="control-group"> 
             			<label for="firstName">${springMacroRequestContext.getMessage("manage_bio_settings.labelfirstname")}</label>             			
             			<div class="relative"><@spring.formInput "changePersonalInfoForm.firstName" 'class="input-xlarge"'/>
             			<a href="http://support.orcid.org/knowledgebase/articles/142948-names-in-the-orcid-registry" target="_blank"><i class="glyphicon glyphicon-question-sign"></i></a>
             			</div>
             		</div>
                    <div class="control-group"> 
             			<label for="lastName">${springMacroRequestContext.getMessage("manage_bio_settings.labellastname")}</label>
             			<div class="relative"><@spring.formInput "changePersonalInfoForm.lastName" 'class="input-xlarge"'/></div>
             		</div>
             		<div class="control-group"> 
                        <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.labelpublishedname")}</label>
             			<div class="relative ie7-zi-fix-top">
             			    <@spring.formInput "changePersonalInfoForm.creditName" 'class="input-xlarge"'/>
             			    <label class="visibility-lbl">
             			        ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                                <@spring.formSingleSelect "changePersonalInfoForm.creditNameVisibility", visibilities />    
                            </label>
                            <@orcid.privacy "" changePersonalInfoForm.creditNameVisibility! />
             			</div>
             		</div>
             		<div class="control-group">
                        <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.othernames")}</label>
                        <div class="relative ie7-zi-fix-bottom">
             			<@spring.formInput "changePersonalInfoForm.otherNamesDelimited" 'class="input-xlarge"'/>
             			<label class="visibility-lbl">
             			    ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
             			    <@spring.formSingleSelect "changePersonalInfoForm.otherNamesVisibility", visibilities />
             			</label>
                        <@orcid.privacy "" changePersonalInfoForm.otherNamesVisibility! />
             			</div>
             		</div>
                
                <h3>${springMacroRequestContext.getMessage("manage_bio_settings.h3aboutme")}</h3>
                <div class="control-group">
                    <label for="biography">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</label>
                    <div class="relative"><@spring.formTextarea "changePersonalInfoForm.biography" 'class="input-xlarge" maxlength="5000"'/></div>
                </div>
             			
             	<div class="control-group">
                    <label for="keywordsDelimited">${springMacroRequestContext.getMessage("manage_bio_settings.labelkeywords")}</label>
                	<div class="relative ie7-zi-fix-top">
                	    <@spring.formInput "changePersonalInfoForm.keywordsDelimited", 'class="input-xlarge"'/>
                	    <label class="visibility-lbl">
                            ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                            <@spring.formSingleSelect "changePersonalInfoForm.keywordsVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.keywordsVisibility! />
                	</div>
                </div>
                
             	<div class="control-group">
                    <label for="biography">${springMacroRequestContext.getMessage("manage_bio_settings.labelcountry")}</label>
                    <div class="relative ie7-zi-fix-bottom">
                    	<select id="isoCountryCode" name="isoCountryCode">
                    		<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
	                    	<#list isoCountries?keys as key>
	                    		<#if (changePersonalInfoForm.isoCountryCode)! = key>
								    <option value="${key}" selected>${isoCountries[key]}</option>
								<#else>
								    <option value="${key}">${isoCountries[key]}</option>
								</#if>
							</#list>
						</select>                        
                        <label class="visibility-lbl">
                            ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                            <@spring.formSingleSelect "changePersonalInfoForm.isoCountryVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.isoCountryVisibility! />
                    </div>
                </div>
                
            </div>
            <!-- Websites -->
            <div class="col-md-6 col-sm-6 col-xs-12" id="websites">
                <#if changePersonalInfoForm.savedResearcherUrls?? && changePersonalInfoForm.savedResearcherUrls.researcherUrl??>
                    <div class="websites-vis">
                        <label class="visibility-lbl">
                            ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                            <@spring.formSingleSelect "changePersonalInfoForm.websiteUrlVisibility", visibilities />
                        </label>
                        <div class="privacy-selector">
	                        <@orcid.privacy "" changePersonalInfoForm.websiteUrlVisibility! />
                        </div>
                    </div>
                    
                 	<h3>${springMacroRequestContext.getMessage("manage_bio_settings.h3websites")}</h3>
                 	<div class="">
	             		<#list changePersonalInfoForm.savedResearcherUrls.researcherUrl as savedResearcherUrl>     		  	
	             			<p>
	             		  		<@spring.formHiddenInput "changePersonalInfoForm.savedResearcherUrls.researcherUrl[${savedResearcherUrl_index}].urlName.content"/>
	             		  		<@spring.formHiddenInput "changePersonalInfoForm.savedResearcherUrls.researcherUrl[${savedResearcherUrl_index}].url.value"/>
	                      		<#if savedResearcherUrl.urlName?? && savedResearcherUrl.urlName.content?? && savedResearcherUrl.urlName.content != ''>
	                      		    ${savedResearcherUrl.urlName.content} (<a href="${savedResearcherUrl.url.value}">${savedResearcherUrl.url.value}</a>)
	                      		<#else> 
	                      		    <a href="${savedResearcherUrl.url.value}">${savedResearcherUrl.url.value}</a>
	                      		</#if>
	                      		<a href="" class="glyphicon glyphicon-trash grey delete-url" ng-click="deleteEmail($index)"></a>
	                      	</p>     		    
	                    </#list>
                    </div>
             	</#if>
               	<label for="websiteUrlText">${springMacroRequestContext.getMessage("manage_bio_settings.labelwebsite")}</label>
             	<div class="control-group form-inline websites">
             	 	<@spring.formInput "changePersonalInfoForm.websiteUrlText", 'class="input-xlarge"  placeholder="${springMacroRequestContext.getMessage("manual_work_form_contents.labeldescription")}"'/>
         			<@spring.formInput "changePersonalInfoForm.websiteUrl", 'class="input-xlarge" placeholder="${springMacroRequestContext.getMessage("manual_work_form_contents.labelURL")}"'/>
             	</div>
             	             	             	             	             	 
            	<div class="control-group">
                    <h3>${springMacroRequestContext.getMessage("manage_bio_settings.emailaddress")}</h3>
                    <div class="relative">
                       <a href="javascript:void(0)" onClick="top.colorOnCloseBoxDest='<@spring.url '/account#editEmail'/>'; top.$.colorbox.close(); return false;">${springMacroRequestContext.getMessage("manage_bio_settings.editEmail")}</a>
                    </div>
                </div>             	 		
             	<div class="control-group save-btns left">
                    <button id="bottom-submit-affiliates" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("manage_bio_settings.btnsavechanges")}</button>
                    <button id="bottom-clear-affiliates" class="btn close-button" type="reset">${springMacroRequestContext.getMessage("manage_bio_settings.btncancel")}</button>
                </div>
            </div>
        </div>
    </form>
</div>
<div id="confirm-dialog"></div>
</@base>
