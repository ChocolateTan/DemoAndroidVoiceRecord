package com.don.voice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by DON on 17/03/01.
 */

/**
 *
 */
public class BarChartView extends View {
  private static final String TAG = BarChartView.class.getSimpleName();
  private int mRectSpace = 2;//px 條狀圖間隔
  private List<Float> mValueList;//條狀圖數值
  private Float mMinValue=0f;//條狀圖數值
  private Float mMaxValue;//條狀圖數值
  private int mListSize = 0;//顯示個數
  private int mColor = Color.BLUE;

  public BarChartView(Context context) {
    super(context);
    mValueList = new ArrayList<>();
  }

  public BarChartView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    Log.i(TAG, "widthMeasureSpec=" + MeasureSpec.getSize(widthMeasureSpec));
    Log.i(TAG, "heightMeasureSpec=" + MeasureSpec.getSize(heightMeasureSpec));
    // 设置自定义的控件MyViewGroup的大小
    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    //計算最大畫布寬度除了要減去左右padding，還需要減去一個間隔距離
    int maxWidth = getWidth() - getPaddingLeft() - getPaddingRight();
    int maxHeight = getHeight() - getPaddingTop() - getPaddingBottom();
    //如果設置了顯示多少條條狀圖，則按照平均size顯示（若超出這個數目，顯示會出問題），否則按照總list數目顯示
    int rectWidth = (maxWidth - (getShowListSize() - 1) * mRectSpace) / getShowListSize();
    float rectHeightP = maxHeight / getHeightDivide();

    canvas.drawColor(Color.WHITE);
    Paint paint = new Paint();
//    paint.setColor(Color.RED);
//    canvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), paint);

    Log.i(TAG, "maxH=" + maxHeight);
    Log.i(TAG, "maxW=" + maxWidth);
    Log.i(TAG, "mH=" + getMeasuredHeight());
    Log.i(TAG, "mW=" + getMeasuredWidth());
    Log.i(TAG, "H=" + getHeight());
    Log.i(TAG, "W=" + getWidth());

    paint.setColor(mColor);
    if (null != mValueList && mValueList.size() > 0) {
      for (int i = 0, size = mValueList.size(); i < size; i++) {
        canvas.drawRect(
          getPaddingLeft() + rectWidth * i + mRectSpace * i,
          getHeight() - (getPaddingTop() + (mValueList.get(i) - mMinValue) * rectHeightP),
          getPaddingLeft() + rectWidth * (i + 1) + mRectSpace * i,
          getHeight() - getPaddingBottom(),
          paint);
      }
    }
  }

  private int measureWidth(int pWidthMeasureSpec) {
    int result = 0;
    int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
    int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸
    int viewGroupWidth = widthMode - getPaddingLeft() - getPaddingRight();

    switch (widthMode) {
      /**
       * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
       * MeasureSpec.AT_MOST。
       *
       *
       * MeasureSpec.EXACTLY是精确尺寸，
       * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
       * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
       *
       *
       * MeasureSpec.AT_MOST是最大尺寸，
       * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
       * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
       * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
       *
       *
       * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
       * 通过measure方法传入的模式。
       */
      case MeasureSpec.AT_MOST:
        /* 将剩余宽度和所有子View + padding的值进行比较，取小的作为ViewGroup的宽度 */
        result = Math.min(viewGroupWidth, widthSize);
        break;
      case MeasureSpec.EXACTLY:
        result = Math.min(viewGroupWidth, widthSize);
        break;
      case MeasureSpec.UNSPECIFIED:
        result = widthSize;
        break;
    }
    return result;
  }

  private int measureHeight(int pHeightMeasureSpec) {
    int result = 0;

    int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
    int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
    int viewGroupHeight = heightSize - getPaddingLeft() - getPaddingRight();

    switch (heightMode) {
      case MeasureSpec.UNSPECIFIED:
        result = heightSize;
        break;
      case MeasureSpec.AT_MOST:
                /* 将剩余高度和所有子View + padding的值进行比较，取小的作为ViewGroup的高度 */
        result = Math.min(viewGroupHeight, heightSize);
        break;
      case MeasureSpec.EXACTLY:
        result = Math.min(viewGroupHeight, heightSize);
        break;
    }
    return result;
  }

  /**
   * 設置list 數值要大於0
   */
  public void setValueList(List<Float> valueList) {
    this.mValueList = valueList;
  }

  /**
   * 添加value 數值要大於0
   */
  public void addValue(Float value) {
    this.mValueList.add(value);
  }

  /**
   * 添加value 數值要大於0
   */
  public void addValue(int pos, Float value) {
    this.mValueList.add(pos, value);
  }

  /**
   * 設置間隔
   */
  public void setRectSpace(int space) {
    this.mRectSpace = space;
  }

  /**
   * 獲取條狀圖顯示的條數
   */
  public int getShowListSize() {
    if (mListSize == 0) return mValueList.size();
    return mListSize;
  }

  /**
   * 設置顯示的條數，如果超過該條數則顯示不正常
   * 如果不設置，則會根據mValueList size
   */
  public void setListSize(int listSize) {
    this.mListSize = listSize;
  }

  /**
   * 設置顏色
   */
  public void setColor(int color) {
    this.mColor = color;
  }

  /**
   * 設置上限，若無設置，則會根據mValue list最大值
   */
  public void setMaxValue(Float value) {
    this.mMaxValue = value;
  }

  /**
   * 返回上限值
   */
  public float getHeightDivide() {
    if (null == mMaxValue) return Collections.max(mValueList);
    return this.mMaxValue;
  }

  /**
   * 設置最低描繪
   * @param value
   */
  public void setMinValue(Float value){
    this.mMinValue = value;
  }
}
