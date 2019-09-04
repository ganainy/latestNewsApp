package com.example.retrofittest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.retrofittest.R;
import com.example.retrofittest.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

   private ImageView imageView;
   private TextView appbar_title,appbar_subtitle,date,time,title;
   private boolean isHideTollbarView =false;
   private FrameLayout date_behaviour;
   private LinearLayout titleAppbar;
   private AppBarLayout appBarLayout;
   private Toolbar toolbar;
   private String mUrl,mImg,mTitle,mDate,mSource,mAuthor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");


        appBarLayout=findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this) ;

        date_behaviour=findViewById(R.id.date_behavior);
        titleAppbar=findViewById(R.id.title_appbar);
        imageView=findViewById(R.id.backdrop);
        appbar_title=findViewById(R.id.title_on_appbar);
        appbar_subtitle=findViewById(R.id.subtitle_on_appbar);
        date=findViewById(R.id.date);
        time=findViewById(R.id.time);
        title=findViewById(R.id.title);



        handleIntent();

    }

    private void handleIntent() {
        Intent intent = getIntent();
        String mUrl = intent.getStringExtra("url");
        String mTitle = intent.getStringExtra("title");
        String mImage = intent.getStringExtra("image");
        String mDate = intent.getStringExtra("date");
        String mSource = intent.getStringExtra("source");
        String mAuthor = intent.getStringExtra("author");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utils.getRandomDrawbleColor());
        Glide.with(this).load(mImage).apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);


        appbar_title.setText(mSource);
        appbar_subtitle.setText(mUrl);
        date.setText(Utils.DateFormat(mDate));
        title.setText(mTitle);

        String author = "";
        if (mAuthor != null || mAuthor != "") {
            //todo error here i guess
            author = " \u2022 " + mAuthor;
        }


        time.setText(mSource+author+" \u2022 "+Utils.DateToTimeFormat(mDate));

        initWebView(mUrl);
    }

    private void initWebView(String url)
    {
        WebView webView=findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll=appBarLayout.getTotalScrollRange();
        float percentage= (float)Math.abs(i)/ (float)maxScroll;
        if(percentage==1f&& isHideTollbarView)
        {
            date_behaviour.setVisibility(View.GONE);
            titleAppbar.setVisibility(View.VISIBLE);
            isHideTollbarView=!isHideTollbarView;
        }
        else if (percentage<1f&& isHideTollbarView)
        {
            date_behaviour.setVisibility(View.VISIBLE);
            titleAppbar.setVisibility(View.GONE);
            isHideTollbarView=!isHideTollbarView;
        }
    }
}
