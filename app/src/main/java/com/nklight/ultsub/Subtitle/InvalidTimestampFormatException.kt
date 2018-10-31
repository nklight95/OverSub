package com.nklight.ultsub.Subtitle

class InvalidTimestampFormatException(val detail: String = "") : SRTException() {
    companion object {

        private val serialVersionUID = 1856680234321642324L
    }

}