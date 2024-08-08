package com.rajput.socialmediasaver.model

import android.net.Uri


class WhatsappStatusModel(var name: String, private var uri: Uri, var path: String) {
    fun getPath(): Any {
        return path


    }
    fun getUri(): Any {
        return uri

    }
}

