package com.compdigitec.libvlcandroidsample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.forwork.triolan.R;
import com.forwork.triolan.main.TriolanAPI;
import com.forwork.triolan.model.CustomData;
import com.forwork.triolan.ui.listener.OnSwipeTouchListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class VideoActivity extends FragmentActivity implements SurfaceHolder.Callback,
        IVideoPlayer {

    public final static String TAG = "LibVLCAndroidSample/VideoActivity";
    //public static String LOCATION = "com.compdigitec.libvlcandroidsample.VideoActivity.location";
    //public static String LOCATION_SOUND = "com.compdigitec.libvlcandroidsample.VideoActivity.location";
    private final static int VideoSizeChanged = -1;
    //private String mFilePath_sound = "0";
    private static final String DATA_CHANNELS = "DataChannels";
    public String mFilePath = "0";
    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;
    // media player
    private LibVLC libvlc;
    private int mVideoWidth;
    private int mVideoHeight;
    private ArrayList<CustomData> objects = new ArrayList<CustomData>();
    private SharedPreferences sPref;
    private Button hwDecoding;
    private ImageButton playStop;
    private ImageButton back;
    private ImageButton next;
    private ImageButton reSize;
    private Run animation;
    private boolean statusSize;
    //  ProgressDialog pd;
    /**
     * **********
     * Events
     * ***********
     */

    private Handler mHandler = new MyHandler(this);

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (animation != null) {
            animation.show();
        }
        return super.dispatchTouchEvent(ev);

    }

    /**
     * **********
     * Activity
     * ***********
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        ((TriolanAPI)getApplication()).trackScreenView("Player View");
        playStop = (ImageButton) findViewById(R.id.sample_play_stop_button);
        next = (ImageButton) findViewById(R.id.sample_next_button);
        back = (ImageButton) findViewById(R.id.sample_back_button);
        reSize = (ImageButton) findViewById(R.id.sample_change_size_button);
        playStop.setOnClickListener(view1 -> {
            if (libvlc.isPlaying()) {
                libvlc.pause();
                playStop.setBackgroundResource(R.drawable.ic_play_arrow_white_48dp);
            } else {
                libvlc.play();
                playStop.setBackgroundResource(R.drawable.ic_stop_white_48dp);
            }
        });
        next.setOnClickListener(view -> skipNext());
        back.setOnClickListener(view -> skipPrevisions());
        reSize.setOnClickListener(view -> setSize(mVideoWidth, mVideoHeight));
        animation = new Run(findViewById(R.id.sample_control_layout));
        animation.show();
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mFilePath = intent.getExtras().getString("LOCATION");
        } else {
            mFilePath = "http://109.86.150.252:8888/udp/238.0.0.19:1234";
        }
        loadDataChannels();
        //mFilePath_sound = intent.getExtras().getString("LOCATION_SOUND");
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        Log.d(TAG, "Playing back " + mFilePath);
        //Log.d(TAG, "Playing back " + mFilePath_sound);

        mSurface = (SurfaceView) findViewById(R.id.surface_video);

        mSurface.setKeepScreenOn(true);

        holder = mSurface.getHolder();
        holder.addCallback(this);
        try {
            createPlayer(mFilePath, "");
        } catch (Exception e) {
            Toast.makeText(VideoActivity.this, "Ваше устройство не может воспроизвести канал", Toast.LENGTH_SHORT).show();
        }

        mSurface.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                //   Toast.makeText(VideoActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                skipPrevisions();
            }

            public void onSwipeLeft() {
                skipNext();
            }

            public void onSwipeBottom() {
                // Toast.makeText(VideoActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void loadDataChannels() {
        sPref = getApplication().getSharedPreferences(
                DATA_CHANNELS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sPref.getString(
                DATA_CHANNELS, null);
        if (json != null)

        {
            Type type = new TypeToken<ArrayList<CustomData>>() {
            }.getType();
            objects = gson.fromJson(json, type);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            libvlc.playMRL(mFilePath);
        } catch (Exception e) {
            Toast.makeText(VideoActivity.this, "Канал временно не доступен", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        try {

            libvlc.stop();

        } catch (Exception e) {
            Toast.makeText(VideoActivity.this, "Ошибка воспроизведения,обновите канал", Toast.LENGTH_LONG).show();
        }

        //finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //releasePlayer();
        System.exit(0);
    }

    /**
     * **********
     * Surface
     * ***********
     */

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
                               int width, int height) {
        if (libvlc != null)
            libvlc.attachSurface(holder.getSurface(), this);
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
    }

    private void setSize(int width, int height) {
        statusSize = !statusSize;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 0)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        if (statusSize) {
            // getWindow().getDecorView() doesn't always take orientation into
            // account, we have to correct the values

     /* Раскомментируйте код ниже для пропорционального отображения видео-потока */

            boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
            if (w > h && isPortrait || w < h && !isPortrait) {
                int i = w;
                w = h;
                h = i;
            }

            float videoAR = (float) mVideoWidth / (float) mVideoHeight;
            float screenAR = (float) w / (float) h;

            if (screenAR < videoAR)
                h = (int) (w / videoAR);
            else
                w = (int) (h * videoAR);
        }

        // force surface buffer size
        if (holder != null)
            holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width,
                               int visible_height, int sar_num, int sar_den) {
        Message msg = Message.obtain(mHandler, VideoSizeChanged, width, height);
        msg.sendToTarget();
    }

    /**
     * **********
     * Player
     * ***********
     */

    private void createPlayer(String media, final String media_sound) {
        //releasePlayer();
        try {

            // Create a new media player
            libvlc = LibVLC.getInstance();
            libvlc.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);

            //libvlc.setSubtitlesEncoding("");
            libvlc.setAout(LibVLC.AOUT_OPENSLES);
            libvlc.setTimeStretching(true);
            libvlc.setChroma("RV32");
            libvlc.setVerboseMode(true);
            LibVLC.restart(this);
            EventHandler.getInstance().addHandler(mHandler);
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.setKeepScreenOn(true);
            MediaList list = libvlc.getMediaList();
            list.clear();
            list.add(new Media(libvlc, LibVLC.PathToURI(media)));
            libvlc.playIndex(0);

            //pd.hide();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating player!", Toast.LENGTH_LONG).show();
        }
    }

    private void pausePlayer() {
        if (libvlc == null)
            return;
        libvlc.pause();
    }

    private void releasePlayer() {
        if (libvlc == null)
            return;
        EventHandler.getInstance().removeHandler(mHandler);
        libvlc.closeAout();
        libvlc.stop();
        libvlc.detachSurface();
        holder = null;
        libvlc.destroy();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    private static class MyHandler extends Handler {
        private WeakReference<VideoActivity> mOwner;

        public MyHandler(VideoActivity owner) {
            mOwner = new WeakReference<VideoActivity>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoActivity player = mOwner.get();

            // SamplePlayer events
            if (msg.what == VideoSizeChanged) {
                player.setSize(msg.arg1, msg.arg2);
                return;
            }

            // Libvlc events
            Bundle b = msg.getData();
            switch (b.getInt("event")) {
                case EventHandler.MediaPlayerEndReached:
                    player.releasePlayer();
                    break;
                case EventHandler.MediaPlayerPlaying:
                case EventHandler.MediaPlayerPaused:
                case EventHandler.MediaPlayerStopped:
                default:
                    break;
            }
        }
    }

    public void skipPrevisions() {

        int indexClick = 0;
        for (int i = 0; i < objects.size(); i++) {
            if (mFilePath.equals(objects.get(i).getStream())) {
                indexClick = i;
            }
        }
        if (indexClick == 0) {
            mFilePath = objects.get(objects.size() - 1).getStream();
            Toast.makeText(VideoActivity.this, objects.size() + "/" + (objects.size()) + " " + '"' + objects.get(objects.size() - 1).getWeb() + '"', Toast.LENGTH_SHORT).show();
        } else {
            mFilePath = objects.get(indexClick - 1).getStream();
            Toast.makeText(VideoActivity.this, (indexClick) + "/" + (objects.size()) + " " + '"' + objects.get(indexClick - 1).getWeb() + '"', Toast.LENGTH_SHORT).show();

        }
        try {
            libvlc.playMRL(mFilePath);
        } catch (Exception e) {
            Toast.makeText(VideoActivity.this, "Канал временно не доступен", Toast.LENGTH_SHORT).show();
        }

    }

    public void skipNext() {

        int indexClick = 0;
        for (int i = 0; i < objects.size(); i++) {
            if (mFilePath.equals(objects.get(i).getStream())) {
                indexClick = i;
            }
        }
        if (indexClick == objects.size() - 1) {
            mFilePath = objects.get(0).getStream();
            Toast.makeText(VideoActivity.this, 1 + "/" + (objects.size()) + " " + '"' + objects.get(0).getWeb() + '"', Toast.LENGTH_SHORT).show();
        } else {
            mFilePath = objects.get(indexClick + 1).getStream();
            Toast.makeText(VideoActivity.this, (indexClick + 2) + "/" + (objects.size()) + " " + '"' + objects.get(indexClick + 1).getWeb() + '"', Toast.LENGTH_SHORT).show();

        }
        libvlc.playMRL(mFilePath);
    }


    private class Run implements Runnable {
        private int statusAnimation;
        private Handler handler;
        private View view;

        public Run(View view) {
            this.view = view;
            handler = new Handler(Looper.myLooper());
        }

        public void show() {
            statusAnimation = 0;
            handler.removeCallbacks(this);
            run();
            view.setEnabled(true);
        }

        @Override
        public void run() {
            switch (statusAnimation) {
                case 0: {

                    view.setAlpha(view.getAlpha() + 0.04f > 1 ? 1f : view.getAlpha() + 0.04f);
                    if (view.getAlpha() == 1) {
                        statusAnimation++;
                        handler.postDelayed(this, 7000);
                    } else {
                        handler.postDelayed(this, 25);
                    }
                    break;
                }
                case 1: {
                    view.setAlpha(view.getAlpha() - 0.01f < 0 ? 0f : view.getAlpha() - 0.01f);
                    if (view.getAlpha() == 0) {
                        statusAnimation++;
                        view.setEnabled(false);
                    } else {
                        handler.postDelayed(this, 25);
                    }
                    break;
                }
            }
        }
    }

}
