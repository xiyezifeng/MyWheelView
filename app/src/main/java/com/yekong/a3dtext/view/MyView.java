package com.yekong.a3dtext.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yekong.a3dtext.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xigua on 2017/9/11.
 */

public class MyView extends FrameLayout implements View.OnTouchListener, View.OnClickListener {


    /**
     * 周长 = itemheight * itemcount
     */
    private int perimeter;
    /**
     * 周长 最小周长
     */
    private int minPerimeter = 360 * 6;
    /**
     * 控件的直径 = 周长/π
     */
    private int diameter;
    /**
     * 控件总高度 = 直径
     */
    private int viewHeight;
    /**
     * 半径 = 直径/2
     */
    private int radius;
    /**
     * item height
     */
    private int itemHeight;
    /**
     * item count
     */
    private int itemCount;
    /**
     * 每度的滑动距离 = 周长/360
     */
    private int setpLImit;
    /**
     * 每两个控件间距度数 =   itemHeight  /  setpLImit
     */
    private int limitItem;
    /**
     * 滑动距离
     */
    private float scroll;
    /**
     * 高度变化比 = 90°从0 - 1 的变化比
     * 也就是一个半径的高度和item的高度的比值
     */
    private float heightScale;

    /**
     * 子控件集合 (只能是Textview)
     */
    private List<TextView> childList = new ArrayList<>();

    /**
     * 子控件显示文字集合
     */
    private List<String> strList = new ArrayList<>();

    /**
     * 屏幕宽高
     */
    private int screenWidth,screenHeight;
    /**
     * 控件所在屏幕的宽高
     */
    private int widgetWidth , widgetHeight;
    /**
     * 初始化加载
     */
    private boolean isInitView = false;

    /**
     * 绘制中间两根线
     */
    private Paint linePaint;
    /**
     * 中间的view
     */
    private int top ,bottom ;
    /**
     * 控件中心Y轴
     */
    private int wedgetCenterY;

    /**
     * 子控件整体Y轴偏移量
     */
    private int transationY;
    /**
     * 中间view index
     */
    private int currentIndex = -1;

    public MyView(@NonNull Context context) {
        super(context);
        init();
    }

    public MyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setChildList(List<TextView> childList) {
        this.childList = childList;
    }

    public void setStrList(List<String> strList) {
        this.strList = strList;
        TextView view;

        for (String s : strList) {
            view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_text, this, false);
            view.setText(s);
            addView(view);
            childList.add(view);
//            view.setOnClickListener(this);
        }
        postInvalidate();
    }



    private void init() {
        setClickable(true);
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = manager.getDefaultDisplay().getWidth();
        screenHeight = manager.getDefaultDisplay().getHeight();
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.BLACK);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //控制控件宽度为屏幕款，高度为屏幕的2/5
        widgetWidth  = screenWidth;
        widgetHeight = screenHeight*2/6;
        setMeasuredDimension(widgetWidth,widgetHeight);
        mathParamt();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        drawView();
        Log.e("MyView", "onlayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("MyView", "onDraw()");
        super.onDraw(canvas);
        if (top != 0) {
            canvas.drawLine(0,top + transationY , widgetWidth , top + transationY,linePaint);
//            canvas.drawLine(0,100 , widgetWidth , 100,linePaint);
        }
        if (bottom != 0) {
            canvas.drawLine(0,bottom + transationY, widgetWidth , bottom + transationY ,linePaint);
//            canvas.drawLine(0,119, widgetWidth ,119 ,linePaint);
        }
        wedgetCenterY = (bottom - top)/2;
    }

    /**
     * 计算全部参数
     */
    private void mathParamt() {
        if (itemCount > 0) {
            itemCount = getChildCount();
            itemHeight = getChildAt(0).getMeasuredHeight();
            //周长
            /*perimeter = itemCount * itemHeight;
            if (perimeter < minPerimeter) {
                perimeter = minPerimeter;
            }*/
            perimeter = minPerimeter;
            Log.e("MyView", "周长   perimeter :  " + perimeter);
            //直径
            diameter = (int) (perimeter / Math.PI);
            //半径
            radius = diameter / 2;
            //控件高
            viewHeight = diameter;
            //滑动步长度数
            setpLImit = perimeter / 360;
            //两个控件间度数
            limitItem = itemHeight / setpLImit;
            //高度缩放比
            heightScale = (float) itemHeight / (float)radius;
            Log.e("MyView", "view heightScale  :  " + ( heightScale));
        }
        Log.e("MyView", "success math ");
    }

    private void drawView(){
        if (getChildCount() > 0) {
            Log.e("MyView", "getChildCount():" + getChildCount());
            itemCount = getChildCount();
            TextView view;
            int s_y = getChildAt(0).getTop();
            for (int i = 0; i < itemCount; i++) {
                view = (TextView) getChildAt(i);
                float scaleH = Math.abs(itemHeight * heightScale * (i - itemCount/2));
//                float scaleH = Math.abs(itemHeight * heightScale * (i));
                int top = (int) (itemHeight*i + scaleH);
                Log.e("MyView", "scaleH :" + scaleH);
                Log.e("MyView", "top  :" + top);
                view.setTranslationY(transationY);
                view.layout(0, itemHeight*i ,view.getRight(),view.getBottom()+i*itemHeight);
                if (top != 0) {
                    float scale = ( view.getHeight() - scaleH )/ itemHeight;
                    if (scale >= 0){
                        view.setScaleY(scale);
                        //规定 从中线到消失位置的X轴缩放为  1 - 0.7

//                        view.setScaleX((float) ((Math.abs(i - itemCount/2) *0.08)));
                    }
                    else{

                    }
                    Log.e("MyView", "scale  :" + scale);
                    if (scaleH == 0) {
                        s_y = view.getTop() + itemHeight / 2;
                        Log.e("MyView", "s_y  : " + s_y);
                        currentIndex = i;
                        //设置文字颜色
                        view.setTextColor(Color.RED);
                        view.setScaleX(1.2f);
                    }else{
                        view.setTextColor(Color.BLACK);
                        view.setScaleX(1f);
                    }
                }
                if (scaleH == 0) {
                    this.top = view.getTop();
                    if (this.top == 0) {
                        this.top = 1;
                    }
                    this.bottom = view.getBottom();
                }

            }
            for (int i = 0; i < itemCount; i++) {
                view = (TextView) getChildAt(i);
                transationY = (widgetHeight/2) - s_y;
                view.setTranslationY(transationY);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("MyView", "touch");
        return super.onTouchEvent(event);
    }

    /**
     * 记录手指按下的位置
     */
    private float startX,startY;
    /**
     * 记录手机当前的位置
     */
    private float currentX,currentY;
    /**
     * 记录手指最后一次的位置
     */
    private float endX,endY;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = motionEvent.getX();
                currentY = motionEvent.getY();

                if (Math.abs(currentY - startY) > 5) {
                    if (itemCount > 1) {
                        //滚动距离
                        float scroll = Math.abs(currentY - startY);
                        //可滑动
                        if (currentY > startY) {
                            //列表上滚
//                            scrollTo(0 , (int) (getScrollY() - scroll));
                            onScroll((int) (getScrollY() - scroll));
                        }else{
                            //列表下滚
//                            scrollTo(0 , (int) (getScrollY() + scroll));
                            onScroll((int) (getScrollY() + scroll));
                        }
                    }
                    //只有一条时不需要滑动
                }

                startY = currentY;
                startX = currentX;
                break;
            case MotionEvent.ACTION_UP: {
                endX = motionEvent.getX();
                endY = motionEvent.getY();
                //吸附效果
                if (currentIndex >= 0) {
                    Log.e("MyView_2", "currentIndex:" + currentIndex);
                    View item = getChildAt(currentIndex);
                    int y = item.getTop() - this.top;
//                    scrollBy(0,y);
                    onScroll(y);
                }
            }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 处理滑动时的变化
     * @param scroll
     */
    private void onScroll(int scroll){
        TextView view;
        int index = 0;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < itemCount; i++) {
            view = (TextView) getChildAt(i);
            view.layout(view.getLeft(),view.getTop() - scroll , view.getRight(), view.getBottom() - scroll);
            int top = view.getTop();
            int center = top - this.top;
//            Log.e("MyView1", "center:" + center);
            //Y轴离中心线的偏移量
            float y = Math.abs(center );
            //缩放比
            float   s = Math.abs(1 - Math.abs(y) / radius);
//            Log.e("MyView1", "y:" + y);
//            Log.e("MyView1", "s:" + s);
            if (s > 0){
                view.setScaleY(s);
//                view.setScaleX((float) ( s * i  +1));
            }

            if (y < min) {
                min = y;
                index = i;
            }
            if (y <= itemHeight / 2) {
                view.setTextColor(Color.RED);
                view.setScaleX(1.2f);
            }else{
                view.setTextColor(Color.BLACK);
                view.setScaleX(1f);
            }
        }
        currentIndex = index;
        postInvalidate();
    }

    @Override
    public void onClick(View view) {
        View item = view;
        int y = item.getTop() - this.top;
        onScroll(y);
    }
}
