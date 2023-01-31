package com.friendzrandroid.home.data.model.events


import com.google.gson.annotations.SerializedName

data class OnlyEventsAroundMeModel(
    @SerializedName("isSuccessful")
    val isSuccessful: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("model")
    val model: Model,
    @SerializedName("statusCode")
    val statusCode: Int
) {
    data class Model(
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("pageNumber")
        val pageNumber: Int,
        @SerializedName("pageSize")
        val pageSize: Int,
        @SerializedName("totalPages")
        val totalPages: Int,
        @SerializedName("totalRecords")
        val totalRecords: Int
    ) {
        data class Data(
            @SerializedName("allday")
            val allday: Boolean,
            @SerializedName("bloked")
            val bloked: Boolean,
            @SerializedName("categorie")
            val categorie: String,
            @SerializedName("categorieimage")
            val categorieimage: String,
            @SerializedName("color")
            val color: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("eventdate")
            val eventdate: String,
            @SerializedName("eventdateto")
            val eventdateto: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("image")
            val image: String,
            @SerializedName("joined")
            val joined: Int,
            @SerializedName("key")
            val key: Int,
            @SerializedName("lang")
            val lang: String,
            @SerializedName("lat")
            val lat: String,
            @SerializedName("orderByDes")
            val orderByDes: Int,
            @SerializedName("timefrom")
            val timefrom: String,
            @SerializedName("timeto")
            val timeto: String,
            @SerializedName("title")
            val title: String,
            @SerializedName("totalnumbert")
            val totalnumbert: Int
        )
    }
}