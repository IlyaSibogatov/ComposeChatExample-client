package com.example.composechatexample.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class NotificationModel(
    @SerializedName("title") val title: String,
    @SerializedName("text") val text: String,
) : Parcelable

@Serializable
data class UserNotification(
    val id: String,
    var type: NotificationType,
    val senderId: String,
    val senderName: String,
)

enum class NotificationType(val value: String) {
    REQUEST_FRIENDSHIP("request_friendship"),

    ACCEPTED_FRIENDSHIP("accepted_friendship"),
    DECLINED_FRIENDSHIP("declined_friendship"),

    USER_ACCEPT_FRIENDSHIP("user_accepted_friendship"),
    USER_DECLINED_FRIENDSHIP("user_declined_friendship"),
}
