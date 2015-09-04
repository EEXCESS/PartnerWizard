package eu.eexcess.partnerrecommender;

import java.io.Serializable;

public class PartnerInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1973507578256164913L;
	private Bean bean;
	
	public Bean getBean() {
		return bean;
	}
	public void setBean(Bean bean) {
		this.bean = bean;
	}
	
	private String username;
	
	private boolean licenseAgreement;
	
	private String contactEmail;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isLicenseAgreement() {
		return licenseAgreement;
	}
	public void setLicenseAgreement(boolean licenseAgreement) {
		this.licenseAgreement = licenseAgreement;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	
}
