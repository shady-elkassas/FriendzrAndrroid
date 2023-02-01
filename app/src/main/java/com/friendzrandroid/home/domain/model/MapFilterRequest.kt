package com.friendzrandroid.home.domain.model

import com.google.android.gms.maps.model.LatLng


data class MapFilterRequest(
    val latLang: LatLng,
    val filterSelectedTags: String,
    val dateCriteria: String?,
    val startDate: String?,
    val endDate: String?,
)
