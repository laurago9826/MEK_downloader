package com.example.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.example.app.helpers.ResourceUtils;

/**
 * Unit test for simple App.
 */
public class AppTest
{
	private static final String FILTERED_DATA_FOLDER = "filtered_for_authorship_attribution";
	private static final Integer AUTHOR_NUMBER_LIMIT = 15;

	@Test
	public void shouldAnswerWithTrue()
	{
		String[] fileNames = new File(ResourceUtils.EXTRACTED_TEXT_DIR).list();
		List<String> authors = extractDistinctAuthorNames(Arrays.asList(fileNames));
		Map<String, Long> authorDataSize = new HashMap<>();
		authors.forEach(a -> authorDataSize.put(a, 0L));
		for (String fileName : fileNames) {
			File f = new File(ResourceUtils.EXTRACTED_TEXT_DIR, fileName);
			String author = extractAuthorName(fileName);
			authorDataSize.compute(author, (k, v) -> v + f.length() / 1024);
		}
		List<String> keepAuthors = authorDataSize.entrySet().stream()
			.sorted((v1, v2) -> - v1.getValue().compareTo(v2.getValue()))
			.limit(AUTHOR_NUMBER_LIMIT).map(Entry::getKey).collect(Collectors.toList());
		for (String fileName : fileNames) {
			if (keepAuthors.contains(extractAuthorName(fileName))) {
				File f = new File(ResourceUtils.EXTRACTED_TEXT_DIR, fileName);
				try {
					Files.copy(f.toPath(),
							new File(ResourceUtils.EXTRACTED_TEXT_DIR + FILTERED_DATA_FOLDER, fileName).toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<String> extractDistinctAuthorNames(List<String> files) {
		Set<String> authors = new HashSet<>();
		for (String fileName : files) {
			authors.add(extractAuthorName(fileName));
		}
		return new ArrayList<String>(authors);
	}
	
	private String extractAuthorName(String fileName) {
		int indexOfUnderscore = fileName.indexOf('_');
		return fileName.substring(0, indexOfUnderscore);
	}
}
