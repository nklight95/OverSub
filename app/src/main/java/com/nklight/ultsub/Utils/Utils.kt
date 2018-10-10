package com.nklight.ultsub.Utils

class Utils {
    fun joinToString(strings: MutableList<String>): String {
        return strings.joinToString { "\n" }
    }
}