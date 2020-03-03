package com.seiko.tv.util.helper

import android.app.SharedElementCallback
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi

/**
 * Created by UserToken on 18.04.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class LeakFreeSupportSharedElementCallback : SharedElementCallback() {

    private var mTempMatrix: Matrix? = null

    override fun onCaptureSharedElementSnapshot(sharedElement: View, viewToGlobalMatrix: Matrix,
                                                screenBounds: RectF): Parcelable {
        if (sharedElement is ImageView) {
            val d = sharedElement.drawable
            val bg = sharedElement.background
            if (d != null && (bg == null || bg.alpha == 0)) {
                val bitmap = TransitionUtils.createDrawableBitmap(d)
                if (bitmap != null) {
                    val bundle = Bundle()
                    bundle.putParcelable(BUNDLE_SNAPSHOT_BITMAP, bitmap)
                    bundle.putString(BUNDLE_SNAPSHOT_IMAGE_SCALETYPE,
                        sharedElement.scaleType.toString())
                    if (sharedElement.scaleType == ImageView.ScaleType.MATRIX) {
                        val matrix = sharedElement.imageMatrix
                        val values = FloatArray(9)
                        matrix.getValues(values)
                        bundle.putFloatArray(BUNDLE_SNAPSHOT_IMAGE_MATRIX, values)
                    }

                    bundle.putString(BUNDLE_SNAPSHOT_TYPE, BUNDLE_SNAPSHOT_TYPE_IMAGE_VIEW)

                    return bundle
                }
            }
        }
        if (mTempMatrix == null) {
            mTempMatrix = Matrix(viewToGlobalMatrix)
        } else {
            mTempMatrix?.set(viewToGlobalMatrix)
        }

        val bundle = Bundle()
        val bitmap = TransitionUtils.createViewBitmap(sharedElement, mTempMatrix as Matrix, screenBounds)
        bundle.putParcelable(BUNDLE_SNAPSHOT_BITMAP, bitmap)

        return bundle
    }

    override fun onCreateSnapshotView(context: Context, snapshot: Parcelable): View? {
        var view: View? = null
        if (snapshot is Bundle) {
            var bitmap = snapshot.getParcelable<Bitmap>(BUNDLE_SNAPSHOT_BITMAP)

            if (bitmap == null) {
                snapshot.clear()
                return null
            }

            // Curiously, this is required to have the bitmap be GCed almost immediately after transition ends
            // otherwise, garbage-collectable mem will still build up quickly
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)

            if (bitmap == null) {
                return null
            }

            if (BUNDLE_SNAPSHOT_TYPE_IMAGE_VIEW == snapshot.getString(BUNDLE_SNAPSHOT_TYPE)) {
                val imageView = ImageView(context)
                view = imageView
                imageView.setImageBitmap(bitmap)
                imageView.scaleType = ImageView.ScaleType.valueOf(snapshot.getString(BUNDLE_SNAPSHOT_IMAGE_SCALETYPE)!!)
                if (imageView.scaleType == ImageView.ScaleType.MATRIX) {
                    val values = snapshot.getFloatArray(BUNDLE_SNAPSHOT_IMAGE_MATRIX)
                    val matrix = Matrix()
                    matrix.setValues(values)
                    imageView.imageMatrix = matrix
                }
            } else {
                view = View(context)
                val resources = context.resources
                view.background = BitmapDrawable(resources, bitmap)
            }
            snapshot.clear()
        }

        return view
    }

    companion object {

        internal const val BUNDLE_SNAPSHOT_BITMAP = "BUNDLE_SNAPSHOT_BITMAP"
        internal const val BUNDLE_SNAPSHOT_IMAGE_SCALETYPE = "BUNDLE_SNAPSHOT_IMAGE_SCALETYPE"
        internal const val BUNDLE_SNAPSHOT_IMAGE_MATRIX = "BUNDLE_SNAPSHOT_IMAGE_MATRIX"

        internal const val BUNDLE_SNAPSHOT_TYPE = "BUNDLE_SNAPSHOT_TYPE"
        internal const val BUNDLE_SNAPSHOT_TYPE_IMAGE_VIEW = "BUNDLE_SNAPSHOT_TYPE"
    }
}