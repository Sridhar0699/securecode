package com.portal.rms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.portal.basedao.BaseEntity;

@Entity
@Table(name = "rms_order_items")
public class RmsOrderItems extends BaseEntity{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "rms_item_id")
	private String rmsItemId;
	
	@Column(name = "item_id")
	private String itemId;
	
	@Column(name = "order_id")
	private String orderId;
	
	@Column(name = "no_of_insertions")
	private Integer noOfInsertions;
	
	@Column(name = "size_width")
	private Double sizeWidth;
	
	@Column(name = "size_height")
	private Double sizeHeight;
	
	@Column(name = "space_width")
	private Double spaceWidth;
	
	@Column(name = "space_height")
	private Double spaceHeight;
	
	@Column(name = "page_position")
	private Integer pagePosition;
	
	@Column(name = "created_ts")
	private Date createdTs;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "changed_ts")
	private Date changedTs;
	
	@Column(name = "changed_by")
	private Integer changedBy;
	
	@Column(name = "mark_as_delete")
	private Boolean markAsDelete;
	
	@Column(name = "format_type")
	private Integer formatType;
	
	@Column(name = "fixed_format")
	private Integer fixedFormat;
	
	@Column(name = "page_number")
	private Integer pageNumber;
	
	@Column(name = "category_discount")
	private Integer categoryDiscount;
	
	@Column(name = "multi_discount")
	private Double multiDiscount;
	
	@Column(name = "additional_discount")
	private Double additionalDiscount;
	
	@Column(name = "surcharge_rate")
	private Double surchargeRate;
	
	@Column(name = "surcharge_amount")
	private Double surchargeAmount;
	
	@Column(name = "multi_discount_amount")
	private Double multiDiscountAmount;
	
	@Column(name = "additional_discount_amount")
    private Double additionalDiscountAmount;
	
	@Column(name="category_discount_amount")
	private Double categoryDiscountAmount;

	public String getRmsItemId() {
		return rmsItemId;
	}

	public void setRmsItemId(String rmsItemId) {
		this.rmsItemId = rmsItemId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getNoOfInsertions() {
		return noOfInsertions;
	}

	public void setNoOfInsertions(Integer noOfInsertions) {
		this.noOfInsertions = noOfInsertions;
	}

	public Double getSizeWidth() {
		return sizeWidth;
	}

	public void setSizeWidth(Double sizeWidth) {
		this.sizeWidth = sizeWidth;
	}

	public Double getSizeHeight() {
		return sizeHeight;
	}

	public void setSizeHeight(Double sizeHeight) {
		this.sizeHeight = sizeHeight;
	}

	public Double getSpaceWidth() {
		return spaceWidth;
	}

	public void setSpaceWidth(Double spaceWidth) {
		this.spaceWidth = spaceWidth;
	}

	public Double getSpaceHeight() {
		return spaceHeight;
	}

	public void setSpaceHeight(Double spaceHeight) {
		this.spaceHeight = spaceHeight;
	}

	public Integer getPagePosition() {
		return pagePosition;
	}

	public void setPagePosition(Integer pagePosition) {
		this.pagePosition = pagePosition;
	}

	public Date getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getChangedTs() {
		return changedTs;
	}

	public void setChangedTs(Date changedTs) {
		this.changedTs = changedTs;
	}

	public Integer getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(Integer changedBy) {
		this.changedBy = changedBy;
	}

	public Boolean getMarkAsDelete() {
		return markAsDelete;
	}

	public void setMarkAsDelete(Boolean markAsDelete) {
		this.markAsDelete = markAsDelete;
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

	public Integer getCategoryDiscount() {
		return categoryDiscount;
	}

	public void setCategoryDiscount(Integer categoryDiscount) {
		this.categoryDiscount = categoryDiscount;
	}

	public Double getMultiDiscount() {
		return multiDiscount;
	}

	public void setMultiDiscount(Double multiDiscount) {
		this.multiDiscount = multiDiscount;
	}

	public Double getAdditionalDiscount() {
		return additionalDiscount;
	}

	public void setAdditionalDiscount(Double additionalDiscount) {
		this.additionalDiscount = additionalDiscount;
	}

	public Double getSurchargeRate() {
		return surchargeRate;
	}

	public void setSurchargeRate(Double surchargeRate) {
		this.surchargeRate = surchargeRate;
	}

	public Double getSurchargeAmount() {
		return surchargeAmount;
	}

	public void setSurchargeAmount(Double surchargeAmount) {
		this.surchargeAmount = surchargeAmount;
	}

	public Double getMultiDiscountAmount() {
		return multiDiscountAmount;
	}

	public void setMultiDiscountAmount(Double multiDiscountAmount) {
		this.multiDiscountAmount = multiDiscountAmount;
	}

	public Double getAdditionalDiscountAmount() {
		return additionalDiscountAmount;
	}

	public void setAdditionalDiscountAmount(Double additionalDiscountAmount) {
		this.additionalDiscountAmount = additionalDiscountAmount;
	}

	public Double getCategoryDiscountAmount() {
		return categoryDiscountAmount;
	}

	public void setCategoryDiscountAmount(Double categoryDiscountAmount) {
		this.categoryDiscountAmount = categoryDiscountAmount;
	}
	
	
	
	
	
}
