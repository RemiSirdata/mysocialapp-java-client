package io.mysocialapp.client.models

import io.mysocialapp.client.Session
import rx.Observable
import java.io.Serializable
import java.util.*

/**
 * Created by evoxmusic on 10/01/15.
 */
interface Wallable : Serializable {

    val createdDate: Date?

    val owner: User?

    val baseTarget: BaseWall?

    val baseObject: BaseWall?

    var bodyMessage: String?

    val bodyMessageTagEntities: TagEntities?

    val bodyImageURL: String?

    val bodyImageText: String?

    val firstURLTagEntity: URLTag?

    val location: BaseLocation?

    fun getBlockingLikes(): Iterable<Like> = getLikes().toBlocking()?.toIterable() ?: emptyList()

    fun getLikes(): Observable<Like>

    fun addBlockingLike(): Like? = addLike().toBlocking()?.first()

    fun addLike(): Observable<Like>

    fun deleteBlockingLike() = deleteLike().toBlocking()?.first()

    fun deleteLike(): Observable<Void>

    fun getBlockingComments(): Iterable<Comment> = getComments().toBlocking()?.toIterable() ?: emptyList()

    fun getComments(): Observable<Comment>

    fun addBlockingComment(commentPost: CommentPost): Comment? = addComment(commentPost).toBlocking()?.first()

    fun addComment(commentPost: CommentPost): Observable<Comment>

    fun blockingIgnore() = ignore().toBlocking()?.first()

    fun ignore(): Observable<Void>

    fun blockingAbuse() = abuse().toBlocking()?.first()

    fun abuse(): Observable<Void>

    fun blockingDelete() = delete().toBlocking()?.first()

    fun delete(): Observable<Void>

    var session: Session?

}
