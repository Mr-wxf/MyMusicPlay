package com.musicplay.administrator.mymusicplay.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.musicplay.administrator.mymusicplay.Bean.SongList;
import com.musicplay.administrator.mymusicplay.R;
import com.musicplay.administrator.mymusicplay.Utils.DBSong;
import com.musicplay.administrator.mymusicplay.View.SideslipListView;
import com.musicplay.administrator.mymusicplay.activity.MainActivity;
import com.musicplay.administrator.mymusicplay.service.PlayMusicService;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */
public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<SongList> songLists;
    private SideslipListView sideslipListView;
    private DeleteListener listener;
   private MediaPlayer mediaPlayer;
    public ListViewAdapter(Context context, List<SongList> songLists, SideslipListView sideslipListView) {
        this.songLists = songLists;
        this.context = context;
        this.sideslipListView = sideslipListView;


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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.people = (TextView) convertView.findViewById(R.id.tv_people);
            viewHolder.txtv_delete = (TextView) convertView.findViewById(R.id.txtv_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(songLists.get(position).title);
        viewHolder.size.setText(songLists.get(position).size + "M");
        viewHolder.people.setText(songLists.get(position).people);
        viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("是否删除");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBSong dbSong = new DBSong(context);
                        dbSong.deleteSong(songLists.get(position).url);
                        songLists.remove(position);
                        notifyDataSetChanged();
                        sideslipListView.turnNormal();
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        mediaPlayer.stop();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();


            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView people;
        TextView size;
        TextView txtv_delete;
    }


    public interface DeleteListener {
         void onDelete();
    }

    public void setOnDeleteListener(DeleteListener listener) {
        this.listener=listener;
    }
    public void  setMediaPlayer(MediaPlayer mediaPlayer){
                 this.mediaPlayer=mediaPlayer;
    }
}
