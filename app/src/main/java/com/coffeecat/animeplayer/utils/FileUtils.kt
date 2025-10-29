package com.coffeecat.animeplayer.utils

import android.content.Context
import android.net.Uri

fun saveFolderUri(context: Context, uri: Uri) {
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .putString("saved_folder_uri", uri.toString())
        .apply()
}

fun getSavedFolderUri(context: Context): Uri? {
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    val uriString = prefs.getString("saved_folder_uri", null)
    return uriString?.let { Uri.parse(it) }
}