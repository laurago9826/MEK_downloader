package com.example.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrowserSearchService {

	private static final String BASE_URL = "https://mek.oszk.hu/katalog/index.phtml";
	private static final String AUTHOR_INPUT_SELECTOR = "form[name='katal'] .bor03 > input[type='text']";
	//second
	private static final String SEARCH_BUTTON_SELECTOR = "table td > input[src='/html/kepek/gombker.gif']";
	private static final List<String> FORBIDDEN_WORDS = Arrays.asList("vers", "hangoskönyv");
	
	@Autowired
	private WebDriver driver;
	
	public List<Content> getAuthorContentInfo(String authorName) {
//		authorName = "Kosztolányi Dezső";
		List<Content> contentData = new ArrayList<>();
		//search by author
		driver.get(BASE_URL);
		WebElement authorInput = driver.findElements(By.cssSelector(AUTHOR_INPUT_SELECTOR)).get(1);
		authorInput.sendKeys(authorName);
		WebElement searchButton = driver.findElement(By.cssSelector(SEARCH_BUTTON_SELECTOR));
		searchButton.click();
		//extract titles and urls from search result
		List<WebElement> entries = driver.findElements(By.cssSelector(".bor03"));
		for (WebElement element : entries) {
			Content content = readBasicAuthorshipContentFromTile(authorName, element);
			if (content != null) {
				contentData.add(content);
			}
		}
		for (Content c : contentData) {
			c.setHtmlUrl(extractHtmlUrlFromPage(c.getUrl()));
		}
		
		return contentData;
	}
	
	private Content readBasicAuthorshipContentFromTile(String origAuthorName, WebElement tile) {
		WebElement titleElement = findElementWithNullCheck(tile, "form b > a");
		String title = titleElement == null ? null : titleElement.getText();
		if (title == null || !title.startsWith(origAuthorName)
				|| FORBIDDEN_WORDS.stream().anyMatch(w -> title.toLowerCase().contains(w))) {
			return null;
		}
		WebElement linkElement = findElementWithNullCheck(tile, ".allis > a");
		String link = linkElement == null ? null : linkElement.getText();
		int separatorIndex = title == null ? null : title.indexOf(':');
		if (separatorIndex != -1) {
			String author = title.substring(0, separatorIndex).trim();
			String contentTitle = title.substring(separatorIndex + 1).trim();
			return new Content(author, contentTitle, link);
		}
		return null;
	}
	
	private String extractHtmlUrlFromPage(String pageUrl) {
		driver.get(pageUrl);
		List<WebElement> elements = driver.findElements(By.cssSelector(".bor02 a"));
		WebElement correctHtmlLink = elements.stream()
				.filter(we -> we.getAttribute("title").contains("HTML")
						&& we.getAttribute("title").contains("Web") )
				.findFirst().orElse(null);
		return correctHtmlLink != null && !correctHtmlLink.getAttribute("href").endsWith("#") ?
				correctHtmlLink.getAttribute("href") : null;
	}
	
	public String downloadHtmlContent(String htmlUrl) {
		driver.get(htmlUrl);
		List<WebElement> elements = new ArrayList<>(driver.findElements(By.cssSelector("p[align='JUSTIFY']")));
		elements.addAll(driver.findElements(By.cssSelector("p[align='JUSTIFY'] > font")));
		elements = elements.stream().filter(e ->
			Integer.parseInt(e.getCssValue("font-weight")) < 600).collect(Collectors.toList());
		List<String> elementTexts = elements.stream().map(WebElement::getText).collect(Collectors.toList());
		driver.navigate().back();
		return extractAndConcatText(new LinkedHashSet<>(elementTexts));
	}
	
	private String extractAndConcatText(Collection<String> elements) {
		StringBuilder stringBuilder = new StringBuilder();
		elements.forEach(text -> {
			if (text.length() > 0 && Character.isDigit(text.charAt(0))
					|| !text.contains(".") || !text.contains("!") || !text.contains("?") //nem mondat)
				) {
				stringBuilder.append(text);
				stringBuilder.append("\n");
			}
		});
		return stringBuilder.toString();
	}
	
	private WebElement findElementWithNullCheck(WebElement parentElement, String cssSelector) {
		List<WebElement> elements = parentElement.findElements(By.cssSelector(cssSelector));
		return elements.isEmpty() ? null : elements.get(0);
	}
	
	@PreDestroy
	public void closeBrowser() {
		driver.quit();
	}
}
