package com.portal.rms.model;

import java.util.Date;

public class RmsDashboardFilter {
	
	private Integer bookingCode;
	private String publishedDate;
	private Integer pageNumber;
	private Integer pageSize;
	private Date fromDate;
	private Date toDate;
	
	
	public Integer getBookingCode() {
		return bookingCode;
	}
	public void setBookingCode(Integer bookingCode) {
		this.bookingCode = bookingCode;
	}
	public String getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	
	
	
	

}
