package com.portal.rms.model;

import java.util.List;

public class InsertionPo {
	
	private String caption;
	private Integer noOfInsertions;
//	private Double pageWidth;
//	private Double pageHeight;
	private String spaceHeight;
	private String spaceWidth;
	private String ratePerSquareCms;
	private Integer scheme;
	private List<Integer> editions;
//	private String adsTypeStr;
//	private String adsSubTypeStr;
	private String rate ;
//	private Double extraLineRate = 0.0;
//	private Integer minLines;
//	private Integer actualLines;
//	private Double extraLineAmount;
	private String totalAmount;
	private List<String> publishDates;
	private Integer pagePosition;
	private Integer classifiedAdsSubtype;
	
	
	private Integer productGroup;
    private Integer productSubGroup;
    private Integer productChildGroup;
    
    private Integer editionType;
    
    private Integer formatType;
    private Integer fixedFormat;
    private Integer pageNumber;
    private String categoryDiscount;
    private String multiDiscount;
    private String additionalDiscount;
    private String surchargeRate;
    private String cgst;
    private String sgst;
    private String igst;
//    private Double gstTotal;
//    private Double totalDiscount;
    private String amount;
    
    
    private String cgstValue;
    private String sgstValue;
    private String igstValue;
    
    private String multiDiscountAmount;
    private String additionalDiscountAmount;
    private String surchargeAmount;
    private String categoryDiscountAmount;
	
	
	public Integer getClassifiedAdsSubtype() {
		return classifiedAdsSubtype;
	}
	public void setClassifiedAdsSubtype(Integer classifiedAdsSubtype) {
		this.classifiedAdsSubtype = classifiedAdsSubtype;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
	public String getSpaceHeight() {
		return spaceHeight;
	}
	public void setSpaceHeight(String spaceHeight) {
		this.spaceHeight = spaceHeight;
	}
	public String getSpaceWidth() {
		return spaceWidth;
	}
	public void setSpaceWidth(String spaceWidth) {
		this.spaceWidth = spaceWidth;
	}
	public String getRatePerSquareCms() {
		return ratePerSquareCms;
	}
	public void setRatePerSquareCms(String ratePerSquareCms) {
		this.ratePerSquareCms = ratePerSquareCms;
	}
	public Integer getScheme() {
		return scheme;
	}
	public void setScheme(Integer scheme) {
		this.scheme = scheme;
	}
	public List<Integer> getEditions() {
		return editions;
	}
	public void setEditions(List<Integer> editions) {
		this.editions = editions;
	}

	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<String> getPublishDates() {
		return publishDates;
	}
	public void setPublishDates(List<String> publishDates) {
		this.publishDates = publishDates;
	}
	public Integer getPagePosition() {
		return pagePosition;
	}
	public void setPagePosition(Integer pagePosition) {
		this.pagePosition = pagePosition;
	}
	public Integer getProductGroup() {
		return productGroup;
	}
	public void setProductGroup(Integer productGroup) {
		this.productGroup = productGroup;
	}
	public Integer getProductSubGroup() {
		return productSubGroup;
	}
	public void setProductSubGroup(Integer productSubGroup) {
		this.productSubGroup = productSubGroup;
	}
	public Integer getProductChildGroup() {
		return productChildGroup;
	}
	public void setProductChildGroup(Integer productChildGroup) {
		this.productChildGroup = productChildGroup;
	}
	public Integer getEditionType() {
		return editionType;
	}
	public void setEditionType(Integer editionType) {
		this.editionType = editionType;
	}
	public Integer getFormatType() {
		return formatType;
	}
	public void setFormatType(Integer formatType) {
		this.formatType = formatType;
	}
	public Integer getFixedFormat() {
		return fixedFormat;
	}
	public void setFixedFormat(Integer fixedFormat) {
		this.fixedFormat = fixedFormat;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getCategoryDiscount() {
		return categoryDiscount;
	}
	public void setCategoryDiscount(String categoryDiscount) {
		this.categoryDiscount = categoryDiscount;
	}
	
	
	public String getCgst() {
		return cgst;
	}
	public void setCgst(String cgst) {
		this.cgst = cgst;
	}
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSgst() {
		return sgst;
	}
	public void setSgst(String sgst) {
		this.sgst = sgst;
	}
	public String getIgst() {
		return igst;
	}
	public void setIgst(String igst) {
		this.igst = igst;
	}
	public String getCgstValue() {
		return cgstValue;
	}
	public void setCgstValue(String cgstValue) {
		this.cgstValue = cgstValue;
	}
	public String getSgstValue() {
		return sgstValue;
	}
	public void setSgstValue(String sgstValue) {
		this.sgstValue = sgstValue;
	}
	public String getIgstValue() {
		return igstValue;
	}
	public void setIgstValue(String igstValue) {
		this.igstValue = igstValue;
	}
	public String getMultiDiscount() {
		return multiDiscount;
	}
	public void setMultiDiscount(String multiDiscount) {
		this.multiDiscount = multiDiscount;
	}
	public String getAdditionalDiscount() {
		return additionalDiscount;
	}
	public void setAdditionalDiscount(String additionalDiscount) {
		this.additionalDiscount = additionalDiscount;
	}
	public String getSurchargeRate() {
		return surchargeRate;
	}
	public void setSurchargeRate(String surchargeRate) {
		this.surchargeRate = surchargeRate;
	}
	public String getMultiDiscountAmount() {
		return multiDiscountAmount;
	}
	public void setMultiDiscountAmount(String multiDiscountAmount) {
		this.multiDiscountAmount = multiDiscountAmount;
	}
	public String getAdditionalDiscountAmount() {
		return additionalDiscountAmount;
	}
	public void setAdditionalDiscountAmount(String additionalDiscountAmount) {
		this.additionalDiscountAmount = additionalDiscountAmount;
	}
	public String getSurchargeAmount() {
		return surchargeAmount;
	}
	public void setSurchargeAmount(String surchargeAmount) {
		this.surchargeAmount = surchargeAmount;
	}
	public String getCategoryDiscountAmount() {
		return categoryDiscountAmount;
	}
	public void setCategoryDiscountAmount(String categoryDiscountAmount) {
		this.categoryDiscountAmount = categoryDiscountAmount;
	}
	public Integer getNoOfInsertions() {
		return noOfInsertions;
	}
	public void setNoOfInsertions(Integer noOfInsertions) {
		this.noOfInsertions = noOfInsertions;
	}
	
	
	
}
