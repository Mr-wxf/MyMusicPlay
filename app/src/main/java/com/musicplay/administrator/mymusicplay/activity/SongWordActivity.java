package com.musicplay.administrator.mymusicplay.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.musicplay.administrator.mymusicplay.Bean.LrcRow;
import com.musicplay.administrator.mymusicplay.R;
import com.musicplay.administrator.mymusicplay.Utils.DefaultLrcBuilder;
import com.musicplay.administrator.mymusicplay.View.ILrcViewListener;
import com.musicplay.administrator.mymusicplay.View.LrcView;
import com.musicplay.administrator.mymusicplay.service.PlayMusicService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SongWordActivity extends AppCompatActivity {

    private long currentPosition;
    private LrcView lv_lrc;
    private ServiceConnection serviceConnection;
    private PlayMusicService playMusicService;
    private Intent playServiceIntent;
    private LrcTask mTask;
    public Timer mTimer;
    private int mPalyTimerDuration = 500;
    private boolean isRunning=false;
    private Thread uiThread;
    private  Context context;
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            if(mTimer == null){
//                mTimer = new Timer();
//                mTask = new LrcTask();
//                mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);
//            }
            long timePassed = playMusicService.mediaPlayer.getCurrentPosition();
            lv_lrc.seekLrcToTime(timePassed);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_word);
        this.context=this;
        Intent intent = getIntent();
        String lrcUrl = intent.getStringExtra("lrcUrl");
        playServiceIntent = new Intent(this, PlayMusicService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayMusicService.MyIBinder binder = (PlayMusicService.MyIBinder) service;
                playMusicService = binder.getService();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(playServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        lv_lrc = (LrcView) findViewById(R.id.lv_Lrc);
//        Log.d("lrcUrl",lrcUrl);

        String lrc = getFromAssets(lrcUrl);
//        Log.d("fromAssets",fromAssets);    //从assets目录下读取歌词文件内容
//        String lrc = getFromAssets("test.lrc");
        //解析歌词构造器
        DefaultLrcBuilder defaultLrcBuilder = new DefaultLrcBuilder();
        //解析歌词返回LrcRow集合
        List<LrcRow> rows = defaultLrcBuilder.getLrcRows(lrc);

        lv_lrc.setLrc(rows);
        lv_lrc.setListener(new ILrcViewListener() {
            @Override
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (playMusicService != null) {
//                    Log.d(TAG, "onLrcSeeked:" + row.time);
                    playMusicService.mediaPlayer.seekTo((int) row.time);
                }
            }
        });

        }

    /**
     * 从assets目录下读取歌词文件内容
     * @param fileName
     * @return
     */
    public String getFromAssets(String fileName){
        try {
            BufferedReader bufReader=null;
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream(fileName));
            bis.mark(4);
            byte[] first3bytes=new byte[3];
//   System.out.println("");
            //找到文档的前三个字节并自动判断文档类型。
            bis.read(first3bytes);
            bis.reset();
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
                    && first3bytes[2] == (byte) 0xBF) {// utf-8

                bufReader = new BufferedReader(new InputStreamReader(bis, "utf-8"));

            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFE) {

                bufReader = new BufferedReader(
                        new InputStreamReader(bis, "unicode"));
            } else if (first3bytes[0] == (byte) 0xFE
                    && first3bytes[1] == (byte) 0xFF) {

                bufReader = new BufferedReader(new InputStreamReader(bis,
                        "utf-16be"));
            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFF) {

                bufReader = new BufferedReader(new InputStreamReader(bis,
                        "utf-16le"));
            } else {

                bufReader = new BufferedReader(new InputStreamReader(bis, "GBK"));
            }

            String line="";
            String result="";
            while((line = bufReader.readLine()) != null){
                if(line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
        uiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRunning) {
                        Thread.sleep(100);
                        handler.sendEmptyMessage(0);
                    }
                } catch (InterruptedException e) {

                }
            }
        });

        uiThread.start();



    }

    @Override
    protected void onStop() {
        super.onStop();
        if(serviceConnection!=null){
            unbindService(serviceConnection);
        }
        isRunning=false;
    }

    /**
     * 展示歌曲的定时任务
     */
    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = playMusicService.mediaPlayer.getCurrentPosition();
            Log.d("timePassed",timePassed+"");
           runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词
                    lv_lrc.seekLrcToTime(timePassed);
                }
            });

        }
    };

}

