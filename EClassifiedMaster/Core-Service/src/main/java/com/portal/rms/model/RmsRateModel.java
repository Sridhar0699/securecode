package com.portal.rms.model;

import java.util.List;

public class RmsRateModel {
	
	private List<Integer> editions;
	private Integer adsSubType;
	private Integer fixedFormat;
	private Double rate;
	public List<Integer> getEditions() {
		return editions;
	}
	public void setEditions(List<Integer> editions) {
		this.editions = editions;
	}
	public Integer getAdsSubType() {
		return adsSubType;
	}
	public void setAdsSubType(Integer adsSubType) {
		this.adsSubType = adsSubType;
	}
	public Integer getFixedFormat() {
		return fixedFormat;
	}
	public void setFixedFormat(Integer fixedFormat) {
		this.fixedFormat = fixedFormat;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	
	
}
