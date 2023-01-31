package com.friendzrandroid.home.domain.model

class PagingListRequest(
    val pageSize: Int,
    val PageNumber: Int,
    var userLat: Double = 0.0,
    var userLang: Double = 0.0,
    var degree: Int = 0,
    var sortByInterestMatch: Boolean = false,
    var requestType: Int = 0,
    var search: String? = null,
    var filterSelectedTags: String = "",

)