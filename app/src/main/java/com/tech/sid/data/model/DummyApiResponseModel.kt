package com.tech.sid.data.model

class DummyApiResponseModel : ArrayList<DummyApiItem>()

data class DummyApiItem(
    val id: Int,
    val imdbId: String,
    val posterURL: String,
    val title: String
)