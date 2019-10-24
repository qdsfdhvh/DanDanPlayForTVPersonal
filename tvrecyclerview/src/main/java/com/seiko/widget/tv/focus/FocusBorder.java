package com.seiko.widget.tv.focus;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface FocusBorder {

    void setVisible(boolean visible);

    void setVisible(boolean visible, boolean anim);

    boolean isVisible();

    View getView();

    void onFocus(@NonNull View focusView, @Nullable Options options);

    void boundGlobalFocusListener(@NonNull OnFocusCallback callback);

    void unBoundGlobalFocusListener();

    interface OnFocusCallback {
        @Nullable Options onFocus(View oldFocus, View newFocus);
    }

    abstract class Options {

    }

}
