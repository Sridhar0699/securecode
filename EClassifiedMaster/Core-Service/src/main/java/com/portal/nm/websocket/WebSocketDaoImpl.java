package com.portal.nm.websocket;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portal.clf.entities.ClfOrders;
import com.portal.clf.models.ClassifiedConstants;
import com.portal.nm.model.Notifications;
import com.portal.repository.ClfOrderItemsRepo;
import com.portal.repository.ClfOrdersRepo;
import com.portal.repository.NmNotificationsRepo;

@Service
public class WebSocketDaoImpl implements WebSocketDao {

	private static final Logger logger = Logger.getLogger(WebSocketDaoImpl.class);

	@Autowired
	private NmNotificationsRepo nmNotificationsRepo;
	
	@Autowired
	private ClfOrdersRepo clfOrdersRepo;
	
	@Autowired
	private ClfOrderItemsRepo clfOrderItemsRepo;

	public long getUnreadNotificationCounts(Notifications notification) {
		String METHOD_NAME = "getUnreadNotificationCounts";
		logger.info("Entered into the method Cloud-Service ::" +METHOD_NAME);
		long count = 0;
		try {
			count = nmNotificationsRepo.getUnReadNotificationCount(notification.getUserId(), notification.getOrgId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while getting unread Notification Counts: " + ExceptionUtils.getStackTrace(e));
		}
		logger.info("Exit from the method Cloud-Service :: " + METHOD_NAME);
		return count;
	}

	@Override
	public long getPendingCartCount(Notifications notification) {
		String METHOD_NAME = "getPendingCartCount";
		logger.info("Entered into the method Cloud-Service ::" +METHOD_NAME);
		long count = 0;
		List<Object[]> counts = new ArrayList<>();
		List<String> itemIds = new ArrayList<>();
		try {
			List<ClfOrders> clfOrders = clfOrdersRepo.getOpenOrderDetailsByLoggedInUser(notification.getUserId(), ClassifiedConstants.ORDER_OPEN);
			ClfOrders clf = new ClfOrders();
			if(!clfOrders.isEmpty())
				 clf = clfOrders.get(0);
			else
				return count;
			counts = clfOrderItemsRepo.getPendingCartItems(clf.getOrderId());
			if (counts != null && !counts.isEmpty()) {
				for (Object[] obj : counts) {
					if (!itemIds.contains(obj[0])) {
						itemIds.add((String) obj[0]);
						count = count + 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while getting Cart Items Counts: " + ExceptionUtils.getStackTrace(e));
		}
		logger.info("Exit from the method Cloud-Service :: " + METHOD_NAME);
		return count;
	}
}
