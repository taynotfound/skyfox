package de.taymaerz.skyfox.common.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


fun Context.hasPermission(permission: Permission): Boolean =
    ContextCompat.checkSelfPermission(this, permission.permissionId) == PackageManager.PERMISSION_GRANTED