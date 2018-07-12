package com.notification.indication.require;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author @ Mohit Soni on 26-04-2018 16:26.
 */

public class Indicator extends View {

    Paint paint;
    int width = 0, height = 0;
    Context context;
    /*TypedArray typedArray;*/
    int[] index = new int[]{0XFFFFFF00, 0XFFFD0000, 0XFF00FF00, 0XFF00FFFF, 0XFF0000FF};
    int color = index[0];
    boolean start = true;

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        /*typedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomView);

        try {
            width = typedArray.getInt(R.styleable.CustomView_widths,0);
            height = typedArray.getInt(R.styleable.CustomView_heights,0);

        }finally {
            typedArray.recycle();
        }*/
    }

    public void setDimen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setColor(int x) {
        color = index[x];
        invalidate();
    }

    public void start() {
        start = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (start) {
                    setColor(i);
                    i++;
                    if (i == index.length) i = 0;
                    try {
                        new Thread().sleep(1700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop(){
        start = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(new Rect(0, 0, width, height), paint);
    }
}
