package com.cjw.bookproject.musicplayer4_10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjw.bookproject.musicplayer4_10.util.DBHelper;
import com.cjw.bookproject.musicplayer4_10.util.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音乐列表页面
 */
public class MusicListActivity extends Activity {

    //用于存储音乐列表的
    private List<Map<String, String>> list = DataUtils.getList();

    //显示列表数据
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_music_list);
        //初始化工作
        init();
    }

    private void init() {
        //初始化视图
        initView();
        //显示一个加载提示
        final ProgressDialog pd = ProgressDialog.show(this, null, "正在加载音乐...");
        //启动一个分线程去读取歌曲数据库表数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //连接自己的歌曲库(第一次会创建库和表)
                DBHelper dbHelper = new DBHelper(MusicListActivity.this);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //读取mp3_music这张表
                Cursor cursor = db.query("mp3_music", null, null, null, null,
                        null, null);
//Log.e("===cjw ", cursor == null ? "true":"false");
//Log.e("===cjw ", cursor.getCount()==0 ? "true":"false");
                //还没有保存过
                if (cursor == null || cursor.getCount()==0) {
                    //模拟耗时操作
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e("===cjw", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString());
                    //读取系统的歌曲库表数据(不能直接读, 只能通过ContentResolver来读取)
                    ContentResolver resolver = getContentResolver();
                    cursor = resolver.query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                            null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//Log.e("===cjw",cursor.moveToNext()?"true":"false");
Log.e("===cjw",cursor.getCount()+"");

                    //读取上面的那张表
                    while (cursor.moveToNext()) {
                        // 歌曲文件的路径
                        String path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATA));
//Log.e("===cjw:path",path);
                        // 歌曲的名称
                        String name = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.TITLE));
                        // 歌曲的歌手名
                        String artist = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        //保存到当前应用的db表中
                        ContentValues values = new ContentValues();
                        values.put("name", name);
                        values.put("artist", artist);
                        values.put("path", path);
                        //写入mp3_music这张表里面
                        db.insert("mp3_music", null, values);

                        //保存到内存List中, 用于显示列表
                        //这个map里面的东西很重要
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", name);
                        map.put("artist", artist);
                        map.put("path",path);
                        list.add(map);
                    }
                } else {
                    //自己应用的库中已经有数据, 直接取出
                    while (cursor.moveToNext()) {
                        //获取歌名
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        // 歌曲的歌手名
                        String artist = cursor.getString(cursor.getColumnIndex("artist"));
                        // 获取文件路径
                        String path = cursor.getString(cursor.getColumnIndex("path"));
                        //保存到内存List中, 用于显示列表
                        //这个map里面的东西很重要
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", name);
                        map.put("artist", artist);
                        map.put("path",path);
                        list.add(map);
                    }
                }

                runOnUiThread(new Runnable() {//下面的方法在主线程执行
                    @Override
                    public void run() {
                        pd.dismiss();//去除提示
                        listView.setAdapter(new AllMusicAdapter());
                    }
                });

            }
        }).start();
    }

    //自定义适配器
    private class AllMusicAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();//数据集合中数据的个数
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Map<String, String> map = list.get(position);//得到集合中当前位置的数据
            if(convertView==null) {
                convertView = View.inflate(MusicListActivity.this, R.layout.all_music_item, null);
            }

            TextView nameTV = (TextView) convertView.findViewById(R.id.item_tv_music_name);
            nameTV.setText(map.get("name"));//显示歌名
            TextView artistTV = (TextView) convertView.findViewById(R.id.item_tv_music_artist);
            artistTV.setText(map.get("artist"));//显示歌手名

            return convertView;
        }

    }


    //读取sd卡music的MP3文件
    private void readSDMP3File(){
        //如果该设备有sd卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            String sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            MediaPlayer m = new MediaPlayer();
        }else {
            Toast.makeText(this, "尚未检测到sd卡", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //初始化一些组件并且监听他们
    private void initView(){
        //显示列表数据
        listView = (ListView) findViewById(R.id.lv_music);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = list.get(i);
                Intent intent = new Intent(MusicListActivity.this, MusicPlayActivity.class);
                //传数据过去
                intent.putExtra("position", i);
                //跳到播放页面
                startActivity(intent );
            }
        });
    }
}
