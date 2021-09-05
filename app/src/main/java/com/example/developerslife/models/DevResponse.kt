package com.example.developerslife.models
import kotlinx.serialization.Serializable

@Serializable
class DevResponse {
    var result: List<DevResponseInfo>? = null
    var totalCount = 0
}