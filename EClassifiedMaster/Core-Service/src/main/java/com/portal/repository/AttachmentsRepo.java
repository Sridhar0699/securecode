package com.portal.repository;

import org.springframework.data.repository.CrudRepository;

import com.portal.doc.entity.Attachments;

public interface AttachmentsRepo extends CrudRepository<Attachments, String>{

}
