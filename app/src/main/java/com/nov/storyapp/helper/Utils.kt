package com.nov.storyapp.helper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DATE_FORMAT = "ddMMyyyy_HHmmss"
val date: String = SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date())
const val MAX_SIZE = 1000000


fun String.toDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val dateFormatted = inputFormat.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL, Locale("in", "ID")).format(dateFormatted) // Set Hari Indonesia
}