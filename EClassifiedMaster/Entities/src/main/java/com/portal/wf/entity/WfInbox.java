package com.portal.wf.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.portal.basedao.BaseEntity;

@Entity
@Table(name = "wf_inbox")
public class WfInbox extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wf_inbox_generator")
	@SequenceGenerator(name = "wf_inbox_generator", sequenceName = "WF_INBOX_SEQ", allocationSize = 1)
	@Column(name = "wf_item_id")
	private Integer wfItemId;

	@Column(name = "tags")
	private String tags;

	@Column(name = "subject")
	private String subject;

	@Column(name = "status")
	private Integer status;

	@Column(name = "req_to_type")
	private String reqToType;

	@Column(name = "req_from_type")
	private String reqFromType;

	@Column(name = "req_date")
	private Date reqDate;

	@Column(name = "object_type")
	private String objectType;

	@Column(name = "object_key")
	private String objectKey;

	@Column(name = "object_from")
	private String objectFrom;

	@Column(name = "mark_as_delete")
	private Integer markAsDelete;

	@Column(name = "created_ts")
	private Date createdTs;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "comments")
	private String comments;

	@Column(name = "changed_ts")
	private Date changedTs;

	@Column(name = "changed_by")
	private String changedBy;

	@Column(name = "read_status")
	private String readStatus;

	@Column(name = "ref_from_id")
	private Integer refFromId;

	@Column(name = "ref_to_id")
	private Integer refToId;

	@Column(name = "rec_user_id")
	private Integer recUserId;

	public Integer getWfItemId() {
		return wfItemId;
	}

	public void setWfItemId(Integer wfItemId) {
		this.wfItemId = wfItemId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getReqToType() {
		return reqToType;
	}

	public void setReqToType(String reqToType) {
		this.reqToType = reqToType;
	}

	public String getReqFromType() {
		return reqFromType;
	}

	public void setReqFromType(String reqFromType) {
		this.reqFromType = reqFromType;
	}

	public Date getReqDate() {
		return reqDate;
	}

	public void setReqDate(Date reqDate) {
		this.reqDate = reqDate;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getObjectFrom() {
		return objectFrom;
	}

	public void setObjectFrom(String objectFrom) {
		this.objectFrom = objectFrom;
	}

	public Integer getMarkAsDelete() {
		return markAsDelete;
	}

	public void setMarkAsDelete(Integer markAsDelete) {
		this.markAsDelete = markAsDelete;
	}

	public Date getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getChangedTs() {
		return changedTs;
	}

	public void setChangedTs(Date changedTs) {
		this.changedTs = changedTs;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public String getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(String readStatus) {
		this.readStatus = readStatus;
	}

	public Integer getRefFromId() {
		return refFromId;
	}

	public void setRefFromId(Integer refFromId) {
		this.refFromId = refFromId;
	}

	public Integer getRefToId() {
		return refToId;
	}

	public void setRefToId(Integer refToId) {
		this.refToId = refToId;
	}

	public Integer getRecUserId() {
		return recUserId;
	}

	public void setRecUserId(Integer recUserId) {
		this.recUserId = recUserId;
	}

}
