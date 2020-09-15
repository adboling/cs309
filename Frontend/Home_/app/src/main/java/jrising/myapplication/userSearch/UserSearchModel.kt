package jrising.myapplication.userSearch

import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import jrising.myapplication.Profiles.HANDLER_BASIC
import jrising.myapplication.Profiles.UserProfile
import jrising.myapplication.Profiles.bundle_uname
import jrising.myapplication.app.AppController
import jrising.myapplication.app.EventBus
import jrising.myapplication.app.User
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONArray
import org.json.JSONObject

/**
 * The interface to define the functions the view needs to update based on the model
 */
interface IUserSearchListener {
    /**
     * The function used to update the results displayed
     * @param users The list of [User]s to display
     */
    fun updateResults(users: List<User>)
}

/**
 * The interface used to define the functions the model will use
 */
interface IUserSearchController {
    /**
     * The function called when a result has been selected
     * @param username The [String] username for the user selected
     */
    fun resultSelected(username: String)

    /**
     * The function called when a result has been selected
     * @param user The [User] that has been selected
     */
    fun resultSelected(user: User)

    /**
     * The function used to request a search
     * @param [username] the [String] username to search for
     */
    fun search(username: String)
}

/**
 * The class used to handle parsing the responses from the server to create a list for the [UserSearchFragment] to display
 * Implements [IUserSearchController] to handle calls from the fragment
 * @param listener The [IUserSearchListener] to update on changes
 * @param vh The [VolleyHandler] to use for communication with the server
 */
class UserSearchModel(var listener: IUserSearchListener, val vh: VolleyHandler, val eb: EventBus): IUserSearchController {

    override fun search(username: String) {
        val url = Const.URL_VC5_USER_SEARCH + "?username=" + username
        vh.createJSONArrayRequest(url, Request.Method.GET, TAG, null, { response -> handleSearchResults(response) }, null)
    }

    /**
     * The method called on response from the server to parse search results and send them to the [IUserSearchListener]
     * @param results The [JSONArray] of results from the server
     */
    fun handleSearchResults(results: JSONArray) {
        val users = ArrayList<User>()
        for(i in 1..results.length()) {
            val result = results[i - 1] as? JSONObject ?: return
            val user = User(result)
            users.add(user)
        }
        listener.updateResults(users)
    }

    override fun resultSelected(user: User) {
        Log.d(TAG, "User selected:\n$user")
        val args = Bundle()
        args.putString(bundle_uname, user.username)
        val fragment = UserProfile.newInstance(args, HANDLER_BASIC,false)
        eb.switchMainFragment(fragment)
    }

    override fun resultSelected(username: String) {
        Log.d(TAG, "Username selected: $username")
        val args = Bundle()
        args.putString(bundle_uname, username)
        val fragment = UserProfile.newInstance(args, HANDLER_BASIC, false)
        eb.switchMainFragment(fragment)
    }

    companion object {
        const val TAG = "UserSearch"
    }
}