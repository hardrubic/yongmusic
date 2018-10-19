package com.hardrubic.sdk.recordaudio.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import com.hardrubic.sdk.recordaudio.R;
import com.hardrubic.sdk.recordaudio.record.MP3Recorder;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AudioWaveView extends View {

    private Context mContext;
    private Bitmap mBitmap, mBackgroundBitmap;
    private Canvas mCanvas = new Canvas();
    private Canvas mBackCanVans = new Canvas();
    private Paint mPaint, mBaseLinePaint;

    private float mOffset = 0.01f;//波形之间线与线的间隔
    private int mWaveCount = 2;
    //波形的颜色
    private int mWaveColor;

    private int mWidthSpecSize;
    private int mHeightSpecSize;
    private int mBaseLine = 1;
    private int mScale = 1;

    private boolean mIsDraw = true;

    private ArrayList<Short> mRecDataList = new ArrayList<>();

    private DrawThread mDrawThread;

    final Object mLock = new Object();

    public AudioWaveView(Context mContext) {
        super(mContext);
        init(mContext, null);
    }

    public AudioWaveView(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        init(mContext, attrs);
    }

    private void init(Context mContext, AttributeSet attrs) {
        this.mContext = mContext;
        if (isInEditMode()) return;
        if (null != attrs) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.AudioWaveView);
            mWaveColor = ta.getColor(R.styleable.AudioWaveView_waveColor, Color.WHITE);
            mWaveCount = ta.getInt(R.styleable.AudioWaveView_waveCount, 4);
            ta.recycle();
        }
        if (mWaveCount < 1) {
            mWaveCount = 1;
        } else if (mWaveCount > 2) {
            mWaveCount = 2;
        }
        mPaint = new Paint();
        mPaint.setColor(mWaveColor);
        mBaseLinePaint = new Paint();
        mBaseLinePaint.setStrokeWidth(5f);
        mBaseLinePaint.setColor(Color.GRAY);
    }

    Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            AudioWaveView.this.invalidate();
        }
    };

    public void startDraw() {
        if (null != mDrawThread && mDrawThread.isAlive()) {
            mIsDraw = false;
            while (mDrawThread.isAlive()) ;
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        reset();
        mIsDraw = true;
        mDrawThread = new DrawThread();
        mDrawThread.start();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, mBaseLine, mWidthSpecSize, mBaseLine, mBaseLinePaint);
        if (null != mBitmap) {
            if (mIsDraw) {
                synchronized (mLock) {
                    canvas.drawBitmap(mBitmap, 0, 0, null);
                }
            } else {
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }
    }

    public void stopDraw() {
        reset();
        mIsDraw = false;
        if (null != mDrawThread) {
            while (mDrawThread.isAlive()) ;
        }
        //这个是清除绘制的内容
        mBackCanVans.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    class DrawThread extends Thread {
        @Override public void run() {
            while (mIsDraw) {
                ArrayList<Short> dataList;
                synchronized (mRecDataList) {
                    dataList = (ArrayList<Short>) mRecDataList.clone();
                }
                resolveToWaveData(dataList);
                if (mBackCanVans != null) {
                    mBackCanVans.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    //绘制基线
                    //mBackCanVans.drawLine(0, mBaseLine, mWidthSpecSize, mBaseLine, mBaseLinePaint);
                    int drawBufsize = dataList.size();
                    Log.i("tag", "drawBufsize:" + drawBufsize);
                    Log.i("tag", "mWidthSpecSize:" + mWidthSpecSize);
                    Log.i("tag", "mOffset:" + mOffset);
                    /*判断大小，是否改变显示的比例*/
                    float j = 0f;
                    for (int i = 0; i < drawBufsize; i++, j += mOffset) {
                        Short sh = dataList.get(i);
                        if (sh != null) {
                            short max = (short) (mBaseLine - sh / mScale);
                            short min;
                            if (mWaveCount == 2) {
                                min = (short) (sh / mScale + mBaseLine);
                                mBackCanVans.drawLine(j, max, j, min, mPaint);
                            } else {
                                min = (short) (mBaseLine);
                                mBackCanVans.drawLine(j, mBaseLine, j, max, mPaint);//波纹的上部分
                                mBackCanVans.drawLine(j, min, j, mBaseLine, mPaint);//波纹的下部分
                            }
                        }
                    }
                    synchronized (mLock) {
                        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        if(null != mBackgroundBitmap)mCanvas.drawBitmap(mBackgroundBitmap, 0, 0, mPaint);
                    }
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
                //休眠暂停资源
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更具当前块数据来判断缩放音频显示的比例
     *
     * @param list 音频数据
     */
    private void resolveToWaveData(ArrayList<Short> list) {
        short allMax = 0;
        for (int i = 0; i < list.size(); i++) {
            Short sh = list.get(i);
            if (sh != null && sh > allMax) {
                allMax = sh;
            }
        }
        int curScale = allMax / mBaseLine;
        if (curScale > mScale) {
            mScale = ((curScale == 0) ? 1 : curScale);
        }
    }

    public void release() {
        mIsDraw = false;
        mRecDataList.clear();
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        if (null != mBackgroundBitmap && !mBackgroundBitmap.isRecycled()) {
            mBackgroundBitmap.recycle();
        }
    }

    public ArrayList<Short> getRecList() {
        return mRecDataList;
    }

    public float getmOffset() {
        return mOffset;
    }

    //设置baseline在底部
    public void setBaseLineOnBottom() {
        mBaseLine = mHeightSpecSize;
        mWaveCount = 1;
    }

    public void reset() {
        if (mRecDataList.size() > 0) {
            mRecDataList.clear();
        }
    }

    public boolean isPlaying() {
        return mIsDraw;
    }

    @Override protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && null == mBackgroundBitmap) {
            ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override public boolean onPreDraw() {
                    if (getWidth() > 0 && getHeight() > 0) {
                        mWidthSpecSize = getWidth();
                        mHeightSpecSize = getHeight();
                        mBaseLine = mHeightSpecSize / 2;
                        mBackgroundBitmap =
                                Bitmap.createBitmap(mWidthSpecSize, mHeightSpecSize, Bitmap.Config.ARGB_8888);
                        mBackCanVans.setBitmap(mBackgroundBitmap);
                        mBitmap = Bitmap.createBitmap(mWidthSpecSize, mHeightSpecSize, Bitmap.Config.ARGB_8888);
                        mCanvas.setBitmap(mBitmap);
                        float temp = (float)(mWidthSpecSize * 7) / MP3Recorder.MAX_DURATION;
                        mOffset = Float.parseFloat(new DecimalFormat("0.00").format(temp));
                        getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    if(0.00f == mOffset) mOffset = 0.01f;
                    return true;
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }
}
