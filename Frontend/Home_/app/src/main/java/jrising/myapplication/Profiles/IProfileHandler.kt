package jrising.myapplication.Profiles

import android.os.Bundle
import android.util.Log
import android.view.View
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import org.json.JSONArray
import org.json.JSONObject

const val bundle_uname = "uname"
const val bundle_fname = "fname"
const val bundle_lname = "lname"
const val bundle_email = "email"
const val bundle_role = "role"
const val bundle_friends = "friends"
const val bundle_timeline = "timeline"
const val bundle_bio = "bio"
const val bundle_avatar = "avatar"
const val bundle_background = "background"
const val bundle_id = "id"

 interface IPRofileHander {
     val requestURL: String

     fun handleSource(source: JSONObject, personal: Boolean): Bundle

     fun generateURL(userID: Int): String
     fun generateURL(username: String): String
     fun checkofEmptyBio(bio: String?): String
 }

class BasicProfileHandler : IPRofileHander {
    private val JSON_uname = "username"
    private val JSON_fname = "firstname"
    private val JSON_lname = "lastname"
    private val JSON_email = "email"
    private val JSON_role = "role"
    private val JSON_friends = "friendsList"
    private val JSON_timeline = "timeline"
    private val JSON_bio = "bio"
    private val JSON_avatar = "avatar"
    private val JSON_background = "background"
    private val json_id = "id"


    override val requestURL: String = Const.URL_VC5_PROFILE
    /**
     * If it is the users personal profile it we will use this function to populate the bundle to save some time an speed things up
     */

    override fun handleSource(source: JSONObject, personal: Boolean): Bundle {
        Log.d("profileView", source.toString())
        val result = Bundle()
        //if it is the user profile we will simply grab form the personal info page
        if(personal) {
            result.putString(bundle_fname, Userinfo.firstName)
            result.putString(bundle_lname, Userinfo.lastName)
            result.putString(bundle_uname, Userinfo.username)
            result.putInt(bundle_id, source.optInt(json_id, -1))
            result.putString(bundle_bio, Userinfo.bio)
        } else {
            result.putString(bundle_fname, source.optString(JSON_fname, "Not given"))
            result.putString(bundle_lname, source.optString(JSON_lname, ""))
            result.putString(bundle_uname, source.optString(JSON_uname, "Not given"))
            result.putString(bundle_bio, source.optString(JSON_bio, ""))
            result.putInt(bundle_id, source.optInt(json_id, -1))
        }
        result.putString(bundle_avatar, source.optString(JSON_avatar, ""))
        result.putString(bundle_background, source.optString(JSON_background, ""))

        result.putString(bundle_email, source.optString(JSON_email, "Not given"))
        result.putString(bundle_role, source.optString(JSON_role, "Not given"))

        val friends = source.optJSONArray(JSON_friends) ?: ""
        result.putString(bundle_friends, friends.toString())

        val timeline = source.optJSONArray(JSON_timeline) ?: ""
        result.putString(bundle_timeline, timeline.toString())

        return result
    }




    override fun generateURL(userID: Int): String {
        return requestURL + "?id=" + userID
    }

    override fun generateURL(username: String): String {
        return requestURL + "?userName=" + username
    }
   override fun checkofEmptyBio(bio: String?): String {

        return bio ?: ""

    }

}