package com.jetpack.demo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @author yuandunbin
 * @date 2022/10/22
 */
class ThreadTest {
    private static final String TAG = "okc";

    private void test() {
        class LooperThread extends Thread {

            private Looper looper;

            public LooperThread(String s) {
                super(s);
            }

            private Looper getLooper() {
                synchronized (this) {
                    if (looper == null && isAlive()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return looper;
            }

            @Override
            public void run() {
                Looper.prepare();
                synchronized (this) {
                    looper = Looper.myLooper();
                    notify();
                }
                Looper.loop();
            }
        }

        LooperThread looperThread = new LooperThread("looper-thread");
        looperThread.start();
        Handler handler = new Handler(looperThread.getLooper()) {


            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.e(TAG, "handleMessage: " + msg.what);
                Log.e(TAG, "handleMessage: " + Thread.currentThread().getName());
            }
        };
        handler.sendEmptyMessage(11111);
    }
}
