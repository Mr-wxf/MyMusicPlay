package com.musicplay.administrator.mymusicplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.musicplay.administrator.mymusicplay.Bean.SongList;
import com.musicplay.administrator.mymusicplay.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */
public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<SongList> songLists;


    public ListViewAdapter(Context context,List<SongList> songLists) {
        this.songLists = songLists;
        this.context=context;
    }

    @Override
    public int getCount() {
        return songLists.size();
    }

    @Override
    public Object getItem(int position) {
        return songLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.people = (TextView) convertView.findViewById(R.id.tv_people);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(songLists.get(position).title);
        viewHolder.size.setText(songLists.get(position).size + "M");
        viewHolder.people.setText(songLists.get(position).people);
        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView people;
        TextView size;
    }
}
