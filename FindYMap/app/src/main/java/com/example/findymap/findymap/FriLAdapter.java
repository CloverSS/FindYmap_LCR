package com.example.findymap.findymap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by apple on 2016/9/23.
 */

public class FriLAdapter extends BaseAdapter {
    private List<persons> mData;       //创建Diary类型的List表
    private LayoutInflater mInflater;               //定义线性布局过滤器

    public FriLAdapter(Context context , List<persons> data){
        this.mData = data ;
        mInflater = LayoutInflater.from(context);       //获取布局
    }
    /**
     * 得到列表长度
     * @return
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;    //得到子项位置id
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.friends_list_item, null);
        holder.Frinum = (TextView) convertView.findViewById(R.id.name_cell);
        convertView.setTag(holder);         //为View设置tag
        //设置布局中控件要显示的视图
        holder.Frinum.setText(mData.get(position).getNames());
        return convertView;     //返回一个view

    }
    /**
     * 实体类
     */
    public final class ViewHolder{
        public TextView Frinum;
    }
}



