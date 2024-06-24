package com.portal.rms.model;

public class PaymentObjectDisplay {
	
	private Short paymentMethod;
	private String paymentMethodDesc;
	private Short paymentMode;
	private String paymentModeDesc;
	private String referenceId;
	private String cashReceiptNo;
	private String otherDetails;
	private String signatureId;
	private String bankOrUpi;

	public Short getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(Short paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentMethodDesc() {
		return paymentMethodDesc;
	}

	public void setPaymentMethodDesc(String paymentMethodDesc) {
		this.paymentMethodDesc = paymentMethodDesc;
	}

	public Short getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(Short paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPaymentModeDesc() {
		return paymentModeDesc;
	}

	public void setPaymentModeDesc(String paymentModeDesc) {
		this.paymentModeDesc = paymentModeDesc;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
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

	public String getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(String signatureId) {
		this.signatureId = signatureId;
	}

	public String getBankOrUpi() {
		return bankOrUpi;
	}

	public void setBankOrUpi(String bankOrUpi) {
		this.bankOrUpi = bankOrUpi;
	}


}
