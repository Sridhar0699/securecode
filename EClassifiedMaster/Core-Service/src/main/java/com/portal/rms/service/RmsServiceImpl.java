package com.portal.rms.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.activation.FileDataSource;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.io.IOException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.portal.basedao.IBaseDao;
import com.portal.clf.entities.ClfEditions;
import com.portal.clf.entities.ClfOrderItemRates;
import com.portal.clf.entities.ClfOrderItems;
import com.portal.clf.entities.ClfOrders;
import com.portal.clf.entities.ClfPaymentResponseTracking;
import com.portal.clf.entities.ClfPublishDates;
import com.portal.clf.models.BillDeskPaymentResponseModel;
import com.portal.clf.models.ClassifiedConstants;
import com.portal.clf.models.ErpClassifieds;
import com.portal.common.models.GenericApiResponse;
import com.portal.common.models.GenericRequestHeaders;
import com.portal.common.service.CommonService;
import com.portal.constants.GeneralConstants;
import com.portal.doc.entity.Attachments;
import com.portal.erp.service.ErpService;
import com.portal.gd.entities.GdCity;
import com.portal.gd.entities.GdNumberSeries;
import com.portal.gd.entities.GdRmsNumberSeries;
import com.portal.gd.entities.GdUserTypes;
import com.portal.reports.utility.CommonUtils;
import com.portal.repository.ClfEditionsRepo;
import com.portal.repository.ClfOrderItemRatesRepo;
import com.portal.repository.ClfOrderItemsRepo;
import com.portal.repository.ClfOrdersRepo;
import com.portal.repository.ClfPublishDatesRepo;
import com.portal.repository.GdCityRepo;
import com.portal.repository.GdNumberSeriesRepo;
import com.portal.repository.GdSettingsDefinitionsRepository;
import com.portal.repository.GdUserTypesRepo;
import com.portal.repository.UmCustomersRepo;
import com.portal.repository.UmUsersRepository;
import com.portal.rms.entity.OtpVerification;
import com.portal.rms.entity.RmsOrderItems;
import com.portal.rms.entity.RmsPaymentsResponse;
import com.portal.rms.entity.RmsRates;
import com.portal.rms.model.ClientPo;
import com.portal.rms.model.CreateOrder;
import com.portal.rms.model.CustomerObjectDisplay;
import com.portal.rms.model.InsertionObjectDisplay;
import com.portal.rms.model.InsertionPo;
import com.portal.rms.model.OtpModel;
import com.portal.rms.model.PaymentDetails;
import com.portal.rms.model.PaymentObjectDisplay;
import com.portal.rms.model.RmsConstants;
import com.portal.rms.model.RmsCustomerDetails;
import com.portal.rms.model.RmsDashboardFilter;
import com.portal.rms.model.RmsKycAttatchments;
import com.portal.rms.model.RmsModel;
import com.portal.rms.model.RmsOrderList;
import com.portal.rms.model.RmsPaymentLinkModel;
import com.portal.rms.model.RmsRateModel;
import com.portal.rms.model.RmsViewDetails;
import com.portal.rms.repository.GdRmsNumberSeriesRepo;
import com.portal.rms.repository.OtpVerificationRepo;
import com.portal.rms.repository.RmsAttachmentsRepo;
import com.portal.rms.repository.RmsClfEditionsRepo;
import com.portal.rms.repository.RmsClfOrderItemsRatesRepo;
import com.portal.rms.repository.RmsClfOrderItemsRepo;
import com.portal.rms.repository.RmsClfOrdersRepo;
import com.portal.rms.repository.RmsClfPublishDates;
import com.portal.rms.repository.RmsOrderItemsRepo;
import com.portal.rms.repository.RmsPaymentsRepository;
import com.portal.rms.repository.RmsRatesRepo;
import com.portal.rms.repository.RmsUmCustomersRepo;
import com.portal.security.model.LoggedUser;
import com.portal.security.util.LoggedUserContext;
import com.portal.send.models.EmailsTo;
import com.portal.send.service.SendMessageService;
import com.portal.setting.dao.SettingDao;
import com.portal.setting.to.SettingTo;
import com.portal.setting.to.SettingTo.SettingType;
import com.portal.user.dao.UserDao;
import com.portal.user.entities.UmCustomers;
import com.portal.user.entities.UmUsers;
import com.portal.utils.HelperUtil;

@Transactional
@Service
public class RmsServiceImpl implements RmsService {

	public static final String DIR_PATH_DOCS = "/SEC/DOCS/";
	public static final String DIR_PATH_PDF_DOCS = "/SEC/PDFS/";

	@Autowired
	private RmsClfOrderItemsRepo rmsOrderItemsRepo;

	@Autowired(required = true)
	private IBaseDao baseDao;

	@Autowired
	private GdNumberSeriesRepo gdNumberSeriesRepo;

	@Autowired
	private RmsClfEditionsRepo rmsClfEditionsRepo;

	@Autowired
	private RmsClfPublishDates rmsClfPublishDates;

	@Autowired
	private RmsUmCustomersRepo rmsUmCustomersRepo;

	@Autowired
	private RmsClfOrdersRepo rmsClfOrdersRepo;

	@Autowired
	private RmsClfOrderItemsRatesRepo rmsClfOrderItemsRatesRepo;

	@Autowired
	private GdCityRepo gdCityRepo;

	@Autowired
	private RmsOrderItemsRepo orderItemsRepo;

	@Autowired
	private RmsPaymentsRepository paymentsRepository;

	@Autowired
	private GdSettingsDefinitionsRepository settingRepo;

	@Autowired
	private RmsRatesRepo rmsRatesRepo;

	@Autowired
	private SendMessageService sendMessageService;

	@Autowired
	private OtpVerificationRepo otpVerificationRepo;

	@Autowired
	private Environment prop;

	@Autowired
	private RmsAttachmentsRepo rmsAttachmentsRepo;

	@Autowired
	private GdRmsNumberSeriesRepo gdRmsNumberSeriesRepo;
	@Autowired
	private ErpService erpService;

	@Autowired
	private ClfPublishDatesRepo clfPublishDatesRepo;

	@Autowired
	private ClfEditionsRepo clfEditionsRepo;

	@Autowired
	private UmCustomersRepo umCustomersRepo;

	@Autowired
	private GdUserTypesRepo gdUserTypesRepo;

	@Autowired
	private UmUsersRepository umUsersRepository;

	@Autowired
	private UserDao userDao;

	@Autowired(required = true)
	private SendMessageService sendService;

	@Autowired(required = true)
	private SettingDao settingDao;

	@Autowired
	private LoggedUserContext userContext;

	public String getDIR_PATH_DOCS() {
		return prop.getProperty("ROOT_DIR") + DIR_PATH_DOCS;
	}

	public String getDIR_PATH_PDF_DOCS() {
		return prop.getProperty("ROOT_DIR") + DIR_PATH_PDF_DOCS;
	}

	@Autowired
	private ClfOrdersRepo clfOrdersRepo;
	
	@Autowired
	private ClfOrderItemRatesRepo clfOrderItemRatesRepo;
	
	@Autowired
	private ClfOrderItemsRepo clfOrderItemsRepo;
	
	@Autowired
	private CommonService commonService;
	
	private static final Logger logger = LogManager.getLogger(RmsServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public GenericApiResponse getDashboardCounts(LoggedUser loggedUser, RmsDashboardFilter payload) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fromDateStr = null;
		String toDateStr = null;
		Map<String, Integer> countsMap = new HashMap<>();
		countsMap.put(ClassifiedConstants.CLASSIFIED_APPROVAL_PENDING, 0);
		countsMap.put(ClassifiedConstants.CLASSIFIED_APPROVAL_APPROVED, 0);
		countsMap.put(ClassifiedConstants.CLASSIFIED_APPROVAL_REJECT, 0);
		String query = "select status,count(*) from clf_order_items itm inner join clf_orders co on co.order_id = itm.order_id inner join  um_customers uc on co.customer_id = uc.customer_id inner join rms_payments_response rps ON itm.order_id =rps.order_id";
		if (payload.getBookingCode() != null) {
			query = query + " and co.booking_unit = " + payload.getBookingCode() + "";
		}
		if (payload.getPublishedDate() != null && !payload.getPublishedDate().isEmpty()) {
			query = query
					+ " inner join clf_publish_dates cpd on cpd.item_id = itm.item_id and to_char(cpd.publish_date,'YYYY-MM-dd') = '"
					+ payload.getPublishedDate() + "'";
		}
		query = query + " where  itm.mark_as_delete = false and co.order_type=1 and co.order_status='OPEN' and itm.created_by ="
				+ loggedUser.getUserId();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -30); // Subtract 30 days from current date
		Date thirtyDaysAgo = calendar.getTime();
		fromDateStr = sdf.format(thirtyDaysAgo);
		toDateStr = sdf.format(new Date()); // Current date
		query += " AND to_char(rps.created_ts,'YYYY-MM-DD') >= '" + fromDateStr
				+ "' AND to_char(rps.created_ts,'YYYY-MM-DD') <= '" + toDateStr + "'";
		query += " AND rps.mark_as_delete=false group by status";
		List<Object[]> dashboardStatus = (List<Object[]>) baseDao.findBySQLQueryWithoutParams(query);
		if (dashboardStatus != null && !dashboardStatus.isEmpty()) {
			for (Object[] obj : dashboardStatus) {
				countsMap.put((String) obj[0], ((BigInteger) obj[1]).intValue());
			}
		}
		if (!countsMap.isEmpty()) {
			genericApiResponse.setStatus(0);
		}
		genericApiResponse.setData(countsMap);
		genericApiResponse.setMessage("Dash Board Counts");
		return genericApiResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GenericApiResponse getRmsClassifiedList(LoggedUser loggedUser, RmsDashboardFilter payload) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String fromDateStr = null;
			String toDateStr = null;
			Date fromDate = payload.getFromDate();
			Date toDate = payload.getToDate();
			String query = "select itm.created_ts ,itm.item_id ,itm.ad_id ,uc.city,COUNT(*) OVER () AS total_count,uc.client_code,bu.booking_description from clf_order_items itm inner join clf_orders co on co.order_id = itm.order_id inner join  um_customers uc on co.customer_id = uc.customer_id left join booking_units bu ON uc.city = CAST(bu.booking_code AS VARCHAR) inner join rms_payments_response rps ON itm.order_id =rps.order_id where itm.created_by = "
					+ loggedUser.getUserId()
					+ " and itm.mark_As_delete = false and co.order_type=1 and co.order_status='OPEN'";
			genericApiResponse.setStatus(1);
			LinkedHashMap<String, Object> orderObject = new LinkedHashMap<String, Object>();
			List<Object[]> classifiedList = new ArrayList<Object[]>();
			if (payload.getBookingCode() != null) {
				query = query + " and co.booking_unit = " + payload.getBookingCode();
			}
			if (payload.getPublishedDate() != null && !payload.getPublishedDate().isEmpty()) {
				query = query + " and to_char(itm.created_ts,'YYYY-MM-DD') = '" + payload.getPublishedDate() + "'";
			}

			if (fromDate != null && toDate != null) {
				if (fromDate.before(toDate)) {
					fromDateStr = sdf.format(fromDate);
					toDateStr = sdf.format(toDate);
				} else {
					genericApiResponse.setStatus(1);
					genericApiResponse.setMessage("To Date should be greater than From Date");
					return genericApiResponse;
				}
			} else if (fromDate != null) {
				Calendar calendar = Calendar.getInstance();
				fromDateStr = sdf.format(fromDate);
				calendar.setTime(fromDate);
				calendar.add(Calendar.DATE, 30);
				toDateStr = sdf.format(calendar.getTime());
			} else if (toDate != null) {
				Calendar calendar = Calendar.getInstance();
				toDateStr = sdf.format(toDate);
				calendar.setTime(toDate);
				calendar.add(Calendar.DATE, -30);
				fromDateStr = sdf.format(calendar.getTime());
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -30); // Subtract 30 days from current date
				Date thirtyDaysAgo = calendar.getTime();
				fromDateStr = sdf.format(thirtyDaysAgo);
				toDateStr = sdf.format(new Date()); // Current date
			}

			query += " AND to_char(rps.created_ts,'YYYY-MM-DD') >= '" + fromDateStr
					+ "' AND to_char(rps.created_ts,'YYYY-MM-DD') <= '" + toDateStr
					+ "' AND rps.mark_as_delete=false  ORDER BY itm.ad_id DESC ";

			if (payload.getPageNumber() != null && payload.getPageSize() != null) {
				int skip = ((Integer) payload.getPageNumber() - 1) * ((Integer) payload.getPageSize());
				query = query + " LIMIT " + payload.getPageSize() + " OFFSET " + skip;
			}

			classifiedList = (List<Object[]>) baseDao.findBySQLQueryWithoutParams(query);

			LinkedHashMap<String, RmsOrderList> classifiedsMap = new LinkedHashMap<String, RmsOrderList>();
			List<String> itemIds = new ArrayList<String>();
			List<Integer> cityIds = new ArrayList<Integer>();
			BigInteger totalCounts = null;
			RmsOrderList classifieds = new RmsOrderList();
			for (Object[] obj : classifiedList) {
				classifieds.setCity((String) obj[6]);
				classifieds.setOrderDate(CommonUtils.dateFormatter((Date) obj[0], "yyyy-MM-dd"));
				classifieds.setOrderNo((String) obj[2]);
				classifieds.setItemId((String) obj[1]);
				classifieds.setClientCode((String) obj[5]);
				itemIds.add((String) obj[1]);
				cityIds.add(Integer.parseInt((String) obj[3]));
				classifiedsMap.put((String) obj[1], classifieds);
				totalCounts = ((BigInteger) obj[4]);
			}
			List<Object[]> publishDatesList = rmsClfPublishDates.getPublishDatesForErpData(itemIds);
			for (Object[] clObj : publishDatesList) {
				if (classifiedsMap.containsKey((String) clObj[0])) {
					if (classifiedsMap.get((String) clObj[0]).getPublishDates() != null) {
						classifiedsMap.get((String) clObj[0]).getPublishDates()
								.add(CommonUtils.dateFormatter((Date) clObj[1], "yyyy-MM-dd"));
					} else {
						List<String> publishDates = new ArrayList<>();
						publishDates.add(CommonUtils.dateFormatter((Date) clObj[1], "yyyy-MM-dd"));
						RmsOrderList classified = classifiedsMap.get((String) clObj[0]);
						classified.setPublishDates(publishDates);
						classifiedsMap.put((String) clObj[0], classified);
					}
				}
			}
			if (!cityIds.isEmpty()) {
				List<GdCity> gdCityDetails = gdCityRepo.getCityDetails(cityIds);
				if (!gdCityDetails.isEmpty()) {
					classifiedsMap.entrySet().forEach(erpData -> {
						Optional<GdCity> gd = gdCityDetails.stream()
								.filter(f -> String.valueOf(f.getId()).equals(erpData.getValue().getCity()))
								.findFirst();
						if (gd.isPresent()) {
							GdCity gdCity = gd.get();
							erpData.getValue().setCity(gdCity.getCity());
						}
					});
				}
			}

			if (!classifiedsMap.isEmpty()) {
				genericApiResponse.setStatus(0);
				orderObject.put("ordersList", classifiedsMap.values());
				orderObject.put("totalCount", totalCounts);
				genericApiResponse.setData(orderObject);
			} else {
				genericApiResponse.setMessage("No Rms orders found.");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return genericApiResponse;

	}

	@SuppressWarnings("unchecked")
	@Override
	public GenericApiResponse getRmsClassifiedsByAdId(LoggedUser loggedUser, @NotNull String adId) {
		GenericApiResponse apiResponse = new GenericApiResponse();
		try {
			List<Object[]> classifiedList = new ArrayList<Object[]>();
			String query = "select uc.mobile_no,itm.created_ts,coir.total_amount,itm.status,cp.payment_status AS cp_payment_status,itm.download_status ,itm.clf_content,itm.item_id ,itm.order_id, co.payment_status AS co_payment_status, itm.scheme AS itm_scheme, gcs.scheme AS gcs_scheme,itm.ad_id ,bu.booking_description,uc.gst_no,uc.customer_name,uc.email_id,uc.address_1,uc.address_2,uc.address_3,uc.pin_code,gs.state,rpr.bank_or_upi,cp.payment_method_type,itm.created_by,itm.changed_by,itm.changed_ts,uc.customer_id,co.booking_unit,coir.rate,roi.no_of_insertions,roi.size_width,roi.size_height,roi.space_width,roi.space_height,roi.page_position,itm.classified_ads_sub_type,gcast.ads_sub_type,uc.client_code,gcg.classified_group,gcsg.classified_sub_group,gcc.classified_child_group,gt.edition_type,grff.size,grft.format_type,grpp.page_name,grmd.discount,gpm.payment_method,gpm2.payment_mode,rpr.signature_id,rpr.cash_receipt_no,rpr.other_details,coir.cgst,coir.sgst,coir.igst,coir.gst_total,coir.total_discount,roi.category_discount,roi.additional_discount,roi.surcharge_rate,uc.city AS city_code,uc.state as state_code,rpr.bank_ref_id,rpr.payment_mode as pMode,rpr.payment_method as pMethod,gct.customer_type,uc.customer_type_id,itm.category_group,itm.sub_group,itm.child_group,co.edition_type as editionId,roi.fixed_format as fixedFormatId,roi.format_type as formatTypeId,roi.page_number as pageNumberId,roi.multi_discount as multiDiscountId,gpd.amount as categoryAmount,gpd.positioning_desc,roi.multi_discount_amount,roi.surcharge_amount,roi.additional_discount_amount,coir.cgst_value,coir.sgst_value,coir.igst_value,coir.amount as coirAmount,coir.rate_per_square_cms,roi.category_discount_amount from clf_order_items itm inner join clf_order_item_rates coir on itm.item_id = coir.item_id inner join clf_orders co on co.order_id = itm.order_id left join clf_payment_response_tracking cp on cp.sec_order_id = co.order_id inner join um_customers uc on co.customer_id = uc.customer_id  inner join gd_rms_schemes gcs on itm.scheme =gcs.id  inner join rms_order_items roi on itm.item_id=roi.item_id inner join gd_classified_ads_sub_types gcast on itm.classified_ads_sub_type =  gcast.id inner join gd_classified_group gcg on itm.category_group=gcg.id inner join gd_classified_sub_group gcsg on itm.sub_group=gcsg.id inner join gd_classified_child_group gcc on itm.child_group=gcc.id left join gd_rms_edition_type gt on co.edition_type=gt.id left join gd_rms_fixed_formats grff on roi.fixed_format=grff.id left join gd_rms_multi_discount grmd on roi.multi_discount=grmd.id  left join gd_rms_format_types grft on roi.format_type = grft.id left join gd_rms_page_positions grpp on roi.page_number=grpp.id inner join rms_payments_response rpr on itm.item_id=rpr.item_id left join gd_payment_method gpm on rpr.payment_method= gpm.id left join gd_payment_mode gpm2 on rpr.payment_mode = gpm2.id left join booking_units bu ON uc.city = CAST(bu.booking_code AS VARCHAR) left join gd_state gs ON uc.state = gs.state_code left join gd_customer_types gct on uc.customer_type_id=gct.id left join gd_rms_positioning_discount gpd on roi.category_discount=gpd.id where itm.created_by = "
					+ loggedUser.getUserId() + " and itm.mark_As_delete = false and co.order_type=1  and itm.ad_id = '"
					+ adId + "' ";
			classifiedList = (List<Object[]>) baseDao.findBySQLQueryWithoutParams(query);
			if (classifiedList == null || classifiedList.size() == 0) {
				apiResponse.setStatus(1);
				apiResponse.setMessage("No Rms orders found.");
				return apiResponse;
			}

			List<String> itemIds = new ArrayList<String>();
			RmsViewDetails classifieds = null;
			InsertionObjectDisplay insertionObjectDisplay = new InsertionObjectDisplay();
			insertionObjectDisplay.setEditions(new ArrayList<String>());
			insertionObjectDisplay.setPublishDates(new ArrayList<String>());
			CustomerObjectDisplay customerObjectDisplay = new CustomerObjectDisplay();
			PaymentObjectDisplay paymentObjectDisplay = new PaymentObjectDisplay();

			for (Object[] obj : classifiedList) {
				customerObjectDisplay.setMobileNo((String) obj[0]);
				insertionObjectDisplay.setContent((String) obj[6]);
				insertionObjectDisplay.setItemId((String) obj[7]);
				float floatValue = (Float) obj[2];
				BigDecimal paidAmount = new BigDecimal(floatValue).setScale(2, RoundingMode.HALF_UP);
				insertionObjectDisplay.setPaidAmount(paidAmount);
//				classifieds.setPaidAmount(new BigDecimal((Float) obj[2]));
				customerObjectDisplay.setCityDesc((String) obj[13]);
				customerObjectDisplay.setGstNo((String) obj[14]);
				customerObjectDisplay.setClientName((String) obj[15]);
				customerObjectDisplay.setEmailId((String) obj[16]);
				customerObjectDisplay.setAddress1((String) obj[17]);
				customerObjectDisplay.setPinCode((String) obj[20]);
				customerObjectDisplay.setStateDesc((String) obj[21]);
				paymentObjectDisplay.setReferenceId((String) obj[62]);
				paymentObjectDisplay.setBankOrUpi((String) obj[22]);
				insertionObjectDisplay.setFixedRate(new BigDecimal((Float) obj[29]));
				insertionObjectDisplay.setClassifiedAdsSubTypeStr((String) obj[37]);
				insertionObjectDisplay.setSchemeStr((String) obj[11]);
				customerObjectDisplay.setCustomerDetails((String) obj[65]);
				float spaceWidth = (obj[33] == null) ? 0.0f : (float) obj[33];
				float spaceHeight = (obj[34] == null) ? 0.0f : (float) obj[34];
				double formattedSpaceWidth = HelperUtil.parseDoubleValue((spaceWidth));
				String spaceW = String.valueOf(formattedSpaceWidth);
				double formattedSpaceHeight = HelperUtil.parseDoubleValue((spaceHeight));
				String spaceH = String.valueOf(formattedSpaceHeight);
				insertionObjectDisplay.setSpaceWidth(spaceW);
				insertionObjectDisplay.setSpaceHeight(spaceH);
				insertionObjectDisplay.setPagePosition((Integer) obj[35]);
				customerObjectDisplay.setClientCode((String) obj[38]);
				insertionObjectDisplay.setClassifiedGroupDesc((String) obj[39]);
				insertionObjectDisplay.setClassifiedSubgroupDesc((String) obj[40]);
				insertionObjectDisplay.setClassifiedChildgroupDesc((String) obj[41]);
				insertionObjectDisplay.setEditionTypeDesc((String) obj[42]);
				insertionObjectDisplay.setFixedFormatsDesc((String) obj[43]);
				insertionObjectDisplay.setFormatTypeDesc((String) obj[44]);
				insertionObjectDisplay.setPageNumberDesc((String) obj[45]);
				paymentObjectDisplay.setPaymentMethodDesc((String) obj[47]);
				paymentObjectDisplay.setPaymentModeDesc((String) obj[48]);
				paymentObjectDisplay.setSignatureId((String) obj[49]);
				paymentObjectDisplay.setCashReceiptNo((String) obj[50]);
				paymentObjectDisplay.setOtherDetails((String) obj[51]);
				float cgst = (obj[52] == null) ? 0.0f : (float) obj[52];
				float sgst = (obj[53] == null) ? 0.0f : (float) obj[53];
				float igst = (obj[54] == null) ? 0.0f : (float) obj[54];
				float additional = (obj[58] == null) ? 0.0f : (float) obj[58];
				float surChargeRate = (obj[59] == null) ? 0.0f : (float) obj[59];
				float multiDiscount = (obj[74] == null) ? 0.0f : (float) obj[74];
				float amount = (obj[75] == null) ? 0.0f : (float) obj[75];
				float multiDA = (obj[77] == null) ? 0.0f : (float) obj[77];
				float surchargeA = (obj[78] == null) ? 0.0f : (float) obj[78];
				float addDA = (obj[79] == null) ? 0.0f : (float) obj[79];
				float cgstVal = (obj[80] == null) ? 0.0f : (float) obj[80];
				float sgstVal = (obj[81] == null) ? 0.0f : (float) obj[81];
				float igstVal = (obj[82] == null) ? 0.0f : (float) obj[82];
				float totalAmount = (obj[2] == null) ? 0.0f : (float) obj[2];
				float amountFinal = (obj[83] == null) ? 0.0f : (float) obj[83];
				float ratePerSC = (obj[84] == null) ? 0.0f : (float) obj[84];
				float categoryDiscAmount = (obj[85] == null) ? 0.0f : (float) obj[85];
				Double cGst = HelperUtil.parseDoubleValue((cgst));
				String cgstValAsString = (cGst == null || cGst == 0.0) ? "" : String.valueOf(cGst);
				Double sGst = HelperUtil.parseDoubleValue((sgst));
				String sgstValAsString = (sGst == null || sGst == 0.0) ? "" : String.valueOf(sGst);
				Double iGst = HelperUtil.parseDoubleValue((igst));
				String igstValAsString = (iGst == null || iGst == 0.0) ? "" : String.valueOf(iGst);
				Double additionalDiscount = HelperUtil.parseDoubleValue((additional));
				String adAsString = (additionalDiscount == null || additionalDiscount == 0.0) ? ""
						: String.valueOf(additionalDiscount);
				Double surCharge = HelperUtil.parseDoubleValue((surChargeRate));
				String surchargeValAsString = (surCharge == null || surCharge == 0.0) ? "" : String.valueOf(surCharge);
				Double multiDisc = HelperUtil.parseDoubleValue((multiDiscount));
				String multiDiscString = (multiDisc == null || multiDisc == 0.0) ? "" : String.valueOf(multiDisc);
				Double amount1 = HelperUtil.parseDoubleValue((amount));
				String amountStr = (amount1 == null || amount1 == 0.0) ? "" : String.valueOf(amount1);
				Double multiDA1 = HelperUtil.parseDoubleValue((multiDA));
				String multiString = (multiDA1 == null || multiDA1 == 0.0) ? "" : String.valueOf(multiDA1);
				Double surchargeA1 = HelperUtil.parseDoubleValue((surchargeA));
				String surchargeA2 = (surchargeA1 == null || surchargeA1 == 0.0) ? "" : String.valueOf(surchargeA1);
				Double addDA1 = HelperUtil.parseDoubleValue((addDA));
				String addDA2 = (addDA1 == null || addDA1 == 0.0) ? "" : String.valueOf(addDA1);
				Double cgstVal1 = HelperUtil.parseDoubleValue((cgstVal));
				String cgstVal2 = (cgstVal1 == null || cgstVal1 == 0.0) ? "" : String.valueOf(cgstVal1);
				Double sgstVal1 = HelperUtil.parseDoubleValue((sgstVal));
				String sgstVal2 = (sgstVal1 == null || sgstVal1 == 0.0) ? "" : String.valueOf(sgstVal1);
				Double igstVal1 = HelperUtil.parseDoubleValue((igstVal));
				String igstVal2 = (igstVal1 == null || igstVal1 == 0.0) ? "" : String.valueOf(igstVal1);
				Double totalAmount1 = HelperUtil.parseDoubleValue((totalAmount));
				String totalAmountStr = (totalAmount1 == null || totalAmount1 == 0.0) ? ""
						: String.valueOf(totalAmount1);
				Double amountFinal1 = HelperUtil.parseDoubleValue((amountFinal));
				String finalAmount = (amountFinal1 == null || amountFinal1 == 0.0) ? "" : String.valueOf(amountFinal1);
				Double ratePerSC1 = HelperUtil.parseDoubleValue((ratePerSC));
				String ratePers = (ratePerSC1 == null || ratePerSC1 == 0.0) ? "" : String.valueOf(ratePerSC1);
				Double categoryDiscAmount1 = HelperUtil.parseDoubleValue((categoryDiscAmount));
				String categoryDiscAmount2 = (categoryDiscAmount1 == null || categoryDiscAmount1 == 0.0) ? ""
						: String.valueOf(categoryDiscAmount1);

				insertionObjectDisplay.setCgst(cgstValAsString);
				insertionObjectDisplay.setSgst(sgstValAsString);
				insertionObjectDisplay.setIgst(igstValAsString);
				insertionObjectDisplay.setCategoryDiscountId((Integer) obj[57]);
				insertionObjectDisplay.setAdditionalDiscount(adAsString);
				insertionObjectDisplay.setSurchargeRate(surchargeValAsString);
				customerObjectDisplay.setCityCode((String) obj[60]);
				customerObjectDisplay.setStateCode((String) obj[61]);
				insertionObjectDisplay.setBookingCode((Integer) obj[28]);
				paymentObjectDisplay.setPaymentMode((Short) obj[63]);
				paymentObjectDisplay.setPaymentMethod((Short) obj[64]);
				insertionObjectDisplay.setCustomerTypeId((Integer) obj[66]);
				insertionObjectDisplay.setCategoryGroup((Integer) obj[67]);
				insertionObjectDisplay.setCategorySubGroup((Integer) obj[68]);
				insertionObjectDisplay.setCategoryChildGroup((Integer) obj[69]);
				insertionObjectDisplay.setEditionType((Integer) obj[70]);
				insertionObjectDisplay.setFixedFormat((Integer) obj[71]);
				insertionObjectDisplay.setFormatType((Integer) obj[72]);
				insertionObjectDisplay.setPageNumber((Integer) obj[73]);
				insertionObjectDisplay.setMultiDiscount(multiDiscString);
				insertionObjectDisplay.setCategoryDiscount(amountStr);
				insertionObjectDisplay.setPositioningDesc((String) obj[76]);
				insertionObjectDisplay.setMultiDiscountAmount(multiString);
				insertionObjectDisplay.setSurchargeAmount(surchargeA2);
				insertionObjectDisplay.setAdditionalDiscountAmount(addDA2);
				insertionObjectDisplay.setScheme((Integer) obj[10]);
				insertionObjectDisplay.setClassifiedAdsSubType((Integer) obj[36]);
				insertionObjectDisplay.setCgstValue(cgstVal2);
				insertionObjectDisplay.setSgstValue(sgstVal2);
				insertionObjectDisplay.setIgstValue(igstVal2);
				insertionObjectDisplay.setTotalAmount(totalAmountStr);
				insertionObjectDisplay.setAmount(finalAmount);
				insertionObjectDisplay.setRatePerSquareCms(ratePers);
				insertionObjectDisplay.setCategoryDiscountAmount(categoryDiscAmount2);
				itemIds.add((String) obj[7]);
			}

			if (itemIds != null && !itemIds.isEmpty()) {
				List<Object[]> editionsList = rmsClfEditionsRepo.getEditionIdAndNameOnItemId(itemIds);
				for (Object[] clObj : editionsList) {
					insertionObjectDisplay.getEditions().add((String) clObj[2]);
				}
				List<Object[]> publishDatesList = rmsClfPublishDates.getPublishDatesForErpData(itemIds);
				for (Object[] clObj : publishDatesList) {
					insertionObjectDisplay.getPublishDates()
							.add(CommonUtils.dateFormatter((Date) clObj[1], "yyyy-MM-dd"));
				}
			}
			classifieds = new RmsViewDetails();
			classifieds.setInsertionObjectDisplay(insertionObjectDisplay);
			classifieds.setCustomerObjectDisplay(customerObjectDisplay);
			classifieds.setPaymentObjectDisplay(paymentObjectDisplay);

			apiResponse.setStatus(0);
			apiResponse.setData(classifieds);
		} catch (NumberFormatException e) {
			logger.error("Error while getting  order by adID:" + e.getMessage());
			apiResponse.setStatus(1);
			apiResponse.setErrorcode("GEN_002");
		}
		return apiResponse;

	}

	@Override
	public GenericApiResponse addRmsClassifiedItemToCart(CreateOrder payload, LoggedUser loggedUser) {

		GenericApiResponse response = new GenericApiResponse();
		try {
			response.setStatus(0);
			response.setMessage("Successfully added");
			Map<String, Object> details = new HashMap<String, Object>();
			ClfOrders clfOrders = new ClfOrders();

			clfOrders = getOpenCartDetails(loggedUser);
			List<ClfOrderItems> itemDetailsOnOrderId = new ArrayList<ClfOrderItems>();
			if (clfOrders != null) {
				itemDetailsOnOrderId = rmsOrderItemsRepo.getOpenOrderItems(clfOrders.getOrderId());
				if (itemDetailsOnOrderId.size() > 0) {
					details.put("itemId", itemDetailsOnOrderId.get(0).getItemId());
				}
			}

			if ("CREATE".equalsIgnoreCase(payload.getAction())) {
				if (clfOrders == null) {
					clfOrders = new ClfOrders();
					clfOrders.setOrderId(UUID.randomUUID().toString());
//					clfOrders.setCustomerId(loggedUser.getCustomerId() == null ? "" : null);
					clfOrders.setUserTypeId(loggedUser.getUserTypeId());
					clfOrders.setOrderStatus(RmsConstants.ORDER_OPEN);
					clfOrders.setPaymentStatus(RmsConstants.ORDER_PAYMENT_PENDING);
					clfOrders.setCreatedBy(loggedUser.getUserId());
					clfOrders.setCreatedTs(new Date());
					clfOrders.setMarkAsDelete(false);
					clfOrders.setOrderType(01);
					clfOrders.setBookingUnit(payload.getBookingOffice());
					rmsClfOrdersRepo.save(clfOrders);
				} 
				//else {
//					ClientPo customerDetail = this.getCustomerDetail(clfOrders.getCustomerId());
//					details.put("customer", customerDetail);
//					if (itemDetailsOnOrderId.size() > 0) {
//						InsertionPo insertionDetails = this.getInsertionDetails(clfOrders.getOrderId());
//						details.put("insertion", insertionDetails);
//					}
				//}
				details.put("orderId", clfOrders.getOrderId());
				response.setData(details);

			}
			if ("CUSTOMER".equalsIgnoreCase(payload.getAction())) {
				ClientPo populateRmsCustomerDetails = populateRmsCustomerDetails(payload.getClientPo(), loggedUser);
				if (populateRmsCustomerDetails != null && populateRmsCustomerDetails.getCustomerId() == null) {
					response.setStatus(1);
					response.setMessage("Customer Details Not found");
					return response;
				}
				clfOrders.setCustomerId(populateRmsCustomerDetails.getCustomerId() == null ? loggedUser.getCustomerId()
						: populateRmsCustomerDetails.getCustomerId());
				details.put("orderId", clfOrders.getOrderId());
				response.setData(details);

			}
			ClfOrderItems clfOrderItems = null;
			if (itemDetailsOnOrderId.size() > 0) {
				clfOrderItems = itemDetailsOnOrderId.get(0);
			}

			if ("INSERTION".equalsIgnoreCase(payload.getAction())) {
				if (clfOrderItems != null) {
					payload.setItemId(clfOrderItems.getItemId());
					this.removeOldCardDet(payload, loggedUser);
				}
				InsertionPo insertionPo = payload.getInsertionPo();
				if (insertionPo != null) {
					if (clfOrderItems == null) {
						clfOrderItems = new ClfOrderItems();
						clfOrderItems.setItemId(UUID.randomUUID().toString());
						clfOrderItems.setCreatedBy(loggedUser.getUserId());
						clfOrderItems.setCreatedTs(new Date());
					} else {
						clfOrderItems.setChangedBy(loggedUser.getUserId());
						clfOrderItems.setChangedTs(new Date());
					}
					clfOrderItems.setClfOrders(clfOrders);
					clfOrderItems.setClassifiedAdsSubType(insertionPo.getClassifiedAdsSubtype());
					clfOrderItems.setScheme(insertionPo.getScheme());
					clfOrderItems.setClfContent(insertionPo.getCaption());
					clfOrderItems.setMarkAsDelete(false);
					clfOrderItems.setDownloadStatus(false);
					clfOrderItems.setStatus(RmsConstants.RMS_APPROVAL_PENDING);
					if (clfOrderItems.getAdId() == null) {
						String adId = this.generateRmsSeries(clfOrders.getBookingUnit());
						if (adId != null) {
							clfOrderItems.setAdId(adId);
						}
					}
					clfOrderItems.setGroup(insertionPo.getProductGroup());
					clfOrderItems.setSubGroup(insertionPo.getProductSubGroup());
					clfOrderItems.setChildGroup(insertionPo.getProductChildGroup());
					clfOrderItems.setClassifiedType(01);
					clfOrderItems.setClassifiedAdsType(02);

					baseDao.saveOrUpdate(clfOrderItems);

					for (String pdates : insertionPo.getPublishDates()) {
						ClfPublishDates clfPublishDate = new ClfPublishDates();
						clfPublishDate.setPublishDateId(UUID.randomUUID().toString());
						clfPublishDate.setClfOrderItems(clfOrderItems);
						clfPublishDate.setPublishDate(dateFormatter(pdates));
						clfPublishDate.setCreatedBy(loggedUser.getUserId());
						clfPublishDate.setCreatedTs(new Date());
						clfPublishDate.setMarkAsDelete(false);
						clfPublishDate.setOrderId(clfOrders.getOrderId());
						clfPublishDate.setDownloadStatus(false);
						baseDao.saveOrUpdate(clfPublishDate);

					}

					for (Integer editionId : insertionPo.getEditions()) {
						ClfEditions clfEditions = new ClfEditions();
						clfEditions.setOrderEditionId(UUID.randomUUID().toString());
						clfEditions.setEditionId(editionId);
						clfEditions.setClfOrderItems(clfOrderItems);
						clfEditions.setCreatedBy(loggedUser.getUserId());
						clfEditions.setCreatedTs(new Date());
						clfEditions.setMarkAsDelete(false);
						clfEditions.setOrderId(clfOrders.getOrderId());
						baseDao.saveOrUpdate(clfEditions);
					}
					List<RmsOrderItems> rmsOrderItem = orderItemsRepo
							.getRmsOrderItemsOnItemId(clfOrderItems.getItemId());
					if (rmsOrderItem.size() == 0) {
						RmsOrderItems newRmsOrderItem = new RmsOrderItems();
						newRmsOrderItem.setRmsItemId(UUID.randomUUID().toString());
						newRmsOrderItem.setItemId(clfOrderItems.getItemId());
						newRmsOrderItem.setOrderId(clfOrders.getOrderId());
						newRmsOrderItem.setSpaceWidth(
								(insertionPo.getSpaceWidth() != null && insertionPo.getSpaceWidth().length() > 0)
										? new BigDecimal(insertionPo.getSpaceWidth()).doubleValue()
										: null);
						newRmsOrderItem.setSpaceHeight(
								(insertionPo.getSpaceHeight() != null && insertionPo.getSpaceHeight().length() > 0)
										? new BigDecimal(insertionPo.getSpaceHeight()).doubleValue()
										: null);
						newRmsOrderItem.setPagePosition(insertionPo.getPagePosition());
						newRmsOrderItem.setCreatedTs(new Date());
						newRmsOrderItem.setCreatedBy(loggedUser.getUserId());
						newRmsOrderItem.setMarkAsDelete(false);
						newRmsOrderItem.setChangedBy(loggedUser.getUserId());
						newRmsOrderItem.setChangedTs(new Date());
						newRmsOrderItem.setFormatType(insertionPo.getFormatType());
						newRmsOrderItem.setFixedFormat(insertionPo.getFixedFormat());
						newRmsOrderItem.setPageNumber(insertionPo.getPageNumber());
						newRmsOrderItem.setCategoryDiscount(Integer.parseInt(insertionPo.getCategoryDiscount()));
						newRmsOrderItem.setMultiDiscount(
								(insertionPo.getMultiDiscount() != null && insertionPo.getMultiDiscount().length() > 0)
										? new BigDecimal(insertionPo.getMultiDiscount()).doubleValue()
										: null);
						newRmsOrderItem.setAdditionalDiscount((insertionPo.getAdditionalDiscount() != null
								&& insertionPo.getAdditionalDiscount().length() > 0)
										? new BigDecimal(insertionPo.getAdditionalDiscount()).doubleValue()
										: null);
						newRmsOrderItem.setSurchargeRate(
								(insertionPo.getSurchargeRate() != null && insertionPo.getSurchargeRate().length() > 0)
										? new BigDecimal(insertionPo.getSurchargeRate()).doubleValue()
										: null);
						newRmsOrderItem.setAdditionalDiscountAmount((insertionPo.getAdditionalDiscountAmount() != null
								&& insertionPo.getAdditionalDiscountAmount().length() > 0)
										? new BigDecimal(insertionPo.getAdditionalDiscountAmount()).doubleValue()
										: null);
						newRmsOrderItem.setSurchargeAmount((insertionPo.getSurchargeAmount() != null
								&& insertionPo.getSurchargeAmount().length() > 0)
										? new BigDecimal(insertionPo.getSurchargeAmount()).doubleValue()
										: null);
						newRmsOrderItem.setMultiDiscountAmount((insertionPo.getMultiDiscountAmount() != null
								&& insertionPo.getMultiDiscountAmount().length() > 0)
										? new BigDecimal(insertionPo.getMultiDiscountAmount()).doubleValue()
										: null);
						newRmsOrderItem.setCategoryDiscountAmount((insertionPo.getCategoryDiscountAmount() != null
								&& insertionPo.getCategoryDiscountAmount().length() > 0)
										? new BigDecimal(insertionPo.getCategoryDiscountAmount()).doubleValue()
										: null);
						newRmsOrderItem.setNoOfInsertions(insertionPo.getNoOfInsertions());
						baseDao.saveOrUpdate(newRmsOrderItem);
					} else {
						for (RmsOrderItems rmsOrderItems : rmsOrderItem) {
							rmsOrderItems.setItemId(clfOrderItems.getItemId());
							rmsOrderItems.setOrderId(clfOrders.getOrderId());
							rmsOrderItems.setSpaceWidth(
									(insertionPo.getSpaceWidth() != null && insertionPo.getSpaceWidth().length() > 0)
											? new BigDecimal(insertionPo.getSpaceWidth()).doubleValue()
											: null);
							rmsOrderItems.setSpaceHeight(
									(insertionPo.getSpaceHeight() != null && insertionPo.getSpaceHeight().length() > 0)
											? new BigDecimal(insertionPo.getSpaceHeight()).doubleValue()
											: null);
							rmsOrderItems.setPagePosition(insertionPo.getPagePosition());
							rmsOrderItems.setChangedTs(new Date());
							rmsOrderItems.setChangedBy(loggedUser.getUserId());
							rmsOrderItems.setMarkAsDelete(false);
							rmsOrderItems.setChangedBy(loggedUser.getUserId());
							rmsOrderItems.setChangedTs(new Date());
							rmsOrderItems.setFormatType(insertionPo.getFormatType());
							rmsOrderItems.setFixedFormat(insertionPo.getFixedFormat());
							rmsOrderItems.setPageNumber(insertionPo.getPageNumber());
							rmsOrderItems.setCategoryDiscount(Integer.parseInt(insertionPo.getCategoryDiscount()));
							rmsOrderItems.setMultiDiscount((insertionPo.getMultiDiscount() != null
									&& insertionPo.getMultiDiscount().length() > 0)
											? new BigDecimal(insertionPo.getMultiDiscount()).doubleValue()
											: null);
							rmsOrderItems.setAdditionalDiscount((insertionPo.getAdditionalDiscount() != null
									&& insertionPo.getAdditionalDiscount().length() > 0)
											? new BigDecimal(insertionPo.getAdditionalDiscount()).doubleValue()
											: null);
							rmsOrderItems.setSurchargeRate((insertionPo.getSurchargeRate() != null
									&& insertionPo.getSurchargeRate().length() > 0)
											? new BigDecimal(insertionPo.getSurchargeRate()).doubleValue()
											: null);
							rmsOrderItems.setAdditionalDiscountAmount((insertionPo.getAdditionalDiscountAmount() != null
									&& insertionPo.getAdditionalDiscountAmount().length() > 0)
											? new BigDecimal(insertionPo.getAdditionalDiscountAmount()).doubleValue()
											: null);
							rmsOrderItems.setSurchargeAmount((insertionPo.getSurchargeAmount() != null
									&& insertionPo.getSurchargeAmount().length() > 0)
											? new BigDecimal(insertionPo.getSurchargeAmount()).doubleValue()
											: null);
							rmsOrderItems.setMultiDiscountAmount((insertionPo.getMultiDiscountAmount() != null
									&& insertionPo.getMultiDiscountAmount().length() > 0)
											? new BigDecimal(insertionPo.getMultiDiscountAmount()).doubleValue()
											: null);
							rmsOrderItems.setCategoryDiscountAmount((insertionPo.getCategoryDiscountAmount() != null
									&& insertionPo.getCategoryDiscountAmount().length() > 0)
											? new BigDecimal(insertionPo.getCategoryDiscountAmount()).doubleValue()
											: null);
							rmsOrderItems.setNoOfInsertions(insertionPo.getNoOfInsertions());
							baseDao.saveOrUpdate(rmsOrderItems);
						}
					}

					List<ClfOrderItemRates> clfOrderItemRate = rmsClfOrderItemsRatesRepo
							.getRmsOrderItemsByItemId(clfOrderItems.getItemId());

					if (clfOrderItemRate.size() == 0) {
						ClfOrderItemRates newClfOrderItemRate = new ClfOrderItemRates();
						newClfOrderItemRate.setItemRateId(UUID.randomUUID().toString());
						newClfOrderItemRate.setOrderId(clfOrders.getOrderId());
						newClfOrderItemRate.setItemId(clfOrderItems.getItemId());
						newClfOrderItemRate
								.setRate((insertionPo.getRate() != null && insertionPo.getRate().length() > 0)
										? new BigDecimal(insertionPo.getRate()).doubleValue()
										: null);
						newClfOrderItemRate.setTotalAmount(
								(insertionPo.getTotalAmount() != null && insertionPo.getTotalAmount().length() > 0)
										? new BigDecimal(insertionPo.getTotalAmount()).doubleValue()
										: null);
						newClfOrderItemRate.setCreatedBy(loggedUser.getUserId());
						newClfOrderItemRate.setCreatedTs(new Date());
						newClfOrderItemRate.setMarkAsDelete(false);
						newClfOrderItemRate
								.setCgst((insertionPo.getCgst() != null && insertionPo.getCgst().length() > 0)
										? new BigDecimal(insertionPo.getCgst()).doubleValue()
										: null);
						newClfOrderItemRate
								.setSgst((insertionPo.getSgst() != null && insertionPo.getSgst().length() > 0)
										? new BigDecimal(insertionPo.getSgst()).doubleValue()
										: null);
						newClfOrderItemRate
								.setIgst((insertionPo.getIgst() != null && insertionPo.getIgst().length() > 0)
										? new BigDecimal(insertionPo.getIgst()).doubleValue()
										: null);
						newClfOrderItemRate.setCgstValue(
								(insertionPo.getCgstValue() != null && insertionPo.getCgstValue().length() > 0)
										? new BigDecimal(insertionPo.getCgstValue()).doubleValue()
										: null);
						newClfOrderItemRate.setSgstValue(
								(insertionPo.getSgstValue() != null && insertionPo.getSgstValue().length() > 0)
										? new BigDecimal(insertionPo.getSgstValue()).doubleValue()
										: null);
						newClfOrderItemRate.setIgstValue(
								(insertionPo.getIgstValue() != null && insertionPo.getIgstValue().length() > 0)
										? new BigDecimal(insertionPo.getIgstValue()).doubleValue()
										: null);
						newClfOrderItemRate.setRatePerSquareCms((insertionPo.getRatePerSquareCms() != null
								&& insertionPo.getRatePerSquareCms().length() > 0)
										? new BigDecimal(insertionPo.getRatePerSquareCms()).doubleValue()
										: null);
						newClfOrderItemRate
								.setAmount((insertionPo.getAmount() != null && insertionPo.getAmount().length() > 0)
										? new BigDecimal(insertionPo.getAmount()).doubleValue()
										: null);
						baseDao.saveOrUpdate(newClfOrderItemRate);
					} else {
						for (ClfOrderItemRates clfOrderItemRates : clfOrderItemRate) {
							clfOrderItemRates.setOrderId(clfOrders.getOrderId());
							clfOrderItemRates.setItemId(clfOrderItems.getItemId());
							clfOrderItemRates
									.setRate((insertionPo.getRate() != null && insertionPo.getRate().length() > 0)
											? new BigDecimal(insertionPo.getRate()).doubleValue()
											: null);
							clfOrderItemRates.setTotalAmount(
									(insertionPo.getTotalAmount() != null && insertionPo.getTotalAmount().length() > 0)
											? new BigDecimal(insertionPo.getTotalAmount()).doubleValue()
											: null);
							clfOrderItemRates.setMarkAsDelete(false);
							clfOrderItemRates.setChangedBy(loggedUser.getUserId());
							clfOrderItemRates.setChangedTs(new Date());
							System.out.println(insertionPo.getCgst());
							clfOrderItemRates
									.setCgst((insertionPo.getCgst() != null && insertionPo.getCgst().length() > 0)
											? new BigDecimal(insertionPo.getCgst()).doubleValue()
											: null);
							clfOrderItemRates
									.setSgst((insertionPo.getSgst() != null && insertionPo.getSgst().length() > 0)
											? new BigDecimal(insertionPo.getSgst()).doubleValue()
											: null);
							clfOrderItemRates
									.setIgst((insertionPo.getIgst() != null && insertionPo.getIgst().length() > 0)
											? new BigDecimal(insertionPo.getIgst()).doubleValue()
											: null);
							clfOrderItemRates.setCgstValue(
									(insertionPo.getCgstValue() != null && insertionPo.getCgstValue().length() > 0)
											? new BigDecimal(insertionPo.getCgstValue()).doubleValue()
											: null);
							clfOrderItemRates.setSgstValue(
									(insertionPo.getSgstValue() != null && insertionPo.getSgstValue().length() > 0)
											? new BigDecimal(insertionPo.getSgstValue()).doubleValue()
											: null);
							clfOrderItemRates.setIgstValue(
									(insertionPo.getIgstValue() != null && insertionPo.getIgstValue().length() > 0)
											? new BigDecimal(insertionPo.getIgstValue()).doubleValue()
											: null);
							clfOrderItemRates.setRatePerSquareCms((insertionPo.getRatePerSquareCms() != null
									&& insertionPo.getRatePerSquareCms().length() > 0)
											? new BigDecimal(insertionPo.getRatePerSquareCms()).doubleValue()
											: null);
							clfOrderItemRates
									.setAmount((insertionPo.getAmount() != null && insertionPo.getAmount().length() > 0)
											? new BigDecimal(insertionPo.getAmount()).doubleValue()
											: null);
							baseDao.saveOrUpdate(clfOrderItemRates);
						}
					}
				}
				clfOrders.setEditionType(insertionPo.getEditionType());
				details.put("orderId", clfOrders.getOrderId());
				details.put("itemId", clfOrderItems.getItemId());
				response.setData(details);

			}
//			if ("KYC".equalsIgnoreCase(payload.getAction())) {
//				if (payload.getKycAttatchments() != null) {
//					this.kycAttatchments(payload, loggedUser);
//				}
//				details.put("orderId", clfOrders.getOrderId());
//				details.put("itemId", payload.getItemId());
//				response.setData(details);
//			}

			if ("PAYMENTS".equalsIgnoreCase(payload.getAction())) {
				PaymentDetails paymentDetails = payload.getPaymentDetails();
				List<RmsPaymentsResponse> paymentsResponses = paymentsRepository
						.getPaymentsByItemId(clfOrderItems.getItemId());
				RmsPaymentsResponse payments = new RmsPaymentsResponse();
				if (paymentsResponses.size() == 0) {
					payments.setPaymentsId(UUID.randomUUID().toString());
					payments.setOrderId(clfOrders.getOrderId());
					payments.setItemId(clfOrderItems.getItemId());
					payments.setCreatedBy(loggedUser.getUserId());
					payments.setCreatedTs(new Date());
					payments.setPaymentMode(paymentDetails.getPaymentMode());
					payments.setBankBranch(paymentDetails.getBankOrBranch());
					payments.setBankRefId(paymentDetails.getReferenceId());
					payments.setMarkAsDelete(false);
					payments.setBankOrUpi(paymentDetails.getBankOrUpi());
					payments.setPaymentMethod(paymentDetails.getPaymentMethod());
					payments.setCashReceiptNo(paymentDetails.getCashReceiptNo());
					payments.setOtherDetails(paymentDetails.getOtherDetails());
					payments.setSignatureId(paymentDetails.getSignatureAttachmentId());
					payments.setChequeAttId(
							paymentDetails.getChequeAttachmentId() != null ? paymentDetails.getChequeAttachmentId()
									: null);
					payments.setPaymentAttId(
							paymentDetails.getPaymentAttachmentId() != null ? paymentDetails.getPaymentAttachmentId()
									: null);
					baseDao.saveOrUpdate(payments);
				} else {
					for (RmsPaymentsResponse paymentsResponse : paymentsResponses) {
						paymentsResponse.setOrderId(clfOrders.getOrderId());
						paymentsResponse.setItemId(clfOrderItems.getItemId());
						paymentsResponse.setPaymentMode(paymentDetails.getPaymentMode());
						paymentsResponse.setBankBranch(paymentDetails.getBankOrBranch());
						paymentsResponse.setBankRefId(paymentDetails.getReferenceId());
						paymentsResponse.setMarkAsDelete(false);
						paymentsResponse.setChangedBy(loggedUser.getUserId());
						paymentsResponse.setChangedTs(new Date());
						paymentsResponse.setBankOrUpi(paymentDetails.getBankOrUpi());
						paymentsResponse.setPaymentMethod(paymentDetails.getPaymentMethod());
						paymentsResponse.setCashReceiptNo(paymentDetails.getCashReceiptNo());
						paymentsResponse.setOtherDetails(paymentDetails.getOtherDetails());
						paymentsResponse.setSignatureId(paymentDetails.getSignatureAttachmentId());
						paymentsResponse.setChequeAttId(
								paymentDetails.getChequeAttachmentId() != null ? paymentDetails.getChequeAttachmentId()
										: null);
						paymentsResponse.setPaymentAttId(paymentDetails.getPaymentAttachmentId() != null
								? paymentDetails.getPaymentAttachmentId()
								: null);

						baseDao.saveOrUpdate(paymentsResponse);
					}
				}
				if (payload.getPaymentDetails().getKycAttatchments() != null) {
					this.kycAttatchments(payload, loggedUser);
				}
				details.put("orderId", clfOrders.getOrderId());
				details.put("itemId", clfOrderItems.getItemId());
				response.setData(details);
			}
		} catch (Exception e) {
			logger.error("Error while adding order to cart:" + e.getMessage());
			response.setStatus(1);
			response.setErrorcode("GEN_002");
		}
		return response;
	}
	
	private ClientPo getCustomerDetail(String customerId) {
		ClientPo clientPo = new ClientPo();
		List<Object[]> customerdetails = rmsUmCustomersRepo.getCustomerByOrderId(customerId, "OPEN");
		try {
			if (customerdetails != null && !customerdetails.isEmpty()) {
				for (Object[] obj : customerdetails) {
					clientPo.setState((String) obj[0]);
					clientPo.setCity((String) obj[1]);
					clientPo.setCustomerId((String) obj[2]);
					clientPo.setCustomerName((String) obj[3]);
					clientPo.setMobileNo((String) obj[4]);
					clientPo.setEmailId((String) obj[5]);
					clientPo.setAddress1((String) obj[6]);
					clientPo.setPinCode((String) obj[7]);
					clientPo.setGstNo((String) obj[8]);
					clientPo.setClientCode((String) obj[9]);
					clientPo.setCustomerDetails((Integer) obj[10]);
					clientPo.setUserId((Integer) obj[11]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientPo;
	}
	
	private InsertionPo getInsertionDetails(String orderId) {
		InsertionPo insertionPo=new InsertionPo();
		insertionPo.setPublishDates(new ArrayList<String>());
		insertionPo.setEditions(new ArrayList<Integer>());
		List<Object[]> insertionDetailsOnOrderId = rmsOrderItemsRepo.getInsertionDetailsOnOrderId(orderId);
		try {
			if(!insertionDetailsOnOrderId.isEmpty()&&insertionDetailsOnOrderId!=null) {
				for(Object[] obj : insertionDetailsOnOrderId) {
					insertionPo.setScheme((Integer) obj[0]);
					insertionPo.setCaption((String) obj[1]);
					insertionPo.setClassifiedAdsSubtype((Integer) obj[2]);
					insertionPo.setProductGroup((Integer) obj[3]);
					insertionPo.setProductSubGroup((Integer) obj[4]);
					insertionPo.setProductChildGroup((Integer) obj[5]);
					insertionPo.setNoOfInsertions((Integer) obj[6]);
					float spaceWidthFloat = (float) obj[7];
					float spaceHeightFloat = (float) obj[8];
					String spaceWidthString = String.valueOf(spaceWidthFloat);
					String spaceHeightString = String.valueOf(spaceHeightFloat);
					insertionPo.setSpaceWidth(spaceWidthString);
					insertionPo.setSpaceHeight(spaceHeightString);
					insertionPo.setPagePosition((Integer) obj[9]);
					insertionPo.setFormatType((Integer) obj[10]);
					insertionPo.setFixedFormat((Integer) obj[11]);	
					insertionPo.setPageNumber((Integer) obj[12]);
					String categoryDiscountString = Integer.toString((int) obj[13]);
					insertionPo.setCategoryDiscount(categoryDiscountString);
					String multiDiscountString = Float.toString((float) obj[14]);
					String additionalDiscountString = Float.toString((float) obj[15]);
					String surchargeRateString = Float.toString((float) obj[16]);
					String multiDiscountAmountString = Float.toString((float) obj[17]);
					String surchargeAmountString = Float.toString((float) obj[18]);
					String additionalDiscountAmountString = Float.toString((float) obj[19]);
					String categoryDiscountAmountString = Float.toString((float) obj[30]);
					insertionPo.setMultiDiscount(multiDiscountString);
					insertionPo.setAdditionalDiscount(additionalDiscountString);
					insertionPo.setSurchargeRate(surchargeRateString);
					insertionPo.setMultiDiscountAmount(multiDiscountAmountString);
					insertionPo.setSurchargeAmount(surchargeAmountString);
					insertionPo.setAdditionalDiscountAmount(additionalDiscountAmountString);
					insertionPo.setCategoryDiscountAmount(categoryDiscountAmountString);
					String rateString = Float.toString((float) obj[20]);
					String totalAmountString = Float.toString((float) obj[21]);
					String cgstString = (obj[22] != null) ? Float.toString((float) obj[22]) : "";
					String sgstString = (obj[23] != null) ? Float.toString((float) obj[23]) : "";
					String igstString = (obj[24] != null) ? Float.toString((float) obj[24]) : "";
					String cgstValueString = (obj[25] != null) ? Float.toString((float) obj[25]) : "";
					String sgstValueString = (obj[26] != null) ? Float.toString((float) obj[26]) : "";
					String igstValueString = (obj[27] != null) ? Float.toString((float) obj[27]) : "";
					String amountString = Float.toString((float) obj[28]);
					String ratePerSquareCmsString = Float.toString((float) obj[29]);
					insertionPo.setRate(rateString);
					insertionPo.setTotalAmount(totalAmountString);
					insertionPo.setCgst(cgstString);
					insertionPo.setSgst(sgstString);
					insertionPo.setIgst(igstString);
					insertionPo.setCgstValue(cgstValueString);
					insertionPo.setSgstValue(sgstValueString);
					insertionPo.setIgstValue(igstValueString);
					insertionPo.setAmount(amountString);
					insertionPo.setRatePerSquareCms(ratePerSquareCmsString);
				}
			}
			List<Object[]> publishDatesOnOrderId = rmsClfPublishDates.getPublishDatesOnOrderId(orderId);
			if(!publishDatesOnOrderId.isEmpty()&&publishDatesOnOrderId!=null) {
				for (Object[] clObj : publishDatesOnOrderId) {
					insertionPo.getPublishDates()
							.add(CommonUtils.dateFormatter((Date) clObj[0], "yyyy-MM-dd"));
				}	
			}
			List<Object[]> editionIdAndNameOnOrderId = rmsClfEditionsRepo.getEditionIdAndNameOnOrderId(orderId);
			if(!editionIdAndNameOnOrderId.isEmpty()&&editionIdAndNameOnOrderId!=null) {
				for (Object[] clObj : editionIdAndNameOnOrderId) {
					insertionPo.getEditions().add((Integer) clObj[0]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insertionPo;
	}

	public void kycAttatchments(CreateOrder payload, LoggedUser loggedUser) {
		try {
			RmsKycAttatchments kycAttatchments = payload.getPaymentDetails().getKycAttatchments();
			List<String> aadharAttatchments = kycAttatchments.getAadharAttatchments();
			List<String> gstAttatchments = kycAttatchments.getGstAttatchments();
			List<String> otherAttatchments = kycAttatchments.getOtherAttatchments();
			List<String> panAttatchments = kycAttatchments.getPanAttatchments();

			String aadhar = aadharAttatchments.stream().map(Object::toString).collect(Collectors.joining(","));
			String gst = gstAttatchments.stream().map(Object::toString).collect(Collectors.joining(","));
			String pan = panAttatchments.stream().map(Object::toString).collect(Collectors.joining(","));
			String other = otherAttatchments.stream().map(Object::toString).collect(Collectors.joining(","));

			String result = aadhar + "," + gst + "," + pan + "," + other;
			System.out.println(result);
			String customerId = payload.getClientPo().getCustomerId();

			Optional<UmCustomers> findById = rmsUmCustomersRepo.findById(customerId);
			UmCustomers umCustomers = findById.get();
			umCustomers.setAttatchId(result);
			umCustomers.setKycRequired(false);
			rmsUmCustomersRepo.save(umCustomers);

			List<String> allAttachments = new ArrayList<>();
			allAttachments.addAll(kycAttatchments.getAadharAttatchments());
			allAttachments.addAll(kycAttatchments.getGstAttatchments());
			allAttachments.addAll(kycAttatchments.getOtherAttatchments());
			allAttachments.addAll(kycAttatchments.getPanAttatchments());

			List<Attachments> attachments = rmsAttachmentsRepo.getAllAttachmentsByCustomerId(customerId);
			List<String> attIds = new ArrayList<String>();
			if (attachments != null && !attachments.isEmpty()) {
				for (Attachments a : attachments) {
					attIds.add(a.getAttachId());
				}
				rmsAttachmentsRepo.removeAttachmentsByCustomerId(true, loggedUser.getUserId(), new Date(), attIds);
			}
			rmsAttachmentsRepo.updateAttachemets(customerId, loggedUser.getUserId(), new Date(), allAttachments);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public GenericApiResponse removeOldCardDet(CreateOrder payload, LoggedUser loggedUser) {

		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		genericApiResponse.setMessage("Successfully Updated");
		try {
			rmsClfPublishDates.removeClfPublishDatesOnItemId(true, loggedUser.getUserId(), new Date(),
					payload.getItemId());

			rmsClfEditionsRepo.removeClfEditionsOnItemId(true, loggedUser.getUserId(), new Date(), payload.getItemId());

		} catch (Exception e) {
			e.printStackTrace();
			genericApiResponse.setStatus(1);
			genericApiResponse.setMessage("Something went wrong. Please contact our administrator.");
		}
		return genericApiResponse;

	}

	public ClientPo populateRmsCustomerDetails(ClientPo customerDetails, LoggedUser loggedUser) {
		if (customerDetails != null && customerDetails.getCustomerId() != null
				&& !customerDetails.getCustomerId().isEmpty()) {
			Optional<UmCustomers> customerDetailsFromDb = rmsUmCustomersRepo.findById(customerDetails.getCustomerId());
			if (customerDetailsFromDb.isPresent()) {
				customerDetailsFromDb.get()
						.setAddress1(customerDetails.getAddress1() != null ? customerDetails.getAddress1()
								: customerDetailsFromDb.get().getAddress1());
				customerDetailsFromDb.get()
						.setAddress2(customerDetails.getAddress2() != null ? customerDetails.getAddress2()
								: customerDetailsFromDb.get().getAddress2());
				customerDetailsFromDb.get()
						.setAddress3(customerDetails.getAddress3() != null ? customerDetails.getAddress3()
								: customerDetailsFromDb.get().getAddress3());
				customerDetailsFromDb.get()
						.setEmailId(customerDetails.getEmailId() != null ? customerDetails.getEmailId()
								: customerDetailsFromDb.get().getEmailId());
				customerDetailsFromDb.get().setCity(customerDetails.getCity() != null ? customerDetails.getCity()
						: customerDetailsFromDb.get().getCity());
				customerDetailsFromDb.get().setState(customerDetails.getState() != null ? customerDetails.getState()
						: customerDetailsFromDb.get().getState());
				customerDetailsFromDb.get()
						.setMobileNo(customerDetails.getMobileNo() != null ? customerDetails.getMobileNo()
								: customerDetailsFromDb.get().getMobileNo());
				customerDetailsFromDb.get().setGstNo(customerDetails.getGstNo() != null ? customerDetails.getGstNo()
						: customerDetailsFromDb.get().getGstNo());
				customerDetailsFromDb.get()
						.setPinCode(customerDetails.getPinCode() != null ? customerDetails.getPinCode()
								: customerDetailsFromDb.get().getPinCode());
				customerDetailsFromDb.get()
						.setCustomerName(customerDetails.getCustomerName() != null ? customerDetails.getCustomerName()
								: customerDetailsFromDb.get().getCustomerName());
				customerDetailsFromDb.get()
						.setClientCode(customerDetails.getClientCode() != null ? customerDetails.getClientCode()
								: customerDetailsFromDb.get().getClientCode());
				customerDetailsFromDb.get().setCustomerTypeId(1);
				rmsUmCustomersRepo.save(customerDetailsFromDb.get());
			} else {
				addRmsCustomer(customerDetails, loggedUser);
			}

		} else {
			addRmsCustomer(customerDetails, loggedUser);
		}
		return customerDetails;
	}

	public boolean addRmsCustomer(ClientPo customerDetails, LoggedUser loggedUser) {
		try {
			List<UmCustomers> customerDetailsList = rmsUmCustomersRepo
					.getCustomerDetails(customerDetails.getMobileNo());
			UmCustomers umCustomers = new UmCustomers();
			BeanUtils.copyProperties(customerDetails, umCustomers);
			if (!customerDetailsList.isEmpty()) {
				umCustomers = customerDetailsList.get(0);
				umCustomers.setChangedBy(loggedUser.getUserId());
				umCustomers.setChangedTs(new Date());
				customerDetails.setCustomerId(umCustomers.getCustomerId());
			} else {
				umCustomers.setCustomerId(UUID.randomUUID().toString());
				umCustomers.setCreatedBy(loggedUser.getUserId());
				umCustomers.setCreatedTs(new Date());
				umCustomers.setMarkAsDelete(false);
				umCustomers.setEmailId(customerDetails.getEmailId());
				umCustomers.setAddress1(customerDetails.getAddress1());
				umCustomers.setAddress2(customerDetails.getAddress2());
				umCustomers.setAddress3(customerDetails.getAddress3());
				umCustomers.setPinCode(customerDetails.getPinCode());
				umCustomers.setCity(customerDetails.getCity());
				umCustomers.setState(customerDetails.getState());
				umCustomers.setMobileNo(customerDetails.getMobileNo());
				umCustomers.setGstNo(customerDetails.getGstNo());
				umCustomers.setClientCode(customerDetails.getClientCode());
				umCustomers.setCustomerTypeId(customerDetails.getCustomerDetails());
				umCustomers.setKycRequired(true);
			}
			umCustomers.setUserId(customerDetails.getUserId());
			rmsUmCustomersRepo.save(umCustomers);
			customerDetails.setCustomerId(umCustomers.getCustomerId());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public ClfOrders getOpenCartDetails(LoggedUser loggedUser) {
		List<ClfOrders> openOrderDetails = rmsClfOrdersRepo.getOpenOrderDetails(loggedUser.getUserId(),
				RmsConstants.ORDER_OPEN);
		if (!openOrderDetails.isEmpty()) {
			if (openOrderDetails.get(0).getOrderStatus().equalsIgnoreCase(RmsConstants.ORDER_OPEN)) {
				return openOrderDetails.get(0);
			} else {
				openOrderDetails.get(0).setMarkAsDelete(true);
				rmsClfOrdersRepo.save(openOrderDetails.get(0));
				return null;
			}
		}
		return null;
	}

	public String generateSeries(String type) {
		StringBuilder sb = new StringBuilder();
		try {
			List<GdNumberSeries> gdNumberSeriesList = gdNumberSeriesRepo.getNumberSeriesByType(type);
			boolean currentYearSeriesFlag = false;
			String currentYear = CommonUtils.dateFormatter(new Date(), "Y");
			String currentYearSeriesFormat = CommonUtils.dateFormatter(new Date(), "yy");
			if (!gdNumberSeriesList.isEmpty()) {
				currentYearSeriesFormat = CommonUtils.dateFormatter(new Date(),
						gdNumberSeriesList.get(0).getYearFormat());
				GdNumberSeries gdCurrentYearNumberSeries = null;
				GdNumberSeries gdNumberSeries = new GdNumberSeries();
				for (GdNumberSeries gns : gdNumberSeriesList) {
					if (gns.getYear() != null && gns.getYear().equalsIgnoreCase(currentYear)) {
						currentYearSeriesFlag = true;
						gdCurrentYearNumberSeries = gns;
					} else {
						gdNumberSeries = gns;
					}
				}
				if (gdCurrentYearNumberSeries == null) {
					gdCurrentYearNumberSeries = gdNumberSeries;
				}
				sb.append(gdCurrentYearNumberSeries.getPrefix());
				sb.append(currentYearSeriesFormat);
				BigDecimal upComingSeries = gdCurrentYearNumberSeries.getCurrentSeries().add(new BigDecimal(1));
				for (int i = 0; i < gdCurrentYearNumberSeries.getSeriesLength() - (upComingSeries + "").length(); i++) {
					sb.append("0");
				}
				sb.append(upComingSeries);
				System.out.println(sb.toString());
				if (!currentYearSeriesFlag) {
					gdCurrentYearNumberSeries.setId(UUID.randomUUID().toString());
					gdCurrentYearNumberSeries.setYear(currentYear);
				}
				gdCurrentYearNumberSeries.setCurrentSeries(upComingSeries);
				gdNumberSeriesRepo.save(gdCurrentYearNumberSeries);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	@Override
	public GenericApiResponse getCustomerDetails(String clientCode,String customerName) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		try {
			List<Object[]> customerClientCodeOrName = new ArrayList<Object[]>();
			if(clientCode!=null &&!clientCode.isEmpty()) {
				customerClientCodeOrName = rmsUmCustomersRepo.getCustomerDetailsOnClientCode(clientCode);
			} else {
				customerClientCodeOrName = rmsUmCustomersRepo.getCustomerDetailsOnName(customerName);
			}

			if (customerClientCodeOrName == null || customerClientCodeOrName.size() == 0) {
				genericApiResponse.setStatus(1);
				genericApiResponse.setMessage("No customer found");
				return genericApiResponse;
			}
			
//			Map<String, RmsCustomerDetails> classifiedsMap = new HashMap<String, RmsCustomerDetails>();
			List<RmsCustomerDetails> customerDetailsList = new ArrayList<>();

			for (Object[] obj : customerClientCodeOrName) {
				RmsCustomerDetails customerDetails = new RmsCustomerDetails();
				customerDetails.setState((String) obj[0]);
				customerDetails.setCity((String) obj[1]);
				customerDetails.setCityDesc((String) obj[2]);
				customerDetails.setStateDesc((String) obj[3]);
				customerDetails.setCustomerId((String) obj[4]);
				customerDetails.setCustomerName((String) obj[5]);
				customerDetails.setMobileNo((String) obj[6]);
				customerDetails.setClientCode((String) obj[7]);
				customerDetails.setAttatchId((String) obj[8]);
				customerDetails.setHouseNo((String) obj[9]);
				customerDetails.setChangedTs((Date) obj[10]);
				customerDetails.setChangedBy((Integer) obj[11]);
				customerDetails.setCreatedTs((Date) obj[12]);
				customerDetails.setCreatedBy((Integer) obj[13]);
				customerDetails.setUserId((Integer) obj[14]);
				customerDetails.setErpRefId((String) obj[15]);
				customerDetails.setAadharNumber((String) obj[16]);
				customerDetails.setPanNumber((String) obj[17]);
				customerDetails.setGstNo((String) obj[18]);
				customerDetails.setOfficeLocation((String) obj[19]);
				customerDetails.setPinCode((String) obj[20]);
				customerDetails.setAddress3((String) obj[21]);
				customerDetails.setAddress2((String) obj[22]);
				customerDetails.setAddress1((String) obj[23]);
				customerDetails.setEmailId((String) obj[24]);
				customerDetails.seteKycRequired((boolean) obj[25]);
				customerDetailsList.add(customerDetails);
//				classifiedsMap.put(customerDetails.getClientCode(), customerDetails);
			}

			genericApiResponse.setStatus(0);
			genericApiResponse.setData(customerDetailsList);

		} catch (Exception e) {
			logger.error("Error while uploading additional documents file" + e.getMessage());
			genericApiResponse.setStatus(1);
			genericApiResponse.setErrorcode("GEN_002");
		}

		return genericApiResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GenericApiResponse getRmsRates(RmsRateModel payload) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		Double rate = 0.0;

		List<RmsRates> ratesList = rmsRatesRepo.getRates(2, payload.getEditions(), payload.getAdsSubType(),
				payload.getFixedFormat());

		if (ratesList != null && !ratesList.isEmpty()) {
			for (RmsRates objs : ratesList) {
				rate = rate + (double) (objs.getRate());
			}
			payload.setRate(rate);
			genericApiResponse.setData(payload);
		} else {
			genericApiResponse.setStatus(1);
			genericApiResponse.setMessage("There is no rate card available with the selected details.");
		}
		return genericApiResponse;
	}

	@Override
	public GenericApiResponse genrateOTP(OtpModel payload) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		if (payload != null && payload.getOrderId() != null) {
			ClfOrders clfOrders = rmsClfOrdersRepo.getOrderDetails(payload.getOrderId());
			if (clfOrders != null && clfOrders.getCustomerId() != null) {
				UmCustomers umCustomers = rmsUmCustomersRepo.getCustomerDetailsByOrderId(clfOrders.getCustomerId());
				if (umCustomers != null) {
					String mobileNo = umCustomers.getMobileNo();
//					String otp = RandomStringUtils.randomNumeric(4);
					String otp = "1234";
					Map<String, String> smsDetails = new HashMap<>();
					if ("false".equalsIgnoreCase(prop.getProperty("ORP_VERIFY_STUB"))
							|| "0".equalsIgnoreCase(prop.getProperty("ORP_VERIFY_STUB"))) {
						smsDetails = sendMessageService.sendSms(otp, mobileNo);
					} else {
						smsDetails.put("status", "Success");
					}
					if ("Success".equalsIgnoreCase(smsDetails.get("status"))) {
						OtpVerification otpVerification = new OtpVerification();
						otpVerification.setId(UUID.randomUUID().toString());
						otpVerification.setMobileNo(mobileNo);
						otpVerification.setOtpNum(otp);
//						otpVerification.setOtpStatus("Success");
						otpVerification.setOtpStatus(smsDetails.get("status"));
						otpVerification.setOtpAttempts(0);
						otpVerification.setCreatedTs(new Date());
						otpVerification.setOrderId(payload.getOrderId());
						otpVerification.setOtpValidityTime(new Date());
						otpVerification.setMarkAsDelete(false);
						baseDao.save(otpVerification);
						genericApiResponse.setMessage("OTP Successfully genrated");
					}
				}
			} else {
				genericApiResponse.setStatus(1);
				genericApiResponse.setMessage("Customer Details Not Found");
			}
		}
		return genericApiResponse;
	}

	@Override
	public GenericApiResponse validateOTP(OtpModel payload) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		OtpVerification otpDetails = new OtpVerification();
		if (payload != null && payload.getOrderId() != null) {
			ClfOrders clfOrders = rmsClfOrdersRepo.getOrderDetails(payload.getOrderId());
			if (clfOrders != null && clfOrders.getCustomerId() != null) {
				UmCustomers umCustomers = rmsUmCustomersRepo.getCustomerDetailsByOrderId(clfOrders.getCustomerId());
				if (umCustomers != null) {
					String mobileNo = umCustomers.getMobileNo();
					List<OtpVerification> otpDetailsList = otpVerificationRepo.getOtpVerificationDetails(mobileNo,
							payload.getOtp(), payload.getOrderId());
					if (!otpDetailsList.isEmpty()) {
						otpDetails = otpDetailsList.get(0);
					} else {
						genericApiResponse.setStatus(1);
						genericApiResponse.setMessage("OTP Incorrect.");
						return genericApiResponse;
					}
					if (otpDetails != null) {
						Date dateFromDB = otpDetails.getOtpValidityTime();
						Date CurrentDate = new Date();
						long diff = CurrentDate.getTime() - dateFromDB.getTime();
						long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
						if (minutes <= 2) {
							if (otpDetails.getOtpNum().equals(payload.getOtp())) {
								genericApiResponse.setStatus(0);
								genericApiResponse.setMessage("OTP Success");
								rmsClfOrdersRepo.updateRmsOnOrderIds("CLOSED", new Date(), payload.getOrderId());
								List<String> orderIds = new ArrayList<String>();
								orderIds.add(payload.getOrderId());
								Map<String, ErpClassifieds> rmsOrderDetailsForErp = this
										.getRmsOrderDetailsForErp(orderIds);
								this.sendRmsMailToCustomer(rmsOrderDetailsForErp, null, userContext.getLoggedUser(),
										null);
							} else {
								genericApiResponse.setStatus(1);
								genericApiResponse.setMessage("OTP Incorrect.");
								return genericApiResponse;
							}
						} else {
							genericApiResponse.setStatus(1);
							genericApiResponse.setMessage("OTP Expired");
						}
					} else {
						genericApiResponse.setStatus(1);
						genericApiResponse.setMessage("OTP Incorrect.");
					}
				}
			} else {
				genericApiResponse.setStatus(1);
				genericApiResponse.setMessage("Customer Details Not Found");
			}
		}
		return genericApiResponse;
	}

	public static Date dateFormatter(String stringDate) {
		Date date = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = simpleDateFormat.parse(stringDate);
		} catch (ParseException e) {
			logger.error("Error parsing date: " + stringDate, e);
		}

		return date;
	}

	@SuppressWarnings("null")
	public String generateRmsSeries(Integer bookingCode) {
		StringBuilder sb = new StringBuilder();
		try {
			List<GdRmsNumberSeries> gdNumberSeriesList = gdRmsNumberSeriesRepo
					.getNumberSeriesByBookingCode(bookingCode);
			boolean currentYearSeriesFlag = false;
			String currentYear = CommonUtils.dateFormatter(new Date(), "Y");
			String currentYearSeriesFormat = CommonUtils.dateFormatter(new Date(), "yy");
			if (!gdNumberSeriesList.isEmpty()) {
				currentYearSeriesFormat = CommonUtils.dateFormatter(new Date(),
						gdNumberSeriesList.get(0).getYearFormat());
				GdRmsNumberSeries gdCurrentYearNumberSeries = null;
				GdRmsNumberSeries gdNumberSeries = new GdRmsNumberSeries();
				for (GdRmsNumberSeries gns : gdNumberSeriesList) {
					if (gns.getBookingCode() != null && gns.getYear() != null
							&& gns.getYear().equalsIgnoreCase(currentYear)) {
						currentYearSeriesFlag = true;
						gdCurrentYearNumberSeries = gns;
					} else {
						gdNumberSeries = gns;
					}
				}
				if (gdCurrentYearNumberSeries == null) {
					gdCurrentYearNumberSeries = gdNumberSeries;
				}
				sb.append(gdCurrentYearNumberSeries.getBookingCode());
				sb.append(currentYearSeriesFormat);
				BigDecimal upComingSeries = gdNumberSeriesList.get(0).getCurrentSeries().add(new BigDecimal(1));
				for (int i = 0; i < gdCurrentYearNumberSeries.getSeriesLength() - (upComingSeries + "").length(); i++) {
					sb.append("0");
				}
				sb.append(upComingSeries);
				System.out.println(sb.toString());
				if (!currentYearSeriesFlag) {
					gdCurrentYearNumberSeries.setId(UUID.randomUUID().toString());
					gdCurrentYearNumberSeries.setYear(currentYear);
				}
				gdCurrentYearNumberSeries.setCurrentSeries(upComingSeries);
				gdRmsNumberSeriesRepo.save(gdCurrentYearNumberSeries);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	@Override
	public GenericApiResponse syncronizeRmsSAPData(GenericRequestHeaders requestHeaders, @NotNull RmsModel payload) {
		GenericApiResponse apiResponse = new GenericApiResponse();
		try {
			if (payload != null) {
				Map<String, ErpClassifieds> erpClassifieds = this.getRmsOrderDetailsForErp(payload.getOrderId());
				if (!erpClassifieds.isEmpty()) {
					Map<String, Object> payloadJson = new HashMap<String, Object>();
					payloadJson.put("userId", requestHeaders.getLoggedUser().getUserId());
					payloadJson.put("orderId", payload.getOrderId());
					payloadJson.put("orgId", requestHeaders.getOrgId());
					payloadJson.put("data", erpClassifieds);
					payloadJson.put("orgOpuId", requestHeaders.getOrgOpuId());
					payloadJson.put("action", "Rms_order");
					boolean flag = erpService.processRmsCreationFtpFiles(payloadJson, erpClassifieds);
					if (flag) {
						apiResponse.setStatus(0);
						apiResponse.setMessage("Success");
					} else {
						apiResponse.setStatus(1);
						apiResponse.setMessage("Failed");
					}
				} else {
					apiResponse.setStatus(1);
					apiResponse.setMessage("Details Not Found For Selected Ads");
				}
			}
		} catch (Exception e) {
//			apiResponse.setMessage(properties.getProperty("GEN_002"));
			apiResponse.setErrorcode("GEN_002");
		}
		return apiResponse;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ErpClassifieds> getRmsOrderDetailsForErp(List<String> orderIds) {
		List<Object[]> classifiedList = new ArrayList<Object[]>();
		Map<String, ErpClassifieds> classifiedsMap = new HashMap<>();
		List<String> itemIds = new ArrayList<String>();
		List<String> customerIds = new ArrayList<String>();
		List<Integer> createdByIds = new ArrayList<Integer>();
		DecimalFormat df = new DecimalFormat("#.###");
		DecimalFormat df1 = new DecimalFormat("#.##");
		df.setRoundingMode(java.math.RoundingMode.HALF_DOWN);
		df1.setRoundingMode(java.math.RoundingMode.HALF_DOWN);
		try {
			String joinedOrderIds = String.join("','", orderIds);
			// String query = "select itm.item_id, itm.order_id, itm.classified_type,
			// itm.classified_ads_type, itm.scheme, itm.clf_content, itm.created_by,
			// itm.created_ts,itm.changed_by, itm.changed_ts, itm.classified_ads_sub_type,
			// itm.status, itm.ad_id, itm.category_group, itm.sub_group, itm.child_group,
			// co.customer_id, co.user_type_id, co.booking_unit, coir.total_amount,
			// gct.type, gct.erp_ref_id as gct_erp_ref_id, gcat.ads_type, gcat.erp_ref_id as
			// gcat_erp_ref_id, gcast.ads_sub_type, gcast.erp_ref_id as gcast_erp_ref_id,
			// grs.scheme as grs_scheme, grs.erp_ref_id as grs_erp_ref_id,
			// gg.classified_group, gg.erp_ref_id as gg_erp_ref_id,
			// gsg.classified_sub_group, gsg.erp_ref_id as gsg_erp_ref_id,
			// gcg.classified_child_group, gcg.erp_ref_id as gcg_erp_ref_id,
			// bu.sales_office, bu.booking_location, bu.sold_to_party,
			// bu.booking_unit_email, roi.no_of_insertions, roi.size_width, roi.size_height,
			// roi.page_position, roi.format_type as roi_format_type, roi.fixed_format,
			// roi.page_number , roi.category_discount , roi.multi_discount ,
			// roi.additional_discount , roi.surcharge_rate, grff.size, grff.erp_ref_code as
			// grff_erp_ref_code, grft.format_type as grft_format_type, grmd.discount,
			// grpp.page_name, grpp.erp_ref_code as grpp_erp_ref_code from clf_order_items
			// itm inner join gd_classified_types gct on itm.classified_type = gct.id inner
			// join gd_classified_ads_types gcat on itm.classified_ads_type = gcat.id inner
			// join gd_classified_ads_sub_types gcast on itm.classified_ads_sub_type =
			// gcast.id inner join gd_rms_schemes grs on itm.scheme = grs.id inner join
			// gd_classified_group gg on itm.category_group = gg.id inner join
			// gd_classified_sub_group gsg on itm.sub_group = gsg.id inner join
			// gd_classified_child_group gcg on itm.child_group = gcg.id inner join
			// clf_order_item_rates coir on itm.item_id = coir.item_id inner join clf_orders
			// co on itm.order_id = co.order_id left join booking_units bu on
			// co.booking_unit = bu.booking_code inner join rms_order_items roi on
			// itm.item_id = roi.item_id left join gd_rms_fixed_formats grff on
			// roi.fixed_format = grff.id left join gd_rms_format_types grft on
			// roi.format_type = grft.id left join gd_rms_multi_discount grmd on
			// roi.multi_discount = grmd.id left join gd_rms_page_positions grpp on
			// roi.page_number = grpp.id where itm.order_id in ('"+ joinedOrderIds + "') and
			// itm.mark_as_delete = false";
			String query = "select itm.item_id, itm.order_id, itm.classified_type, itm.classified_ads_type, itm.scheme, itm.clf_content, itm.created_by, itm.created_ts,itm.changed_by, itm.changed_ts, itm.classified_ads_sub_type, itm.status, itm.ad_id, itm.category_group, itm.sub_group, itm.child_group, co.customer_id, co.user_type_id, co.booking_unit, coir.total_amount, gct.type, gct.erp_ref_id as gct_erp_ref_id, gcat.ads_type, gcat.erp_ref_id as gcat_erp_ref_id, gcast.ads_sub_type, gcast.erp_ref_id as gcast_erp_ref_id, grs.scheme as grs_scheme, grs.erp_ref_id as grs_erp_ref_id, gg.classified_group, gg.erp_ref_id as gg_erp_ref_id, gsg.classified_sub_group, gsg.erp_ref_id as gsg_erp_ref_id, gcg.classified_child_group, gcg.erp_ref_id as gcg_erp_ref_id, bu.sales_office, bu.booking_location, bu.sold_to_party, bu.booking_unit_email, roi.no_of_insertions, roi.size_width, roi.size_height, roi.page_position, roi.format_type as roi_format_type, roi.fixed_format, roi.page_number , roi.category_discount , roi.multi_discount , roi.additional_discount , roi.surcharge_rate, grff.size, grff.erp_ref_code as grff_erp_ref_code, grft.format_type as grft_format_type, grmd.discount, grpp.page_name, grpp.erp_ref_code as grpp_erp_ref_code,uc.customer_name,uc.client_code,bu.booking_description,gs.state,uc.mobile_no,uc.house_no,uc.gst_no,uc.pin_code,coir.rate,grpd.positioning_desc,grpd.amount,coir.igst ,coir.cgst,coir.sgst,coir.cgst_value,coir.sgst_value ,coir.igst_value,coir.amount as total_value,rps.bank_branch,rps.cash_receipt_no,roi.space_width,roi.space_height  from clf_order_items itm inner join gd_classified_types gct on itm.classified_type = gct.id inner join gd_classified_ads_types gcat on itm.classified_ads_type = gcat.id inner join gd_classified_ads_sub_types gcast on itm.classified_ads_sub_type  = gcast.id inner join gd_rms_schemes grs on itm.scheme = grs.id inner join gd_classified_group gg on itm.category_group = gg.id inner join gd_classified_sub_group gsg on itm.sub_group = gsg.id inner join gd_classified_child_group gcg on itm.child_group = gcg.id inner join clf_order_item_rates coir on itm.item_id = coir.item_id inner join clf_orders co on itm.order_id = co.order_id left join booking_units bu on co.booking_unit = bu.booking_code inner join rms_order_items roi on itm.item_id = roi.item_id left join gd_rms_fixed_formats grff on roi.fixed_format = grff.id left join gd_rms_format_types grft on roi.format_type = grft.id left join gd_rms_multi_discount grmd on roi.multi_discount = grmd.id left join gd_rms_page_positions grpp on roi.page_number = grpp.id inner join um_customers uc  left join gd_state gs ON uc.state = gs.state_code on co.customer_id = uc.customer_id  left join gd_rms_positioning_discount grpd on roi.page_position=grpd.id inner join rms_payments_response rps on rps.item_id=itm.item_id where itm.order_id in ('"
					+ joinedOrderIds + "') and itm.mark_as_delete = false";
			classifiedList = (List<Object[]>) baseDao.findBySQLQueryWithoutParams(query);

			for (Object[] objs : classifiedList) {
				ErpClassifieds classified = new ErpClassifieds();
				classified.setItemId((String) objs[0]);
				classified.setOrderId((String) objs[1]);
				classified.setClassifiedType((Integer) objs[2]);
				classified.setClassifiedAdsType((Integer) objs[3]);
				classified.setScheme((Integer) objs[4]);
				classified.setContent((String) objs[5]);
				classified.setCreatedBy((Integer) objs[6]);
				classified.setCreatedTs(CommonUtils.dateFormatter((Date) objs[7], "yyyy-MM-dd"));
				classified.setCreatedDate(CommonUtils.dateFormatter((Date) objs[7], "yyyyMMdd"));
				classified.setBookingDate(CommonUtils.dateFormatter((Date) objs[7], "yyyy-MM-dd HH:mm:ss"));
				classified.setChangedBy((Integer) objs[8]);
				classified.setChangedTs(objs[9] != null ? CommonUtils.dateFormatter((Date) objs[9], "ddMMyyyy") : "");
				classified.setClassifiedAdsSubType((Integer) objs[10]);
				classified.setContentStatus((String) objs[11]);
				classified.setAdId((String) objs[12]);
				classified.setGroup((Integer) objs[13]);
				classified.setSubGroup((Integer) objs[14]);
				classified.setChildGroup((Integer) objs[15]);
				classified.setCustomerId((String) objs[16]);
				classified.setUserTypeId((Integer) objs[17]);
				classified.setBookingUnit((Integer) objs[18]);
				Double val = (Double.valueOf(df.format(objs[19])));
				classified.setPaidAmount(new BigDecimal(df1.format(val)));
				classified.setClassifiedTypeStr((String) objs[20]);
				classified.setClassifiedTypeErpRefId((String) objs[21]);
				classified.setAdsType((String) objs[22]);
				classified.setAdsTypeErpRefId((String) objs[23]);
				classified.setAdsSubType((String) objs[24]);
				classified.setAdsSubTypeErpRefId((String) objs[25]);
				classified.setSchemeStr((String) objs[26]);
				classified.setSchemeErpRefId((String) objs[27]);
				classified.setGroupStr((String) objs[28]);
				classified.setGroupErpRefId((String) objs[29]);
				classified.setSubGroupStr((String) objs[30]);
				classified.setSubGroupErpRefId((String) objs[31]);
				classified.setChildGroupStr((String) objs[32]);
				classified.setChildGroupErpRefId((String) objs[33]);
				classified.setSalesOffice((String) objs[34]);
				classified.setBookingUnitStr((String) objs[35]);
				classified.setSoldToParty((String) objs[36]);
				classified.setBookingUnitEmail((String) objs[37]);
				classified.setNoOfInsertions((Integer) objs[38]);
				classified.setPagePosition((Integer) objs[41]);
				classified.setFormatType((Integer) objs[42]);
				classified.setFixedFormat((Integer) objs[43]);
				classified.setPageNumber((Integer) objs[44]);
				classified.setCategoryDiscount((Integer) objs[45]);
				Double mD = (Double.valueOf(df.format(objs[46])));
				classified.setMultiDiscount(new BigDecimal(df1.format(mD)));
				Double aD = (Double.valueOf(df.format(objs[47])));
				classified.setAdditionalDiscount(new BigDecimal(df1.format(aD)));
				Double sR = (Double.valueOf(df.format(objs[48])));
				classified.setSurchargeRate(new BigDecimal(df1.format(sR)));
				classified.setFixedFormatsize((String) objs[49]);
				classified.setFixedFormatErpRefId((String) objs[50]);
				classified.setFormatTypeStr((String) objs[51]);
				classified.setMultiDiscountStr((String) objs[52]);
				classified.setPagePositionpageName((String) objs[53]);
				classified.setPagePositionErpRefId((String) objs[54]);

				classified.setCityDisc((String) objs[57]);
				classified.setStateDisc((String) objs[58]);
				classified.setRate(new Double((Float) objs[63]));
				classified.setPositioningDesc((String) objs[64]);
				classified.setDiscountValue(new Double((Float) objs[65]));
				if (objs[39] != null) {
					classified.setSizeWidth(new Double((Float) objs[39]));
				}
				if (objs[40] != null) {
					classified.setSizeHeight(new Double((Float) objs[40]));
				}
				if (objs[66] != null) {
					classified.setIgst(new Double((Float) objs[66]));
				}
				if (objs[67] != null) {
					classified.setCgst(new Double((Float) objs[67]));
				}
				if (objs[68] != null) {
					classified.setSgst(new Double((Float) objs[68]));
				}
				if (objs[69] != null) {
					Double cVal = (Double.valueOf(df.format(objs[69])));
					classified.setCgstValue(new BigDecimal(df1.format(cVal)));
				}
				if (objs[70] != null) {
					Double sVal = (Double.valueOf(df.format(objs[70])));
					classified.setSgstValue(new BigDecimal(df1.format(sVal)));
				}
				if (objs[71] != null) {
					Double sVal = (Double.valueOf(df.format(objs[71])));
					classified.setIgstValue(new BigDecimal(df1.format(sVal)));
				}
				Double tVal = (Double.valueOf(df.format(objs[72])));
				classified.setTotalValue(new BigDecimal(df1.format(tVal)));
				classified.setBankOrBranch((String) objs[73]);
				classified.setCashReceiptNo((String) objs[74]);
				if (objs[75] != null) {
					classified.setSpaceWidth(new Double((Float) objs[75]));
				}
				if (objs[76] != null) {
					classified.setSpaceHeight(new Double((Float) objs[76]));
				}

				classified.setKeyword("Rms Order");
				classified.setTypeOfCustomer("01");
				classified.setCreatedTime(CommonUtils.dateFormatter((Date)objs[7],"HHmmss"));
				classified.setOrderIdentification("01");

				itemIds.add((String) objs[0]);
				customerIds.add((String) objs[16]);
				createdByIds.add((Integer) objs[6]);
				classifiedsMap.put((String) objs[0], classified);
			}

			if (itemIds != null && !itemIds.isEmpty()) {
				List<Object[]> editionsList = clfEditionsRepo.getRmsEditionIdAndNameOnItemId(itemIds);
				for (Object[] clObj : editionsList) {
					if (classifiedsMap.containsKey((String) clObj[0])) {
						if (classifiedsMap.get((String) clObj[0]).getEditions() != null) {
							classifiedsMap.get((String) clObj[0]).getEditions().add((String) clObj[2]);
							classifiedsMap.get((String) clObj[0]).getEditionsErpRefId().add((String) clObj[3]);
						} else {
							List<String> edditions = new ArrayList<>();
							List<String> edditionsErpRefIds = new ArrayList<>();
							edditions.add((String) clObj[2]);
							edditionsErpRefIds.add((String) clObj[3]);
							ErpClassifieds classified = classifiedsMap.get((String) clObj[0]);
							classified.setEditions(edditions);
							classified.setEditionsErpRefId(edditionsErpRefIds);
							classifiedsMap.put((String) clObj[0], classified);
						}
					}
				}

				List<Object[]> publishDatesList = clfPublishDatesRepo.getPublishDatesForErpData(itemIds);
				for (Object[] clObj : publishDatesList) {
					if (classifiedsMap.containsKey((String) clObj[0])) {
						if (classifiedsMap.get((String) clObj[0]).getPublishDates() != null) {
							classifiedsMap.get((String) clObj[0]).getPublishDates()
									.add(CommonUtils.dateFormatter((Date) clObj[1], "yyyyMMdd"));
						} else {
							List<String> publishDates = new ArrayList<>();
							publishDates.add(CommonUtils.dateFormatter((Date) clObj[1], "yyyyMMdd"));
							ErpClassifieds classified = classifiedsMap.get((String) clObj[0]);
							classified.setPublishDates(publishDates);
							classifiedsMap.put((String) clObj[0], classified);
						}
					}
				}

				if (customerIds != null) {
					List<UmCustomers> umCustomersList = umCustomersRepo.getMulCustomerDetails(customerIds);
					List<Integer> cityIds = new ArrayList<Integer>();
					if (!umCustomersList.isEmpty()) {
						classifiedsMap.entrySet().forEach(erpData -> {
							Optional<UmCustomers> umCus = umCustomersList.stream()
									.filter(f -> f.getCustomerId().equals(erpData.getValue().getCustomerId()))
									.findFirst();
							if (umCus.isPresent()) {
								UmCustomers umCustom = umCus.get();
								erpData.getValue().setCustomerName(umCustom.getCustomerName());
								erpData.getValue().setMobileNumber(umCustom.getMobileNo());
								erpData.getValue().setEmailId(umCustom.getEmailId());
								erpData.getValue().setAddress1(umCustom.getAddress1());
								erpData.getValue().setAddress2(umCustom.getAddress2());
								erpData.getValue().setAddress3(umCustom.getAddress3());
								erpData.getValue().setPinCode(umCustom.getPinCode());
								erpData.getValue().setOfficeLocation(umCustom.getOfficeLocation());
								erpData.getValue().setGstNo(umCustom.getGstNo());
								erpData.getValue().setPanNumber(umCustom.getPanNumber());
								erpData.getValue().setAadharNumber(umCustom.getAadharNumber());
								erpData.getValue().setErpRefId(umCustom.getErpRefId());
								erpData.getValue().setState(umCustom.getState());
								erpData.getValue().setCity(umCustom.getCity());
								erpData.getValue().setHouseNo(umCustom.getHouseNo());
								erpData.getValue().setClientCode(umCustom.getClientCode());
								if (umCustom != null && !umCustom.getCity().isEmpty()) {
									cityIds.add(Integer.parseInt(umCustom.getCity()));
								}
							}

						});
					}
				}

				if (!createdByIds.isEmpty()) {
					List<Integer> userTypes = new ArrayList<Integer>();
					List<UmUsers> umUsers = umUsersRepository.getUsersByCreatedId(createdByIds, false);
					if (!umUsers.isEmpty()) {
						classifiedsMap.entrySet().forEach(erpData -> {
							Optional<UmUsers> gd = umUsers.stream()
									.filter(f -> (f.getUserId()).equals(erpData.getValue().getCreatedBy())).findFirst();
							if (gd.isPresent()) {
								UmUsers umUser = gd.get();
								erpData.getValue().setEmpCode(umUser.getEmpCode());
								erpData.getValue().setCustomerName(umUser.getFirstName());
								if (!"2".equalsIgnoreCase(erpData.getValue().getUserTypeId() + "")) {
									erpData.getValue().setSoldToParty(umUser.getSoldToParty());
								}
								if ("3".equalsIgnoreCase(erpData.getValue().getUserTypeId() + "")) {
									erpData.getValue().setCustomerName(umUser.getFirstName());
									erpData.getValue().setMobileNumber(umUser.getMobile());
									erpData.getValue().setEmailId(umUser.getEmail());
									erpData.getValue().setAddress1(umUser.getAddress());
									erpData.getValue().setState(umUser.getState());
									erpData.getValue().setSoldToParty(umUser.getLogonId());
								}
								userTypes.add(umUser.getGdUserTypes().getUserTypeId());
							}
						});
					}

					if (!userTypes.isEmpty()) {
						List<GdUserTypes> gdUserTypes = gdUserTypesRepo.getUserTypesList(userTypes);
						classifiedsMap.entrySet().forEach(erpData -> {
							Optional<GdUserTypes> gd = gdUserTypes.stream()
									.filter(f -> (f.getUserTypeId()).equals(erpData.getValue().getUserTypeId()))
									.findFirst();
							if (gd.isPresent()) {
								GdUserTypes gdUserType = gd.get();
								erpData.getValue().setUserTypeIdErpRefId(gdUserType.getErpRefId());
							}
						});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifiedsMap;
	}

	@SuppressWarnings("unused")
	private void sendRmsMailToCustomer(Map<String, ErpClassifieds> erpClassifiedsMap,
			BillDeskPaymentResponseModel payload, LoggedUser loggedUser,
			ClfPaymentResponseTracking clfPaymentResponseTracking) {
		try {

			Map<String, Object> params = new HashMap<>();
			params.put("stype", SettingType.APP_SETTING.getValue());
			params.put("grps", Arrays.asList(GeneralConstants.EMAIL_SET_GP, GeneralConstants.ENV_SET_GP));
			SettingTo settingTo = settingDao.getSMTPSettingValues(params);
			Map<String, String> emailConfigs = settingTo.getSettings();

			Map<String, Object> mapProperties = new HashMap<String, Object>();
			EmailsTo emailTo = new EmailsTo();
			emailTo.setFrom(emailConfigs.get("EMAIL_FROM"));
			String [] ccMails = {loggedUser.getEmail()};
			emailTo.setBcc(ccMails);
			emailTo.setOrgId("1000");
			mapProperties.put("action_url", emailConfigs.get("WEB_URL"));
			mapProperties.put("logo_url", emailConfigs.get("LOGO_URL"));
			mapProperties.put("userName", loggedUser.getLogonId());// created by userName
			mapProperties.put("userId", loggedUser.getLogonId());// new userName
			erpClassifiedsMap.entrySet().forEach(erpData -> {
				emailTo.setTo(erpData.getValue().getEmailId());
				mapProperties.put("orderId", erpData.getValue().getAdId());
				mapProperties.put("clientCode", erpData.getValue().getClientCode());
				mapProperties.put("city", erpData.getValue().getCityDisc());
				mapProperties.put("state", erpData.getValue().getStateDisc());
				mapProperties.put("clientName", erpData.getValue().getCustomerName());
				mapProperties.put("amount", erpData.getValue().getPaidAmount());
				mapProperties.put("nameOfDiscount", erpData.getValue().getPositioningDesc());
				mapProperties.put("discountValue", erpData.getValue().getDiscountValue());
				mapProperties.put("iGst", erpData.getValue().getIgst());
				mapProperties.put("caption", erpData.getValue().getContent());
				mapProperties.put("approvalStatus", "PENDING");
				mapProperties.put("cGst", erpData.getValue().getCgst());
				mapProperties.put("sGst", erpData.getValue().getSgst());
				mapProperties.put("phone", erpData.getValue().getMobileNumber());
				mapProperties.put("Code", erpData.getValue().getAdId());
				mapProperties.put("categoryType", erpData.getValue().getAdsType());
				String gst = null;
				if (erpData.getValue().getIgst() != null) {
					gst = "<tr><th colspan=4>GST- IGST (" + erpData.getValue().getIgst()
							+ "%)</th><th style=\"text-align: right;\">" + erpData.getValue().getIgstValue()
							+ "</th></tr>";
					mapProperties.put("gst", gst);
				} else {
					gst = "</th></tr>" + "<tr><th colspan=4>GST- CGST(" + erpData.getValue().getCgst()
							+ "%)</th><th style=\"text-align: right;\">" + erpData.getValue().getCgstValue()
							+ "</th></tr>" + "<tr><th colspan=4>GST- SGST(" + erpData.getValue().getSgst()
							+ "%)</th><th style=\"text-align: right;\">" + erpData.getValue().getSgstValue()
							+ "</th></tr>";
					mapProperties.put("gst", gst);
				}
				mapProperties.put("totalValue", erpData.getValue().getTotalValue());
				mapProperties.put("bankOrBranch", erpData.getValue().getBankOrBranch());
				mapProperties.put("cashReceiptNo", erpData.getValue().getCashReceiptNo());
				mapProperties.put("scheme", erpData.getValue().getSchemeStr());

				mapProperties.put("street", erpData.getValue().getHouseNo());
				mapProperties.put("gstNo", erpData.getValue().getGstNo());
				mapProperties.put("pinCode", erpData.getValue().getPinCode());
				mapProperties.put("noOfInsertion", erpData.getValue().getNoOfInsertions());
				mapProperties.put("createdTs", erpData.getValue().getCreatedTs());
				mapProperties.put("sizeHeight", erpData.getValue().getSpaceHeight());
				mapProperties.put("sizeWidth", erpData.getValue().getSpaceWidth());
				mapProperties.put("Position", erpData.getValue().getPagePosition());
				mapProperties.put("employeeHrCode", erpData.getValue().getEmpCode());
				mapProperties.put("employee", erpData.getValue().getCustomerName());
				if (erpData.getValue().getSpaceWidth() != null && erpData.getValue().getSpaceHeight() != null) {
					mapProperties.put("space",
							erpData.getValue().getSpaceHeight() * erpData.getValue().getSpaceWidth());
				}
				mapProperties.put("cardRate", erpData.getValue().getRate());
				mapProperties.put("rate", erpData.getValue().getPaidAmount());

				List<String> pubDates = erpData.getValue().getPublishDates();
				List<String> pubDatesList = new ArrayList<String>();
				for (String pubD : pubDates) {
					Date date;
					String formattedDate = null;
					try {
						date = new SimpleDateFormat("yyyyMMdd").parse(pubD);
						formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					pubDatesList.add(formattedDate);
				}
				String publishDates = pubDatesList.stream().map(Object::toString).collect(Collectors.joining(","));
				mapProperties.put("date", publishDates);
				List<String> editionsList = erpData.getValue().getEditions();
				String editions = editionsList.stream().map(Object::toString).collect(Collectors.joining(","));
				mapProperties.put("editionName", editions);

				emailTo.setTemplateName(GeneralConstants.RMS_RO);
				emailTo.setTemplateProps(mapProperties);

//              String imagePath = "D:\\Projects During Training\\Pictures\\Screenshots\\Screenshot (19).png";
			//	String imagePath = "http://192.168.1.86:8072/staticresources/docs/Screenshot%20(1)_902a9525-356c-4e52-bc48-36dff71011de.png";
//				File pdfFile = null;
//				try {
//					pdfFile = convertImageToPdf(imagePath);
//				} catch (IOException | DocumentException e) {
//					e.printStackTrace();
//				} catch (java.io.IOException e) {
//					e.printStackTrace();
//				}

				List<Map<String, Object>> multiAttachments = new ArrayList<Map<String, Object>>();
				Map<String, Object> mapData = new HashMap<>();
				
				List<Attachments> allAttachmentsByCustomerId = rmsAttachmentsRepo.getAllAttachmentsByCustomerId(erpData.getValue().getCustomerId());
				if(allAttachmentsByCustomerId!=null && !allAttachmentsByCustomerId.isEmpty()) {
					for(Attachments attach:allAttachmentsByCustomerId) {
	                    mapData.put(attach.getAttachName(), new FileDataSource(getDIR_PATH_DOCS()+attach.getAttachUrl()));
					}
				}
				
				List<Object> pdfFileNames = new ArrayList<Object>(mapData.values());
	            List<String> fileNames = new ArrayList<>();
	            for(Object fileName : pdfFileNames) {
	            	  if (fileName instanceof String) {
	                      fileNames.add(getDIR_PATH_DOCS() + fileName.toString());
	                  } else if (fileName instanceof javax.activation.FileDataSource) {
	                      javax.activation.FileDataSource fileDataSource = (javax.activation.FileDataSource) fileName;
	                      fileNames.add(getDIR_PATH_DOCS() + fileDataSource.getName());
	                  } else {
	                      throw new IllegalArgumentException("Unsupported file name type: " + fileName.getClass());
	                  }
	            }
	            mapProperties.put("pdf_download", fileNames);
				try {
					this.genratePDF(erpClassifiedsMap,fileNames);
				} catch (IOException | DocumentException | java.io.IOException e) {
					e.printStackTrace();
				}
				 String pdfFilePath =  getDIR_PATH_PDF_DOCS()+erpData.getValue().getAdId()+".pdf";
		         mapData.put(erpData.getValue().getAdId()+".pdf", new FileDataSource(pdfFilePath));
		
				multiAttachments.add(mapData);
				
				emailTo.setDataSource(multiAttachments);
	            emailTo.setTemplateProps(mapProperties);
				sendService.sendCommunicationMail(emailTo, emailConfigs);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private File convertImageToPdf(String imageFile) throws IOException, DocumentException, java.io.IOException {
		BufferedImage image = javax.imageio.ImageIO.read(new File(imageFile));

		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		// Create a new Document with A4 page size
		Document document = new Document(PageSize.A4);

		// Create a temporary file for the PDF
		File pdfFile = File.createTempFile("image_to_pdf", ".pdf");

		// Create PdfWriter to write the document to the temporary PDF file
		PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

		int newWidth = 1400; // Adjust as needed
		int newHeight = 1200; // Adjust as needed

		// Resize the image
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, newWidth, newHeight, null);
		g.dispose();
		document.open();
		Image pdfImage = Image.getInstance(image, null);
		float scaleWidth = PageSize.A4.getWidth() / imageWidth;
		float scaleHeight = PageSize.A4.getHeight() / imageHeight;
		float scaleFactor = Math.min(scaleWidth, scaleHeight);
		pdfImage.scaleToFit(imageWidth * scaleFactor, imageHeight * scaleFactor);
		document.add(pdfImage);
		document.close();

		return pdfFile;
	}
	
	

	public void genratePDF(Map<String, ErpClassifieds> erpClassifiedsMap,List<String> fileNames)
			throws DocumentException, IOException, java.io.IOException {
		Document document = new Document(PageSize.A4);
		LineSeparator line = new LineSeparator();
		erpClassifiedsMap.entrySet().forEach(erpData -> {

			try {
				PdfWriter.getInstance(document, new FileOutputStream( getDIR_PATH_PDF_DOCS()+erpData.getValue().getAdId()+".pdf"));
				document.open();

				// Creating a table of the columns
				com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font();// text font
				fontHeader.setSize(11);
				com.itextpdf.text.Font fontSubHeader = new com.itextpdf.text.Font();
				fontSubHeader.setSize(10);
				com.itextpdf.text.Font fontColumn = new com.itextpdf.text.Font();
				fontColumn.setSize(8);

				line.setLineColor(BaseColor.BLACK);

//			PdfPTable header = new PdfPTable(1);
//			header.setWidthPercentage(100);
//			PdfPCell headerCell = new PdfPCell(new com.itextpdf.text.Paragraph("Welcome to Sakshi e-Classifieds!"));
//			headerCell.setBorder(0);
//			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//			header.addCell(headerCell);
//			document.add(header);
				//
//			document.add(new Paragraph(" "));
//			document.add(new Chunk(line));

				PdfPTable table = new PdfPTable(3);
				table.setWidthPercentage(100);
				PdfPCell cell1 = new PdfPCell(new com.itextpdf.text.Paragraph("JAGATI PUBLICATIONS LIMITED"));
				cell1.setBorder(0);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

				Image image1;
				image1 = Image
						.getInstance(new URL("https://pre-prod-asp.s3.ap-south-1.amazonaws.com/static_assets/u3.png"));
				image1.scaleAbsolute(80, 40);
//	            image1.scalePercent(20);
				PdfPCell cell2 = new PdfPCell(image1);
				cell2.setBorder(0);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell cell3 = new PdfPCell(new com.itextpdf.text.Paragraph("Casual Insertion Order"));
				cell3.setBorder(0);
				cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(cell1);
				table.addCell(cell2);
				table.addCell(cell3);

				document.add(table);

				PdfPTable child = new PdfPTable(2);
				child.setWidthPercentage(100);
				Paragraph address = new Paragraph("Banjara Hills Hyderabad", fontColumn);
				PdfPCell child1 = new PdfPCell(address);
				child1.setBorder(0);
				child1.setHorizontalAlignment(Element.ALIGN_LEFT);

				Paragraph id = new Paragraph("HSN/SAC Code :" + erpData.getValue().getAdId(), fontColumn);
//				PdfPCell child2 = new PdfPCell(new com.itextpdf.text.Paragraph("HSN/SAC Code : SCA241234"));
				PdfPCell child2 = new PdfPCell(id);
				child1.setBorder(0);
				child2.setBorder(0);
				child2.setHorizontalAlignment(Element.ALIGN_RIGHT);

				child.addCell(child1);
				child.addCell(child2);
				document.add(child);

				PdfPTable childG = new PdfPTable(1);
				childG.setWidthPercentage(100);
				Paragraph gstn = new Paragraph("GSTIN : 123456", fontColumn);
				PdfPCell child3 = new PdfPCell(gstn);
//				PdfPCell child3 = new PdfPCell(new com.itextpdf.text.Paragraph("GSTIN : 123456"));
				child3.setBorder(0);
				child3.setHorizontalAlignment(Element.ALIGN_LEFT);

				Paragraph mobile = new Paragraph("Ph : 9809098909", fontColumn);
				PdfPCell child4 = new PdfPCell(mobile);
//				PdfPCell child4 = new PdfPCell(new com.itextpdf.text.Paragraph("Ph : 9809098909"));
				child4.setBorder(0);
				child4.setHorizontalAlignment(Element.ALIGN_LEFT);
				childG.addCell(child3);
				childG.addCell(child4);
				document.add(childG);
				document.add(new Paragraph(" "));
				document.add(new Chunk(line));
				PdfPTable table1 = new PdfPTable(4);
				table1.addCell(createCell("Client Code", BaseColor.WHITE, fontSubHeader, false, false));
				table1.addCell(
						createCell(erpData.getValue().getClientCode(), BaseColor.WHITE, fontSubHeader, false, false));
				table1.addCell(createCell("City", BaseColor.WHITE, fontSubHeader, false, false));
				table1.addCell(
						createCell(erpData.getValue().getCityDisc(), BaseColor.WHITE, fontSubHeader, false, false));
				table1.setWidthPercentage(100);
				document.add(table1);
				PdfPTable table2 = new PdfPTable(4);
				table2.addCell(createCell("Order No", BaseColor.WHITE, fontSubHeader, false, false));
				table2.addCell(createCell(erpData.getValue().getAdId(), BaseColor.WHITE, fontSubHeader, false, false));
				table2.addCell(createCell("State", BaseColor.WHITE, fontSubHeader, false, false));
				table2.addCell(
						createCell(erpData.getValue().getStateDisc(), BaseColor.WHITE, fontSubHeader, false, false));
				table2.setWidthPercentage(100);
				document.add(table2);

				PdfPTable table3 = new PdfPTable(4);
				table3.addCell(createCell("Client Name ", BaseColor.WHITE, fontSubHeader, false, false));
				table3.addCell(
						createCell(erpData.getValue().getCustomerName(), BaseColor.WHITE, fontSubHeader, false, false));
				table3.addCell(createCell("Generated by", BaseColor.WHITE, fontSubHeader, false, false));
				table3.addCell(createCell("", BaseColor.WHITE, fontSubHeader, false, false));
				table3.setWidthPercentage(100);
				document.add(table3);

				PdfPTable table4 = new PdfPTable(4);
				table4.addCell(createCell("Date", BaseColor.WHITE, fontSubHeader, false, false));
				table4.addCell(
						createCell(erpData.getValue().getCreatedTs(), BaseColor.WHITE, fontSubHeader, false, false));
				table4.addCell(createCell("Phone", BaseColor.WHITE, fontSubHeader, false, false));
				table4.addCell(
						createCell(erpData.getValue().getMobileNumber(), BaseColor.WHITE, fontSubHeader, false, false));
				table4.setWidthPercentage(100);
				document.add(table4);

				PdfPTable table5 = new PdfPTable(4);
				table5.addCell(createCell("Street/House No/Locality", BaseColor.WHITE, fontSubHeader, false, false));
				table5.addCell(
						createCell(erpData.getValue().getHouseNo(), BaseColor.WHITE, fontSubHeader, false, false));
				table5.addCell(createCell("Client GSTN", BaseColor.WHITE, fontSubHeader, false, false));
				table5.addCell(createCell(erpData.getValue().getGstNo(), BaseColor.WHITE, fontSubHeader, false, false));
				table5.setWidthPercentage(100);
				document.add(table5);

				PdfPTable table6 = new PdfPTable(4);
				table6.addCell(createCell("Employee / HR Code", BaseColor.WHITE, fontSubHeader, false, false));
				table6.addCell(
						createCell(erpData.getValue().getEmpCode(), BaseColor.WHITE, fontSubHeader, false, false));
				table6.addCell(createCell("Advertiesement Caption", BaseColor.WHITE, fontSubHeader, false, false));
				table6.addCell(
						createCell(erpData.getValue().getContent(), BaseColor.WHITE, fontSubHeader, false, false));
				table6.setWidthPercentage(100);
				document.add(table6);

				PdfPTable table7 = new PdfPTable(4);
				table7.addCell(createCell("PinCode", BaseColor.WHITE, fontSubHeader, false, false));
				table7.addCell(
						createCell(erpData.getValue().getPinCode(), BaseColor.WHITE, fontSubHeader, false, false));
				table7.addCell(createCell("No of Insertions", BaseColor.WHITE, fontSubHeader, false, false));
				table7.addCell(createCell(Integer.toString(erpData.getValue().getNoOfInsertions()), BaseColor.WHITE,
						fontSubHeader, false, false));
				table7.setWidthPercentage(100);
				document.add(table7);

				PdfPTable table8 = new PdfPTable(4);
				table8.addCell(createCell("Employee", BaseColor.WHITE, fontSubHeader, false, false));
				table8.addCell(
						createCell(erpData.getValue().getCustomerName(), BaseColor.WHITE, fontSubHeader, false, false));
				table8.addCell(createCell("Scheme", BaseColor.WHITE, fontSubHeader, false, false));
				table8.addCell(
						createCell(erpData.getValue().getSchemeStr(), BaseColor.WHITE, fontSubHeader, false, false));
				table8.setWidthPercentage(100);
				document.add(table8);

				document.add(new Paragraph(" "));

				PdfPTable tableF = new PdfPTable(8);
				BaseColor silverColor = new BaseColor(192, 192, 192);
				tableF.addCell(createCell("Date of Insertion", silverColor, fontSubHeader, false, false));
				tableF.addCell(createCell("District/Edition", silverColor, fontSubHeader, false, false));
				tableF.addCell(createCell("Size Width", silverColor, fontSubHeader, false, false));
				tableF.addCell(createCell("Size Height", silverColor, fontSubHeader, false, false));
				tableF.addCell(createCell("Space", silverColor, fontSubHeader, false, false));
				tableF.addCell(createCell("Card Rate", silverColor, fontSubHeader, false, false));
				tableF.addCell(createCell("Position", silverColor, fontSubHeader, false, false));
				tableF.addCell(createCell("Total Amount", silverColor, fontSubHeader, false, false));
				tableF.setWidthPercentage(100);
				document.add(tableF);

				PdfPTable tableG = new PdfPTable(8);
				tableG.addCell(
						createCell(erpData.getValue().getCreatedTs(), BaseColor.WHITE, fontColumn, false, false));
				List<String> editions = erpData.getValue().getEditions();
				String editionsString = String.join(", ", editions);
				tableG.addCell(createCell(editionsString, BaseColor.WHITE, fontColumn, false, false));
				tableG.addCell(createCell(Double.toString(erpData.getValue().getSpaceWidth()), BaseColor.WHITE,
						fontColumn, false, false));
				tableG.addCell(createCell(Double.toString(erpData.getValue().getSpaceHeight()), BaseColor.WHITE,
						fontColumn, false, false));
				tableG.addCell(createCell(
						Double.toString(erpData.getValue().getSpaceWidth() * erpData.getValue().getSpaceHeight()),
						BaseColor.WHITE, fontColumn, false, false));
				tableG.addCell(createCell(Double.toString(erpData.getValue().getRate()), BaseColor.WHITE, fontColumn,
						false, false));
				tableG.addCell(createCell(Integer.toString(erpData.getValue().getPagePosition()), BaseColor.WHITE,
						fontColumn, false, false));
				tableG.addCell(createCell(erpData.getValue().getPaidAmount().toString(), BaseColor.WHITE, fontColumn,
						false, false));
				tableG.setWidthPercentage(100);
				document.add(tableG);

				PdfPTable totalAmount = new PdfPTable(8);
				PdfPCell totalLabelCell = createCell("Total", BaseColor.WHITE, fontSubHeader, false, false);
				totalLabelCell.setColspan(7);
				totalAmount.addCell(totalLabelCell);
				totalAmount.addCell(createCell(erpData.getValue().getPaidAmount().toString(), BaseColor.WHITE,
						fontSubHeader, false, false));
				totalAmount.setWidthPercentage(100);
				document.add(totalAmount);

				document.add(new Paragraph(" "));

				PdfPTable discount = new PdfPTable(5);
				discount.addCell(createCell("Name of discount", silverColor, fontSubHeader, false, false));
				discount.addCell(createCell("Category type", silverColor, fontSubHeader, false, false));
				discount.addCell(createCell("Discount %", silverColor, fontSubHeader, false, false));
				discount.addCell(createCell("Discount Value", silverColor, fontSubHeader, false, false));
				discount.addCell(createCell("Balance after discount (to be hide only for calculation)", silverColor,
						fontSubHeader, false, false));
				discount.setWidthPercentage(100);
				document.add(discount);

				PdfPTable discountVal = new PdfPTable(5);
				discountVal.addCell(
						createCell(erpData.getValue().getPositioningDesc(), BaseColor.WHITE, fontColumn, false, false));
				discountVal.addCell(
						createCell(erpData.getValue().getAdsType(), BaseColor.WHITE, fontColumn, false, false));
				discountVal.addCell(createCell("C", BaseColor.WHITE, fontColumn, false, false));
				discountVal.addCell(createCell(Double.toString(erpData.getValue().getDiscountValue()), BaseColor.WHITE,
						fontColumn, false, false));
				discountVal.addCell(createCell("", BaseColor.WHITE, fontColumn, false, false));
				discountVal.setWidthPercentage(100);
				document.add(discountVal);
				PdfPTable totalAmount1 = new PdfPTable(5);
				PdfPCell totalLabelCell1 = createCell("Total Discount", BaseColor.WHITE, fontSubHeader, false, false);
				totalLabelCell1.setColspan(4);
				totalLabelCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				totalAmount1.addCell(totalLabelCell1);
				totalAmount1.addCell(createCell("100", BaseColor.WHITE, fontSubHeader, false, false));

				PdfPCell totalLabelCell2 = createCell("Taxable value", BaseColor.WHITE, fontSubHeader, false, false);
				totalLabelCell2.setColspan(4);
				totalAmount1.addCell(totalLabelCell2);
				totalAmount1.addCell(createCell("100", BaseColor.WHITE, fontSubHeader, false, false));

//				totalAmount1.setWidthPercentage(100);
//				document.add(totalAmount1);
				if (erpData.getValue().getIgst() != null) {
					PdfPCell totalLabelCell3 = createCell("GST(IGST)", BaseColor.WHITE, fontSubHeader, false, false);
					totalLabelCell3.setColspan(4);
					totalAmount1.addCell(totalLabelCell3);
					totalAmount1.addCell(createCell(
							erpData.getValue().getIgstValue() != null ? erpData.getValue().getIgstValue().toString()
									: "",
							BaseColor.WHITE, fontSubHeader, false, false));

				} else {
					PdfPCell totalLabelCell4 = createCell("GST(CGST)", BaseColor.WHITE, fontSubHeader, false, false);
					totalLabelCell4.setColspan(4);
					totalAmount1.addCell(totalLabelCell4);
					totalAmount1.addCell(createCell(
							erpData.getValue().getCgstValue() != null ? erpData.getValue().getCgstValue().toString()
									: "",
							BaseColor.WHITE, fontSubHeader, false, false));

					PdfPCell totalLabelCell5 = createCell("GST(SGST)", BaseColor.WHITE, fontSubHeader, false, false);
					totalLabelCell5.setColspan(4);
					totalAmount1.addCell(totalLabelCell5);
					totalAmount1.addCell(createCell(
							erpData.getValue().getSgstValue() != null ? erpData.getValue().getSgstValue().toString()
									: "",
							BaseColor.WHITE, fontSubHeader, false, false));

				}

				PdfPCell totalLabelCell6 = createCell("Total Value", BaseColor.WHITE, fontSubHeader, false, false);
				totalLabelCell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				totalLabelCell6.setColspan(4);
				totalAmount1.addCell(totalLabelCell6);
				totalAmount1.addCell(createCell(erpData.getValue().getTotalValue().toString(), BaseColor.WHITE,
						fontSubHeader, false, false));

				totalAmount1.setWidthPercentage(100);
				document.add(totalAmount1);

				document.add(new Paragraph(" "));

				document.add(new Chunk(line));
				document.add(new Paragraph(" "));

				Paragraph mainContent = new Paragraph(
						"Advertisement may be released as per the above details. Subject to your Terms & Conditions mentioned in the Rate Card.");
				document.add(mainContent);

				document.add(new Chunk(line));

				Paragraph paymentParticulars = new Paragraph("Payment Particulars");
				document.add(paymentParticulars);
				document.add(new Paragraph(" "));
				PdfPTable tabl = new PdfPTable(3);
				tabl.addCell(createCell("Chq./D.D No ", BaseColor.WHITE, fontColumn, false, false));
				tabl.addCell(createCell("Bank/Branch", BaseColor.WHITE, fontColumn, false, false));
				tabl.addCell(createCell("Cash Receipt No", BaseColor.WHITE, fontColumn, false, false));
				tabl.setWidthPercentage(100);
				document.add(tabl);

				PdfPTable tabl1 = new PdfPTable(3);
				tabl1.addCell(createCell("A", BaseColor.WHITE, fontColumn, false, false));
				tabl1.addCell(
						createCell(erpData.getValue().getBankOrBranch(), BaseColor.WHITE, fontColumn, false, false));
				tabl1.addCell(
						createCell(erpData.getValue().getCashReceiptNo(), BaseColor.WHITE, fontColumn, false, false));
				tabl1.setWidthPercentage(100);
				document.add(tabl1);

				Paragraph signatureLines = new Paragraph("*All Advertisements are accepted on advance payment only");
				document.add(signatureLines);

				document.add(new Chunk(line));
				Paragraph remarks = new Paragraph("Remarks.");
				document.add(remarks);
				document.add(new Chunk(line));
				document.add(new Paragraph(" "));

				PdfPTable sig = new PdfPTable(2);
				sig.setWidthPercentage(100);
				PdfPCell sig1 = new PdfPCell(new com.itextpdf.text.Paragraph("(E-Signature of the employee)"));
				sig1.setBorder(0);
				sig1.setHorizontalAlignment(Element.ALIGN_LEFT);

				PdfPCell sigA1 = new PdfPCell(new com.itextpdf.text.Paragraph("(E-Signature of the client)"));
				sigA1.setBorder(0);
				sigA1.setHorizontalAlignment(Element.ALIGN_RIGHT);

				sig.addCell(sig1);
				sig.addCell(sigA1);
				document.add(sig);

				document.add(new Paragraph(" "));
				PdfPTable sigAdvt = new PdfPTable(2);
				sigAdvt.setWidthPercentage(100);
				PdfPCell sigAdvt1 = new PdfPCell(new com.itextpdf.text.Paragraph("Advt.Rep."));
				sigAdvt1.setBorder(0);
				sigAdvt1.setHorizontalAlignment(Element.ALIGN_LEFT);

				PdfPCell sigAdv = new PdfPCell(new com.itextpdf.text.Paragraph("Signature of the Advertiser"));
				sigAdv.setBorder(0);
				sigAdv.setHorizontalAlignment(Element.ALIGN_RIGHT);

				sigAdvt.addCell(sigAdvt1);
				sigAdvt.addCell(sigAdv);
				document.add(sigAdvt);
				document.add(new Paragraph(" "));

				for(String fileName:fileNames) {
					Image image = Image.getInstance(fileName);
					image.scaleAbsolute(200, 100);
					document.add(image);
				}
				document.add(new Paragraph(" "));
				document.close();

				System.out.println("PDF generated successfully.");
				
			} catch (java.io.IOException | DocumentException e) {
				e.printStackTrace();
			}
		});

	}

	private static PdfPCell createCell(String text, BaseColor backgroundColor, com.itextpdf.text.Font font,
			boolean textColor, boolean border) {
		PdfPCell cell = new PdfPCell();
		cell.addElement(new com.itextpdf.text.Paragraph(text, font));
		if (textColor) {
			font.setColor(BaseColor.WHITE);
		}
		// cell.setHorizontalAlignment(PdfPCell.ALIGN_MIDDLE);
		// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBackgroundColor(backgroundColor);
		if (border) {
			cell.setPaddingLeft(5);
			cell.setPaddingRight(5);
			cell.setPaddingBottom(5);
			cell.setPaddingTop(0);
		}
		return cell;
	}

	@Override
	public GenericApiResponse generateSendPaymentLink(RmsPaymentLinkModel payload) {
		GenericApiResponse apiResponse = new GenericApiResponse();
		apiResponse.setStatus(0);
		try {
			if (payload.getOrderId() != null && payload.getItemId() != null) {
				List<Object[]> data = clfOrderItemsRepo.getOrderDetailsOnItemId(payload.getItemId());
				if (data != null && !data.isEmpty()) {
					for (Object[] obj : data) {
						Map<String, Object> params = new HashMap<>();
						params.put("stype", SettingType.APP_SETTING.getValue());
						params.put("grps", Arrays.asList(GeneralConstants.EMAIL_SET_GP, GeneralConstants.ENV_SET_GP));
						SettingTo settingTo = settingDao.getSMTPSettingValues(params);
						Map<String, String> emailConfigs = settingTo.getSettings();
						Map<String, Object> mapProperties = new HashMap<String, Object>();
						EmailsTo emailTo = new EmailsTo();
						emailTo.setFrom(emailConfigs.get("EMAIL_FROM"));
						if (obj[0] != null) {
							emailTo.setTo((String) obj[0]);
							String[] cc = { userContext.getLoggedUser().getEmail() };
							emailTo.setBcc(cc);
						} else {
							emailTo.setTo(userContext.getLoggedUser().getEmail());
						}
						emailTo.setOrgId("1000");
						mapProperties.put("action_url", emailConfigs.get("WEB_URL"));
						mapProperties.put("logo_url", emailConfigs.get("LOGO_URL"));
						emailTo.setTemplateName(GeneralConstants.GENERATE_PAYMENT_LINK);

						String amount = obj[2] + "";
						String paymentChildId = (String) obj[3];
						String adId = (String) obj[4];
						String accessObjectId = commonService.getRequestHeaders().getAccessObjId();

						mapProperties.put("link",
								prop.getProperty("PORTAL_URL") + "rms/checkout?orderId=" + payload.getOrderId() + "&itemId=" + payload.getItemId()
										+ "&amount=" + amount + "&paymentChildId=" + paymentChildId + "&adId=" + adId
										+ "&tokenObject=" + payload.getTokenObject() + "&accessObjectId=" + accessObjectId);
						emailTo.setTemplateProps(mapProperties);

						sendService.sendCommunicationMail(emailTo, emailConfigs);
					}
				}
			}
		} catch (Exception e) {
			apiResponse.setStatus(1);
			apiResponse.setMessage("Link not generated");
			e.printStackTrace();
		}
		return apiResponse;
	}

	public static void main(String[] args){

	}
}