package io.mysocialapp.client.models

import rx.Observable

/**
 * Created by evoxmusic on 08/01/15.
 */
interface Likable : BaseImpl {

    fun getBlockingLikes(): Iterable<Like>

    fun getLikes(): Observable<Like>

    fun setLikersTotal(total: Int?)

    var likersTotal: Int

    var isLiked: Boolean

    fun addBlockingLike(): Like?

    fun addLike(): Observable<Like>

    fun deleteBlockingLike()

    fun deleteLike(): Observable<Void>

}
