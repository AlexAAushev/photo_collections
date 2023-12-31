package com.practicum.practice_work_1_1.exhibit

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practicum.practice_work_1_1.FeedPhoto
import com.practicum.practice_work_1_1.data.repositories.Repository

class PhotoPagingSource :
    PagingSource<Int, FeedPhoto>() {
    private val firstPage: Int = 1
    private val repository = Repository()

    override fun getRefreshKey(state: PagingState<Int, FeedPhoto>): Int =
        firstPage

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedPhoto> {
        val page = params.key ?: firstPage
        return kotlin.runCatching {
            repository.getPhoto(page)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.map {
                        FeedPhoto(
                            id = it.id,
                            urlsSmall = it.urls.small,
                            likes = it.likes!!,
                            likedByUser = it.liked_by_user!!,
                            nameAuthor = it.user.name,
                            usernameAuthor = it.user.username,
                            userAvatar = it.user.profile_image.small
                        )
                    },
                    prevKey = null,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }
}