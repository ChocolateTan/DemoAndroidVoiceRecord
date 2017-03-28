package com.don.voice.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.View.OnClickListener;
import com.don.voice.R;
import com.don.voice.audiorecorder.AudioRecordFunc;

public class AudioActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_audio);

    findViewById(R.id.btn_record).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if(AudioRecordFunc.getInstance().isRecore()){
          ((AppCompatButton)v).setText("start");
          AudioRecordFunc.getInstance().stopRecordAndFile();
        }else {
          ((AppCompatButton)v).setText("stop");
          AudioRecordFunc.getInstance().startRecordAndFile();
        }
      }
    });
  }
}
