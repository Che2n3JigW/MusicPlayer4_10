package com.cjw.bookproject.musicplayer4_10.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Administrator on 2017/4/18.
 */

public class MusicFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".mp3"));//返回当前目录所有以.mp3结尾的文件
    }
}
