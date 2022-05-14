package com.rymo.felfel.common

object Constants {


    /**
     * Base urls
     */
    const val BASE_URL = "http://richato.zavoshsoftware.com/"
    const val BASE_IMAGE_URL = "http://richato.com"
    const val BASE_USER_IMAGE_URL = "http://richato.zavoshsoftware.com"
    const val BASE_API_URL = "http://79.175.155.143/sanjabapi/api/"

    /**
     * Req code
     */
    const val SELECT_COUNTRY_REQ_CODE = 1001
    const val SELECT_IMAGE_FROM_GALLERY = 1002
    const val CHANGE_ACTIVITY = 1003
    const val CROP_IMAGE = 1004
    const val REQ_ALL_PERMISSIONS = 1005
    const val REQ_SMS_PERMISSIONS = 1006
    const val REQ_STORAGE_PERMISSIONS = 1007

    /**
     * Shp Keys
     */
    const val SHP_NAME = "APP_SETTING"
    const val SHP_LANGUAGE = "LANGUAGE"
    const val SHP_THEME = "THEME"
    const val SHP_FIRST_OPEN_ALARM = "SHP_FIRST_OPEN_ALARM"
    const val SHP_LAST_MESSAGE_DATE = "SHP_LAST_MESSAGE_DATE"
    const val SHP_CURRENT_USER_ID = "CURRENT_USER_ID"
    const val SHP_DEVICE_ID_ID = "DEVICE_ID"
    const val SHP_FCM_TOKEN = "FCM_TOKEN"
    const val NO_STRING_DATA = ""


    /**
     * Language
     */
    const val LANGUAGE_FA = "fa"
    const val LANGUAGE_EN = "en"
    const val LANGUAGE_DEFAULT = "fa"

    /**
     * Theme
     */
    const val THEME_LIGHT = "light"
    const val THEME_NIGHT = "night"

    /**
     * Key extras
     */
    const val KEY_EXTRA_TITLE = "KEY_EXTRA_TITLE"
    const val KEY_EXTRA_NAME = "KEY_EXTRA_NAME"
    const val KEY_EXTRA_TYPE = "KEY_EXTRA_TYPE"
    const val KEY_EXTRA_QUERY = "KEY_EXTRA_QUERY"
    const val KEY_EXTRA_IMAGE = "KEY_EXTRA_IMAGE"
    const val KEY_EXTRA_CATEGORY_ID = "KEY_EXTRA_CATEGORY_ID"
    const val KEY_EXTRA_ID = "KEY_EXTRA_ID"
    const val KEY_EXTRA_DATA = "KEY_EXTRA_DATA"

    /**
     * GENDER
     */
    const val BOY = 1
    const val GIRL = 2

    /**
     * Banner types
     */
    const val BANNER_VIDEO_TYPE = "Video"
    const val BANNER_CATEGORY_TYPE = "Category"

    /**
     * Event bus messages
     */
    const val OPEN_DEEP_LINK = "OPEN_DEEP_LINK"
    const val LOGIN_SUCCESS = "LOGIN_SUCCESS"
    const val UPDATE_PROFILE = "UPDATE_PROFILE"

}
