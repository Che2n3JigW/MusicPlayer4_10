package com.cjw.bookproject.musicplayer4_10.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 这个是一个工具类
 * 保存了音乐数据的集合
 */


public class DataUtils {

    private static List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    public static List<Map<String, String>> getList() {
        return list;
    }

    public static Map<String, String> getMusicMap(int index) {
        return list.get(index);
    }
}
