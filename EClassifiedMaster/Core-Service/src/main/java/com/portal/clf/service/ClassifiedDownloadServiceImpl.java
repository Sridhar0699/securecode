package com.portal.clf.service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.springframework.stereotype.Service;

@Service
public class ClassifiedDownloadServiceImpl implements ClassifiedDownloadService {
	public byte[] creteTablePdf(LinkedHashMap<String, List<Object>> dataMap)  {
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		contentStream.setLineWidth(1);
		int initX = 50;
		int initY = 800 - 50;
		int cellHeight = 20;
		int cellWidth = 100;
		FontName font_name_3v = Standard14Fonts.getMappedFontName("Times-Roman");
		FontName font_name_3v_bold = Standard14Fonts.getMappedFontName("Times-Bold");
		PDFont pdfFont = new PDType1Font(font_name_3v);
		PDFont pdfFontBold = new PDType1Font(font_name_3v_bold);
		dataMap = splitLines(dataMap);
		for (Map.Entry<String, List<Object>> data : dataMap.entrySet()) {
			contentStream.addRect(initX, initY, cellWidth, -cellHeight);
			contentStream.beginText();
			contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
			contentStream.setFont(pdfFontBold, 11);
			contentStream.showText(data.getKey());
			contentStream.endText();
			initY -= cellHeight;
			if (initY == 50 && initX > 400) {
				page = new PDPage();
				document.addPage(page);
				contentStream.close();
				contentStream = new PDPageContentStream(document, page);
				initY = 800 - 50;
				initX = 50;
			} else if (initY == 50 && initX <= (cellWidth + initX) + 25) {
				initX = (initX + cellWidth) + 25;
				initY = 800 - 50;
			}

			for (Object adsLineObj : data.getValue()) {

				if (adsLineObj instanceof String) {
					String adsLine = (String) adsLineObj;
					contentStream.addRect(initX, initY, cellWidth, -cellHeight);

					contentStream.beginText();
					contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
					contentStream.setFont(pdfFont, 10);
					contentStream.showText(adsLine);
					contentStream.endText();

					initY -= cellHeight;
					if (initY == 50 && initX > 400) {
						page = new PDPage();
						document.addPage(page);
						contentStream.close();
						contentStream = new PDPageContentStream(document, page);
						initY = 800 - 50;
						initX = 50;
					} else if (initY <= 50 && initX <= (cellWidth + initX) + 25) {
						initX = (initX + cellWidth) + 25;
						initY = 800 - 50;
					}
				} else if (adsLineObj instanceof List) {
					List<String> lineParts = (List) adsLineObj;
					int partsIndex = 0;
					for (String parts : lineParts) {

						String adsLine = (String) parts;
						// contentStream.addRect(initX, initY, cellWidth,
						// -cellHeight);
						if (partsIndex == 0) {
							contentStream.beginText();
							contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
							contentStream.setFont(pdfFont, 10);
							contentStream.showText(adsLine);
							contentStream.endText();
							initY -= cellHeight;
						} else {
							contentStream.beginText();
							contentStream.newLineAtOffset(initX + 10, initY);
							contentStream.setFont(pdfFont, 10);
							contentStream.showText(adsLine.trim());
							contentStream.endText();
							initY -= 10;
						}

						if (initY == 50 && initX > 400) {
							page = new PDPage();
							document.addPage(page);
							contentStream.close();
							contentStream = new PDPageContentStream(document, page);
							initY = 800 - 50;
							initX = 50;
						} else if (initY == 50 && initX <= (cellWidth + initX) + 25) {
							initX = (initX + cellWidth) + 25;
							initY = 800 - 50;
						}
						partsIndex++;
					}
				}
			}
		}
		
		contentStream.close();
		document.save(byteArrayOutputStream);
		document.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public LinkedHashMap<String, List<Object>> splitLines(Map<String, List<Object>> data) {
		LinkedHashMap<String, List<Object>> tmpDataMap = new LinkedHashMap<>();
		FontName font_name_3v_bold = Standard14Fonts.getMappedFontName("Times-Bold");
		for (Map.Entry<String, List<Object>> dataMap : data.entrySet()) {
			List<Object> lines = new ArrayList<>();
			try {
				int partSize = 17;
				for (Object line : dataMap.getValue()) {
					if (((String) line).length() < partSize) {
						lines.add(line);
					} else {
						List<String> tmpLines = new ArrayList<>();
						while (((String) line).length() > partSize) {
							tmpLines.add(((String) line).substring(0, (partSize + 1)));
							line = ((String) line).substring((partSize + 1)).trim();
						}
						if (!((String) line).isEmpty()) {
							tmpLines.add((String) line);
							line = "";
						}
						lines.add(tmpLines);
					}
				}
			} catch (Exception e) {
			}
			tmpDataMap.put(dataMap.getKey(), lines);
		}
		return tmpDataMap;
	}

	public static void main(String a[]) {
		try {
			LinkedHashMap<String, List<Object>> dataMap = new LinkedHashMap<>();
			List<Object> ads = new ArrayList<>();
			ads.add("Wanted");
			ads.add("Business ads 1");
			ads.add("Wanted sales boyes for my cloths shop in guntur brodipet line no 4.");
			// ads.add("గుంటూరు బ్రోడీపేట్ లైన్ నంబర్ 4లో నా బట్టల షాపులకు
			// సేల్స్ బాయ్స్ కావాలి.");
			ads.add("Business ads 1");
			ads.add("Business ads 2");
			dataMap.put("Header 1", ads);
			ClassifiedDownloadServiceImpl cd = new ClassifiedDownloadServiceImpl();
			/*
			 * OutputStream out = new
			 * FileOutputStream("/home/incresol/Documents/Sathish Backups 07-04-2021/Sakshi Classified/pdfDocs/out.pdf"
			 * ); ClassifiedsDownloadServiceImpl cd = new
			 * ClassifiedsDownloadServiceImpl();
			 * out.write(((ByteArrayOutputStream)
			 * cd.downloadLineAdsDocument().getData()).toByteArray());
			 */
			cd.creteTablePdf(dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] createTableText(LinkedHashMap<String, List<Object>> dataMap) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, List<Object>> entry : dataMap.entrySet()) {
			sb.append(entry.getKey()).append("\n");
			sb.append("\n");
			List<Object> values = entry.getValue();
			for (Object value : values) {
				sb.append(value.toString()).append("\n");
			}
			sb.append("\n\n");
		}
		byte[] byteArray = null;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			outputStream.write(sb.toString().getBytes());
			byteArray = outputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;
	}

}
