package com.musicplay.administrator.mymusicplay.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class PlayMusicService extends Service {

    private MediaPlayer mediaPlayer;


    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log.d("--------------",intent.getStringExtra("url"));
//        return super.onStartCommand(intent, flags, startId);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(intent.getStringExtra("url"));
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return  new MyIBinder();
    }
  public class MyIBinder extends Binder {
       public   PlayMusicService getService(){
             return PlayMusicService.this;
         }

  }
    public void stopMusic(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }
    public void playMusic()  {
        if(mediaPlayer!=null){
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("+++++++++++++++++++","重新播放");
        }
    }
    public boolean isPlaying(){

        return  mediaPlayer.isPlaying();
    }
}
