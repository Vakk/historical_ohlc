package com.vakk.ohlc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by vakk on 3/9/16.
 */
public class GraphDrawer extends SurfaceView implements SurfaceHolder.Callback {

    public GraphDrawer(Context context) {
        super(context);
    }

    public GraphDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

    }

    public GraphDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        drawGrid();
    }

    /**
     * Draw grid for future graph
     */
    void drawGrid() {
        Canvas canvas = null;
        try {
            // получаем объект Canvas и выполняем отрисовку
            if (canvas != null) {
                canvas = null;
            }
            try {
                canvas = getHolder().lockCanvas();
                synchronized (getHolder()) {
                    canvas.drawColor(Color.WHITE);
                    Paint paint = new Paint();
                    paint.setColor(Color.LTGRAY);
                    canvas.drawLine(0, 0, 500, 500, paint);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } finally {
            if (canvas != null)
                // отрисовка выполнена. выводим результат на экран
                getHolder().unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }
}
