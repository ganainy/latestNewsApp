package com.example.retrofittest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.retrofittest.R;
import com.example.retrofittest.Utils;
import com.example.retrofittest.activity.MainActivity;
import com.example.retrofittest.activity.NewsDetailActivity;
import com.example.retrofittest.model.Article;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private static final String TAG = "NewsAdapter";
    List<Article> articleList;
    private Context context;

    public NewsAdapter(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.news_item,parent,false);


       return new NewsViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, int position) {

        Article currentArticle = articleList.get(position);
/*code related to main image and applying image place holder till loading*/
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();
        requestOptions.timeout(3000);

        Glide.with(context).load(currentArticle.getUrlToImage()).apply(requestOptions).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progress_load_photo.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progress_load_photo.setVisibility(View.GONE);
                return false;
            }
        }).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img);

        /*load data to rest of views*/
        holder.source.setText(currentArticle.getSource().getName());
        holder.desc.setText(currentArticle.getDescription());
        holder.title.setText(currentArticle.getTitle());
        holder.time.setText("\u2022"+Utils.DateToTimeFormat(currentArticle.getPublishedAt()));
        Log.i(TAG, "onBindViewHolder: "+currentArticle.getPublishedAt());
        holder.date_main.setText(Utils.DateFormat(currentArticle.getPublishedAt()));
        /*don't show textview if author if it returns null from server*/
        Log.i(TAG, "onBindViewHolder: "+currentArticle.getAuthor());
        if (currentArticle.getAuthor()!=null && currentArticle.getAuthor().length()<16)
        {
            holder.author.setText(currentArticle.getAuthor());

        }


    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        ImageView img;

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.desc)
        TextView desc;

        @BindView(R.id.source)
        TextView source;

        @BindView(R.id.time)
        TextView time;

        @BindView(R.id.date_main)
        TextView date_main;

        @BindView(R.id.author)
        TextView author;



        @BindView(R.id.progress_load_photo)
        ProgressBar progress_load_photo

                ;


        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ImageView imageView=itemView.findViewById(R.id.img);


                    Intent intent=new Intent(context, NewsDetailActivity.class);
                    Article article=articleList.get(getAdapterPosition());
                    intent.putExtra("url",article.getUrl());
                    intent.putExtra("title",article.getTitle());
                    intent.putExtra("img",article.getUrlToImage());
                    intent.putExtra("date",article.getPublishedAt());
                    intent.putExtra("source",article.getSource().getName());
                    intent.putExtra("author",article.getAuthor());

                    MainActivity mainActivity=(MainActivity) context;
                    mainActivity.startActivity(intent);

                }
            });

        }
    }
}
