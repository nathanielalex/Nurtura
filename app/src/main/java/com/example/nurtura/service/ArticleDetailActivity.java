package com.example.nurtura.service;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nurtura.R;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        TextView tvTitle = findViewById(R.id.tvArticleTitle);
        TextView tvSubtitle = findViewById(R.id.tvArticleDescription);
        TextView tvContent = findViewById(R.id.tvArticleContent);
        ImageView ivImage = findViewById(R.id.ivArticleImage);

        String title = getIntent().getStringExtra("ARTICLE_TITLE");
        String subtitle = getIntent().getStringExtra("ARTICLE_SUBTITLE");
        String content = getIntent().getStringExtra("ARTICLE_CONTENT");
        String imageUrl = getIntent().getStringExtra("ARTICLE_IMAGE_URL");

        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
        tvContent.setText(content);

        ivImage.setImageResource(R.drawable.ic_launcher_background);
    }
}
