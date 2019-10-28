package com.seiko.common.permission

import android.content.Context
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * A simple [Fragment] subclass.
 */
class PermissionManager : BasePermissionManager() {

    private var callbackMap = mutableMapOf<Int, PermissionResult.() -> Unit>()

    override fun onPermissionResult(permissionResult: PermissionResult) {
        callbackMap[permissionResult.requestCode]?.let {
            permissionResult.it()
        }
        callbackMap.remove(permissionResult.requestCode)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackMap.clear()
    }

    companion object {

        private const val TAG = "PermissionManager"

        /**
         * A static factory inline method to request permission for activity.
         *
         * @param activity an instance of [AppCompatActivity]
         * @param permissions vararg of all permissions for request
         * @param requestBlock [PermissionRequest] block for permission request
         *
         */
        @JvmStatic
        @MainThread
        inline fun requestPermissions(
            activity: AppCompatActivity,
            vararg permissions: String,
            requestBlock: PermissionRequest.() -> Unit
        ) {
            val permissionRequest = PermissionRequest().apply(requestBlock)
            requireNotNull(permissionRequest.requestCode) {
                "No request code specified."
            }
            requireNotNull(permissionRequest.resultCallback) {
                "No result callback found."
            }
            requestPermissions(
                activity.supportFragmentManager,
                permissionRequest.requestCode!!,
                permissionRequest.resultCallback!!,
                *permissions
            )
        }

        /**
         * A static factory inline method to request permission for fragment.
         *
         * @param fragment an instance of [Fragment]
         * @param permissions vararg of all permissions for request
         * @param requestBlock [PermissionRequest] block for permission request
         *
         */
        @JvmStatic
        @MainThread
        inline fun requestPermissions(
            fragment: Fragment,
            vararg permissions: String,
            requestBlock: PermissionRequest.() -> Unit
        ) {
            val permissionRequest = PermissionRequest().apply(requestBlock)
            requireNotNull(permissionRequest.requestCode) {
                "No request code specified."
            }
            requireNotNull(permissionRequest.resultCallback) {
                "No result callback found."
            }
            requestPermissions(
                fragment.childFragmentManager,
                permissionRequest.requestCode!!,
                permissionRequest.resultCallback!!,
                *permissions
            )
        }

        fun requestPermissions(
            fragmentManager: FragmentManager,
            requestId: Int,
            callback: PermissionResult.() -> Unit,
            vararg permissions: String
        ) {
            if (fragmentManager.findFragmentByTag(TAG) != null) {
                (fragmentManager.findFragmentByTag(TAG) as PermissionManager)
                    .also { it.callbackMap[requestId] = callback }
                    .requestPermissions(requestId, *permissions)
            } else {
                val permissionManager = PermissionManager()
                fragmentManager.beginTransaction().add(permissionManager, TAG).commitNow()
                permissionManager.callbackMap[requestId] = callback
                permissionManager.requestPermissions(requestId, *permissions)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callbackMap.clear()
    }
}