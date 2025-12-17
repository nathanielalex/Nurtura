package com.example.nurtura;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.adapter.ArticleAdapter;
import com.example.nurtura.model.Article;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private FirebaseFirestore db;
    private List<Article> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        recyclerView = findViewById(R.id.rvArticle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(articleList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Fetch Data
        fetchArticles();
    }

    private void fetchArticles() {
        db.collection("articles")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Article> fetchedList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Article article = document.toObject(Article.class);
                            article.setId(document.getId());
                            fetchedList.add(article);
                        }
                        adapter.updateList(fetchedList);
                    } else {
                        Toast.makeText(ArticleActivity.this, "No articles found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ArticleActivity.this, "Failed to load articles.", Toast.LENGTH_SHORT).show();
                });
    }
}
