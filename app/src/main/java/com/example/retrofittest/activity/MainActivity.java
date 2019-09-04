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
import android.widget.SearchView;
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

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    List<Article> articleList=new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "MainActivity";
    private static final String API_KEY = "b95e55b9676a4817a9e8969ada077731";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout=findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


       onLoadingSwipeRefresh("");
        /*todo
        * 2-check connection on start
        * 4-continue videos list
        * */


    }


    private void loadArticles(final String keyword)
    {
        swipeRefreshLayout.setRefreshing(true);
        GetNewsDataService getNewsDataService= RetrofitInstance.getRetrofitInstance().
                create(GetNewsDataService.class);
        String country= Utils.getCountry();
        String language= Utils.getLanguage();
        Call<News> newsCall;
        if(keyword.equals(""))
        newsCall=getNewsDataService.getNews(country,API_KEY);
        else
            newsCall=getNewsDataService.getSpecificNews(language,keyword,"publishedAt",API_KEY);

        newsCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful() && response.body().getArticles().size()>0)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.i(TAG, "onResponse:isSuccessful ");
                    articleList.clear();
                    articleList=response.body().getArticles();
                    initRecycler();

                }else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.i(TAG, "onResponse: No results!");
                    Toast.makeText(MainActivity.this, "No results!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.i(TAG, "onResponse: Failure: "+t.getMessage());
            }
        });
    }

    private void initRecycler() {
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        NewsAdapter newsAdapter=new NewsAdapter(this,articleList);
        recyclerView.setAdapter(newsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView= (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenu=menu.findItem(R.id.action_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Find something specific?");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.length()>2)
                {
                    onLoadingSwipeRefresh(s);
                }else
                {
                    Toast.makeText(MainActivity.this, "Need more than two letters for accurate search", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchMenu.getIcon().setVisible(false,false);

            return true;

    }


    //swipe refresh layout
    @Override
    public void onRefresh() {
        loadArticles("");
    }

    private void onLoadingSwipeRefresh(final String keyword)
    {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadArticles(keyword);
            }
        });
    }
}
