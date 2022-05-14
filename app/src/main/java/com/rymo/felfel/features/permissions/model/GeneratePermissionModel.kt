package com.rymo.felfel.features.permissions.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeneratePermissionModel(
    val permissionName: String,
    val permissionDescription: String,
    val permissionImage: Int,
):Parcelable
