package com.example.developerslife.models

import kotlinx.serialization.Serializable

@Serializable
class DevResponseInfo {
    var id = 0
    var description: String? = null
    var votes = 0
    var author: String? = null
    var date: String? = null
    var gifURL: String? = null
    var gifSize = 0
    var previewURL: String? = null
    var videoURL: String? = null
    var videoPath: String? = null
    var videoSize = 0
    var type: String? = null
    var width: String? = null
    var height: String? = null
    var commentsCount = 0
    var fileSize = 0
    var canVote = false
}