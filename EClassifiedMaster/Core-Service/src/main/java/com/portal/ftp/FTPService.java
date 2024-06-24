package com.portal.ftp;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.record.Record;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.clf.service.ClassifiedService;
import com.portal.erp.constants.EtlGeneralConstants;
import com.portal.erp.to.ApiResponse;
import com.portal.erp.to.ErpDataResponsePayload;
import com.portal.erp.to.FTPConnectionDetails;
import com.portal.repository.ClfPaymentResponseTrackingRepo;
import com.portal.rms.repository.RmsPaymentResponseTrackingRepo;

@Service("ftpService")
@Transactional
public class FTPService {

	@Autowired
	private ClassifiedService classifiedService;

	@Autowired
	private FTPUtilService fTPUtilService;

	@Autowired
	private ClfPaymentResponseTrackingRepo clfPaymentResponseTrackingRepo;
	
	@Autowired
	private RmsPaymentResponseTrackingRepo rmsPaymentResponseTrackingRepo;

	/* Set the FTP server details */
	@SuppressWarnings("rawtypes")
	public void setFtpServer(Map settingValues, FTPConnectionDetails ftpConDetails) throws Exception {

		try {
			ftpConDetails.setFtpServer(EtlGeneralConstants.FTP_HOST);
			ftpConDetails.setFtpUserName(EtlGeneralConstants.FTP_USERNAME);
			ftpConDetails.setFtpPassword(EtlGeneralConstants.FTP_PASSWORD);
			ftpConDetails.setFtpPort(Integer.parseInt(EtlGeneralConstants.FTP_PORT));
			
			//Internal Details
//			ftpConDetails.setFtpServer("ftp.asptax.com");
//			ftpConDetails.setFtpUserName("sakshieclassuser");
//			ftpConDetails.setFtpPassword("qrDAE221Mh7U");
//			ftpConDetails.setFtpPort(Integer.parseInt("21"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Uploading file data to FTP server */
	public boolean ftpServerUploading(String filePath, String fileName, byte[] uploadData,
			FTPConnectionDetails ftpConDetails) {

		boolean isUploaded = false;
		try {
			InputStream fis = new ByteArrayInputStream(uploadData);
			FTPClient ftpClient = this.connectFtpServer(ftpConDetails);
			isUploaded = ftpClient.storeFile(filePath + "/" + fileName, fis);
			this.disconnectFtpServer(ftpClient);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isUploaded;
	}

	/* Connecting to FTP server */
	private FTPClient connectFtpServer(FTPConnectionDetails ftpConDetails) {

		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(ftpConDetails.getFtpServer());// , ftpConDetails.getFtpPort()
			ftpClient.login(ftpConDetails.getFtpUserName(), ftpConDetails.getFtpPassword());
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ftpClient;
	}

	/* Disconnecting to FTP server */
	private void disconnectFtpServer(FTPClient ftpClient) {

		try {
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Uploading file data to local server */
	public boolean uploadFile(String filePath, String fileName, String uploadData) {

		boolean isUploaded = false;
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(filePath + "/" + fileName);
			file.getParentFile().mkdirs();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(uploadData);
			this.setPermissions(file);
			isUploaded = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return isUploaded;
	}

	/* Set permissions to given file */
	private void setPermissions(File file) {

		try {
			HashSet<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);

			perms.add(PosixFilePermission.GROUP_EXECUTE);
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_WRITE);

			perms.add(PosixFilePermission.OTHERS_EXECUTE);
			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_WRITE);

			Files.setPosixFilePermissions(file.toPath(), perms);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ApiResponse ftpServerFile(FTPConnectionDetails ftpConDetails) {
		InputStream inputStream = null;
		ApiResponse apiResponse = new ApiResponse();
		try {
			FTPClient ftpClient = this.connectFtpServer(ftpConDetails);
			for (FTPFile file : ftpClient.listFiles(ftpConDetails.getFtpFilePath())) {
				inputStream = ftpClient.retrieveFileStream(ftpConDetails.getFtpFilePath() + "/" + file.getName());
				if (inputStream != null) {
					apiResponse = this.processFile(inputStream, ftpConDetails, file);
				}
			}
			this.disconnectFtpServer(ftpClient);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return apiResponse;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private ApiResponse processFile(InputStream inputStream, FTPConnectionDetails ftpConDetails, FTPFile file) {
		ApiResponse apiResponse = new ApiResponse();
		try {
			CsvParserSettings settings = new CsvParserSettings();
			settings.setHeaderExtractionEnabled(true);
			settings.setNullValue("");
			CsvParser parser = new CsvParser(settings);	
			List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
			List<ErpDataResponsePayload> erpResponseList = new ArrayList<ErpDataResponsePayload>();
			if (!parseAllRecords.isEmpty()) {
				parseAllRecords.forEach(record -> {
					ErpDataResponsePayload erpResponse = new ErpDataResponsePayload();
					erpResponse.setPortalId(record.getString(EtlGeneralConstants.PORTAL_ID));
					erpResponse.setOrderId(record.getString(EtlGeneralConstants.ORDER_NUMBER));
					erpResponse.setType(record.getString(EtlGeneralConstants.TYPE));
					erpResponse.setOrderStatus(record.getString(EtlGeneralConstants.ORDER_STATUS));
					erpResponseList.add(erpResponse);
				});
				apiResponse = classifiedService.updateSyncStatus(erpResponseList);
				ByteArrayInputStream processedInputStream = new ByteArrayInputStream(
						serializeProcessedData(erpResponseList));
				Boolean flag1 = fTPUtilService.uploadApiResponse(processedInputStream, ftpConDetails, file);
				Boolean flag = fTPUtilService.deleteSourceFile(ftpConDetails, file);
				if (flag) {
					apiResponse.setMessage("FTP Processing Info :: Success");
				} else {
					apiResponse.setMessage("FTP Processing Info :: Failed");
				}
				String ftpFilePath = ftpConDetails.getFtpFilePath() + "/" + file.getName();
				Map<String, String> docDetailsMap = new HashMap<String, String>();
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonString = objectMapper.writeValueAsString(erpResponseList);
				JSONArray jsonArray = new JSONArray(jsonString);
				JSONObject jsonObject = new JSONObject();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject element = jsonArray.getJSONObject(i);
					fTPUtilService.createFTPFilesLogs(element, docDetailsMap, ftpConDetails, "", ftpFilePath);
				}
				// Send payment details to sap
				List<String> orderIds = new ArrayList<String>();
				for (ErpDataResponsePayload erp : erpResponseList) {
					if (!"E".equalsIgnoreCase(erp.getType())) {
						orderIds.add(erp.getPortalOrderId());
					}
				}
				List<Object[]> transactionDetails = clfPaymentResponseTrackingRepo.getTransactionDetails(orderIds);
				if (!transactionDetails.isEmpty()) {
					boolean check = fTPUtilService.sendTransactionDetailsToErp(transactionDetails);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}

	/* Removing file data from FTP server */
	public boolean ftpServerDelete(FTPConnectionDetails ftpConDetails, FTPFile file) {

		boolean isDeleted = false;
		try {
			FTPClient ftpClient = this.connectFtpServer(ftpConDetails);
			isDeleted = ftpClient.deleteFile(ftpConDetails.getFtpFilePath() + "/" + file.getName());
			this.disconnectFtpServer(ftpClient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDeleted;
	}

	private byte[] serializeProcessedData(List<ErpDataResponsePayload> erpResponseList) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			String header = "Portal Id,Order Id,Type,Order Status\n";
			outputStream.write(header.getBytes(StandardCharsets.UTF_8));

			// Write data
			for (ErpDataResponsePayload erpResponse : erpResponseList) {
				String row = String.format("%s,%s,%s,%s\n", erpResponse.getPortalId(), erpResponse.getOrderId(),
						erpResponse.getType(), erpResponse.getOrderStatus());
				outputStream.write(row.getBytes(StandardCharsets.UTF_8));
			}

			return outputStream.toByteArray();
		} catch (IOException e) {
			// Handle the exception appropriately
			e.printStackTrace();
			return new byte[0];
		}
	}
	
	public ApiResponse ftpRmsServerFile(FTPConnectionDetails ftpConDetails) {
		InputStream inputStream = null;
		ApiResponse apiResponse = new ApiResponse();
		try {
			FTPClient ftpClient = this.connectFtpServer(ftpConDetails);
			for (FTPFile file : ftpClient.listFiles(ftpConDetails.getFtpFilePath())) {
				inputStream = ftpClient.retrieveFileStream(ftpConDetails.getFtpFilePath() + "/" + file.getName());
				if (inputStream != null) {
					apiResponse = this.processRmsFile(inputStream, ftpConDetails, file);
				}
			}
			this.disconnectFtpServer(ftpClient);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return apiResponse;
	}
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private ApiResponse processRmsFile(InputStream inputStream, FTPConnectionDetails ftpConDetails, FTPFile file) {
		ApiResponse apiResponse = new ApiResponse();
		try {
			CsvParserSettings settings = new CsvParserSettings();
			settings.setHeaderExtractionEnabled(true);
			settings.setNullValue("");
			CsvParser parser = new CsvParser(settings);	
			List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
			List<ErpDataResponsePayload> erpResponseList = new ArrayList<ErpDataResponsePayload>();
			if (!parseAllRecords.isEmpty()) {
				parseAllRecords.forEach(record -> {
					ErpDataResponsePayload erpResponse = new ErpDataResponsePayload();
					erpResponse.setPortalId(record.getString(EtlGeneralConstants.PORTAL_ID));
					erpResponse.setOrderId(record.getString(EtlGeneralConstants.ORDER_NUMBER));
					erpResponse.setType(record.getString(EtlGeneralConstants.TYPE));
					erpResponse.setOrderStatus(record.getString(EtlGeneralConstants.ORDER_STATUS));
					erpResponseList.add(erpResponse);
				});
				apiResponse = classifiedService.updateSyncStatus(erpResponseList);
				ByteArrayInputStream processedInputStream = new ByteArrayInputStream(
						serializeProcessedData(erpResponseList));
				Boolean flag1 = fTPUtilService.uploadApiResponse(processedInputStream, ftpConDetails, file);
				Boolean flag = fTPUtilService.deleteSourceFile(ftpConDetails, file);
				if (flag) {
					apiResponse.setMessage("FTP Processing Info :: Success");
				} else {
					apiResponse.setMessage("FTP Processing Info :: Failed");
				}
				String ftpFilePath = ftpConDetails.getFtpFilePath() + "/" + file.getName();
				Map<String, String> docDetailsMap = new HashMap<String, String>();
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonString = objectMapper.writeValueAsString(erpResponseList);
				JSONArray jsonArray = new JSONArray(jsonString);
				JSONObject jsonObject = new JSONObject();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject element = jsonArray.getJSONObject(i);
					fTPUtilService.createFTPFilesLogs(element, docDetailsMap, ftpConDetails, "", ftpFilePath);
				}
				// Send payment details to sap
				List<String> orderIds = new ArrayList<String>();
				for (ErpDataResponsePayload erp : erpResponseList) {
					if (!"E".equalsIgnoreCase(erp.getType())) {
						orderIds.add(erp.getPortalOrderId());
					}
				}
//				List<Object[]> transactionDetails = clfPaymentResponseTrackingRepo.getTransactionDetails(orderIds);
				List<Object[]> transactionDetails = rmsPaymentResponseTrackingRepo.getRmsTransactionDetails(orderIds);
				if (!transactionDetails.isEmpty()) {
					boolean check = fTPUtilService.sendRmsTransactionDetailsToErp(transactionDetails);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}
}
