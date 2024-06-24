package com.portal.clf.service;

import java.util.LinkedHashMap;
import java.util.List;

public interface ClassifiedDownloadService {

	public byte[] creteTablePdf(LinkedHashMap<String, List<Object>> dataMap);

	public byte[] createTableText(LinkedHashMap<String, List<Object>> dataMap);
}
