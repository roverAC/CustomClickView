package test.zhuangbi.com.customclickview_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 项目名称:zhuangbi
 * 创建人:hpf
 * 创建时间:2017/7/27 11:00
 */

public class CustomClickView extends View{

    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Paint mHourPaint;
    private int mViewHeigth;
    private int mViewWidth;
    private Time time;
    private Handler handler;

    public CustomClickView(Context context) {
        this(context,null);
    }

    public CustomClickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomClickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        //因为是wrap，但是在onMeasure的时候，只对match具体的值有效，我们还需要重写onMeasure方法，确定view的大小
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        handler = new Handler();
        //初始化Calendar
        time = new Time();
        //用来绘制表盘
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.BLACK);
        mCirclePaint.setAntiAlias(true); //设置抗锯齿
        mCirclePaint.setStyle(Paint.Style.STROKE);//空心
        mCirclePaint.setStrokeWidth(5);  //设置空心宽度

        //绘制文字
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true); //设置抗锯齿
        mTextPaint.setStyle(Paint.Style.STROKE);//空心

        //用来绘制指针（指定不同的宽度）
        mHourPaint = new Paint();
        mHourPaint.setColor(Color.BLACK);
        mHourPaint.setAntiAlias(true); //设置抗锯齿
        mHourPaint.setStyle(Paint.Style.STROKE);//空心
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //保存宽高(通过模型来确定)
        setMeasuredDimension(measureView(widthMeasureSpec),measureView(heightMeasureSpec));
    }

    /**
     * 获取view的宽和高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getMeasuredWidth();
        mViewHeigth = getMeasuredHeight();
    }

    /**
     * 在画布上进行绘制clock(绘制各个分割线以及text)
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mViewWidth/2,mViewHeigth/2,mViewWidth/2-10,mCirclePaint);//在view中心绘制表盘
        mCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mViewWidth/2,mViewHeigth/2,10,mCirclePaint);//在view中心绘制表盘
        canvas.save();//绘制表盘中心的线(利用canvase的旋转进行绘制)
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setTextAlign(Paint.Align.CENTER);  //以文字的底部中心为起始点
        for(int i =0;i < 12;i++) {
            if(i%3==0) {
                mCirclePaint.setStrokeWidth(5);
                mTextPaint.setTextSize(15);
                //设置长度为60
                canvas.drawLine(mViewWidth/2,mViewHeigth/2-(mViewWidth/2-10),mViewWidth/2,mViewHeigth/2-(mViewWidth/2-10)+40,mCirclePaint);
                //绘制时间text
                canvas.drawText(i+"",mViewWidth/2,mViewHeigth/2-(mViewWidth/2-10)+50+textHight(i+""),mTextPaint);
            }else {
                mCirclePaint.setStrokeWidth(3);
                mTextPaint.setTextSize(12);
                canvas.drawLine(mViewWidth/2,mViewHeigth/2-(mViewWidth/2-10),mViewWidth/2,mViewHeigth/2-(mViewWidth/2-10)+30,mCirclePaint);
                canvas.drawText(i+"",mViewWidth/2,mViewHeigth/2-(mViewWidth/2-10)+40+textHight(i+""),mTextPaint);
            }
            //每次进行旋转(需要指定绘制中心，未指定的时候就会平移到0，0位置)
            canvas.rotate(30,mViewWidth/2,mViewHeigth/2);
        }
        canvas.restore();//将这次的与以前的图层进行合并

        //进行时针,分钟和秒针
        drawHour_Minute_Second(canvas);
    }

    private void drawHour_Minute_Second(Canvas canvas) {
        time.setToNow();
        int hour = time.hour;
        int minute = time.minute;
        int second = time.second;
        float degree_hour = hour*1f/12*360;
        float degree_minute = minute*1f/60*360;
        float degree_second = second*1f/60*360;
        Log.e("==degree==","hour=="+hour+"...minute=="+minute+"...second=="+second);
        Log.e("==degree==","degree_hour=="+degree_hour+"...degree_minute=="+degree_minute+"...degree_second=="+degree_second);
        //绘制时针
        canvas.save();
        mHourPaint.setStrokeWidth(8);
        mHourPaint.setColor(Color.BLACK);
        canvas.rotate(degree_hour,mViewWidth/2,mViewHeigth/2);
        canvas.drawLine(mViewWidth/2,mViewHeigth/2-100,mViewWidth/2,mViewHeigth/2+30,mHourPaint);
        canvas.restore();
        //绘制分针
        canvas.save();
        canvas.rotate(degree_minute,mViewWidth/2,mViewHeigth/2);
        mHourPaint.setStrokeWidth(5);
        mHourPaint.setColor(Color.BLACK);
        canvas.drawLine(mViewWidth/2,mViewHeigth/2-140,mViewWidth/2,mViewHeigth/2+60,mHourPaint);
        canvas.restore();
        //绘制秒针
        canvas.save();
        canvas.rotate(degree_second,mViewWidth/2,mViewHeigth/2);
        mHourPaint.setStrokeWidth(3);
        mHourPaint.setColor(Color.RED);
        canvas.drawLine(mViewWidth/2,mViewHeigth/2-200,mViewWidth/2,mViewHeigth/2+70,mHourPaint);
        canvas.restore();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        },1000);
    }

    /**
     * 转动
     * @param s
     * @return
     */



    //获得绘制文字的高度
    private int textHight(String s) {
        Rect bounds = new Rect();
        mCirclePaint.getTextBounds(s,0,1,bounds);
        return bounds.height();
    }

    /**
     * 绘制View的宽和高
     * @param measureSpec
     * @return
     */
    private int measureView(int measureSpec) {
        int result = 600;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode){
            case MeasureSpec.EXACTLY:
                result=size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result,size);
                break;
            default:
                break;
        }
        return result;
    }
}
