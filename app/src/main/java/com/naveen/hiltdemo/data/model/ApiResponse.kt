package com.naveen.hiltdemo.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T?,
    @SerializedName("error")
    val error: String?
)

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class Post(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("image")
    val image: String?,
    @SerializedName("file")
    val file: String?,
    @SerializedName("created_at")
    val createdAt: String
)

data class CreateUserRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String?
)

data class CreatePostRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("image")
    val image: String?,
    @SerializedName("file")
    val file: String?
)

data class FileUploadResponse(
    @SerializedName("filename")
    val filename: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("type")
    val type: String
)
