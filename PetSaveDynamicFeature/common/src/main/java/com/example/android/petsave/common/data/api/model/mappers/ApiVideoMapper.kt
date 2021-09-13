package com.example.android.petsave.common.data.api.model.mappers

import com.example.android.petsave.common.data.api.model.ApiVideoLink
import com.example.android.petsave.common.domain.model.animal.Media
import javax.inject.Inject

class ApiVideoMapper @Inject constructor() : ApiMapper<ApiVideoLink?, Media.Video> {

    override fun mapToDomain(apiEntity: ApiVideoLink?): Media.Video {
        return Media.Video(video = apiEntity?.embed.orEmpty())
    }
}
