package com.portal.clf.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.portal.basedao.BaseEntity;

@Entity
@Table(name = "clf_order_items")
public class ClfOrderItems extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "item_id")
	private String itemId;
	
	@ManyToOne
	@JoinColumn(name = "order_id")
	private ClfOrders clfOrders;
	
	@Column(name = "classified_type")
	private Integer classifiedType;
	
	@Column(name = "classified_ads_type")
	private Integer classifiedAdsType;
	
	@Column(name = "classified_ads_sub_type")
	private Integer classifiedAdsSubType;
	
	@Column(name = "scheme")
	private Integer scheme;
	
	@Column(name = "category")
	private Integer category;
	
	@Column(name = "subcategory")
	private Integer subcategory;
	
	@Column(name = "category_group")
	private Integer group;
	
	@Column(name = "sub_group")
	private Integer subGroup;
	
	@Column(name = "child_group")
	private Integer childGroup;
	
	@Column(name = "lang")
	private Integer lang;
	
	@Column(name = "clf_content")
	private String clfContent;
	
	@Column(name = "download_status")
	private Boolean downloadStatus;
	
	@OneToMany(mappedBy = "clfOrderItems", fetch = FetchType.LAZY)
	private Set<ClfPublishDates> clfPublishDates;
	
	@OneToMany(mappedBy = "clfOrderItems", fetch = FetchType.LAZY)
	private Set<ClfEditions> clfEditions;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_ts")
	private Date createdTs;

	@Column(name = "changed_by")
	private Integer changedBy;

	@Column(name = "changed_ts")
	private Date changedTs;
	
	@Column(name = "mark_as_delete")
	private Boolean markAsDelete;
	
	@Column(name = "ad_id")
	private String adId; 

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public ClfOrders getClfOrders() {
		return clfOrders;
	}

	public void setClfOrders(ClfOrders clfOrders) {
		this.clfOrders = clfOrders;
	}

	public Integer getClassifiedType() {
		return classifiedType;
	}

	public void setClassifiedType(Integer classifiedType) {
		this.classifiedType = classifiedType;
	}

	public Integer getClassifiedAdsType() {
		return classifiedAdsType;
	}

	public void setClassifiedAdsType(Integer classifiedAdsType) {
		this.classifiedAdsType = classifiedAdsType;
	}

	public Integer getClassifiedAdsSubType() {
		return classifiedAdsSubType;
	}

	public void setClassifiedAdsSubType(Integer classifiedAdsSubType) {
		this.classifiedAdsSubType = classifiedAdsSubType;
	}

	public Integer getScheme() {
		return scheme;
	}

	public void setScheme(Integer scheme) {
		this.scheme = scheme;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(Integer subcategory) {
		this.subcategory = subcategory;
	}

	public Integer getLang() {
		return lang;
	}

	public void setLang(Integer lang) {
		this.lang = lang;
	}

	public String getClfContent() {
		return clfContent;
	}

	public void setClfContent(String clfContent) {
		this.clfContent = clfContent;
	}

	public Boolean getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(Boolean downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public Set<ClfPublishDates> getClfPublishDates() {
		return clfPublishDates;
	}

	public void setClfPublishDates(Set<ClfPublishDates> clfPublishDates) {
		this.clfPublishDates = clfPublishDates;
	}

	public Set<ClfEditions> getClfEditions() {
		return clfEditions;
	}

	public void setClfEditions(Set<ClfEditions> clfEditions) {
		this.clfEditions = clfEditions;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	public Integer getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(Integer changedBy) {
		this.changedBy = changedBy;
	}

	public Date getChangedTs() {
		return changedTs;
	}

	public void setChangedTs(Date changedTs) {
		this.changedTs = changedTs;
	}

	public Boolean getMarkAsDelete() {
		return markAsDelete;
	}

	public void setMarkAsDelete(Boolean markAsDelete) {
		this.markAsDelete = markAsDelete;
	}

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getSubGroup() {
		return subGroup;
	}

	public void setSubGroup(Integer subGroup) {
		this.subGroup = subGroup;
	}

	public Integer getChildGroup() {
		return childGroup;
	}

	public void setChildGroup(Integer childGroup) {
		this.childGroup = childGroup;
	}

}
