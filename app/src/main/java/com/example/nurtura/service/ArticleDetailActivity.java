package com.example.nurtura.service;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.nurtura.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class ArticleDetailActivity extends AppCompatActivity {

    private static final String TAG = "ArticleDetailDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ImageView ivImage = findViewById(R.id.ivArticleImage);
        TextView tvTitle = findViewById(R.id.tvArticleTitle);
        TextView tvSubtitle = findViewById(R.id.tvArticleSubtitle);
        TextView tvContent = findViewById(R.id.tvArticleContent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(" ");
        }

        String title = getIntent().getStringExtra("ARTICLE_TITLE");
        String subtitle = getIntent().getStringExtra("ARTICLE_SUBTITLE");
        String content = getIntent().getStringExtra("ARTICLE_CONTENT");
        String imageUrl = getIntent().getStringExtra("ARTICLE_IMAGE_URL");

        tvTitle.setText(title != null ? title : "Health Article");
        tvSubtitle.setText(subtitle != null ? subtitle : "");
        tvContent.setText(content != null ? content : "No content available.");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide Load Failed: " + e.getMessage());
                            if (e != null) e.logRootCauses(TAG);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide Load Success!");
                            return false;
                        }
                    })
                    .into(ivImage);
        } else {
            Log.e(TAG, "Image URL is NULL or EMPTY");
            ivImage.setImageResource(R.drawable.ic_launcher_background);
        }
    }
}
