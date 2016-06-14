package com.example.liuqun.timerdemo;

import android.os.Handler;
import android.util.Log;

/**
 * Created by liuqun on 6/13/2016.
 */
public abstract class TaskTimer implements Runnable {
    private static final String TAG = "MainActivity";
    private Handler mHandler;

    private long timeRemaining;//剩余时间
    private boolean isKilled = false;


    public TaskTimer(Handler handler) {
        mHandler = handler;
        this.timeRemaining = 0;
    }

    public TaskTimer(Handler handler, long timeRemaining) {
        mHandler = handler;
        this.timeRemaining = timeRemaining;
    }

    //判断用户输入的时间是否有效
    public static boolean isValidInput(String timeInput) {
        if (timeInput == null || timeInput.isEmpty()) {
            return false;
        }
        String trimmedInput = timeInput.trim();
        if (trimmedInput.length() == 5 && trimmedInput.indexOf(':') == 2) {
            try {
                int totalDuration = extractTotalDuration(trimmedInput);
                return totalDuration > 5 ? true : false;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }

    }

    //从用户输入的时间中提取出分钟数
    public static int extractMinutes(String timeInput) throws
            NumberFormatException {
        int minutes = Integer.parseInt(timeInput.substring(0, 2));
        return minutes;
    }

    //从用户输入的时间中提取出后边的秒数
    public static int extractSeconds(String timeInput) throws
            NumberFormatException {
        int seconds = Integer.parseInt(timeInput.substring(3, timeInput.length
                ()));
        return seconds;
    }

    //从用户输入的时间中提取出总的秒数
    public static int extractTotalDuration(String timeInput) throws
            NumberFormatException {
        int totalDuration = extractMinutes(timeInput) * 60 + extractSeconds
                (timeInput);
        return totalDuration;
    }

    //把用户输入的时间转换成long格式的毫秒数
    public static long convertToMilliseconds(String timeInput) {
        try {
            long milliseconds = extractTotalDuration(timeInput) * 1000;
            return milliseconds;
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    //把处理好的long时间转换成可以显示到UI界面的String格式
    public static String convertToString(long timeInput) {
        int totalSeconds = (int) (timeInput / 1000);
        int minutes      = totalSeconds / 60;
        int seconds      = totalSeconds % 60;

        String minutesString = (minutes < 10) ? "0" + minutes : minutes + "";
        String secondsString = (seconds < 10) ? "0" + seconds : seconds + "";

        return minutesString + ":" + secondsString;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    //倒计时开始
    public void start() {
        isKilled = false;
        mHandler.postDelayed(this, 1000);
    }

    //ToggleButton点击停止
    public void stop() {
        isKilled = true;
        onTimerStopped();
    }

    //每隔一秒显示到UI的线程
    @Override
    public void run() {
        if (!isKilled) {
            Log.d(TAG, "run: ");
            updateUI(timeRemaining);
            timeRemaining -= 1000;
            if (timeRemaining >= 0) {
                mHandler.postDelayed(this, 1000);
            } else {
                onTimerFinished();
            }
        }

    }

    public abstract void onTimerStopped();

    public abstract void onTimerFinished();

    public abstract void updateUI(long timeRemaining);
}
