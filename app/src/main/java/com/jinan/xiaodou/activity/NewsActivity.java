package com.jinan.xiaodou.activity;

import android.os.Bundle;

import com.jinan.xiaodou.BaseActivity;
import com.jinan.xiaodou.R;
import com.jinan.xiaodou.adapter.NewsAdapter;
import com.jinan.xiaodou.bean.NewsBean;
import com.jovision.xunwei.junior.lib.view.pullrefresh.XListView;

import java.util.ArrayList;

public class NewsActivity extends BaseActivity implements XListView.IXListViewListener{

    private static final String TAG = NewsActivity.class.getSimpleName();
    private XListView mListView;
    private NewsAdapter mAdapter;
    private ArrayList<NewsBean> mList = new ArrayList<NewsBean>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initView();
    }
    private void initView(){
        getTitleBar().setTitle("新闻");
        mListView = $(R.id.news_listview);

        for(int i = 0; i<4; i++){
            mList.add(new NewsBean());
        }

        mAdapter = new NewsAdapter(this, mList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
