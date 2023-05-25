package com.covid19.data.model

data class CentersResponse(
    val currentCount: Int,
    val data: List<Centers>,
    val matchCount: Int,
    val page: Int,
    val perPage: Int,
    val totalCount: Int
)