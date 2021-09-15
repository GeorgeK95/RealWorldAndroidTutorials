package com.example.android.petsave.core.data.api.model.mappers

import com.example.android.petsave.core.data.api.model.ApiVideoLink
import com.example.android.petsave.core.domain.model.animal.Media
import javax.inject.Inject

class ApiVideoMapper @Inject constructor() : ApiMapper<ApiVideoLink?, Media.Video> {

    override fun mapToDomain(apiEntity: ApiVideoLink?): Media.Video {
        return Media.Video(video = apiEntity?.embed.orEmpty())
    }
}
