package com.portal.clf.models;

import java.util.List;

public class ClassifiedRateDetails {

	private List<ClassifiedRates> classifiedRates;
	private Double tax;
	private Integer maxLinesAllowed;
	private Double agencyCommission;

	public List<ClassifiedRates> getClassifiedRates() {
		return classifiedRates;
	}

	public void setClassifiedRates(List<ClassifiedRates> classifiedRates) {
		this.classifiedRates = classifiedRates;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Integer getMaxLinesAllowed() {
		return maxLinesAllowed;
	}

	public void setMaxLinesAllowed(Integer maxLinesAllowed) {
		this.maxLinesAllowed = maxLinesAllowed;
	}

	public Double getAgencyCommission() {
		return agencyCommission;
	}

	public void setAgencyCommission(Double agencyCommission) {
		this.agencyCommission = agencyCommission;
	}

}
