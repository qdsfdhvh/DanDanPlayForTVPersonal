package com.seiko.tv.util.fix;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;


/**
 * author: lsh
 * Date: 2017-03-08
 * Time: 20:20
 */

public class LeakFreeSupportSharedElementCallback extends SharedElementCallback {

    private static final String BUNDLE_SNAPSHOT_BITMAP = "BUNDLE_SNAPSHOT_BITMAP";
    private static final String BUNDLE_SNAPSHOT_IMAGE_SCALETYPE = "BUNDLE_SNAPSHOT_IMAGE_SCALETYPE";
    private static final String BUNDLE_SNAPSHOT_IMAGE_MATRIX = "BUNDLE_SNAPSHOT_IMAGE_MATRIX";

    private static final String BUNDLE_SNAPSHOT_TYPE = "BUNDLE_SNAPSHOT_TYPE";
    private static final String BUNDLE_SNAPSHOT_TYPE_IMAGE_VIEW = "BUNDLE_SNAPSHOT_TYPE";

    private Matrix mTempMatrix;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix,
                                                     RectF screenBounds) {
        if (sharedElement instanceof ImageView) {
            ImageView imageView = ((ImageView) sharedElement);
            Drawable d = imageView.getDrawable();
            Drawable bg = imageView.getBackground();
            if (d != null && (bg == null || bg.getAlpha() == 0)) {
                Bitmap bitmap = TransitionUtils.createDrawableBitmap(d);
                if (bitmap != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(BUNDLE_SNAPSHOT_BITMAP, bitmap);
                    bundle.putString(BUNDLE_SNAPSHOT_IMAGE_SCALETYPE,
                            imageView.getScaleType().toString());
                    if (imageView.getScaleType() == ImageView.ScaleType.MATRIX) {
                        Matrix matrix = imageView.getImageMatrix();
                        float[] values = new float[9];
                        matrix.getValues(values);
                        bundle.putFloatArray(BUNDLE_SNAPSHOT_IMAGE_MATRIX, values);
                    }

                    bundle.putString(BUNDLE_SNAPSHOT_TYPE, BUNDLE_SNAPSHOT_TYPE_IMAGE_VIEW);

                    return bundle;
                }
            }
        }
        if (mTempMatrix == null) {
            mTempMatrix = new Matrix(viewToGlobalMatrix);
        } else {
            mTempMatrix.set(viewToGlobalMatrix);
        }

        Bundle bundle = new Bundle();
        Bitmap bitmap = TransitionUtils.createViewBitmap(sharedElement, mTempMatrix, screenBounds);
        bundle.putParcelable(BUNDLE_SNAPSHOT_BITMAP, bitmap);

        return bundle;
    }

    @Override
    public View onCreateSnapshotView(Context context, Parcelable snapshot) {
        View view = null;
        if (snapshot instanceof Bundle) {
            Bundle bundle = (Bundle) snapshot;
            Bitmap bitmap = bundle.getParcelable(BUNDLE_SNAPSHOT_BITMAP);

            if (bitmap == null) {
                bundle.clear();
                return null;
            }

            // Curiously, this is required to have the bitmap be GCed almost immediately after transition ends
            // otherwise, garbage-collectable mem will still build up quickly
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);

            if (bitmap == null) {
                return null;
            }

            if (BUNDLE_SNAPSHOT_TYPE_IMAGE_VIEW.equals(((Bundle)snapshot).getString(BUNDLE_SNAPSHOT_TYPE))) {
                ImageView imageView = new ImageView(context);
                view = imageView;
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(
                        ImageView.ScaleType.valueOf(bundle.getString(BUNDLE_SNAPSHOT_IMAGE_SCALETYPE)));
                if (imageView.getScaleType() == ImageView.ScaleType.MATRIX) {
                    float[] values = bundle.getFloatArray(BUNDLE_SNAPSHOT_IMAGE_MATRIX);
                    Matrix matrix = new Matrix();
                    matrix.setValues(values);
                    imageView.setImageMatrix(matrix);
                }
            } else {
                view = new View(context);
                Resources resources = context.getResources();
                view.setBackground(new BitmapDrawable(resources, bitmap));
            }
            bundle.clear();
        }

        return view;
    }
}
