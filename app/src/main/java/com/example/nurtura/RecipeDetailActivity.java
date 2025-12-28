package com.example.nurtura;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.nurtura.model.Recipe;
import com.google.android.material.imageview.ShapeableImageView;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView imageView = findViewById(R.id.imgDetailRecipe);
        TextView titleView = findViewById(R.id.txtRecipeTitle);
        TextView timeView = findViewById(R.id.txtReadyTime);
        TextView ingredientsView = findViewById(R.id.txtIngredientsList);
        TextView instructionsView = findViewById(R.id.txtInstructionsList);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe_data");

        if (recipe != null) {
            titleView.setText(recipe.getTitle());
            timeView.setText("Ready in " + recipe.getReadyInMinutes() + " minutes");

            Glide.with(this).load(recipe.getImageUrl()).into(imageView);

            StringBuilder ingredientsBuilder = new StringBuilder();
            for (String ingredient : recipe.getIngredients()) {
                ingredientsBuilder.append("• ").append(ingredient).append("\n");
            }
            ingredientsView.setText(ingredientsBuilder.toString());

            StringBuilder instructionsBuilder = new StringBuilder();
            int stepNum = 1;
            for (String step : recipe.getInstructions()) {
                instructionsBuilder.append(stepNum).append(". ").append(step).append("\n\n");
                stepNum++;
            }
            instructionsView.setText(instructionsBuilder.toString());
        }
    }
}