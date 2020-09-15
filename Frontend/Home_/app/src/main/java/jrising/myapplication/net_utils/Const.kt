package jrising.myapplication.net_utils

import org.json.JSONArray

object Const {
    //web addresses
    val URL_JSON_OBJECT = "https://api.androidhive.info/volley/person_object.json"
    val URL_VC5_SERVER = "http://cs309-vc-5.misc.iastate.edu:8080/"
    val URL_JSON_TEST_IP =  "http://ip.jsontest.com/"
    val URL_VC5_SEARCH = "http://cs309-vc-5.misc.iastate.edu:8080/search/"
    val URL_VC5_LOGIN = "http://cs309-vc-5.misc.iastate.edu:8080/users/login?"
    val URL_VC5_USER_BY_ID = "http://cs309-vc-5.misc.iastate.edu:8080/users/findbyid?"
    val URL_VC5_USER_SIGNUP = "http://cs309-vc-5.misc.iastate.edu:8080/users/create?"
    val URL_VC5_USER_BY_USERNAME = "http://cs309-vc-5.misc.iastate.edu:8080/users/findbyusername?"
    val URL_VC5_COMMENT = "http://cs309-vc-5.misc.iastate.edu:8080/comments"
    val URL_VC5_RECIPE = URL_VC5_SERVER + "recipe/"
    val URL_VC5_RECIPES = URL_VC5_SERVER + "recipes"
    val URL_VC_USERLOGS_BY_USERNAME = "http://cs309-vc-5.misc.iastate.edu:8080/timelines/findTimelineByUsername?"
    val URL_VC5_SUBMIT_LOG= "http://cs309-vc-5.misc.iastate.edu:8080/timelines/create"
    val URL_VC5_HOMESCREEN= "http://cs309-vc-5.misc.iastate.edu:8080/timelines/all"
    val URL_VC5_TIMELINES = "http://cs309-vc-5.misc.iastate.edu:8080/timelines"
    val URL_VC5_PROFILE = "http://cs309-vc-5.misc.iastate.edu:8080/profile"
    val URL_VC5_USER = "http://cs309-vc-5.misc.iastate.edu:8080/users"
    val URL_VC5_CHANGEBIO = "http://cs309-vc-5.misc.iastate.edu:8080/users/modifyBio"
    val URL_VC5_MODIFY_AVATAR = "http://cs309-vc-5.misc.iastate.edu:8080/users/modifyAvatar"
    val URL_VC5_FRIENDS = "http://cs309-vc-5.misc.iastate.edu:8080/friends"
    val URL_VC5_WEBSOCKET = "http://cs309-vc-5.misc.iastate.edu:8080/websocket/"
    val URL_VC5_PANTRY = "http://cs309-vc-5.misc.iastate.edu:8080/pantry"
    val URL_VC5_USER_SEARCH = URL_VC5_SERVER + "users/search"
    val URL_VC5_SHOPPING_LIST = URL_VC5_SERVER + "shoppinglist"

    // for images return type
    val PICK_IMAGE = 1
    val PICK_CAPTURE = 1337
    val PICK_THUMBNAIL = 2
    val CAPTURE_THUMBNAIL = 1338

    // names for JSON arguments
    const val users_json_id = "id"
}

