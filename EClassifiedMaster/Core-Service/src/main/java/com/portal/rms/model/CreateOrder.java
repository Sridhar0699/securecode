package com.portal.rms.model;

public class CreateOrder {
	private ClientPo clientPo;
	private InsertionPo insertionPo;
	private PaymentDetails paymentDetails;
	private String itemId;
	private String action;
	private String orderId;	
	private Integer bookingOffice;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public ClientPo getClientPo() {
		return clientPo;
	}
	public void setClientPo(ClientPo clientPo) {
		this.clientPo = clientPo;
	}
	public InsertionPo getInsertionPo() {
		return insertionPo;
	}
	public void setInsertionPo(InsertionPo insertionPo) {
		this.insertionPo = insertionPo;
	}
	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public Integer getBookingOffice() {
		return bookingOffice;
	}
	public void setBookingOffice(Integer bookingOffice) {
		this.bookingOffice = bookingOffice;
	}
	
	
	
	
	
}
