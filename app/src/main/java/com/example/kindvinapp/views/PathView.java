package com.example.kindvinapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public class PathView extends View {

    private Paint pathPaint;
    private Path drawPath;
    private List<PointF> coordinates;
    private float cellRadius;

    public PathView(Context context) {
        super(context);
        init();
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pathPaint = new Paint();
        pathPaint.setColor(Color.WHITE);
        pathPaint.setAlpha(150);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(10f);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setAntiAlias(true);

        drawPath = new Path();
        cellRadius = getResources().getDimensionPixelSize(com.example.kindvinapp.R.dimen.cell_diameter) / 2f;
    }

    public void setPath(List<PointF> pathCoordinates) {
        this.coordinates = pathCoordinates;
        if (coordinates != null && !coordinates.isEmpty()) {
            drawPath.reset();
            PointF startPoint = coordinates.get(0);
            drawPath.moveTo(startPoint.x + cellRadius, startPoint.y + cellRadius);
            for (int i = 1; i < coordinates.size(); i++) {
                PointF nextPoint = coordinates.get(i);
                drawPath.lineTo(nextPoint.x + cellRadius, nextPoint.y + cellRadius);
            }
        }
        invalidate(); // Перерисовать View
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawPath != null) {
            canvas.drawPath(drawPath, pathPaint);
        }
    }
}

