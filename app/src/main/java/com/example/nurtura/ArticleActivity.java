package com.example.nurtura;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.adapter.ArticleAdapter;
import com.example.nurtura.model.Article;

import java.util.ArrayList;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        RecyclerView recyclerView = findViewById(R.id.rvArticle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mandatory Feature: Static Health Information (No dynamic CMS)
        ArrayList<Article> staticArticles = new ArrayList<>();

        Article a1 = new Article();
        a1.setTitle("5 Signs of Stunting");
        a1.setContent("1. Slow growth rate\n2. Low weight for age\n3. Delayed walking\n4. Poor appetite\n5. Frequent infections");

        Article a2 = new Article();
        a2.setTitle("Nutrition for Toddlers");
        a2.setContent("Ensure your child gets enough protein (eggs, fish), calcium (milk), and vitamins from fruits and vegetables.");

        Article a3 = new Article();
        a3.setTitle("Emergency Preparedness");
        a3.setContent("Always keep the midwife's number saved. Use the Panic button on the home screen for emergencies.");

        staticArticles.add(a1);
        staticArticles.add(a2);
        staticArticles.add(a3);

        ArticleAdapter adapter = new ArticleAdapter(staticArticles);
        recyclerView.setAdapter(adapter);
    }
}
