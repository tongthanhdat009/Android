package com.project.myapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.squareup.picasso.Transformation;

public class RoundedTransformation implements Transformation {
    private final int radius; // Bán kính bo tròn
    private final int margin; // Lề (nếu có)

    public RoundedTransformation(int radius, int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        // Tạo một bitmap mới để vẽ hình tròn
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);

        // Vẽ hình tròn
        canvas.drawRoundRect(new RectF(0 + margin, 0 + margin, width - margin, height - margin), radius, radius, paint);

        // Giải phóng bitmap gốc
        if (source != output) {
            source.recycle();
        }
        return output;
    }

    @Override
    public String key() {
        return "rounded(radius=" + radius + ", margin=" + margin + ")";
    }
}

