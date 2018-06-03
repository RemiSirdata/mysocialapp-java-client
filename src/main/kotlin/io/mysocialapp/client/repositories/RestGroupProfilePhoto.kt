package io.mysocialapp.client.repositories

import io.mysocialapp.client.models.Photo
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by evoxmusic on 28/02/17.
 */
interface RestGroupProfilePhoto {

    @GET("group/{id}/profile/photo")
    operator fun get(@Path("id") id: Long?): Observable<Photo>

    @Multipart
    @POST("group/{id}/profile/photo")
    fun post(@Path("id") id: Long?, @Part("file\"; filename=\"image\"") photo: RequestBody): Observable<Photo>

}
