package com.musicplay.administrator.mymusicplay.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.musicplay.administrator.mymusicplay.Bean.SongList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 *
 * 查询手机中的所有歌曲
 */
public class DBSong {
    private Context context;
    private ArrayList<SongList> songList;

    public DBSong(Context context) {
        this.context = context;
    }

    public List<SongList> querySong() {

        songList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            SongList song = new SongList();
            //歌曲编号
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
//            Log.d(".........", id + "");
            //歌曲标题
            String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
//            Log.d(".........", tilte + "");
            String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
//            Log.d(".........", album + "");
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
//            Log.d(".........", artist + "");
            String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
            Log.d(".........", url + "");
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
//            Log.d(".........", duration + "");
            //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
            Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
//            Log.d(".........", size + "");

            song.title = tilte;
            song.people = artist;
            song.duration = duration;
            song.size = Double.parseDouble(String.format("%.2f",size/1024.0/1024.0));
            song.url=url;
            songList.add(song);
        }
        return songList;
    }
    public void deleteSong(String url){
            ContentResolver contentResolver = context.getContentResolver();
            File file = new File(url);
        if(file.exists()){
            file.delete();
        }
            contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,MediaStore.Audio.Media.DATA+"=?", new String[]{url});

    }

}