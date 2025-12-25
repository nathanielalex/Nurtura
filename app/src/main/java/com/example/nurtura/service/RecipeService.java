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
                        List<Recipe> recipes = new ArrayList<>();
                        JSONArray array = response.getJSONArray("recipes");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            recipes.add(new Recipe(
                                    obj.getString("title"),
                                    obj.getString("image"),
                                    obj.getInt("readyInMinutes")
                            ));
                        }
                        listener.onResponse(recipes); // Send data back
                    } catch (JSONException e) { listener.onError("JSON Error"); }
                },
                error -> listener.onError(error.getMessage())
        );
        queue.add(request);
    }
}
