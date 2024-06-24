package com.portal.rms.model;

public class PaymentDetails {
	
	private Integer paymentMode;
	private String bankOrBranch;
	private String referenceId;
	private String signatureAttachmentId;
	private Integer paymentMethod;
	private String bankOrUpi;
	private String cashReceiptNo;
	private String otherDetails;
	private String paymentAttachmentId;
	private String chequeAttachmentId;
	
	private RmsKycAttatchments kycAttatchments;

	public String getBankOrBranch() {
		return bankOrBranch;
	}
	public void setBankOrBranch(String bankOrBranch) {
		this.bankOrBranch = bankOrBranch;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getBankOrUpi() {
		return bankOrUpi;
	}
	public void setBankOrUpi(String bankOrUpi) {
		this.bankOrUpi = bankOrUpi;
	}
	public String getCashReceiptNo() {
		return cashReceiptNo;
	}
	public void setCashReceiptNo(String cashReceiptNo) {
		this.cashReceiptNo = cashReceiptNo;
	}
	public String getOtherDetails() {
		return otherDetails;
	}
	public void setOtherDetails(String otherDetails) {
		this.otherDetails = otherDetails;
	}
	public Integer getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(Integer paymentMode) {
		this.paymentMode = paymentMode;
	}
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getSignatureAttachmentId() {
		return signatureAttachmentId;
	}
	public void setSignatureAttachmentId(String signatureAttachmentId) {
		this.signatureAttachmentId = signatureAttachmentId;
	}
	public String getPaymentAttachmentId() {
		return paymentAttachmentId;
	}
	public void setPaymentAttachmentId(String paymentAttachmentId) {
		this.paymentAttachmentId = paymentAttachmentId;
	}
	public String getChequeAttachmentId() {
		return chequeAttachmentId;
	}
	public void setChequeAttachmentId(String chequeAttachmentId) {
		this.chequeAttachmentId = chequeAttachmentId;
	}
	public RmsKycAttatchments getKycAttatchments() {
		return kycAttatchments;
	}
	public void setKycAttatchments(RmsKycAttatchments kycAttatchments) {
		this.kycAttatchments = kycAttatchments;
	}
	
}
