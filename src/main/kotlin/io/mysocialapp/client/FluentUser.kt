package io.mysocialapp.client

import io.mysocialapp.client.extensions.PaginationResource
import io.mysocialapp.client.extensions.stream
import io.mysocialapp.client.models.User
import io.mysocialapp.client.models.Users
import rx.Observable

/**
 * Created by evoxmusic on 05/05/2018.
 */
class FluentUser(private val session: Session) {

    fun blockingStream(limit: Int = Int.MAX_VALUE): Iterable<Users> = stream(limit).toBlocking().toIterable()

    fun stream(limit: Int = Int.MAX_VALUE): Observable<Users> = list(0, limit)

    fun blockingList(page: Int = 0, size: Int = 10): Iterable<Users> = list(page, size).toBlocking().toIterable()

    fun list(page: Int = 0, size: Int = 10): Observable<Users> {
        return stream(page, size, object : PaginationResource<Users> {
            override fun onNext(page: Int, size: Int): List<Users> {
                return listOf(session.clientService.user.list(page, size).toBlocking().first())
            }
        }).map { it.users?.forEach { u -> u.session = session }; it }
    }

    fun blockingGet(id: Long): User? = get(id).toBlocking()?.first()

    fun get(id: Long): Observable<User> = session.clientService.user.get(id).map { it.session = session; it }

}