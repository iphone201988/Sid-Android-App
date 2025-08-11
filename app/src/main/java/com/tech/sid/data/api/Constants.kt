package com.tech.sid.data.api
object Constants {
    const val BASE_URL = "http://3.130.247.10:8000/api/v1/"
    const val BASE_URL_PHOTO = "http://3.130.247.10:8000"
    const val UN_AUTHORISED_CODE: Int = 401
    const val UN_AUTHORISED_STRING: String = "Unauthorized"
    const val DEFAULT_FLAG_LINK: String = "https://cdn.jsdelivr.net/npm/country-flag-emoji-json@2.0.0/dist/images/IN.svg"
    const val HEADER_API = "X-API-Key:lkcMuYllSgc3jsFi1gg896mtbPxIBzYkEL"
    /**************** API LIST *****************/

    const val LOGIN_API = "user/login"
    const val GET_ONBOARDING_API = "onboarding-question"
    const val POST_ONBOARDING_API = "onboarding-question/analyze"
    const val SIGNUP_API = "user/register"
    const val VERIFICATION_API = "user/verify-email"
    const val CHANGE_PASSWORD_API = "user/change-password"
    const val RESEND_OTP = "user/resend-otp"
    const val FORGOT_PASSWORD = "user/forget-password"
    const val RESET_PASSWORD = "user/reset-password"
    const val GET_EMPATHY_OPTIONS_API = "empathy/options"
    const val GET_EMPATHY_OPTIONS_SCENARIOS_API = "empathy/options-scenarios/"
    const val GET_RELATION_API = "relation/2"
    const val GET_EMPATHY_OPTIONS_RESPONSE_STYLES_API = "empathy/options-response-styles/"
    const val POST_EMPATHY_INTERACTIONS_API = "empathy/interactions"
    const val POST_CHAT_API = "empathy/chat"
    const val PUT_EDIT_PROFILE = "user/update"
    const val LOGOUT = "user/logout"
    const val DELETE_ACCOUNT = "user/delete-account"
    const val HOME_ACCOUNT = "home"
    const val HOME_GRAPH_ACCOUNT = "mood/week"
    const val JOURNAL_ACCOUNT = "empathy/get-user-simulations"
    const val INSIGHTS_ACCOUNT = "empathy/insights"
    const val ADD_MOOD = "mood"
    const val ADD_JOURNAL = "journal"


}