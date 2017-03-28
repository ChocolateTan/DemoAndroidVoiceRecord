package com.don.voice.audiorecorder;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;

/**
 * Created by DON on 17/03/15.
 */

public class AudioFileFunc {
  //音频输入-麦克风
  public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

  //采用频率
  //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
  public final static int AUDIO_SAMPLE_RATE = 44100;  //44.1KHz,普遍使用的频率
  //录音输出文件
//  private final static String AUDIO_RAW_FILENAME = "donRawAudio.raw";
//  private final static String AUDIO_WAV_FILENAME = "donFinalAudio.wav";
//  public final static String AUDIO_AMR_FILENAME = "donFinalAudio.amr";
  private static String mFilePath;
  private static String mFileName;

  /**
   * 判断是否有外部存储设备sdcard
   * @return true | false
   */
//  public static boolean isSdcardExit(){
//    if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//      return true;
//    else
//      return false;
//  }

  /**
   * 获取麦克风输入的原始音频流文件路径
   * @return
   */
  public static String getRawFilePath(){
    String mAudioRawPath = "";
//    if(isSdcardExit()){
//      String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//      mAudioRawPath = fileBasePath+"/"+AUDIO_RAW_FILENAME;

      String fileBasePath = mFilePath;
      mAudioRawPath = fileBasePath+"/"+mFileName+".raw";

//    }

    return mAudioRawPath;
  }

  /**
   * 获取编码后的WAV格式音频文件路径
   * @return
   */
  public static String getWavFilePath(){
    String mAudioWavPath = "";
//    if(isSdcardExit()){
//      String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//      mAudioWavPath = fileBasePath+"/"+AUDIO_WAV_FILENAME;

      String fileBasePath = mFilePath;
      mAudioWavPath = fileBasePath+"/"+mFileName+".wav";
//    }
    return mAudioWavPath;
  }


  /**
   * 获取编码后的AMR格式音频文件路径
   * @return
   */
  public static String getAMRFilePath(){
    String mAudioAMRPath = "";
//    if(isSdcardExit()){
//      String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//      mAudioAMRPath = fileBasePath+"/"+AUDIO_AMR_FILENAME;

      String fileBasePath = mFilePath;
      mAudioAMRPath = fileBasePath+"/"+mFileName+".amr";
//    }
    return mAudioAMRPath;
  }

  /**
   * 获取文件大小
   * @param path,文件的绝对路径
   * @return
   */
  public static long getFileSize(String path){
    File mFile = new File(path);
    if(!mFile.exists())
      return -1;
    return mFile.length();
  }
  //文件所在的路徑，不包含文件名
  public static void setFilePath(String path){
    mFilePath=path;
  }
  //文件名，輸出哪種類型必須對應相同後綴
  public static void setFileName(String fileName){
    mFileName=fileName;
  }
}
