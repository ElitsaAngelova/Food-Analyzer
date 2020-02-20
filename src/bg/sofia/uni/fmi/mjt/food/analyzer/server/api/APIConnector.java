package bg.sofia.uni.fmi.mjt.food.analyzer.server.api;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bg.sofia.uni.fmi.mjt.food.analyzer.server.cache.Cache;

public class APIConnector {
	
	private static final String API_URL = "https://api.nal.usda.gov/fdc";
    private static final String API_KEY = "ssw1lDSa1pIpDR6TpSyoVYUgs6DKsINa6RZB8sgB";
    
    private static final String CACHE_FILE_FOOD_INFO = "resources/cache/cache_food_info.txt";
	private static final String CACHE_FILE_FOOD_REPORT = "resources/cache/cache_food_report.txt";
    
    HttpClient client;
    Cache cache;

	public APIConnector() {
		client = HttpClient.newHttpClient();
		cache = new Cache();
	}

	public String printFoodInfo(String input) throws Exception {

		if (!cache.searchInCache(CACHE_FILE_FOOD_INFO, input).isEmpty()) {
			StringBuilder toPrint = new StringBuilder();
			for (String foodInfo : cache.searchInCache(CACHE_FILE_FOOD_INFO, input)) {
				toPrint.append(foodInfo + "\n");
			}
			return toPrint.toString();

		} else {
			
			while (input.contains(" ")) {
				int index = input.indexOf(" ");
				String newInput = input.substring(0, index) + "%20" + input.substring(index + 1);
				input = newInput;
			}
			
			StringBuilder toPrint = new StringBuilder();

			APIConnector apiConnectorObject = new APIConnector();
			String result = apiConnectorObject.sendGETInfo(input);

			Gson gson = new Gson();
			FoodInfo foodInfo = gson.fromJson(result, FoodInfo.class);
			for (JsonElement jsonObject : foodInfo.getFoods()) {

				String description = ((JsonObject) jsonObject).get("description").getAsString();
				toPrint.append("description: " + description);
				String fdcId = ((JsonObject) jsonObject).get("fdcId").getAsString();
				toPrint.append(", fdcId: " + fdcId);
				String dataType = ((JsonObject) jsonObject).get("dataType").getAsString();
				if (dataType.equals("Branded")) {
					String gtinUpc = ((JsonObject) jsonObject).get("gtinUpc").getAsString();
					toPrint.append(", gtinUpc: " + gtinUpc);
				}
				toPrint.append("\n");
			}
			cache.writeToCache(CACHE_FILE_FOOD_INFO, toPrint.toString());
			return toPrint.toString();
		}
    }
    
    public String printFoodReportInfo(String input) throws Exception {
    	
		if (!cache.searchInCache(CACHE_FILE_FOOD_REPORT, input).isEmpty()) {
			
			return cache.searchInCache(CACHE_FILE_FOOD_REPORT, input).get(0);

		} else {
			
			StringBuilder toPrint = new StringBuilder();

			APIConnector apiConnectorObject = new APIConnector();
			String result = apiConnectorObject.sendGETReportInfo(input);

			Gson gson = new Gson();
			FoodReportInfo foodInfo = gson.fromJson(result, FoodReportInfo.class);

			toPrint.append("Name: " + foodInfo.getDescription());

			toPrint.append(", Ingredients: " + foodInfo.getIngredients());

			JsonObject jsonObject = foodInfo.getLabelNutrients();

			JsonObject caloriesObject = (JsonObject) jsonObject.get("calories");
			double calories = caloriesObject.get("value").getAsDouble();
			toPrint.append(", Calories: " + calories);

			JsonObject proteinObject = (JsonObject) jsonObject.get("protein");
			double protein = proteinObject.get("value").getAsDouble();
			toPrint.append(", Protein: " + protein);

			JsonObject fatObject = (JsonObject) jsonObject.get("fat");
			double fat = fatObject.get("value").getAsDouble();
			toPrint.append(", Fat: " + fat);

			JsonObject carbohydratesObject = (JsonObject) jsonObject.get("carbohydrates");
			double carbohydrates = carbohydratesObject.get("value").getAsDouble();
			toPrint.append(", Carbohydrates: " + carbohydrates);

			JsonObject fiberObject = (JsonObject) jsonObject.get("fiber");
			double fiber = fiberObject.get("value").getAsDouble();
			toPrint.append(", Fiber: " + fiber);

			
			cache.writeToCache(CACHE_FILE_FOOD_REPORT, input + ": " + toPrint.toString());
			return toPrint.toString();
		}
	}

	public String printFoodInfoByBarcode(String input) throws Exception {

		if (!cache.searchInCache(CACHE_FILE_FOOD_INFO, input).isEmpty()) {
			String toPrint = cache.searchInCache(CACHE_FILE_FOOD_INFO, input).get(0);
				return toPrint + "\n";
		} else {
			return null;
		}
	}

	private String sendGETInfo(String input) throws Exception {

		String uri = new String(API_URL + "/v1/" + "search?generalSearchInput=" + input + "&requireAllWords=true&"
				+ "api_key=" + API_KEY);
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();

		CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
				HttpResponse.BodyHandlers.ofString());

		return response.thenApply(HttpResponse::body).get(30, TimeUnit.SECONDS);

	}

	private String sendGETReportInfo(String input) throws Exception {

		String uri = new String(API_URL + "/v1/" + input + "?api_key=" + API_KEY);
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();

		CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
				HttpResponse.BodyHandlers.ofString());

		return response.thenApply(HttpResponse::body).get(30, TimeUnit.SECONDS);

	}

}