package com.jinan.xiaodou.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinan.xiaodou.R;
import com.jinan.xiaodou.bean.NewsBean;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsAdapter extends BaseAdapter {

    private Context mContext;
    private List<NewsBean> mDataList;

    public NewsAdapter(Context context, List<NewsBean> list) {
        this.mContext = context;
        mDataList = list;
    }

    public void update(List<NewsBean> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        holder = new Holder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_list, null);
        convertView.setTag(holder);

        holder.mTitle = (TextView) convertView.findViewById(R.id.news_title);
        holder.mImg = (ImageView) convertView.findViewById(R.id.news_img);

        return convertView;
    }

    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /**
     * 定义HTML标签的正则表达式
     */
    private static final String REGEX_HTML = "<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE = "\\s*|\t|\r|\n";

    public static String delHTMLTag(String htmlStr) {
        // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");

        Pattern a_space = Pattern.compile("&nbsp;", Pattern.CASE_INSENSITIVE);
        Matcher b_space = a_space.matcher(htmlStr);
        htmlStr = b_space.replaceAll("");

        Pattern md_space = Pattern.compile("&mdash;", Pattern.CASE_INSENSITIVE);
        Matcher nd_space = md_space.matcher(htmlStr);
        htmlStr = nd_space.replaceAll("");
        return htmlStr.trim(); // 返回文本字符串
    }

    class Holder {
        TextView mTitle;
        ImageView mImg;
    }
}
