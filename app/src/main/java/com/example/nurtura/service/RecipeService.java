package com.example.nurtura.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nurtura.BuildConfig;
import com.example.nurtura.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeService {

    private Context context;

    public RecipeService(Context context) {
        this.context = context;
    }

    public interface RecipeResponseListener {
        void onResponse(List<Recipe> recipes);
        void onError(String message);
    }

    public void getRecipes(RecipeResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String key = BuildConfig.API_KEY;
        String url = "https://api.spoonacular.com/recipes/random?number=1&include-tags=whole30&apiKey=" + key;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Recipe> recipesList = new ArrayList<>();
                        JSONArray recipesArray = response.getJSONArray("recipes");

                        for (int i = 0; i < recipesArray.length(); i++) {
                            JSONObject recipeObj = recipesArray.getJSONObject(i);

                            int id = recipeObj.getInt("id");
                            String title = recipeObj.getString("title");
                            String image = recipeObj.getString("image");
                            int time = recipeObj.getInt("readyInMinutes");

                            List<String> ingredients = new ArrayList<>();
                            JSONArray ingredientsArray = recipeObj.getJSONArray("extendedIngredients");
                            for (int j = 0; j < ingredientsArray.length(); j++) {
                                ingredients.add(ingredientsArray.getJSONObject(j).getString("original"));
                            }

                            List<String> steps = new ArrayList<>();
                            JSONArray analyzedInstructions = recipeObj.getJSONArray("analyzedInstructions");
                            if (analyzedInstructions.length() > 0) {
                                JSONArray stepsArray = analyzedInstructions.getJSONObject(0).getJSONArray("steps");
                                for (int k = 0; k < stepsArray.length(); k++) {
                                    steps.add(stepsArray.getJSONObject(k).getString("step"));
                                }
                            }

                            recipesList.add(new Recipe(id, title, image, time, ingredients, steps));
                        }

                        listener.onResponse(recipesList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> listener.onError(error.getMessage())
        );
        queue.add(request);
    }
}
