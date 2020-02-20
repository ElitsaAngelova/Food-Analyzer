package bg.sofia.uni.fmi.mjt.food.analyzer.server.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cache {

	public Cache() {

	}

	public void writeToCache(String file, String response) {
		try (FileWriter writer = new FileWriter(file, true);
				BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

			bufferedWriter.write(response);
			bufferedWriter.newLine();

		} catch (IOException e) {
			e.getMessage();
		}
	}

	public List<String> searchInCache(String file, String requestedInfo) {
		List<String> matchesInCache = new ArrayList<>();
		try (FileReader reader = new FileReader(file); 
				BufferedReader bufferedReader = new BufferedReader(reader)) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.toLowerCase().contains(requestedInfo.toLowerCase())) {
					matchesInCache.add(line);
				}
			}

		} catch (IOException e) {
			e.getMessage();
		}
		return matchesInCache;
	}

}
