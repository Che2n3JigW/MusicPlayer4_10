package com.cjw.bookproject.musicplayer4_10;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cjw.bookproject.musicplayer4_10.util.DataUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 音乐播放界面
 */
public class MusicPlayActivity extends Activity {

    //另一个AT传过来的参数表示播放那一首歌曲
    private int position;
    //音乐播放器
    private MediaPlayer mediaPlayer = new MediaPlayer();

    /**
     * 0:初始化状态
     * 1:正在播放状态
     * 2:暂停状态
     */
    private int status = 0;     //用于判断音乐当前的状态


    private TextView tv_musicName;
    private TextView tv_progess;
    private TextView tv_duration;
    private SeekBar seekBar1;
    private Button btn_play;
    private Button btn_next;
    private Button btn_last;


    //处理消息
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 1){
                //seekBar与mediaPlayer音乐同步
                seekBar1.setProgress(mediaPlayer.getCurrentPosition());     //进度设置成音乐当前的位置
                tv_progess.setText(getTimeString(mediaPlayer.getCurrentPosition()));    //文本设置成音乐当前的位置
                //发一个延迟消息
                sendEmptyMessageDelayed(1,1000);//会在一秒之后处理
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        //初始化操作
        init();
    }

    private void init() {

        initView();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //播放下一曲
                playNext();
            }
        });

    }

    /**
     * 播放音乐(在播放之前有两种状态  初始化0  暂停2 正在播放的时候不允许播放)
     */
    private void paly() {

        //点击后变成暂停
        btn_play.setBackgroundResource(R.drawable.pause_select);
        //如果为初始化状态
        if (status == 0){
            //获取音乐路径
            Map<String, String> musicMap = DataUtils.getMusicMap(position);
            String path = musicMap.get("path");
            tv_musicName.setText(musicMap.get("name"));
            try {
                //重置
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                //异步准备
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        //更新状态此时为播放状态
                        status = 1;
                        //播放之前获取音乐的时长
                        int duration = mediaPlayer.getDuration();
                        seekBar1.setMax(duration);
                        //时长
                        tv_duration.setText(getTimeString(duration));
                        tv_progess.setText("00:00");
                        //发送一个消息更新seekBar
                        handle.sendEmptyMessage(1);

                        //开始播放
                        mediaPlayer.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (status == 2){
            //暂停状态 要续播放
            status = 1;
            mediaPlayer.start();
            handle.sendEmptyMessage(1);
        }

    }

    /**
     * 上一首音乐
     */
    private void playLast(){
        //防止数组越界
        if (position<=0){
            Toast.makeText(this, "已经是第一首了", Toast.LENGTH_SHORT).show();
        }else {
            position--;
            //如果正在播放,先暂停
            if (status == 1){
                mediaPlayer.stop();
                status = 0;
                //停止发送消息
                handle.removeMessages(1);
                //播放前面的状态可能是暂停 或者是 初始化
                paly();
            }
            if (status == 2){
                mediaPlayer.stop();
                status = 0;
                //停止发送消息
                handle.removeMessages(1);
                //播放前面的状态可能是暂停 或者是 初始化
                paly();
            }

        }

    }

    /**
     * 下一首音乐
     */
    private void playNext(){
        //防止数组越界
        if (position>=DataUtils.getList().size()-1){
            Toast.makeText(this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
        }else {
            position++;
            //如果正在播放,先暂停
            if (status == 1){
                mediaPlayer.stop();
                status = 0;
            }
            //停止发送消息
            handle.removeMessages(1);
            //播放前面的状态可能是暂停 或者是 初始化
            paly();
        }

    }

    /**
     * 暂停
     */
    private void pause(){
        status = 2;
        mediaPlayer.pause();
        btn_play.setBackgroundResource(R.drawable.play_select);
        //不再更新进度条
        handle.removeMessages(1);
    }

    //初始化控件及其监听
    private void initView(){

        //接收传过来的参数
        position = getIntent().getIntExtra("position", 0);//默认值为0

        tv_musicName = (TextView) findViewById(R.id.tv_musicName);
        tv_progess = (TextView) findViewById(R.id.tv_progess);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_last = (Button) findViewById(R.id.btn_last);


        //进度天监听
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //进度改变的时候
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            //手指拖动的
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //手指离开的时候,最后的位置
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //当前的进度
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
            }
        });

        //上一首按钮监听
        btn_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playLast();
            }
        });

        //下一首按钮监听
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        });

        //播放按钮
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果正在播放
                if (status == 1){
                    //暂停
                    pause();
                }else {
                   paly();
                }
            }
        });
    }



    /**
     * 处理毫秒数，以00:00的方式返回
     * 工具方法
     */
    private  String getTimeString(long ts) {
        int time = (int) ts / 1000;
        int ms = time % 60;
        int ss = time / 60;
        ss = ss > 99 ? 99 : ss;
        StringBuffer str = new StringBuffer();
        str.append(ss < 10 ? "0" + ss + ":" : ss + ":");
        str.append(ms < 10 ? "0" + ms : ms + "");
        return str.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (status == 1){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
