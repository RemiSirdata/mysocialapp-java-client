package io.mysocialapp.client.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.mysocialapp.client.extensions.PaginationResource
import io.mysocialapp.client.extensions.stream
import rx.Observable
import java.io.File
import java.util.*

/**
 * Created by evoxmusic on 31/03/15.
 */
class Event : BaseWall(), WallTextable, Localizable {

    var name: String? = null
    var description: String? = null
    val cancelled: Boolean = false
    var maxSeats: Int = 0
    val freeSeats: Int = 0 // can be used, but only when in query param or direct /event/:id limited=false
    val takenSeats: Int = 0 // can be used, but only when in query param or direct /event/:id limited=false
    val totalMembers: Int? = null
    var startDate: Date? = null
    var endDate: Date? = null
    val members: ArrayList<EventMember>? = null
    var eventMemberAccessControl: EventMemberAccessControl? = null
    var location: Location? = null
    val member: Boolean = false
    @JsonProperty("static_maps_url")
    val staticMapsURL: String? = null
    val distanceInMeters: Int? = null
    val profileCoverPhoto: Photo? = null
    val available: Boolean = true
    val remainingSecondsBeforeStart: Long = 0
    val customFields: List<CustomField>? = null

    @JsonIgnore
    var profileImageFile: File? = null
    @JsonIgnore
    var profileCoverImageFile: File? = null

    override val bodyImageURL: String?
        get() = displayedPhoto?.highURL ?: staticMapsURL

    override val bodyImageText: String?
        get() = displayedName

    override var bodyMessage: String?
        get() = displayedName
        set(value) = Unit

    override fun getLocality(): BaseLocation? {
        return location
    }

    fun blockingStreamNewsFeed(limit: Int = Int.MAX_VALUE): Iterable<Feed> = streamNewsFeed(limit).toBlocking().toIterable()

    fun streamNewsFeed(limit: Int = Int.MAX_VALUE): Observable<Feed> = listNewsFeed(0, limit)

    fun blockingListNewsFeed(page: Int = 0, size: Int = 10): Iterable<Feed> =
            listNewsFeed(page, size).toBlocking()?.toIterable() ?: emptyList()

    fun listNewsFeed(page: Int = 0, size: Int = 10): Observable<Feed> {
        return stream(page, size, object : PaginationResource<Feed> {
            override fun onNext(page: Int, size: Int): List<Feed> {
                return session?.clientService?.eventWall?.list(id, page, size)?.toBlocking()?.first() ?: emptyList()
            }
        }).map { it.session = session; it }
    }

    fun blockingSendWallPost(feedPost: FeedPost): Feed? = sendWallPost(feedPost).toBlocking().first()

    fun sendWallPost(feedPost: FeedPost): Observable<Feed> {
        if (feedPost.multipartPhoto == null) {
            return feedPost.textWallMessage?.let { session?.clientService?.eventWallMessage?.post(idStr?.toLong(), it) }?.map {
                it.session = session; it
            } ?: Observable.empty()
        }

        val obs = when {
            feedPost.multipartPhoto.message == null -> session?.clientService?.eventPhoto?.post(idStr?.toLong(), feedPost.multipartPhoto.photo,
                    feedPost.multipartPhoto.accessControl!!)
            feedPost.multipartPhoto.tagEntities == null -> session?.clientService?.eventPhoto?.post(idStr?.toLong(), feedPost.multipartPhoto.photo,
                    feedPost.multipartPhoto.message, feedPost.multipartPhoto.accessControl!!)
            else -> session?.clientService?.eventPhoto?.post(idStr?.toLong(), feedPost.multipartPhoto.photo,
                    feedPost.multipartPhoto.message, feedPost.multipartPhoto.accessControl!!, feedPost.multipartPhoto.tagEntities)
        }

        return obs?.map { it.session = session; it } ?: Observable.empty()
    }

    override fun save(): Observable<Event> {
        return session?.clientService?.event?.put(idStr?.toLong(), this)?.map { it.session = session; it } ?: Observable.empty()
    }

    fun blockingCancel() {
        cancel().toBlocking()?.first()
    }

    fun cancel(): Observable<Void> {
        return session?.clientService?.eventCancel?.post(idStr?.toLong()) ?: Observable.empty()
    }

    fun participate(): Observable<EventMember> {
        return session?.clientService?.eventMember?.post(idStr?.toLong()) ?: Observable.empty()
    }

    fun unParticipate(): Observable<EventMember> {
        return session?.clientService?.eventMember?.delete(idStr?.toLong()) ?: Observable.empty()
    }

    class Builder {
        private var mName: String? = null
        private var mDescription: String? = null
        private var mStartDate: Date? = null
        private var mEndDate: Date? = null
        private var mLocation: SimpleLocation? = null
        private var mMaxSeats: Int = 10
        private var mMemberAccessControl = EventMemberAccessControl.PUBLIC
        private var mImage: File? = null
        private var mCoverImage: File? = null

        fun setName(name: String): Builder {
            this.mName = name
            return this
        }

        fun setDescription(description: String): Builder {
            this.mDescription = description
            return this
        }

        fun setStartDate(date: Date): Builder {
            this.mStartDate = date
            return this
        }

        fun setEndDate(date: Date): Builder {
            this.mEndDate = date
            return this
        }

        fun setLocation(location: SimpleLocation): Builder {
            this.mLocation = location
            return this
        }

        fun setMaxSeats(maxSeats: Int): Builder {
            this.mMaxSeats = maxSeats
            return this
        }

        fun setMemberAccessControl(memberAccessControl: EventMemberAccessControl): Builder {
            this.mMemberAccessControl = memberAccessControl
            return this
        }

        fun setImage(image: File): Builder {
            this.mImage = image
            return this
        }

        fun setCoverImage(image: File): Builder {
            this.mCoverImage = image
            return this
        }

        fun build(): Event {
            if (mName.isNullOrBlank()) {
                IllegalArgumentException("Name cannot be null or empty")
            }

            if (mDescription.isNullOrBlank()) {
                IllegalArgumentException("Description cannot be null or empty")
            }

            if (mStartDate == null || mEndDate == null) {
                IllegalArgumentException("Start date and end date cannot be null")
            }

            if (mStartDate!! < Date()) {
                IllegalArgumentException("Start date cannot be lower than now")
            }

            if (mStartDate!! > mEndDate!!) {
                IllegalArgumentException("Start date cannot be greater than end date")
            }

            if (mLocation == null) {
                IllegalArgumentException("Meeting location cannot be null or empty")
            }

            return Event().apply {
                name = mName
                description = mDescription
                startDate = mStartDate
                endDate = mEndDate
                location = Location(mLocation)
                maxSeats = mMaxSeats
                eventMemberAccessControl = mMemberAccessControl
                profileImageFile = mImage
                profileCoverImageFile = mCoverImage
            }
        }
    }

}
