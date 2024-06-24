package com.portal.rms.model;

public class RmsViewDetails {

	private CustomerObjectDisplay customerObjectDisplay;
	private InsertionObjectDisplay insertionObjectDisplay;
	private PaymentObjectDisplay paymentObjectDisplay;

	public CustomerObjectDisplay getCustomerObjectDisplay() {
		return customerObjectDisplay;
	}

	public void setCustomerObjectDisplay(CustomerObjectDisplay customerObjectDisplay) {
		this.customerObjectDisplay = customerObjectDisplay;
	}

	public InsertionObjectDisplay getInsertionObjectDisplay() {
		return insertionObjectDisplay;
	}

	public void setInsertionObjectDisplay(InsertionObjectDisplay insertionObjectDisplay) {
		this.insertionObjectDisplay = insertionObjectDisplay;
	}

	public PaymentObjectDisplay getPaymentObjectDisplay() {
		return paymentObjectDisplay;
	}

	public void setPaymentObjectDisplay(PaymentObjectDisplay paymentObjectDisplay) {
		this.paymentObjectDisplay = paymentObjectDisplay;
	}

}