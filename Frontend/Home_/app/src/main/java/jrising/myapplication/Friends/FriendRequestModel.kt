package jrising.myapplication.Friends

import android.util.Log
import com.android.volley.Request
import jrising.myapplication.app.EventBus
import jrising.myapplication.app.User
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONArray
import org.json.JSONObject

/**
 * The interface defining what a view has to implement to properly update the friend request list
 */
interface IFriendRequestListener {
    /**
     * The function called when there is a change to the list of incoming requests
     * @param users The list of incoming requests
     */
    fun updateIncomingRequests(users: List<User>)

    /**
     * The function called when there is a change to the list of outgoing requests
     * @param users the list of outgoing requests
     */
    fun updateOutgoingRequests(users: List<User>)
}

/**
 * The interface defining the functions [FriendRequestListFragment]
 */
interface IFriendRequestController {
    /**
     * The function called to accept a request from another user
     * @param user the [User] who's request is accepted
     */
    fun acceptRequest(user: User)

    /**
     * The function called to decline a request from another user
     * @param user the [User] who's request is declined
     */
    fun declineRequest(user: User)

    /**
     * The function called to cancel a request to another user
     * @param user the [User] who's request is canceld
     */
    fun cancelRequest(user: User)

    /**
     * The function called to request the list of incoming requests
     */
    fun requestIncoming()

    /**
     * The function called to request the list of outgoing requests
     */
    fun requestOutgoing()
}

class FriendRequestModel(var listener: IFriendRequestListener, var volleyHandler: VolleyHandler, var eventBus: EventBus) : IFriendRequestController {
    override fun acceptRequest(user: User) {
        val obj = JSONObject()
        obj.put(json_senderID, user.id)
        obj.put(json_senderUsername, user.username)
        obj.put(json_receiverID, Userinfo.u_id)
        obj.put(json_receiverUsername, Userinfo.username)
        val url = Const.URL_VC5_FRIENDS + "/acceptRequest"
        volleyHandler.createJSONRequest(url, Request.Method.POST, TAG, obj, { requestIncoming() }, null)
    }

    override fun cancelRequest(user: User) {
        val fID = user.id
        val sID = Userinfo.u_id
        val url = Const.URL_VC5_FRIENDS + "/deleteFriend?friendID=" + fID + "&userID=" + sID
        volleyHandler.createJSONRequest(url, Request.Method.DELETE, TAG, null, { requestOutgoing() }, null)
    }

    override fun declineRequest(user: User) {
        val obj = JSONObject()
        obj.put(json_senderID, user.id)
        obj.put(json_senderUsername, user.username)
        obj.put(json_receiverID, Userinfo.u_id)
        obj.put(json_receiverUsername, Userinfo.username)
        val url = Const.URL_VC5_FRIENDS + "/declineRequest"
        volleyHandler.createJSONRequest(url, Request.Method.POST, TAG, obj, { requestIncoming() }, null)
    }

    override fun requestIncoming() {
        val url = Const.URL_VC5_FRIENDS + "/getIncomingByUserID?userID=" + Userinfo.u_id
        volleyHandler.createJSONArrayRequest(url, Request.Method.GET, TAG, null, { response -> handleIncoming(response) }, null)
    }

    fun handleIncoming(response: JSONArray) {
        val result = ArrayList<User>()
        for(i in 1..response.length()) {
            val it = response[i - 1] as? JSONObject ?: return
            result.add(User(it))
        }
        listener.updateIncomingRequests(result)
    }

    override fun requestOutgoing() {
        Log.d(TAG, "Creating request")
        val url = Const.URL_VC5_FRIENDS + "/getOutgoingByUserID?userID=" + Userinfo.u_id
        Log.d(TAG, "Sending request to " + url)
        volleyHandler.createJSONArrayRequest(url, Request.Method.GET, TAG, null, { response -> handleOutgoing(response) }, null)
        Log.d(TAG, "Request sent!")
    }

    fun handleOutgoing(response: JSONArray) {
        Log.d(TAG, "Response: " + response)
        val result = ArrayList<User>()
        for(i in 1..response.length()) {
            val it = response[i - 1] as? JSONObject ?: return
            result.add(User(it))
        }
        Log.d(TAG, "Forwarding result: " + response)
        listener.updateOutgoingRequests(result)
    }
    companion object {
        const val TAG = "FReqList"
        const val json_senderID = "senderID"
        const val json_senderUsername = "senderUsername"
        const val json_receiverID = "recipientID"
        const val json_receiverUsername = "recipientUsername"
    }
}