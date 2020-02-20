package bg.sofia.uni.fmi.mjt.food.analyzer.server.api;

import com.google.gson.JsonObject;

public class FoodReportInfo {

	private String description;
	private String ingredients;
	private JsonObject labelNutrients;

	public FoodReportInfo(String description, String ingredients, JsonObject labelNutrients) {
		this.description = description;
		this.ingredients = ingredients;
		this.labelNutrients = labelNutrients;
	}

	public JsonObject getLabelNutrients() {
		return labelNutrients;
	}

	public void setLabelNutrients(JsonObject labelNutrients) {
		this.labelNutrients = labelNutrients;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	
}
