package com.example.time_to_code.face_detection;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.time_to_code.camerax.GraphicOverlay;
import com.google.ar.sceneform.math.Vector3;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.Objects;

public class FaceContourGraphic extends GraphicOverlay.Graphic {


    private final Face face;
    private final Rect imageRect;

    private final Paint facePositionPaint;
    private final Paint idPaint;
    private final Paint boxPaint;

    public FaceContourGraphic(GraphicOverlay overlay, Face face, Rect imageRect) {
        super(overlay);
        this.face = face;
        this.imageRect = imageRect;

        int selectedColor = Color.WHITE;

        facePositionPaint = new Paint();
        facePositionPaint.setColor(selectedColor);

        idPaint = new Paint();
        idPaint.setColor(selectedColor);

        boxPaint = new Paint();
        boxPaint.setColor(selectedColor);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

    }

    @Override
    public void draw(Canvas canvas) throws Exception {
        RectF rect = calculateRect(
                imageRect.height(),
                imageRect.width(),
                face.getBoundingBox()
        );
        canvas.drawRect(rect, boxPaint);
    }

    private static final float BOX_STROKE_WIDTH = 5.0f;






}
