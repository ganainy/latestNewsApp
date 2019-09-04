package com.example.retrofittest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Article> articleList=new ArrayList<>();

    private static final String TAG = "MainActivity";
    private static final String API_KEY = "b95e55b9676a4817a9e8969ada077731";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        loadArticles();
        /*todo
        * 2-check connection on start
        * 4-continue videos list
        * */


    }


    private void loadArticles()
    {
        GetNewsDataService getNewsDataService= RetrofitInstance.getRetrofitInstance().
                create(GetNewsDataService.class);

        String country= Utils.getCountry();
        Call<News> newsCall;
        newsCall=getNewsDataService.getNews(country,"sports",API_KEY);

        newsCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful() && response.body().getArticles()!=null)
                {
                    articleList.clear();
                    articleList=response.body().getArticles();
                    initRecycler();

                }else
                {
                    Toast.makeText(MainActivity.this, "No results!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.i(TAG, "onFailure: "+t.getMessage());
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
}
