package com.seiko.common.navigation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import com.seiko.common.R;


@Navigator.Name("keep_state_fragment")
public class KeepStateNavigator extends FragmentNavigator {
    private final Context context;
    private final FragmentManager manager;
    private final int containerId;

    @Nullable
    @Override
    public NavDestination navigate(@NonNull FragmentNavigator.Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        String tag = String.valueOf(destination.getId());
        FragmentTransaction transaction = this.manager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : R.anim.slide;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            transaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

        Fragment currentFragment = this.manager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            transaction.detach(currentFragment);
        }

        Fragment fragment = this.manager.findFragmentByTag(tag);
        if (fragment == null) {
            String className = destination.getClassName();
            fragment = this.manager.getFragmentFactory().instantiate(this.context.getClassLoader(), className);
//            fragment = this.instantiateFragment(this.context, this.manager, className, args);
            transaction.add(this.containerId, fragment, tag);
        } else {
            transaction.attach(fragment);
        }

        transaction.setPrimaryNavigationFragment(fragment);
        transaction.setReorderingAllowed(false);
        transaction.commit();
        return destination;
    }

    public KeepStateNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        this.context = context;
        this.manager = manager;
        this.containerId = containerId;
    }
}
