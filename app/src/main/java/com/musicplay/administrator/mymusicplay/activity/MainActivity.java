package com.musicplay.administrator.mymusicplay.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.musicplay.administrator.mymusicplay.Bean.SongList;
import com.musicplay.administrator.mymusicplay.R;
import com.musicplay.administrator.mymusicplay.Utils.QuerySong;
import com.musicplay.administrator.mymusicplay.Utils.SpUtil;
import com.musicplay.administrator.mymusicplay.Value.Value;
import com.musicplay.administrator.mymusicplay.adapter.ListViewAdapter;
import com.musicplay.administrator.mymusicplay.service.PlayMusicService;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_forward;
    private Button bt_stop;
    private Button bt_play;
    private Button bt_back;
    private Context context;
    private List<SongList> songLists;
    private ListView lv_musicitem;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListViewAdapter listViewAdapter = new ListViewAdapter(context, songLists);
            lv_musicitem.setAdapter(listViewAdapter);


        }
    };
    private PlayMusicService playMusicService;
    private Intent playServiceIntent;
    private ServiceConnection serviceConnection;
    private int index=0;

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
        context = this;
        initUI();
        initData();
        playServiceIntent = new Intent(context, PlayMusicService.class);
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
        bt_back.setOnClickListener(this);
        bt_play.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_forward.setOnClickListener(this);
        lv_musicitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusic(position);
                SpUtil.putInt(context, Value.POSITIONVALUE,position);
                bt_play.setVisibility(View.VISIBLE);
                bt_stop.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                int backPosition = SpUtil.getInt(context, Value.POSITIONVALUE);
                if (backPosition<=0){
                    backPosition=songLists.size()-1;
                }
                playServiceIntent.putExtra("url", songLists.get(backPosition-1).url);
                startService(playServiceIntent);
                index = backPosition-1;
                SpUtil.putInt(context,Value.POSITIONVALUE,index);
                break;
            case R.id.bt_play:
                bt_play.setVisibility(View.GONE);
                bt_stop.setVisibility(View.VISIBLE);
                playMusicService.stopMusic();
                break;
            case R.id.bt_stop:
                bt_stop.setVisibility(View.GONE);
                bt_play.setVisibility(View.VISIBLE);
                try {
                    playMusicService.playMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.bt_forward:
                int forwardPosition = SpUtil.getInt(context, Value.POSITIONVALUE);
                if (forwardPosition>=songLists.size()-1){
                    forwardPosition=-1;
                }
                playServiceIntent.putExtra("url", songLists.get(forwardPosition+1).url);
                startService(playServiceIntent);
                index = forwardPosition+1;
                SpUtil.putInt(context,Value.POSITIONVALUE,index);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        stopService(playServiceIntent);
        super.onDestroy();

    }
}
