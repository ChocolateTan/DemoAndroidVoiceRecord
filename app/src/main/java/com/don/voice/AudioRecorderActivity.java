package com.don.voice;

import com.don.voice.audiorecorder.AMRAudioRecorder;
import com.don.voice.common.CommonUtils;
import com.don.voice.common.InjectView;
import com.don.voice.common.InjectViewOnClick;
import com.don.voice.common.Injector;
import com.don.voice.common.InjectorAdapter;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AudioRecorderActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = AudioRecorderActivity.class.getSimpleName();
  @InjectView(R.id.recycler_view)
  private RecyclerView mRecyclerView;
  @InjectView(R.id.btn_record)
  private Button mBtnRecord;
  private AMRAudioRecorder mAMRAudioRecorder;
  private boolean isStart = false;
  String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dontest20170119/";
  String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dontestFFmpeg/";
  private MediaPlayer mMediaPlay;
  private FFmpeg ffmpeg;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(new BarChartView(this));
//    setContentView(new BarChartPanel(this, "Quarter Vs. sales volume"));
    setContentView(R.layout.activity_audio_recorder);
    Log.i(TAG, TAG + "hi");
    Injector.get(this).inject();

    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
    File file2 = new File(path2);
    if (!file2.exists()) {
      file2.mkdirs();
    }
    progressDialog = new ProgressDialog(this);
    loadFFMpegBinary();
    getRecordList();
  }

  @InjectViewOnClick({R.id.btn_record})
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_record:
        if (isStart) {
          isStart = false;
          Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
          mAMRAudioRecorder.stop();
          Log.i(TAG, "path=" + mAMRAudioRecorder.getAudioFilePath());
          ((Button) v).setText("錄音");
//          AudioRecordFunc.getInstance().stopRecordAndFile();
          String cmd = "-y -i "+ mAMRAudioRecorder.getAudioFilePath() + " " + path2 + mAMRAudioRecorder.getFileName().replace(".amr", ".mp3");
          Log.i(TAG, "cmd=" + cmd);
          String[] command = cmd.split(" ");
          execFFmpegBinary(command);

          getRecordList();
        } else {
          Toast.makeText(this, "start record", Toast.LENGTH_SHORT).show();
          isStart = true;
          if (mAMRAudioRecorder != null) {
            mAMRAudioRecorder.stop();
            mAMRAudioRecorder = null;
          }
          mAMRAudioRecorder = new AMRAudioRecorder(getApplication(), path);
          mAMRAudioRecorder.start();

//          AudioRecordFunc.getInstance().startRecordAndFile();

          ((Button) v).setText("停止");
        }
//        mAMRAudioRecorder.
        break;
    }
  }

  private void getRecordList() {
//    Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
    final File file = new File(path);
    final File[] files = file.listFiles();
    final ArrayList<File> filesArray = new ArrayList<>(Arrays.asList(files));
    Log.i(TAG, "files=" + files.length);
    Log.i(TAG, "filesArray=" + filesArray.size());
    Collections.sort(filesArray, new Comparator<File>() {
      @Override
      public int compare(File o1, File o2) {
        long result = Long.parseLong(o2.getName().replace(".amr", "")) - Long.parseLong(o1.getName().replace(".amr", ""));
        Log.i(TAG, "result=" + result);
        if (result < 0) {
          return 0;
        } else {
          return 1;
        }
      }
    });

    RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_audio, parent, false);
        return new ViewHolderAudio(view);
      }

      @Override
      public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderAudio) {
          ViewHolderAudio viewHolderAudio = (ViewHolderAudio) holder;

          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String name = filesArray.get(position).getName().replace(".amr", "").toString();
          Date date = new Date(Long.parseLong(name));
//          try {
//
//          } catch (ParseException e) {
//            e.printStackTrace();
//          }
//          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//          double fileSize = filesArray.get(position).length() * 0.1 / 1024 / 1024;
          double fileSize = filesArray.get(position).length() * 1.0 / 1024;
          viewHolderAudio.mTvAudio.setText((position + 1) + " : " + sdf.format(date) + " # " + String.format("%.2f", fileSize) + "KB"
            + " # " + getAmrDuration(filesArray.get(position)) * 1.0 / 1000 + "s");
          List<Float> list = CommonUtils.getVoiceViewData(getApplication(), filesArray.get(position).getName());
          if (null != list && list.size() > 0) {
            viewHolderAudio.mBarChartView.setListSize(100);
            viewHolderAudio.mBarChartView.setMinValue(40f);
            viewHolderAudio.mBarChartView.setMaxValue(120f);
            viewHolderAudio.mBarChartView.setValueList(list);
            viewHolderAudio.mBarChartView.setVisibility(View.VISIBLE);
          } else {
            viewHolderAudio.mBarChartView.setVisibility(View.GONE);
          }

          viewHolderAudio.mTvAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onStartAudio(filesArray.get(position).getAbsolutePath());
            }
          });
        }
      }

      @Override
      public int getItemCount() {
        return filesArray.size();
      }

      class ViewHolderAudio extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_record_text)
        private TextView mTvAudio;
        @InjectView(R.id.bar_chart_view)
        private BarChartView mBarChartView;

        public ViewHolderAudio(View itemView) {
          super(itemView);
          InjectorAdapter.get(this).inject();
        }
      }
    };

    RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
  }

  private void onStartAudio(String filepath) {
    if (null != mMediaPlay && mMediaPlay.isPlaying()) {
      mMediaPlay.release();
      mMediaPlay = null;
    }
    mMediaPlay = new MediaPlayer();
    try {
      mMediaPlay.setDataSource(filepath);
      mMediaPlay.prepare();
      mMediaPlay.start();
    } catch (IOException e) {
      e.printStackTrace();
      Toast.makeText(this, "讀取失敗", Toast.LENGTH_SHORT).show();
    }
  }

  private void onStopPlayAudio() {
    mMediaPlay.release();
    mMediaPlay = null;
  }


  /**
   * 得到amr的时长
   */
  public static long getAmrDuration(File file) {
    long duration = -1;
    int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
    RandomAccessFile randomAccessFile = null;
    try {
      randomAccessFile = new RandomAccessFile(file, "rw");
      long length = file.length();//文件的长度
      int pos = 6;//设置初始位置
      int frameCount = 0;//初始帧数
      int packedPos = -1;
      /////////////////////////////////////////////////////
      byte[] datas = new byte[1];//初始数据值
      while (pos <= length) {
        randomAccessFile.seek(pos);
        if (randomAccessFile.read(datas, 0, 1) != 1) {
          duration = length > 0 ? ((length - 6) / 650) : 0;
          break;
        }
        packedPos = (datas[0] >> 3) & 0x0F;
        pos += packedSize[packedPos] + 1;
        frameCount++;
      }
      /////////////////////////////////////////////////////
      duration += frameCount * 20;//帧数*20
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (randomAccessFile != null) {
        try {
          randomAccessFile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return duration;
  }


  private void loadFFMpegBinary() {
    try {
      ffmpeg = FFmpeg.getInstance(this);
      ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
        @Override
        public void onFailure() {
          Toast.makeText(AudioRecorderActivity.this, "fail to load", Toast.LENGTH_SHORT).show();
//          showUnsupportedExceptionDialog();
        }
      });
    } catch (FFmpegNotSupportedException e) {
//      showUnsupportedExceptionDialog();
      e.printStackTrace();
    }
  }

  private void execFFmpegBinary(final String[] command) {
    try {
      ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
        @Override
        public void onFailure(String s) {
//          addTextViewToLayout("FAILED with output : "+s);
          Toast.makeText(AudioRecorderActivity.this, "fail execFFmpegBinary" + s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(String s) {
//          addTextViewToLayout("SUCCESS with output : "+s);
          Toast.makeText(AudioRecorderActivity.this, "s" + s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgress(String s) {
          Log.d(TAG, "Started command : ffmpeg "+command);
//          addTextViewToLayout("progress : "+s);
          progressDialog.setMessage("Processing\n"+s);
        }

        @Override
        public void onStart() {
//          outputLayout.removeAllViews();

          Log.d(TAG, "Started command : ffmpeg " + command);
          progressDialog.setMessage("Processing...");
          progressDialog.show();
        }

        @Override
        public void onFinish() {
          Log.d(TAG, "Finished command : ffmpeg "+command);
          progressDialog.dismiss();
        }
      });
    } catch (FFmpegCommandAlreadyRunningException e) {
      // do nothing for now
      e.printStackTrace();
    }
  }
}
