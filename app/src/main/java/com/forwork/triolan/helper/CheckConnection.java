package com.forwork.triolan.helper;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class CheckConnection {

    final static String TAG = "mylog";
    Thread t2, t3;
    int existInternet = 0;

    public boolean CheckConnect() {

        Log.d(TAG, "---- start---------");

        t2 = new Thread(new Runnable() {
            public void run() {
                checkConnectivity("http://www.ya.ru");
            }
        });

        t3 = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000); // это 1 секунда,
                    // нужно настроить для вашего соединения, сколько готовы ждать ответа
                    if (t2.isAlive()) {
                        //existInternet = 0; // интернета нет
                        t2.isInterrupted();
                    }

                    Log.d(TAG, "---- internet: " + existInternet);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        t2.start();
        t3.start();

        try {
            t3.join(); // ожидание завершения потока t3
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "----Main internet: " + existInternet);
        if (existInternet == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void checkConnectivity(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            // conn.setRequestMethod("HEAD");
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                existInternet = 1; // интернет есть
            }

        } catch (Exception e) {
            Log.d(TAG, "error: " + e); // на ошибку можно не обращать внимание
        }

    }
}