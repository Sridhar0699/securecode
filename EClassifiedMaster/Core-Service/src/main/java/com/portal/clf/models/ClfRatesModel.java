package com.portal.clf.models;

public class ClfRatesModel {

	private Integer classifiedAdsType;
	private Integer classifiedAdsSubtype;
	private Integer editionId;
	private Double rate;
	private Double extraLineAmount;
	private Integer minLines;
	private Integer charCountPerLine;
	private String classifiedAdsTypeStr;
	private String classifiedAdsSubtypeStr;
	private String editionStr;
	private String modalType;
	private String rateId;
	
	public String getRateId() {
		return rateId;
	}
	public void setRateId(String rateId) {
		this.rateId = rateId;
	}
	public String getModalType() {
		return modalType;
	}
	public void setModalType(String modalType) {
		this.modalType = modalType;
	}
	public Integer getClassifiedAdsType() {
		return classifiedAdsType;
	}
	public void setClassifiedAdsType(Integer classifiedAdsType) {
		this.classifiedAdsType = classifiedAdsType;
	}
	public Integer getClassifiedAdsSubtype() {
		return classifiedAdsSubtype;
	}
	public void setClassifiedAdsSubtype(Integer classifiedAdsSubtype) {
		this.classifiedAdsSubtype = classifiedAdsSubtype;
	}
	public Integer getEditionId() {
		return editionId;
	}
	public void setEditionId(Integer editionId) {
		this.editionId = editionId;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getExtraLineAmount() {
		return extraLineAmount;
	}
	public void setExtraLineAmount(Double extraLineAmount) {
		this.extraLineAmount = extraLineAmount;
	}
	public Integer getMinLines() {
		return minLines;
	}
	public void setMinLines(Integer minLines) {
		this.minLines = minLines;
	}
	public Integer getCharCountPerLine() {
		return charCountPerLine;
	}
	public void setCharCountPerLine(Integer charCountPerLine) {
		this.charCountPerLine = charCountPerLine;
	}
	public String getClassifiedAdsTypeStr() {
		return classifiedAdsTypeStr;
	}
	public void setClassifiedAdsTypeStr(String classifiedAdsTypeStr) {
		this.classifiedAdsTypeStr = classifiedAdsTypeStr;
	}
	public String getClassifiedAdsSubtypeStr() {
		return classifiedAdsSubtypeStr;
	}
	public void setClassifiedAdsSubtypeStr(String classifiedAdsSubtypeStr) {
		this.classifiedAdsSubtypeStr = classifiedAdsSubtypeStr;
	}
	public String getEditionStr() {
		return editionStr;
	}
	public void setEditionStr(String editionStr) {
		this.editionStr = editionStr;
	}
	
	
	
}
