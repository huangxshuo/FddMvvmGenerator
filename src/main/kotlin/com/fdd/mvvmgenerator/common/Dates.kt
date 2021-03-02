package com.fdd.mvvmgenerator.common

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDate() = Date(this)

fun Date.format(format: String) = SimpleDateFormat(format).format(this)