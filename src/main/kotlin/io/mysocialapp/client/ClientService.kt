package io.mysocialapp.client

import io.mysocialapp.client.repositories.*
import io.mysocialapp.client.utils.MyObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by evoxmusic on 27/04/2018.
 */
class ClientService(private val configuration: Configuration,
                    private val clientConfiguration: ClientConfiguration,
                    private val session: Session? = null) {

    val login: RestLogin by lazy { retrofit.create(RestLogin::class.java) }
    val logout: RestLogout by lazy { retrofit.create(RestLogout::class.java) }
    val reset: RestReset by lazy { retrofit.create(RestReset::class.java) }
    val register: RestRegister by lazy { retrofit.create(RestRegister::class.java) }
    val account: RestAccount by lazy { retrofit.create(RestAccount::class.java) }
    val accountDelete: RestAccountDelete by lazy { retrofit.create(RestAccountDelete::class.java) }
    val accountProfilePhoto: RestAccountProfilePhoto by lazy { retrofit.create(RestAccountProfilePhoto::class.java) }
    val accountProfileCoverPhoto: RestAccountProfileCoverPhoto by lazy { retrofit.create(RestAccountProfileCoverPhoto::class.java) }
    val feed: RestFeed by lazy { retrofit.create(RestFeed::class.java) }
    val feedIgnore: RestFeedIgnore by lazy { retrofit.create(RestFeedIgnore::class.java) }
    val feedAbuse: RestFeedAbuse by lazy { retrofit.create(RestFeedAbuse::class.java) }
    val feedComment: RestFeedComment by lazy { retrofit.create(RestFeedComment::class.java) }
    val feedLike: RestFeedLike by lazy { retrofit.create(RestFeedLike::class.java) }
    val photo: RestPhoto by lazy { retrofit.create(RestPhoto::class.java) }
    val photoComment: RestPhotoComment by lazy { retrofit.create(RestPhotoComment::class.java) }
    val photoLike: RestPhotoLike by lazy { retrofit.create(RestPhotoLike::class.java) }
    val status: RestStatus by lazy { retrofit.create(RestStatus::class.java) }
    val statusLike: RestStatusLike by lazy { retrofit.create(RestStatusLike::class.java) }
    val statusComment: RestStatusComment by lazy { retrofit.create(RestStatusComment::class.java) }
    val user: RestUser by lazy { retrofit.create(RestUser::class.java) }
    val userExternal: RestUserExternal by lazy { retrofit.create(RestUserExternal::class.java) }
    val userActive: RestUserActive by lazy { retrofit.create(RestUserActive::class.java) }
    val userGroup: RestUserGroup by lazy { retrofit.create(RestUserGroup::class.java) }
    val userEvent: RestUserEvent by lazy { retrofit.create(RestUserEvent::class.java) }
    val userFriend: RestUserFriend by lazy { retrofit.create(RestUserFriend::class.java) }
    val userWall: RestUserWall by lazy { retrofit.create(RestUserWall::class.java) }
    val userWallMessage: RestUserWallMessage by lazy { retrofit.create(RestUserWallMessage::class.java) }
    val friendRequest: RestFriendRequest by lazy { retrofit.create(RestFriendRequest::class.java) }
    val notificationRead: RestNotificationRead by lazy { retrofit.create(RestNotificationRead::class.java) }
    val notificationUnread: RestNotificationUnread by lazy { retrofit.create(RestNotificationUnread::class.java) }
    val notificationUnreadConsume: RestNotificationUnreadConsume by lazy { retrofit.create(RestNotificationUnreadConsume::class.java) }
    val notificationAck: RestNotificationAck by lazy { retrofit.create(RestNotificationAck::class.java) }
    val search: RestSearch by lazy { retrofit.create(RestSearch::class.java) }
    val conversation: RestConversation by lazy { retrofit.create(RestConversation::class.java) }
    val conversationMessage: RestConversationMessage by lazy { retrofit.create(RestConversationMessage::class.java) }
    val conversationMessageConsume: RestConversationMessageConsume by lazy { retrofit.create(RestConversationMessageConsume::class.java) }
    val conversationMessagePhoto: RestConversationMessagePhoto by lazy { retrofit.create(RestConversationMessagePhoto::class.java) }
    val event: RestEvent by lazy { retrofit.create(RestEvent::class.java) }
    val eventCancel: RestEventCancel by lazy { retrofit.create(RestEventCancel::class.java) }
    val eventCustomField: RestEventCustomField by lazy { retrofit.create(RestEventCustomField::class.java) }
    val eventMember: RestEventMember by lazy { retrofit.create(RestEventMember::class.java) }
    val eventPhoto: RestEventPhoto by lazy { retrofit.create(RestEventPhoto::class.java) }
    val eventProfilePhoto: RestEventProfilePhoto by lazy { retrofit.create(RestEventProfilePhoto::class.java) }
    val eventProfileCoverPhoto: RestEventProfileCoverPhoto by lazy { retrofit.create(RestEventProfileCoverPhoto::class.java) }
    val eventRide: RestEventRide by lazy { retrofit.create(RestEventRide::class.java) }
    val eventWall: RestEventWall by lazy { retrofit.create(RestEventWall::class.java) }
    val eventWallMessage: RestEventWallMessage by lazy { retrofit.create(RestEventWallMessage::class.java) }
    val group: RestGroup by lazy { retrofit.create(RestGroup::class.java) }
    val groupCustomField: RestGroupCustomField by lazy { retrofit.create(RestGroupCustomField::class.java) }
    val groupMember: RestGroupMember by lazy { retrofit.create(RestGroupMember::class.java) }
    val groupPhoto: RestGroupPhoto by lazy { retrofit.create(RestGroupPhoto::class.java) }
    val groupProfilePhoto: RestGroupProfilePhoto by lazy { retrofit.create(RestGroupProfilePhoto::class.java) }
    val groupProfileCoverPhoto: RestGroupProfileCoverPhoto by lazy { retrofit.create(RestGroupProfileCoverPhoto::class.java) }
    val groupWall: RestGroupWall by lazy { retrofit.create(RestGroupWall::class.java) }
    val groupWallMessage: RestGroupWallMessage by lazy { retrofit.create(RestGroupWallMessage::class.java) }

    private val retrofit by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()

        httpLoggingInterceptor.level = if (clientConfiguration.debug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        val coreOkHttpClient = OkHttpClient.Builder()
                .connectionPool(clientConfiguration.connectionPool)
                .readTimeout(clientConfiguration.readTimeoutInMilliseconds, TimeUnit.MILLISECONDS)
                .writeTimeout(clientConfiguration.writeTimeoutInMilliseconds, TimeUnit.MILLISECONDS)
                .addInterceptor { chain ->

                    val builder = chain.request().newBuilder()

                    // add request headers
                    clientConfiguration.headersToInclude?.forEach { k, v -> builder.addHeader(k, v) }

                    session?.authenticationToken?.accessToken?.let { builder.addHeader("Authorization", it) }

                    chain.proceed(builder.build())
                }
                .addInterceptor { chain ->

                    val res = chain.proceed(chain.request().newBuilder().build())

                    if (res.code() >= 400 || res.code() < 200) {
                        //val responseBody = res.peekBody(Long.MAX_VALUE)?.string()
                        if (res.code() == 401) {
                            //throw MyObjectMapper.objectMapper.readValue(responseBody, InvalidCredentialsMySocialAppException::class.java)
                        }

                        //throw MyObjectMapper.objectMapper.readValue(responseBody, MySocialAppException::class.java)
                        res.newBuilder()
                    }

                    res
                }
                .addInterceptor(httpLoggingInterceptor)
                .build()

        Retrofit.Builder()
                .baseUrl(configuration.completeAPIEndpointURL)
                .client(coreOkHttpClient)
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(MyObjectMapper.objectMapper))
                .build()
    }

}