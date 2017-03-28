package com.don.voice.activity;

import com.don.voice.BarChartView;
import com.don.voice.R;
import com.don.voice.common.InjectView;
import com.don.voice.common.InjectViewOnClick;
import com.don.voice.common.Injector;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//  @InjectView(R.id.btn_audio_recorder)
//  private Button mBtnAudioRecorder;
//
//  @InjectView(R.id.btn_media_recorder)
//  private Button mBtnMediaRecorder;

  @InjectView(R.id.bar_chart_view)
  private BarChartView barChartView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
//    BarChartView barChartView = new BarChartView(this);
//    setContentView(barChartView);
    Injector.get(this).inject();
    List<Float> list = new ArrayList<>();
    list.add(100f);
    list.add(200f);
    list.add(300f);
    list.add(400f);
    list.add(300f);
    list.add(200f);
    list.add(400f);
    list.add(100f);
    list.add(400f);
    list.add(200f);
    list.add(400f);
    list.add(200f);
    barChartView.setValueList(list);
  }

  @InjectViewOnClick({R.id.btn_media_recorder, R.id.btn_audio_recorder, R.id.btn_audio_recorder2})
  public void onClick(View view){
    switch (view.getId()){
      case R.id.btn_media_recorder:
        ActivityCompat.startActivity(this, new Intent(this, MediaRecorderActivity.class), null);
        break;
      case R.id.btn_audio_recorder:
        ActivityCompat.startActivity(this, new Intent(this, MediaRecorder2Activity.class), null);
        break;
      case R.id.btn_audio_recorder2:
        ActivityCompat.startActivity(this, new Intent(this, AudioActivity.class), null);
        break;
    }
  }
}
