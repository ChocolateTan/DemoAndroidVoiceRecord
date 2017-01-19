package com.don.voice;

import com.don.voice.common.InjectView;
import com.don.voice.common.InjectViewOnClick;
import com.don.voice.common.Injector;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  @InjectView(R.id.btn_audio_recorder)
  private Button mBtnAudioRecorder;

  @InjectView(R.id.btn_media_recorder)
  private Button mBtnMediaRecorder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Injector.get(this).inject();
  }

  @InjectViewOnClick({R.id.btn_media_recorder, R.id.btn_audio_recorder})
  public void onClick(View view){
    switch (view.getId()){
      case R.id.btn_media_recorder:
        ActivityCompat.startActivity(this, new Intent(this, MediaRecorderActivity.class), null);
        break;
      case R.id.btn_audio_recorder:
        ActivityCompat.startActivity(this, new Intent(this, AudioRecorderActivity.class), null);
        break;
    }
  }
}
