package com.portal.common.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.portal.common.models.DocumentsModel;
import com.portal.common.models.GenericApiResponse;
import com.portal.common.service.DocumentService;
import com.portal.security.model.LoggedUser;
import com.portal.security.util.LoggedUserContext;


@RestController
public class DocumentsApiController implements DocumentApi{
	
	private static final Logger logger = LogManager.getLogger(DocumentsApiController.class);
	
	@Autowired(required = true)
	private LoggedUserContext userContext;
	
	@Autowired 
	private DocumentService documentService;

	@SuppressWarnings("unused")
	@Override
	public ResponseEntity<?> uploadRmsDocUpload(HttpServletRequest request) {
		String METHOD_NAME = "amDocsUpload";

        logger.info("Entered into the method: " + METHOD_NAME);
        Date respFrmTs = new Date();
        ResponseEntity<GenericApiResponse> respObj = null;
        LoggedUser loggedUser = userContext.getLoggedUser();
        GenericApiResponse apiResp = new GenericApiResponse();
        DocumentsModel rmsDocumentsModel = new DocumentsModel();
//        rmsDocumentsModel.setDocIds(docId);
        rmsDocumentsModel.setLoggedUser(loggedUser);
//        rmsDocumentsModel.setDocType(attType);
//        rmsDocumentsModel.setDealerAchDataId(achievementId);
//        rmsDocumentsModel.setDealerAuditId(dealerAuditId);

                if (request instanceof MultipartHttpServletRequest) {
                    MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                    // Taking the Multiple files from upload document request
                    List<MultipartFile> uploadMultipleFiles = multipartHttpServletRequest.getFiles("files");
                    logger.info("Number of documnets requested to upload: " + uploadMultipleFiles.size());
                    if (uploadMultipleFiles != null && !uploadMultipleFiles.isEmpty()) {
                    	rmsDocumentsModel.setMultipartFileAtachments(uploadMultipleFiles);
                    }else {
                    	apiResp.setMessage("File Not Found");
                    	respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);
                    	return respObj;
                    }
                }
                apiResp = documentService.uploadAdditionalDocuments(rmsDocumentsModel);
//                if (action == 1) {
//                    apiResp = documentService.uploadAdditionalDocuments(auditDocumentsModel);
//                } else if (action == 2) {
//                    apiResp = auditDocumentService.updateUploadedDocuments(auditDocumentsModel);
//                } else if (action == 5) {
//                    apiResp = auditDocumentService.uploadUserProfile(auditDocumentsModel);
//                } else {
//                    apiResp = auditDocumentService.removeUploadedDocuments(auditDocumentsModel);
//                }

        respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

        logger.info("Exit from the method: " + METHOD_NAME);

        return respObj;
	}

	
}
