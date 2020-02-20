package bg.sofia.uni.fmi.mjt.food.analyzer.server.api;

import com.google.gson.JsonArray;

public class FoodInfo {
	
	private JsonArray foods;
	
	public FoodInfo(JsonArray foods) {
		this.foods = foods;
	}

	public JsonArray getFoods() {
		return foods;
	}

	public void setFoods(JsonArray foods) {
		this.foods = foods;
	}
	
}
