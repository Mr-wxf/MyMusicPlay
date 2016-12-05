package com.musicplay.administrator.mymusicplay.View;

import com.musicplay.administrator.mymusicplay.Bean.LrcRow;

import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public interface ILrcView {

    /**
     * 设置要展示的歌词行集合
     */
    void setLrc(List<LrcRow> lrcRows);

    /**
     * 音乐播放的时候调用该方法滚动歌词，高亮正在播放的那句歌词
     */
    void seekLrcToTime(long time);
    /**
     * 设置歌词拖动时候的监听类
     */
    void setListener(ILrcViewListener l);
}
