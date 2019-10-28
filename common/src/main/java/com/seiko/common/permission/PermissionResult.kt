package com.seiko.common.permission

sealed class PermissionResult(val requestCode: Int) {
    class PermissionGranted(requestCode: Int) : PermissionResult(requestCode)

    class PermissionDenied(requestCode: Int,
                           val deniedPermissions: List<String>
    ) : PermissionResult(requestCode)

    class ShowRational(requestCode: Int) : PermissionResult(requestCode)

    class PermissionDeniedPermanently(
        requestCode: Int,
        val permanentlyDeniedPermissions: List<String>
    ) : PermissionResult(requestCode)
}