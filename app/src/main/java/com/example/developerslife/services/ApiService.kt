package com.example.developerslife.services

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiService {

    private var client = OkHttpClient()

    fun getCategoryRequest(category: String, page: Int): Call {
        val url = "https://developerslife.ru/$category/$page?json=true";
        val request = Request.Builder()
            .url(url)
            .build()

        val task: Call = client.newCall(request);
        return task;
    }

}