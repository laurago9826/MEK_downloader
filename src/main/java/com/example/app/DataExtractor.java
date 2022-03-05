package com.example.app;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.helpers.ResourceUtils;

@Service
public class DataExtractor {

	@Autowired
	private AuthorNameCollectorService authorNameCollector;
	
	@Autowired
	private BrowserSearchService downloaderService;
	
	public void extractData() {
		List<String> authors = authorNameCollector.getAuthorsList();
		for (String author : authors) {
			List<Content> content = downloaderService.getAuthorContentInfo(author);
			content.forEach(this::extractAndWriteToFile);
		}
	}
	
	private void extractAndWriteToFile(Content content) {
		if (content.getHtmlUrl() == null) {
			return;
		}
		String fileName = ResourceUtils.EXTRACTED_TEXT_DIR
				+ content.getAuthor() + "_" + content.getTitle() + ".txt";
		String text = downloaderService.downloadHtmlContent(content.getHtmlUrl());
		if (text != null && !text.equals("") && !isDrama(text)) {
			ResourceUtils.writeTextToFile(fileName, filterLines(text));
		}
	}
	
	//filter out all upper case lines and lines starting with numbers --> usually references
	private String filterLines(String text) {
		List<String> lines = text.lines().filter(line -> {
			if (line.toUpperCase().equals(line) ||
					line.length() > 0 && Character.isDigit(line.charAt(0))) {
				return false;
			}
			return true;
		}).collect(Collectors.toList());
		StringBuilder sb = new StringBuilder();
		lines.forEach(l -> {
			sb.append(l);
			sb.append("\n");
		});
		return sb.toString();
	}
	
	private boolean isDrama(String text) {
		long linesCount = text.lines().count();
		long startOfLineIsUpperCaseCount = text.lines().filter(l -> {
			String strippedLine = l.replaceAll("[^a-zA-Z0-9]","");
			String textStart = strippedLine.substring(0, Math.min(4, strippedLine.length()));
			return textStart.toUpperCase().equals(textStart);
		}).count();
		return (double)linesCount / startOfLineIsUpperCaseCount < 15;
	}
}
