package com.ammar.ammarstoryapp2.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem> = emptyList(),

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
@Parcelize
@Entity(tableName = "story")
data class ListStoryItem(

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,


    @field:SerializedName("photoUrl")
    val photoUrl: String,


    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("createdAt")
    val createdAt: String,


    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("lat")
    val lat: Double
) : Parcelable
