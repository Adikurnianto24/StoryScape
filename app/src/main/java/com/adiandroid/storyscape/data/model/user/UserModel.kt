package com.adiandroid.storyscape.data.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel (
    var userId: String? = null,
    var name: String? = null,
    var token: String? = null
) : Parcelable