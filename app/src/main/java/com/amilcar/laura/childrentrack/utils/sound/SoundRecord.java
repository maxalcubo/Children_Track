package com.amilcar.laura.childrentrack.utils.sound;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.util.Log;

import com.amilcar.laura.childrentrack.interfaces.OnRecordCompletedListener;

import java.io.IOException;

public class SoundRecord {
    public static final int SECONDS_RECORD = 10;
    private OnRecordCompletedListener onRecordCompletedListener;
    private static final String LOG_TAG = "SoundRecord";

    private static String fileName = null;
    private MediaRecorder recorder = null;

    public SoundRecord(Context context){
        // Record to the external cache directory for visibility
        fileName = context.getExternalCacheDir().getAbsolutePath();
        fileName += "/audio.mp3";
    }
    public void record(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();

        CountDownTimer countDowntimer = new CountDownTimer(SECONDS_RECORD * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                //after SECONDS_RECORD seconds of recording, it will stop
                stopRecording();
                onRecordCompletedListener.OnRecordCompleted();
            }
        };
        countDowntimer.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public String getFileName(){
        return fileName;
    }

    public void setOnRecordCompletedListener(OnRecordCompletedListener onRecordCompletedListener){
        this.onRecordCompletedListener = onRecordCompletedListener;
    }
}
