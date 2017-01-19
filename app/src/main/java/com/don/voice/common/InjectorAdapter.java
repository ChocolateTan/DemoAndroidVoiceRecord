package com.don.voice.common;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by DON on 17/01/16.
 */

public final class InjectorAdapter {
  private static final String TAG = "don test";
  private final RecyclerView.ViewHolder mViewHolder;

  private InjectorAdapter(RecyclerView.ViewHolder viewHolder) {
    mViewHolder = viewHolder;
  }

  /**
   * Gets an {@link InjectorAdapter} capable of injecting fields for the given Activity.
   */
  public static InjectorAdapter get(RecyclerView.ViewHolder viewHolder) {
    return new InjectorAdapter(viewHolder);
  }

  /**
   * Injects all fields that are marked with the {@link InjectView} annotation.
   * <p>
   * For each field marked with the InjectView annotation, a call to
   * {@link Activity#findViewById(int)} will be made, passing in the resource id stored in the
   * value() method of the InjectView annotation as the int parameter, and the result of this call
   * will be assigned to the field.
   *
   * @throws IllegalStateException if injection fails, common causes being that you have used an
   *                               invalid id value, or you haven't called setContentView() on your
   *                               Activity.
   */
  public void inject() {
    for (Field field : mViewHolder.getClass().getDeclaredFields()) {
      for (Annotation annotation : field.getAnnotations()) {
        Log.i(TAG, TAG + "#" + annotation.annotationType());
        if (annotation.annotationType().equals(InjectView.class)) {
          injectView(annotation, field);
        }
      }
    }

    for (Method method : mViewHolder.getClass().getDeclaredMethods()) {
      for (Annotation annotation : method.getAnnotations()) {
        Log.i(TAG, TAG + "#" + annotation.annotationType());
        if (annotation.annotationType().equals(InjectViewOnClick.class)) {
          injectViewOnClick(annotation, method);
        }
      }
    }
  }

  private void injectView(Annotation annotation, Field field) {
    try {
      Class<?> fieldType = field.getType();
      int idValue = InjectView.class.cast(annotation).value();
      field.setAccessible(true);
      Object injectedValue = fieldType.cast(mViewHolder.itemView.findViewById(idValue));
      if (injectedValue == null) {
        throw new IllegalStateException("findViewById(" + idValue
          + ") gave null for " +
          field + ", can't inject");
      }
      field.set(mViewHolder, injectedValue);
      field.setAccessible(false);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private void injectViewOnClick(Annotation annotation, final Method method) {
    //      final Class<?> fieldType = method.getType();
    int[] idValues = InjectViewOnClick.class.cast(annotation).value();
    Log.i(TAG, TAG + "idValues size=" + idValues.length);
    //取得类方法
//      Method[] methods = fieldType.getDeclaredMethods();
    for (int i = 0; i < idValues.length; i++) {
      View view = mViewHolder.itemView.findViewById(idValues[i]);
      if (view == null) {
        throw new IllegalStateException("findViewById(" + idValues[i]
          + ") gave null for " +
          method.getName() + ", can't inject");
      }
      view.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          try {
            method.setAccessible(true);
            method.invoke(mViewHolder, v);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          } finally {
            method.setAccessible(false);
          }
//          Log.i(TAG, TAG + "don=hihi");
        }
      });
    }
  }
}
