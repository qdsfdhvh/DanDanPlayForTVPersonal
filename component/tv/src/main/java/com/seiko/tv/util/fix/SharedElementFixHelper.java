package com.seiko.tv.util.fix;

import android.app.Activity;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class SharedElementFixHelper {
    public static void removeActivityFromTransitionManager(Activity activity) {
//        if (Build.VERSION.SDK_INT < 21) {
//            return;
//        }
        Class transitionManagerClass = TransitionManager.class;
        try {
            Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            //noinspection unchecked
            ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> runningTransitions
                    = (ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>)
                    runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions.get() == null || runningTransitions.get().get() == null) {
                return;
            }
            ArrayMap map = runningTransitions.get().get();
            View decorView = activity.getWindow().getDecorView();
            if (map.containsKey(decorView)) {
                map.remove(decorView);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
