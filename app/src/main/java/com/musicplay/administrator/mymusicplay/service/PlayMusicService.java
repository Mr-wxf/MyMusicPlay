package com.musicplay.administrator.mymusicplay.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;


public class PlayMusicService extends Service {

    public MediaPlayer mediaPlayer;
    private String nextUrl;


    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        super.onCreate();


    }
    @Override
    public int onStartCommand( Intent intent, int flags, int startId) {

//        Log.d("--------------",intent.getStringExtra("url"));
//        return super.onStartCommand(intent, flags, startId);

        nextUrl = intent.getStringExtra("nextUrl");
        if (mediaPlayer.isPlaying()) {
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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if(nextUrl!=null){
                        mp.reset();
                        mp.setDataSource(nextUrl);

                        mp.prepare();
                        mp.start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyIBinder();
    }

    public class MyIBinder extends Binder {
        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }



    public void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            Log.d("+++++++++++++++++++", "重新播放");
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;

    }

    public void seekTo(int duration) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(duration);
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public long getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }


}
