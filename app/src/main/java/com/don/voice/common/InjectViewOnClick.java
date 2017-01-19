package com.don.voice.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by DON on 17/01/18.
 */

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectViewOnClick {
  /**
   * The resource id of the View to find and inject.
   */
  int[] value();
}
