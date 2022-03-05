package com.example.app;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.app.helpers.ResourceUtils;

/**
 * Hello world!
 *
 */
@Configuration
@ComponentScan
public class App {
	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		applicationContext = new AnnotationConfigApplicationContext(App.class);
		applicationContext.getBean(DataExtractor.class).extractData();

		try {
			PDDocument doc = PDDocument
					.load(ResourceUtils.readInputStreamFromResources(ResourceUtils.PDF_RESOURCE_DIR + "juhaszok1.pdf"));
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			String text = pdfTextStripper.getText(doc);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Bean(destroyMethod = "quit")
	public WebDriver webDriver() {
		System.setProperty("webdriver.chrome.driver","D:\\Programs\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}
}
