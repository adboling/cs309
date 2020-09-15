package jrising.myapplication.net_utils



import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.net.URL
import java.util.*

/**
 * The interface users of the socketHandler class should use to handle communication coming from the server
 * The class using the client should use the send message to send messages to the server, this interface handles
 * incoming communication
 */
interface ISocketListener {
    /**
     * The method used to handle messages coming from the server
     * Invoked on a separate thread whenever a message is received
     * @param message A [String] message from the server
     */
    fun handleMessage(message: String)
    /**
     * The method used to handle messages coming from the server
     * Invoked on a separate thread whenever a message is received
     * @param message A [JSONObject] message from the server
     */
    fun handleMessage(message: JSONObject)
    /**
     * The method used to handle messages coming from the server
     * Invoked on a separate thread whenever a message is received
     * @param message A [JSONArray] message from the server
     */
    fun handleMessage(message: JSONArray)

    /**
     * The method used to handle the opening handshake from the server
     * @param handshake The [ServerHandshake] from the server
     */
    fun handleOpen(handshake: ServerHandshake?)

    /**
     * The method used to handle a close message from the server
     * @param code The [Int] response code
     * @param reason The [String] reason for the close
     * @param remote True if the remote server closed the connection, false if it was the client
     */
    fun handleClose(code: Int, reason: String?, remote: Boolean)

    /**
     * The method used to handle an error on the thread
     * @param e The [Exception] that caused the error
     */
    fun handleError(e: Exception?)

}

/**
 * A class to separate out the creation of JSONObjects for testing
 * Unused now because we're using robolectric
 */
class JSONFactory {
    /**
     * Method to create a [JSONObject] from a string
     * @param source The [String] to create the object
     */
    fun createObject(source: String): JSONObject {
        return JSONObject(source)
    }
    /**
     * Method to create a [JSONArray] from a string
     * @param source The [String] to create the object
     */
    fun createArray(source: String): JSONArray {
        return JSONArray(source)
    }
}

/**
 * This class provides an implementation of WebSocketClient.
 * It uses a multiple listener observer pattern based on a HashSet.
 * Classes which use this class should implement ISocketListener to handle messages coming from the server, and use this
 * class to handle sending messages.
 */
open class socketHandler : WebSocketClient {
    /**
     * A [MutableSet] of [ISocketListener]s to notify on messages
     */
    private val listeners: MutableSet<ISocketListener> = HashSet()

    /**
     * The [JSONFactory] to create [JSONObject]s and [JSONArray]s
     */
    var jsonFactory = JSONFactory()

    /**
     * The constructors all simply wrap the constructors for the base WebSocketClient
     * I don't actually know the implementation details there
     */
    constructor(uri: URI) : super(uri)

    constructor(uri: URI, draft: Draft) : super(uri, draft)

    constructor(uri: URI, httpHeaders: MutableMap<String, String>) : super(uri, httpHeaders)

    constructor(uri: URI, draft: Draft, httpHeaders: MutableMap<String, String>) : super(uri, draft, httpHeaders)

    constructor(uri: URI, draft: Draft, httpHeaders: MutableMap<String, String>, timeout: Int) : super(uri, draft, httpHeaders, timeout)

    // Function to add a listener for messages
    // Returns true if added successfully, false if the listener was already added, or could not be added
    /**
     * A function to add a listener for messages
     * Returns true if added successfully, false otherwise
     * @param listener the [ISocketListener] to add
     * @return true if added successfully, false otherwise
     */
    fun addListener(listener: ISocketListener): Boolean {
        return listeners.add(listener)
    }

    // Function to remove a listener
    // Returns true if removed successfully, false if the listener was not in the set
    /**
     * A function to remove a listener for messages
     * Returns true if removed successfully, false otherwise
     * @param listener The [ISocketListener] to remove
     * @return true if removed successfully, false otherwise
     */
    fun removeListener(listener: ISocketListener): Boolean {
        return listeners.remove(listener)
    }

    /**
     * A function to remove all listeners from the set
     */
    fun clearListeners() {
        listeners.clear()
    }

    // Function to handle receiving messages
    // Will try to parse the message as a JSONObject or JSONArray
    // Will pass the string, JSONObject, or JSONArray to all of the listeners using handleMessage
    /**
     * A function to handle messages from the server
     * Takes a [String] from the server and tries to parse it to a [JSONObject] or [JSONArray]
     * Will ultimately call the corresponding message in the [ISocketListener]
     * @param message The [String] message from the server
     */
    override fun onMessage(message: String) {
        var jsonObject: JSONObject? = null
        var jsonArray: JSONArray? = null
        try {
            jsonObject = jsonFactory.createObject(message)
        } catch(e: JSONException) {
            try {
                jsonArray = jsonFactory.createArray(message)
            } catch(e: JSONException) {

            }
        } catch(e: Exception) {

        }
        if(jsonObject != null) {
            listeners.forEach{listener ->
                listener.handleMessage(jsonObject)
            }
        } else if(jsonArray != null) {
            listeners.forEach{ listener ->
                listener.handleMessage(jsonArray)
            }
        } else {
            listeners.forEach{ listener ->
                listener.handleMessage(message)
            }
        }
    }

    // Sends the handshake to each listener using handleOpen
    /**
     * Sends the [ServerHandshake] to each [ISocketListener] by calling [ISocketListener.handleOpen]
     * @param handshake The [ServerHandshake] from the server
     */
    override fun onOpen(handshake: ServerHandshake?) {
        listeners.forEach{ listener ->
            listener.handleOpen(handshake)
        }
    }

    /**
     * Receives the code, reason, and remote parameters from the server, and forwards them to each
     * [ISocketListener]
     * @param code The [Int] response code
     * @param reason The [String] reason for the close
     * @param remote True if the remote server closed the connection, false if it was the client
     */
    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        listeners.forEach { listener ->
            listener.handleClose(code, reason, remote)
        }
    }

    // Sends the exception to each listener using handleError
    /**
     * Receives an [Exception] and forwards it to each [ISocketListener]
     * @param e The [Exception] to send to each [ISocketListener]
     */
    override fun onError(e: Exception?) {
        listeners.forEach{ listener ->
            listener.handleError(e)
        }
    }
}


/**
 * A subclass identical to socketHandler, except it also sends debug information to [Log] when it receives messages
 */
class DebugSocketHandler : socketHandler {
    // Basic constructors which basically just wrap the WebSockerClient constructors
    constructor(uri: URI) : super(uri)

    constructor(uri: URI, draft: Draft) : super(uri, draft)

    constructor(uri: URI, httpHeaders: MutableMap<String, String>) : super(uri, httpHeaders)

    constructor(uri: URI, draft: Draft, httpHeaders: MutableMap<String, String>) : super(uri, draft, httpHeaders)

    constructor(uri: URI, draft: Draft, httpHeaders: MutableMap<String, String>, timeout: Int) : super(uri, draft, httpHeaders, timeout)

    override fun onMessage(message: String) {
        Log.d("SocketHandler", "Message Received: " + message)
        super.onMessage(message)
    }

    // Sends the handshake to each listener using handleOpen
    override fun onOpen(handshake: ServerHandshake?) {
        Log.d("SocketHandler", "Connection opened")
        super.onOpen(handshake)
    }

    // Sends the code, reason, and remote to each listener using  handleClose
    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("SocketHandler", "Connection closed.\nCode: " + code + "\nReason: " + reason + "\nremote? " + remote)
        super.onClose(code, reason, remote)
    }

    // Sends the exception to each listener using handleError
    override fun onError(e: Exception?) {
        if(e != null)
            Log.d("SocketHandler", "Error: " + e.message + "\n" + e.stackTrace)
        else
            Log.d("SocketHandler", "Error!")
        super.onError(e)
    }
}