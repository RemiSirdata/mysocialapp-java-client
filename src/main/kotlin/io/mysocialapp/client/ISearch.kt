package io.mysocialapp.client

import io.mysocialapp.client.extensions.toISO8601
import io.mysocialapp.client.models.SearchQuery

/**
 * Created by evoxmusic on 11/05/2018.
 */
interface ISearch {

    val searchQuery: SearchQuery

    fun toQueryParams(): MutableMap<String, String> {
        val m = mutableMapOf<String, String>()

        searchQuery.q?.let { m["q"] = it }

        searchQuery.name?.let { m["name"] = it }
        searchQuery.content?.let { m["content"] = it }

        searchQuery.user?.firstName?.let { m["first_name"] = it }
        searchQuery.user?.lastName?.let { m["last_name"] = it }
        searchQuery.user?.presentation?.let { m["content"] = it }
        searchQuery.user?.gender?.let { m["gender"] = it.name }
        searchQuery.user?.livingLocation?.let { v ->
            v.latitude?.toString()?.let { m["latitude"] = it }
            v.longitude?.toString()?.let { m["longitude"] = it }
        }

        searchQuery.maximumDistanceInMeters?.toString()?.let { m["maximum_distance"] = it }

        m["date_field"] = searchQuery.dateField ?: "created_date"
        searchQuery.startDate?.let { m["start_date"] = it.toISO8601() }
        searchQuery.endDate?.let { m["end_date"] = it.toISO8601() }

        searchQuery.sortOrder?.let { m["sort_order"] = it.name }

        return m
    }

}