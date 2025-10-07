package com.chesire.nekome.kaspresso.mock

import com.chesire.nekome.kaspresso.mock.MockTestData.AttackOnTitanSeason2.LIBRARY_ENTRY_ID

/**
 * Extension functions for MockWebServerManager containing complex mock scenarios for Nekome tests.
 * Each extension function represents a complete user flow scenario composed of multiple HTTP reques
 */
fun MockWebServerManager.mockSuccessfulLogin() {
    with(this) {
        mockPost(
            "/api/oauth/token",
            "kitsu/auth/login_success.json"
        )

        mockGet(
            "/api/edge/users",
            "kitsu/users/user_profile.json"
        )

        mockGet(
            requestPath = "/api/edge/users/1638957/library-entries",
            responseJsonFile = "kitsu/library/anime_list.json",
            queryParams = mapOf("filter[kind]" to "anime")
        )

        mockGet(
            requestPath = "/api/edge/users/1638957/library-entries",
            responseJsonFile = "kitsu/library/manga_list.json",
            queryParams = mapOf("filter[kind]" to "manga")
        )
    }

}

fun MockWebServerManager.mockSearchSeries() {
    mockSuccessfulLogin()

    mockGet(
        "/api/edge/anime",
        "kitsu/search/attack_on_titan_search.json"
    )

    mockPost(
        "/api/edge/library-entries",
        "kitsu/library/add_anime_response.json"
    )
}

fun MockWebServerManager.mockLoginWithSingleAnimeInTrack() {
    with(this) {
        mockPost(
            "/api/oauth/token",
            "kitsu/auth/login_success.json"
        )

        mockGet(
            "/api/edge/users",
            "kitsu/users/user_profile.json"
        )

        mockGet(
            requestPath = "/api/edge/users/1638957/library-entries",
            responseJsonFile = "kitsu/library/anime_list_single_aot_s2.json",
            queryParams = mapOf("filter[kind]" to "anime")
        )

        mockGet(
            requestPath = "/api/edge/users/1638957/library-entries",
            responseJsonFile = "kitsu/library/manga_list.json",
            queryParams = mapOf("filter[kind]" to "manga")
        )
    }
}

fun MockWebServerManager.mockDeleteAnime(libraryEntryId: Int = LIBRARY_ENTRY_ID) {
    mockRequestWithJsonResponse(
        requestMethod = DELETE,
        requestPath = "/api/edge/library-entries/$libraryEntryId",
        responseJson = "",
        responseCode = HTTP_204_NO_CONTENT
    )
}