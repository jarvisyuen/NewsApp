package com.example.yuan.newsapp;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yuan.newsapp.Bean.News;
import com.example.yuan.newsapp.Bean.NewsBean;
import com.example.yuan.newsapp.util.HttpUtil;
import com.example.yuan.newsapp.util.ParseUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private ListView listView;
    private List<NewsTitle> newsTitleList = new ArrayList<NewsTitle>();
    private NewsTitleAdapter newsTitleAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        listView = findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                requestNews(type); //未知该type是否有效
            }
        });
        newsTitleAdapter = new NewsTitleAdapter(this, R.layout.list_item, newsTitleList);
        listView.setAdapter(newsTitleAdapter);
        type = "top";
        requestNews(type);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(MainActivity.this, ContentActivity.class);
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsTitle newsTitle = newsTitleList.get(position);
                intent.putExtra("url", newsTitle.getUrl());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top:
                type = "top";
                requestNews(type);
                return true;
            case R.id.domestic:
                type = "guonei";
                requestNews(type);
                return true;
            case R.id.international:
                type = "guoji";
                requestNews(type);
                return true;
            case R.id.entertainment:
                type = "yule";
                requestNews(type);
                return true;
            case R.id.sports:
                type = "tiyu";
                requestNews(type);
                return true;
            case R.id.social:
                type = "shehui";
                requestNews(type);
                return true;
            case R.id.military:
                type = "junshi";
                requestNews(type);
                return true;
            case R.id.technology:
                type = "keji";
                requestNews(type);
                return true;
            case R.id.finance:
                type = "caijing";
                requestNews(type);
                return true;
            case R.id.fashion:
                type = "shishang";
                requestNews(type);
                return true;
                default: return super.onOptionsItemSelected(item);
        }
    }

    public void requestNews(String type) {
        String url = "http://v.juhe.cn/toutiao/index?type=" + type + "&key=c9bafcd7aff837f20267aa453ff843e4";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                    }
                });
                final String responseText = response.body().string();
                final NewsBean newsBean = ParseUtil.paresJsonWithGson(responseText);
                final String reason = newsBean.reason;
                Log.d(TAG, "onResponse: " + reason);
                if (reason.equals("成功的返回")) {
                    newsTitleList.clear();
                    Log.d(TAG, "onResponse: " + newsBean.error_code + " " + newsBean.reason);
//                    Log.d(TAG, "onResponse: " + newsBean.result.data);
                    Log.d(TAG, "onResponse: " + responseText);
                    for (News news : newsBean.result.data) {
//                        Log.d(TAG, "onResponse: " + news.title);
                        NewsTitle newsTitle = new NewsTitle(news.title, news.date, news.category, news.author_name, news.url);
                        newsTitleList.add(newsTitle);
                    }
                }
            }
        });
    }
}
