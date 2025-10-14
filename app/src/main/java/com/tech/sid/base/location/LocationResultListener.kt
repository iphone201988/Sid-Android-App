package com.tech.sid.base.location

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}