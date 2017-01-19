package com.don.voice;

import com.don.voice.audiorecorder.AMRAudioRecorder;
import com.don.voice.common.InjectView;
import com.don.voice.common.InjectViewOnClick;
import com.don.voice.common.Injector;
import com.don.voice.common.InjectorAdapter;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class AudioRecorderActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = AudioRecorderActivity.class.getSimpleName();
  @InjectView(R.id.recycler_view)
  private RecyclerView mRecyclerView;
  @InjectView(R.id.btn_record)
  private Button mBtnRecord;
  private AMRAudioRecorder mAMRAudioRecorder;
  private boolean isStart = false;
  String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dontest20170119/";
  private MediaPlayer mMediaPlay;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_audio_recorder);
    Log.i(TAG, TAG + "hi");
    Injector.get(this).inject();

    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }

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
          ((Button)v).setText("錄音");
          getRecordList();
        } else {
          Toast.makeText(this, "start record", Toast.LENGTH_SHORT).show();
          isStart = true;
          if (mAMRAudioRecorder != null) {
            mAMRAudioRecorder.stop();
            mAMRAudioRecorder = null;
          }
          mAMRAudioRecorder = new AMRAudioRecorder(path);
          mAMRAudioRecorder.start();
          ((Button)v).setText("停止");
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
        return (int) (Long.parseLong(o2.getName().replace(".amr", "")) - Long.parseLong(o1.getName().replace(".amr", "")));
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
          viewHolderAudio.mTvAudio.setText(position + " : " + filesArray.get(position).getName());
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
}
