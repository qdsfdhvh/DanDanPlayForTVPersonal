package com.seiko.player;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.PlaybackControlsRow;

import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;

public class PlaybackControlsGlue<T extends LeanbackPlayerAdapter> extends PlaybackTransportControlGlue<LeanbackPlayerAdapter> {

    private PlaybackControlsRow.RepeatAction mRepeatAction;

    public PlaybackControlsGlue(Activity context, T adapter) {
        super(context, adapter);
        mRepeatAction = new PlaybackControlsRow.RepeatAction(context);
        setControlsOverlayAutoHideEnabled(true);
    }

    @Override
    protected void onCreatePrimaryActions(ArrayObjectAdapter adapter) {
        super.onCreatePrimaryActions(adapter);
        adapter.add(mRepeatAction);
    }

    @Override
    public void onActionClicked(Action action) {
        if (action == mRepeatAction) {
            repeatAction((PlaybackControlsRow.RepeatAction) action);
            return;
        }
        super.onActionClicked(action);
    }

    private void repeatAction(PlaybackControlsRow.RepeatAction action) {
        Toast.makeText(getContext(), action.toString(), Toast.LENGTH_SHORT).show();
        action.nextIndex();
        ArrayObjectAdapter primaryActionsAdapter = (ArrayObjectAdapter) getControlsRow().getPrimaryActionsAdapter();
        int repeatIndex = primaryActionsAdapter.indexOf(action);
        primaryActionsAdapter.notifyArrayItemRangeChanged(repeatIndex, 1);

    }

    private Handler mHandler = new Handler();

    @Override
    protected void onPlayCompleted() {
        super.onPlayCompleted();
        mHandler.post(() -> {
            if (mRepeatAction.getIndex() != PlaybackControlsRow.RepeatAction.NONE) {
                play();
                PlaybackControlsGlue.this.getHost().hideControlsOverlay(true);
            }
        });
    }
}