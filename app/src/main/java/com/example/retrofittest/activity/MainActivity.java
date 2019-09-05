package com.example.retrofittest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retrofittest.R;
import com.example.retrofittest.Utils;
import com.example.retrofittest.adapter.NewsAdapter;
import com.example.retrofittest.model.Article;
import com.example.retrofittest.model.News;
import com.example.retrofittest.network.GetNewsDataService;
import com.example.retrofittest.network.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Article> articleList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;

    private static final String TAG = "MainActivity";
    private static final String API_KEY = "b95e55b9676a4817a9e8969ada077731";
    private NewsAdapter newsAdapter;
    private TextView errorTitle, errorMessage;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        /*attatching listener to listen for swipe gesture*/
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadArticles("");

            }
        });
        /*color of loading circle*/
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);




        errorLayout = findViewById(R.id.errorLayout);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        retryButton = findViewById(R.id.retryButton);


        loadArticles("");
    }


    private void loadArticles(final String keyword) {


        swipeRefreshLayout.setRefreshing(true);
        GetNewsDataService getNewsDataService = RetrofitInstance.getRetrofitInstance().
                create(GetNewsDataService.class);
        String country = Utils.getCountry();
        String language = Utils.getLanguage();
        Call<News> newsCall;
        if (keyword.equals(""))
            newsCall = getNewsDataService.getNews(country, API_KEY);
        else
            newsCall = getNewsDataService.getSpecificNews(language, keyword, "publishedAt", API_KEY);

        newsCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles().size() > 0) {
                    errorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Log.i(TAG, "onResponse:isSuccessful ");
                    articleList.clear();
                    articleList = response.body().getArticles();
                    initRecycler();

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.i(TAG, "onResponse: No results!");
                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = getString(R.string.not_found);
                            break;
                        case 500:
                            errorCode = getString(R.string.server_broken);
                            break;
                        default:
                            errorCode = getString(R.string.unknow_error);
                            break;
                    }
                    showErrorMessage(errorCode, getString(R.string.no_results));
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.i(TAG, "onResponse: Failure: " + t.getMessage());
                showErrorMessage(getString(R.string.check_connection),getString(R.string.network_issue) );

            }
        });
    }

    private void initRecycler() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        newsAdapter = new NewsAdapter(this, articleList);
        recyclerView.setAdapter(newsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
          /*getActionView()
        Returns the currently set action view for this menu item.
        next line +   app:actionViewClass="android.widget.SearchView"
        is telling app that i want that menu item to act as SearchView
        */
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.Find));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 2) {
                    loadArticles(s);
                } else {
                    Toast.makeText(MainActivity.this, R.string.need_more_than_two_chars, Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        return true;

    }


    private void showErrorMessage(String message, String title) {
        if (errorLayout.getVisibility() == View.GONE)
            errorLayout.setVisibility(View.VISIBLE);

        errorTitle.setText(title);
        errorMessage.setText(message);


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadArticles("");

            }
        });
    }


}
