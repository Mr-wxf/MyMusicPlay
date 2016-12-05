package com.musicplay.administrator.mymusicplay.View;

import com.musicplay.administrator.mymusicplay.Bean.LrcRow;

/**
 * Created by Administrator on 2016/12/5.
 */
public interface ILrcViewListener {

    /**
     * 当歌词被用户上下拖动的时候回调该方法
     */
    void onLrcSeeked(int newPosition, LrcRow row);
}
