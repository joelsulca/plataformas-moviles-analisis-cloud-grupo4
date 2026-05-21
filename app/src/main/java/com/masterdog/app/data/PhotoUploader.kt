package com.masterdog.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object PhotoUploader {

    private val client = OkHttpClient()

    suspend fun upload(fileName: String, bytes: ByteArray, mimeType: String): String {
        return try {
            val result = ApiClient.api.getPresignedUrl(fileName).data
            val requestBody = bytes.toRequestBody(mimeType.toMediaType())
            val request = Request.Builder()
                .url(result.url)
                .put(requestBody)
                .build()
            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) result.objectUrl else ""
                }
            }
        } catch (e: Exception) {
            ""
        }
    }
}
