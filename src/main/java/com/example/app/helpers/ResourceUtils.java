package com.example.app.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceUtils {

	public static final String PDF_RESOURCE_DIR = "pdf/";
	public static final String EXTRACTED_TEXT_DIR = "D:\\ML\\HUN-DATA-COLLECTION\\";

	public static InputStream readInputStreamFromResources(String filePath) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		return classloader.getResourceAsStream(filePath);
	}

	public static URL readUrlFromResources(String filePath) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URL url = classloader.getResource(filePath);
		return url;
	}

	public static String readContentFromInputStream(InputStream is) {
		try {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			return null;
		}
	}

	public static List<String> readLinesFromResourceFile(InputStream is) {
		try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(is))) {
			return bufReader.lines().collect(Collectors.toList());
		} catch (IOException e) {
			return new ArrayList<>();
		}
	}
	
	public static void writeTextToFile(String filePath, String text) {
		try (FileWriter fileWriter = new FileWriter(new File(filePath))) {
			fileWriter.write(text);
		} catch (IOException e) {
			
		}
	}
	
	public static boolean fileExists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}
}
