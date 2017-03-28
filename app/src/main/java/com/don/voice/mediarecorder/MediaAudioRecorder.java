package com.don.voice.mediarecorder;

import com.don.voice.common.CommonUtils;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DON on 17/01/18.
 */

public class MediaAudioRecorder {
  private static final String TAG = MediaAudioRecorder.class.getSimpleName();
  private final Context mContext;
  private boolean singleFile = true;

  private MediaRecorder recorder;

  private ArrayList<String> files = new ArrayList<String>();

  private String fileDirectory;

  private String finalAudioPath;

  private boolean isRecording;

  private List<Float> mListVoice = new ArrayList<>();
  private String mFileName;

  public boolean isRecording() {
    return isRecording;
  }

  public String getAudioFilePath() {
    return finalAudioPath;
  }

  public String getFileName(){
    return mFileName;
  }

  public MediaAudioRecorder(Context ctx, String audioFileDirectory) {
    this.fileDirectory = audioFileDirectory;
    this.mContext = ctx;

    if (!this.fileDirectory.endsWith("/")) {
      this.fileDirectory += "/";
    }

    newRecorder();
  }

  public boolean start () {
    prepareRecorder();

    try {
      recorder.prepare();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    recorder.start();
    isRecording = true;

    updateMicStatus();

    return true;
  }

  public boolean pause () {
    if (recorder == null || !isRecording) {
      throw new IllegalStateException("[MediaAudioRecorder] recorder is not recording!");
    }

    recorder.stop();
    recorder.release();
    recorder = null;

    isRecording = false;

    return true;
  }

  public boolean resume () {
    if (isRecording) {
      throw new IllegalStateException("[MediaAudioRecorder] recorder is recording!");
    }

    singleFile = false;
    newRecorder();
    return start();
  }

  public boolean stop () {
    if (!isRecording) {
      return merge();
    }

    if (recorder == null) {
      return false;
    }

    recorder.stop();
    recorder.release();
    recorder = null;
    isRecording = false;

    return merge();
  }

  private boolean merge () {

//    String fileName = new Date().getTime() + ".amr";
    CommonUtils.setVoiceViewData(mContext, mFileName, mListVoice);

    // If never paused, just return the file
    if (singleFile) {
      this.finalAudioPath = this.files.get(0);
      return true;
    }

    // Merge files
    String mergedFilePath = this.fileDirectory + mFileName;
    try {
      FileOutputStream fos = new FileOutputStream(mergedFilePath);

      for (int i = 0,len = files.size();i<len;i++) {
        File file = new File(this.files.get(i));
        FileInputStream fis = new FileInputStream(file);

        // Skip file header bytes,
        // amr file header's length is 6 bytes
        if (i > 0) {
          for (int j=0; j<6; j++) {
            fis.read();
          }
        }

        byte[] buffer = new byte[512];
        int count = 0;
        while ( (count = fis.read(buffer)) != -1 ) {
          fos.write(buffer,0,count);
        }

        fis.close();
        fos.flush();
        file.delete();
      }

      fos.flush();
      fos.close();

      this.finalAudioPath = mergedFilePath;
      return true;
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  private void newRecorder () {
    recorder = new MediaRecorder();
  }

  private void prepareRecorder () {
    File directory = new File(this.fileDirectory);
    if (!directory.exists() || !directory.isDirectory()) {
      throw new IllegalArgumentException("[MediaAudioRecorder] audioFileDirectory is a not valid directory!");
    }
    mFileName = new Date().getTime() + ".amr";
    String filePath = directory.getAbsolutePath() + "/" + mFileName;
    this.files.add(filePath);

    recorder.setOutputFile(filePath);
    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
  }

  private int BASE = 1;
  private int SPACE = 100;// 间隔取样时间
  public void updateMicStatus(){
    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        if (recorder != null && isRecording) {
          double ratio = (double)recorder.getMaxAmplitude() /BASE;
          double db = 0;// 分贝
          if (ratio > 1)
            db = 20 * Math.log10(ratio);
          Log.d(TAG,"分贝值："+db);
          mListVoice.add((float) db);
        }else{
          this.cancel();
        }
      }
    };
    timer.schedule(timerTask, 1000, 1000);
  }
}
