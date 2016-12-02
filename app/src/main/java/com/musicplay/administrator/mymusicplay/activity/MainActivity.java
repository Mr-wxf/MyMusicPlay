package com.musicplay.administrator.mymusicplay.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.musicplay.administrator.mymusicplay.Bean.SongList;
import com.musicplay.administrator.mymusicplay.R;
import com.musicplay.administrator.mymusicplay.Utils.QuerySong;
import com.musicplay.administrator.mymusicplay.Utils.SpUtil;
import com.musicplay.administrator.mymusicplay.Value.Value;
import com.musicplay.administrator.mymusicplay.adapter.ListViewAdapter;
import com.musicplay.administrator.mymusicplay.service.PlayMusicService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    private Button bt_forward;
    private Button bt_stop;
    private Button bt_play;
    private Button bt_back;
    private Context context;
    private List<SongList> songLists;
    private ListView lv_musicitem;
    private PlayMusicService playMusicService;
    private Intent playServiceIntent;
    private ServiceConnection serviceConnection;
    private int index = 0;
    private SeekBar sb_volume;
    private boolean isFirstPlay = false;//判断是不是第一次进去按下播放键
    private boolean isRunning=true;//判断线程是否在运行
    private AudioManager mAudioManager;
    private int volumeMax;
    private int volumeCurrent;
    private SeekBar sb_progress;
    private Thread uiThread;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ListViewAdapter listViewAdapter = new ListViewAdapter(context, songLists);
                    lv_musicitem.setAdapter(listViewAdapter);
                    break;
                case 1:


                    int currentPosition = playMusicService.mediaPlayer.getCurrentPosition();//获取当前播放时间
                    int duration = playMusicService.mediaPlayer.getDuration();//总是时间
                    int i = (int) ((double) currentPosition / (double) duration * sb_progress.getMax());
                    Log.d("+++++++++++++", currentPosition + "");
                    Log.d("+++++++++++++", i + "");
                    if (!playMusicService.isPlaying() && currentPosition == duration && sb_progress.getProgress() == 0) {
                        sb_progress.setProgress(0);
                    } else if (playMusicService.isPlaying()) {
                        sb_progress.setProgress(i);
                    } else if (!playMusicService.isPlaying()) {

                        sb_progress.setProgress(sb_progress.getProgress());
                    }
                    break;
            }


        }
    };


    private void playMusic(int position) {
//        playServiceIntent = new Intent(context, PlayMusicService.class);
        playServiceIntent.putExtra("url", songLists.get(position).url);
        startService(playServiceIntent);
//        Log.d("----------------------",position+"");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("oncreat", "oncreat");
        context = this;
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //音量最大值
        volumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //音量当前值
        volumeCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        initUI();
        initData();


        playServiceIntent = new Intent(context, PlayMusicService.class);
        startService(playServiceIntent);
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

    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        isRunning=false;
//uiThread.interrupt();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                QuerySong querySong = new QuerySong(context);
                songLists = querySong.querySong();
                Log.d("............", songLists.size() + "");
                handler.sendEmptyMessage(0);
            }

        }.start();


    }

    private void initUI() {
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_play = (Button) findViewById(R.id.bt_play);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_forward = (Button) findViewById(R.id.bt_forward);
        lv_musicitem = (ListView) findViewById(R.id.lv_musicitem);
        sb_volume = (SeekBar) findViewById(R.id.sb_volume);
        sb_progress = (SeekBar) findViewById(R.id.sb_progress);
        bt_back.setOnClickListener(this);
        bt_play.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_forward.setOnClickListener(this);
        lv_musicitem.setOnItemClickListener(this);
        sb_volume.setOnSeekBarChangeListener(this);
        sb_progress.setOnSeekBarChangeListener(this);
        sb_volume.setProgress((int) ((double) volumeCurrent / (double) volumeMax * sb_volume.getMax()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                bt_stop.setVisibility(View.GONE);
                bt_play.setVisibility(View.VISIBLE);
                int backPosition = SpUtil.getInt(context, Value.POSITIONVALUE);
                if (backPosition <= 0) {
                    backPosition = songLists.size();
                }
                playServiceIntent.putExtra("url", songLists.get(backPosition - 1).url);
                startService(playServiceIntent);
                index = backPosition - 1;
                SpUtil.putInt(context, Value.POSITIONVALUE, index);
                break;
            case R.id.bt_play:
                bt_play.setVisibility(View.GONE);
                bt_stop.setVisibility(View.VISIBLE);

                if (playMusicService.isPlaying()) {
                    playMusicService.pause();
                } else if (!isFirstPlay) {
                    int playPosition = SpUtil.getInt(context, Value.POSITIONVALUE);
                    playServiceIntent.putExtra("url", songLists.get(playPosition).url);
                    startService(playServiceIntent);
                } else {
                    playMusicService.playMusic();
                }
                isFirstPlay = true;
                break;
            case R.id.bt_stop:
                bt_stop.setVisibility(View.GONE);
                bt_play.setVisibility(View.VISIBLE);
                Log.d("stopMusic", "stopMusic");
                if (playMusicService.isPlaying()) {
                    playMusicService.pause();
                } else {
                    playMusicService.playMusic();
                }
//                playMusicService.playMusic();
                break;
            case R.id.bt_forward:
                bt_stop.setVisibility(View.GONE);
                bt_play.setVisibility(View.VISIBLE);
                int forwardPosition = SpUtil.getInt(context, Value.POSITIONVALUE);
                if (forwardPosition >= songLists.size() - 1) {
                    forwardPosition = -1;
                }
                playServiceIntent.putExtra("url", songLists.get(forwardPosition + 1).url);
                startService(playServiceIntent);
                index = forwardPosition + 1;
                SpUtil.putInt(context, Value.POSITIONVALUE, index);
                break;

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("log", "onStart");
        bindService(playServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onResume() {
        isRunning=true;
        uiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRunning) {
                       Thread.sleep(1000);
                        handler.sendEmptyMessage(1);
                    }
                } catch (InterruptedException e) {

                }
            }
        });
        uiThread.start();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        playMusic(position);
        SpUtil.putInt(context, Value.POSITIONVALUE, position);
        bt_play.setVisibility(View.VISIBLE);
        bt_stop.setVisibility(View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()) {
            case R.id.sb_volume:
                int maxProgress = seekBar.getMax();
                int voleme = (int) ((double) volumeMax / (double) maxProgress * progress);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voleme, AudioManager.FLAG_ALLOW_RINGER_MODES);
                Log.d("Music", volumeMax + "__" + volumeCurrent);
                Log.d("Progress", voleme + "");
                break;

            case R.id.sb_progress:
                if (progress + 1 >= seekBar.getMax()) {
                    int position = SpUtil.getInt(context, Value.POSITIONVALUE);
                    if (position >= songLists.size() - 1) {
                        position = -1;
                    }
                    playServiceIntent.putExtra("url", songLists.get(position + 1).url);
                    startService(playServiceIntent);
                    position += 1;
                    SpUtil.putInt(context, Value.POSITIONVALUE, position);
                }
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_progress:
                int progress = seekBar.getProgress();
                int position = SpUtil.getInt(context, Value.POSITIONVALUE);
                int durationTotal = songLists.get(position).duration;
                int maxMusicProgress = seekBar.getMax();
                int musicProgress = (int) ((double) progress / (double) maxMusicProgress * durationTotal);
                playMusicService.seekTo(musicProgress);
                break;
        }
    }
}
