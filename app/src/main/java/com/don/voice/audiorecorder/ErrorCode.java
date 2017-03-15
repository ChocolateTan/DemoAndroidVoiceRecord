package com.don.voice.audiorecorder;

import com.don.voice.R;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by DON on 17/03/15.
 */

public class ErrorCode {
  public final static int SUCCESS = 1000;
  public final static int E_NOSDCARD = 1001;
  public final static int E_STATE_RECODING = 1002;
  public final static int E_UNKOWN = 1003;


  public static String getErrorInfo(Context vContext, int vType) throws Resources.NotFoundException
  {
    switch(vType)
    {
      case SUCCESS:
        return "success";
      case E_NOSDCARD:
        return "E_NOSDCARD";
      case E_STATE_RECODING:
        return "E_STATE_RECODING";
      case E_UNKOWN:
      default:
        return "unknow";

    }
  }
}
