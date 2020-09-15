package jrising.myapplication.net_utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.widget.ImageView
import com.android.volley.toolbox.*
import jrising.myapplication.app.*
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

const val defaultMaxWidth = 1000
const val defaultMaxHeight = 1000
val defaultBitmapConfig = Bitmap.Config.ARGB_8888
val defaultScaleType = ImageView.ScaleType.CENTER_INSIDE
const val friends_json_senderUsername = "senderUsername"
const val friends_json_senderID = "senderID"
const val friends_json_recipientID = "recipientID"
const val friends_json_recipientUsername = "recipientUsername"

/**
 * The interface used to handle sending information to the server, and calling methods when it comes back
 */
interface VolleyHandler {
    val imageLoader: ImageLoader
    val methodGet: Int
    val methodPost: Int
    val methodDelete:Int
    val methodPut:Int

    /**
     * A function to add a given request to the server and the given TAG
     */

    @Deprecated("TAG is not used", ReplaceWith("addToRequestQueue(req)"))
    fun <T> addToRequestQueue(req: Request<T>, TAG: String)

    /**
     * A function to add a given request to the server
     * @param req The [Request] to send
     */
    fun <T> addToRequestQueue(req: Request<T>)

    /**
     * A function to cancel all pending requests with the given tag
     * @param tag The [String] tag to cancel requests with
     */
    fun cancelPendingRequests(tag: String)

    /**
     * A helper method used to handle errors. Takes the error message and prints it to the [Log]
     * @param error The [VolleyError] received
     * @param tag The tag to display on [Log] with
     */
    fun handleError(error: VolleyError, tag: String)
    fun getUserLogs(userId: String?, handler : timelineHandler, isOwner: Boolean)
    fun postLog(logObj: JSONObject, handler : createLogsHandler)
    fun getHomeScreen(handler: homescreenHandler)
    /**
     * A helper method to request an image from a given url and put it in a given ImageView
     * Uses the default maxWidth, maxHeight, Bitmap config, and scale type
     * @param view The [ImageView] to fill
     * @param url The URL to load from
     */
    fun requestImage(view: ImageView, url: String)

    /**
     * A helper method to request an image from a given url and populate a given ImageView
     * view and url are required, but any other argument can be left null to use the defaults
     * @param view The [ImageView to fill
     * @param url The URL to load from
     * @param maxWidth The maximum width for the picture
     * @param maxHeight The maximum height for the picture
     * @param config The [Bitmap.Config] for the picture
     * @param scaleType The [ImageView.ScaleType] for the picture
     */
    fun requestImage(view: ImageView, url: String, maxWidth: Int?, maxHeight: Int?, config: Bitmap.Config?, scaleType: ImageView.ScaleType?)

    /**
     * A helper method to create a request to add the givne username as a friend
     * @param uname The username of the friend to add
     * @param responseHandler The callback to call with the response
     */
    fun addFriend(uname: String, responseHandler: (JSONObject)->Unit)

    /**
     * A helper method to create a request to add the givne username as a friend
     * @param uname The username of the friend to add
     * @param responseHandler The callback to call with the response
     */
    fun addFriend(uname: String, u_id: Int, responseHandler: (JSONObject)->Unit)

    /**
     * A helper method to create and send a JSONRequest with the given url, method, tag, parameters, callback, and errorhandler
     * params and error handler can be null.
     * @param url The URL to send the request to
     * @param method The REST method to use
     * @param tag The tag for an error message, if using the default error handler
     * @param params The [JSONObject] to put in the request body. Can be left null for a get request
     * @param callback The method called when the response is received. Possibly called on a separate thread
     * @param errorHandler The method to use if there is an error. Leave null to use [handleError]
     */
    fun createJSONRequest(url: String, method: Int, tag: String, params: JSONObject?, callback: (JSONObject)->Unit, errorHandler: ((VolleyError)->Unit)?)
    /**
     * A helper method to create and send a JSONRequest with the given url, method, tag, parameters, callback, and errorhandler
     * params and error handler can be null.
     * @param url The URL to send the request to
     * @param method The REST method to use
     * @param tag The tag for an error message, if using the default error handler
     * @param params The [JSONArray] to put in the request body. Can be left null for a get request
     * @param callback The method called when the response is received. Possibly called on a separate thread
     * @param errorHandler The method to use if there is an error. Leave null to use [handleError]
     */
    fun createJSONArrayRequest(url: String, method: Int, tag: String, params: JSONArray?, callback: (JSONArray)->Unit, errorHandler: ((VolleyError)->Unit)?)
    /**
     * A helper method to create and send a JSONRequest with the given url, method, tag, parameters, callback, and errorhandler
     * The error handler can be null.
     * @param url The URL to send the request to
     * @param method The REST method to use
     * @param tag The tag for an error message, if using the default error handler
     * @param callback The method called when the response is received. Possibly called on a separate thread
     * @param errorHandler The method to use if there is an error. Leave null to use [handleError]
     */
    fun createStringRequest(url: String, method: Int, tag: String, callback: (String)->Unit, errorHandler: ((VolleyError)->Unit)?)
}

/**
 * The original implementation for the VolleyHandler
 */
open class VolleyController(context: Context) : VolleyHandler {
    override val methodGet = Request.Method.GET
    override val methodPost = Request.Method.POST
    override val methodDelete = Request.Method.DELETE
    override val methodPut = Request.Method.PUT

    companion object {
        @Volatile
        private var INSTANCE : VolleyController? = null
        fun getInstance(context:Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: VolleyController(context).also {
                        INSTANCE = it
                    }
                }
    }
    val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    override val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String) : Bitmap {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    override fun <T> addToRequestQueue(req: Request<T>, TAG : String) {
        requestQueue.add(req)
    }

    override fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    override fun cancelPendingRequests(tag : String) {
        requestQueue.cancelAll(tag)
    }

    override fun createJSONRequest(
        url: String,
        method: Int,
        tag: String,
        params: JSONObject?,
        callback: (JSONObject) -> Unit,
        errorHandler: ((VolleyError) -> Unit)?
    ) {
        if(errorHandler == null) {
            val req = JsonObjectRequest(method, url, params, Response.Listener(callback), Response.ErrorListener { error ->
                handleError(error, tag)
            })
            addToRequestQueue(req)
        } else {
            val req = JsonObjectRequest(method, url, params, Response.Listener(callback), Response.ErrorListener {error ->
                errorHandler(error)
            })
            addToRequestQueue(req)
        }
    }

    override fun createJSONArrayRequest(
        url: String,
        method: Int,
        tag: String,
        params: JSONArray?,
        callback: (JSONArray) -> Unit,
        errorHandler: ((VolleyError) -> Unit)?
    ) {
        if(params == null) {
            if(errorHandler == null) {
                val req = JsonArrayRequest(url, Response.Listener(callback), Response.ErrorListener { error ->
                    handleError(error, tag)
                })
                addToRequestQueue(req)
            } else {
                val req = JsonArrayRequest(url, Response.Listener(callback), Response.ErrorListener { error ->
                    errorHandler(error)
                })
                addToRequestQueue(req)
            }
        } else {
            if(errorHandler == null) {
                val req = JsonArrayRequest(method, url, params, Response.Listener(callback), Response.ErrorListener { error ->
                    handleError(error, tag)
                })
                addToRequestQueue(req)
            } else {
                val req = JsonArrayRequest(method, url, params, Response.Listener(callback), Response.ErrorListener { error ->
                    errorHandler(error)
                })
                addToRequestQueue(req)
            }
        }
    }

    override fun createStringRequest(
        url: String,
        method: Int,
        tag: String,
        callback: (String) -> Unit,
        errorHandler: ((VolleyError) -> Unit)?
    ) {
        if(errorHandler == null) {
            val req = StringRequest(method, url, Response.Listener(callback), Response.ErrorListener { error ->
                handleError(error, tag)
            })
            addToRequestQueue(req)
        } else {
            val req = StringRequest(method, url, Response.Listener(callback), Response.ErrorListener { error ->
                errorHandler(error)
            })
            addToRequestQueue(req)
        }
    }


    override fun handleError(error : VolleyError, tag : String) {
        Log.e(tag, "Error type: " + error.toString())
        Log.e(tag, "Error message: " + error.message)
        val response = error.networkResponse
        if(response?.data != null) {
            val body = String(response.data, Charset.defaultCharset())
            Log.e(tag, "Error body: " + body)
        }

    }

    /**
     * used in the timeline to get the userId's timeline logs
     */
    override fun getUserLogs(userId: String?, handler : timelineHandler, isOwner: Boolean){
        val TAG = "LoadLogs"
        var logdata = JSONArray()
       var url = Const.URL_VC_USERLOGS_BY_USERNAME + "username="+userId
        val JsonReq = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                AppController.eventBus.handleTimelineResults(response, handler, isOwner)


            },Response.ErrorListener { error ->
                handleError(error, TAG)
            })
        addToRequestQueue(JsonReq, TAG)

    }

    /**
     * volley function to post logs to the server
     */
    override fun postLog(logObj: JSONObject, handler : createLogsHandler){
        val TAG = "submitLogs"
        val req = JsonObjectRequest(Request.Method.POST, Const.URL_VC5_SUBMIT_LOG,logObj,
            Response.Listener { response ->
                AppController.eventBus.handleLogSubmission(handler)
            },Response.ErrorListener { error ->
                handleError(error,TAG)

            })
        addToRequestQueue(req, TAG)
    }

    override fun getHomeScreen(handler: homescreenHandler){
        val TAG = "HomeScreen"
        val reg = JsonArrayRequest(Request.Method.GET, Const.URL_VC5_HOMESCREEN, null,
            Response.Listener { response ->
                AppController.eventBus.handleTheHomescreen(response, handler)
            },
            Response.ErrorListener { error ->
                handleError(error,TAG)
            })
        addToRequestQueue(reg, TAG)
    }

    override fun requestImage(view: ImageView, url: String) {
        val req = ImageRequest(url, Response.Listener{ response: Bitmap ->
            view.setImageBitmap(response)
    }, defaultMaxWidth, defaultMaxHeight, defaultScaleType, defaultBitmapConfig, Response.ErrorListener {error ->
            handleError(error, "ImageLoad")
        })
        addToRequestQueue(req)
    }

    override fun requestImage(view: ImageView, url: String, maxWidth: Int?, maxHeight: Int?, config: Bitmap.Config?, scaleType: ImageView.ScaleType?) {
        val width = maxWidth ?: defaultMaxWidth
        val height = maxHeight ?: defaultMaxHeight
        val _scaleType = scaleType ?: defaultScaleType
        var bitmapConfig = config ?: defaultBitmapConfig


        val req = ImageRequest(url, Response.Listener{ response: Bitmap ->
            view.setImageBitmap(response)
        }, width, height, _scaleType, bitmapConfig, Response.ErrorListener { error ->
            handleError(error, "ImageLoad")
        })
        addToRequestQueue(req)
    }

    /*override fun addFriend(uname: String, responseHandler: (JSONObject) -> Unit) {
        val senderUsername = Userinfo.username
        val senderID = Userinfo.u_id
        val recipientUsername = uname
        val body = JSONObject()
        //body.put(friends_json_recipientID, )
        body.put(friends_json_recipientUsername, recipientUsername)
        body.put(friends_json_senderID, senderID)
        body.put(friends_json_senderUsername, senderUsername)

        val url = Const.URL_VC5_FRIENDS + "/sendRequest"
        val req = JsonObjectRequest(url, body, Response.Listener{response: JSONObject ->
            responseHandler(response)
        }, Response.ErrorListener {error ->
            handleError(error, "AddFriend")
        })
        requestQueue.add(req)
    }*/

    override fun addFriend(uname: String, responseHandler: (JSONObject) -> Unit) {
        val url = Const.URL_VC5_USER_BY_USERNAME + "username=" + uname
        val req = JsonObjectRequest(url, null,
            Response.Listener { response: JSONObject -> addFriend(response.getString("username"), response.getInt("id"), responseHandler) },
            Response.ErrorListener { error -> handleError(error, "AddFriend")})
        addToRequestQueue(req)
        }

    override fun addFriend(uname: String, u_id: Int, responseHandler: (JSONObject) -> Unit) {
        val senderUsername = Userinfo.username
        val senderID = Userinfo.u_id
        val recipientUsername = uname
        val recipientID = u_id

        val body = JSONObject()
        body.put(friends_json_recipientID, recipientID)
        body.put(friends_json_recipientUsername, recipientUsername)
        body.put(friends_json_senderID, senderID)
        body.put(friends_json_senderUsername, senderUsername)

        val url = Const.URL_VC5_FRIENDS + "/sendRequest"
        val req = JsonObjectRequest(url, body, Response.Listener{response: JSONObject ->
            responseHandler(response)
        }, Response.ErrorListener {error ->
            handleError(error, "AddFriend")
        })
        addToRequestQueue(req)
    }

}