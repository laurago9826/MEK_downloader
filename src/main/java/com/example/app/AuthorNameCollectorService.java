package com.example.app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.app.helpers.ResourceUtils;

@Service
public class AuthorNameCollectorService {

	private static final String AUTHORS_LIST_FILE = "authors.txt";
	
	public List<String> getAuthorsList() {
		List<String> artists = new ArrayList<>();
		List<String> lines = ResourceUtils.readLinesFromResourceFile(
				ResourceUtils.readInputStreamFromResources(AUTHORS_LIST_FILE))
				.stream().filter(l ->
				!l.startsWith("#") &&
				!l.contains("költő") && !l.equals(""))
				.collect(Collectors.toList());
		for (String l : lines) {
			int idx = l.indexOf('(');
			if (idx != -1) {
				artists.add(l.substring(0, idx).trim());
			}
		}
		return artists;
	}
}
