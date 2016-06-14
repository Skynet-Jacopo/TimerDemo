package com.example.liuqun.timerdemo;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements CompoundButton
        .OnCheckedChangeListener, DialogInterface.OnClickListener {

    private static final String TAG = "MainActivity";
    //ButterKnife的测试使用
    @BindView(R.id.imageView)
    ImageView    imageView;
    @BindView(R.id.text_time)
    TextView     textTime;
    @BindView(R.id.toggleButton)
    ToggleButton toggleButton;

    //新加了几个控制音乐播放的按钮
    @BindView(R.id.music_pause)
    ImageView    mMusicPause;
    @BindView(R.id.music_continue)
    ImageView    mMusicContinue;
    @BindView(R.id.music_stop)
    ImageView    mMusicStop;

    private Handler mHandler;

    private EditText    mTextUserInput; //时间输入框
    private TaskTimer   mTimer;//实例化TaskTimer对象
    private MediaPlayer mPlayer;//实例化控制手机播放器的媒体播放器类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //修改显示时间的字体
        AssetManager assetManager = getAssets();
        Typeface     typeface     = Typeface.createFromAsset(assetManager, "fonts/COLONNA.TTF");
        textTime.setTypeface(typeface);

        //设置ToggleButton的状态监听事件
        toggleButton.setOnCheckedChangeListener(this);

        mHandler = new Handler();
        mTimer = new TaskTimer(mHandler) {
            @Override
            public void onTimerStopped() {
                textTime.setText("00:00");
            }

            @Override
            public void onTimerFinished() {
                toggleButton.setChecked(false);
                playSounds();
                mMusicPause.setVisibility(View.VISIBLE);
                mMusicContinue.setVisibility(View.VISIBLE);
                mMusicStop.setVisibility(View.VISIBLE);
            }

            @Override
            public void updateUI(long timeRemaining) {
                textTime.setText(TaskTimer.convertToString(timeRemaining));
            }
        };

    }

    //播放声音文件
    private void playSounds() {
        try {
            AssetFileDescriptor file = getAssets().openFd("sounds/5.mp3");
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(file.getFileDescriptor());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View           view     = inflater.inflate(R.layout.user_input, null);
            mTextUserInput = (EditText) view.findViewById(R.id.text_input);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入时间");
            builder.setView(view);
            builder.setPositiveButton("确定", this);
            builder.setNegativeButton("取消", this);
            builder.show();
        } else {
            mTimer.stop();
//            Toast.makeText(MainActivity.this, "off", Toast.LENGTH_SHORT).show();
        }
    }

    //AlertDialog的点击事件监听
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                String input = mTextUserInput.getText().toString();
                if (TaskTimer.isValidInput(input)) {
                    mTimer.setTimeRemaining(TaskTimer.convertToMilliseconds(input));
                    mTimer.start();
                } else {
                    toggleButton.setChecked(false);
                    Toast.makeText(MainActivity.this, "输入格式有误,请重新操作!", Toast
                            .LENGTH_SHORT).show();
                }

                break;
            case DialogInterface.BUTTON_NEGATIVE:
                toggleButton.setChecked(false);
//                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @OnClick({R.id.music_pause, R.id.music_continue, R.id.music_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.music_pause:
                mPlayer.pause();
                break;
            case R.id.music_continue:
                mPlayer.start();
                break;
            case R.id.music_stop:
                mPlayer.stop();
                mMusicPause.setVisibility(View.INVISIBLE);
                mMusicContinue.setVisibility(View.INVISIBLE);
                mMusicStop.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
