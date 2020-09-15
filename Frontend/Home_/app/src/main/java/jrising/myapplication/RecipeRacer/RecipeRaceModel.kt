package jrising.myapplication.RecipeRacer
import android.os.SystemClock
import android.util.Log
import com.android.volley.Request
import jrising.myapplication.app.AppController
import jrising.myapplication.net_utils.*
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

/**
 * A data class to represent recipe race participants
 * @property Username The Username of the participant
 * @property UserID The UserID of the participant, not actually used
 * @property progress The current progress of the participant
 * @property totalProgress The progress required to win the race
 */
data class RRParticipant (
    val Username: String,
    val UserID: Int,
    var progress: Int,
    var totalProgress: Int
) {
    override fun equals(other: Any?): Boolean {
        if(other !is RRParticipant) return false
        if(other.Username != Username) return false
        if(other.UserID != UserID) return false
        if(other.progress != progress) return false
        if(other.totalProgress != totalProgress) return false
        return true
    }
    override fun hashCode(): Int {
        return Username.hashCode() + UserID.hashCode() + progress.hashCode() + totalProgress.hashCode()
    }
}

/**
 * A data class to represent ingredients in the recipe race
 * @property type The name or type of the ingredient, EG: "potatoes"
 * @property amount The amount of the ingredient
 * @property unit The unity type of the ingredient
 * @property complete Whether the user has used this ingredient or not
 */
data class RRIngredient (
    val type: String,
    val amount: Double,
    val unit: String,
    var complete: Boolean
) {
    override fun toString(): String {
        return toJSONObject().toString()
    }

    /**
     * A method to generate a [JSONObject] representation of this class
     */
    fun toJSONObject(): JSONObject {
        val result = JSONObject()
        result.put(RecipeRaceModel.json_ingredient_type, type)
        result.put(RecipeRaceModel.json_ingredient_amount, amount)
        result.put(RecipeRaceModel.json_ingredient_unit, unit)
        result.put(RecipeRaceModel.json_ingredient_complete, complete)
        return result
    }
}

/**
 * A data class for the Recipe Race game state
 * @property startTime The time the chronometer was started
 * @property running True if the chronometer is running, false otherwise
 * @property elapsedTime The time the chronometer has been running
 * @property sessionID The sessionID for the game
 * @property connected True if connected to the server, false otherwise
 * @property recipeID The recipeID for the game
 * @property ingredients The list of the ingredients for the game
 * @property participants The list of participants in the game
 * @property steps The steps for the recipe for the game
 */
data class RecipeRaceData (
    var startTime: Long,
    var running: Boolean,
    var elapsedTime: Long,
    var sessionID: Int,
    var connected: Boolean,
    var recipeID: String,
    var ingredients: MutableList<RRIngredient>,
    val participants: MutableSet<RRParticipant>,
    var steps: String
)


/**
 * The interface to define the methods the view needs to have to show the state
 */
interface IRecipeRaceListener {
    /**
     * The method used to update the client's screen with session data
     * @param data The [RecipeRaceData] to send to the view
     */
    fun update(data: RecipeRaceData)

    /**
     * The method used to display a chat messsage on the user's screen
     * @param message The [String] message to show
     */
    fun handleMessage(message: String)
    /**
     * The method used to start the user's timer
     * @param startTime The time the chronometer should have started at
     */
    fun startTimer(startTime: Long)
    /**
     * The method used to pause the user's time
     */
    fun pauseTimer()
    /**
     * The method used to reset the user's time
     */
    fun resetTimer()
}

interface IRecipeRaceModel: ISocketListener {
    /**
     * The listener to communicate changes back to the user
     */
    var listener: IRecipeRaceListener?
    /**
     * The method used to set the sessionID
     */
    fun setSessionId(sessionID: Int)
    /**
     * The method used to connect to the websocket with the given session ID, and the username in userinfo
     */
    fun connect(sessionID: Int)

    /**
     * The method used to disconnect from the websocket
     */
    fun disconnect()

    /**
     * The method used to start or resume the session timer
     */
    fun startTime()

    /**
     * The method used to pause the session timer
     */
    fun pauseTime()

    /**
     * The method used to reset the session timer to zero
     */
    fun resetTime()

    /**
     * The method used to send a chat message
     */
    fun sendChat(message: String)

    /**
     * The method used to send a JSONObject
     */
    fun sendJSONObject(content: JSONObject)

    /**
     * The method used to send the session information to the other clients
     */
    fun sendData(data: RecipeRaceData)
    /**
     * The method used to handle loading recipe data
     */
    fun loadRecipeData(data: JSONObject)
    /**
     * The method used to mark an ingredient as complete
     */
    fun completeIngredient(index: Int)

}

class RecipeRaceModel(sessionID: Int, recipeID: String) : IRecipeRaceModel {
    override var listener: IRecipeRaceListener? = null
    lateinit var sc: socketHandler
    var state: RecipeRaceData = RecipeRaceData(0, false, 0, sessionID, false, recipeID, ArrayList(), HashSet(), "")
    var isHost = false
    var volleyController = AppController.volleyController
    var playerStates: HashMap<String, Array<Boolean>> = HashMap()

    constructor(sessionID: Int) : this(sessionID, "")
    constructor(recipeID: String) : this(0, recipeID)
    constructor() : this(0, "")

    override fun setSessionId(sessionID: Int) {
        state.sessionID = sessionID
    }

    override fun connect(sessionID: Int) {
        val url = url + Userinfo.username + "/" + sessionID + "/"
        sc = DebugSocketHandler(URI(url))
        sc.addListener(this)
        sc.connect()
        state.connected = true
        val listener = listener ?: return
        listener.update(state)
    }

    override fun disconnect() {
        val obj = JSONObject()
        obj.put(json_type, type_leave)
        obj.put(json_player, Userinfo.username)
        sc.close()
        sc.removeListener(this)
        state.connected = false
        val listener = listener ?: return
        listener.update(state)
        listener.handleMessage("Connection closed!")
    }

    override fun pauseTime() {
        if(isHost) {
            val obj = JSONObject()
            obj.put(json_type, type_chrono)
            obj.put(json_chrono_command, json_chrono_pause)
            obj.put(json_start_time, state.startTime)
            sendJSONObject(obj)
        }
    }

    override fun startTime() {
        if(isHost) {
            val obj = JSONObject()
            obj.put(json_type, type_chrono)
            obj.put(json_chrono_command, json_chrono_start)
            obj.put(json_elapsedTime, state.elapsedTime)
            sendJSONObject(obj)
        }
    }

    override fun resetTime() {
        if(isHost) {
            val obj = JSONObject()
            obj.put(json_type, type_chrono)
            obj.put(json_chrono_command, json_chrono_reset)
            sendJSONObject(obj)
        }
    }

    override fun sendChat(message: String) {
        val obj = JSONObject()
        obj.put(json_type, type_chat)
        obj.put(json_message, message)
        obj.put(json_sender, Userinfo.username)
        sendJSONObject(obj)
    }

    override fun sendJSONObject(content: JSONObject) {
        try {
            sc.send(content.toString())
        } catch(e: Exception) {
            Log.e("RRModel", e.toString())
        }
    }

    override fun handleOpen(handshake: ServerHandshake?) {
        val listener = listener ?: return
        val message = "Connection established!"
        listener.handleMessage(message)
        if(handshake == null) return
        Log.d("RRModel", "Status:" + handshake.httpStatus)
        Log.d("RRModel", "Status message: " + handshake.httpStatusMessage)
    }

    override fun handleClose(code: Int, reason: String?, remote: Boolean) {
        val message = "Connection closed!\n" + reason
        val listener = listener ?: return
        listener.handleMessage(message)
    }

    override fun handleError(e: Exception?) {
        if(e == null) return
        Log.e("RRModel", e.message)
        e.printStackTrace()
    }

    override fun handleMessage(message: String) {
        val listener = listener ?: return
        listener.handleMessage(message)
    }

    override fun handleMessage(message: JSONObject) {
        val listener = listener ?: return
        val type = message.optString(json_type)
        when(type) {
            type_chat -> {
                val out = message.getString(json_sender) + ": " +
                        message.getString(json_message)
                listener.handleMessage(out)
            }
            type_info -> {
                val out = message.getString(json_message)
                listener.handleMessage(out)
            }
            type_join -> {
                val out = message.getString(json_player) + " has joined the game!"
                if(isHost) {
                    val player = message.getString(json_player)
                    val progress = 0
                    val totalProgress = state.ingredients.size
                    val participant = RRParticipant(player, 0, progress, totalProgress)
                    state.participants += participant
                    sendData(state)
                    listener.update(state)
                }
                listener.handleMessage(out)
            }
            type_leave -> {
                val out = message.getString(json_player) + " has left the game!"
                listener.handleMessage(out)
            }
            type_data -> {
                if(isHost) return
                state.running = message.getBoolean(json_running)
                state.elapsedTime = message.getLong(json_elapsedTime)
                state.recipeID = message.getString(json_recipeID)
                val ingredientString = message.getString(json_ingredients)
                val _ingredients = JSONArray(ingredientString)
                for(i in 1.._ingredients.length()) {
                    val ingredient = _ingredients[i - 1] as JSONObject
                    val ingredient_type = ingredient.getString(json_ingredient_type)
                    val amount = ingredient.getDouble(json_ingredient_amount)
                    val unit = ingredient.getString(json_ingredient_unit)
                    val complete = ingredient.getBoolean(json_ingredient_complete)
                    state.ingredients.add(RRIngredient(ingredient_type, amount, unit, complete))
                }
                val _participants: JSONArray = message.getJSONArray(json_participants)
                for(i in 1.._participants.length()) {
                    val participant = _participants[i - 1] as JSONObject
                    val username = participant.getString(json_player)
                    val userID = participant.getInt(json_userID)
                    val progress = participant.getInt(json_progress)
                    val totalProgress = participant.getInt(json_participant_total_progress)
                    val RRparticipant = RRParticipant(username, userID, progress, totalProgress)
                    if(!state.participants.contains(RRparticipant))
                        state.participants.add(RRParticipant(username, userID, progress, totalProgress))
                }
                listener.update(state)
            }
            type_chrono -> {
                when(message.getString(json_chrono_command)) {
                    json_chrono_start -> {
                        val elapsedTime = message.getLong(json_elapsedTime)
                        state.startTime = SystemClock.elapsedRealtime() - elapsedTime
                        listener.startTimer(state.startTime)
                    }
                    json_chrono_pause -> {
                        val startTime = message.getLong(json_start_time)
                        state.elapsedTime = SystemClock.elapsedRealtime() - startTime
                        listener.pauseTimer()
                    }
                    json_chrono_reset -> {
                        state.elapsedTime = 0
                        listener.resetTimer()
                    }
                    else -> {
                        Log.d("RRModel", "Tried to handle invalid chrono command!")
                        Log.d("RRModel", message.toString())
                        listener.handleMessage("Received an invalid chrono command!")
                    }
                }
            }
            type_create -> {
                // We grab the creator's username from the server message
                val user = message.getString(json_player)
                if(user == Userinfo.username) {
                    // we know the creator is us!
                    isHost = true
                    state.participants.add(RRParticipant(Userinfo.username, 0, 0, state.ingredients.size))
                    requestRecipeData()
                }
            }
            type_complete_request -> {
                if(isHost) {
                    val username = message.getString(json_player)
                    val arr = playerStates[username] ?: return
                    val index = message.getInt(json_index)
                    if(!arr[index]) {
                        sendProgressMessage(username)
                        playerStates[username]?.set(index, true)
                        var result = true
                        playerStates[username]?.forEach{
                            result = result && it
                        }
                        if(result) {
                            sendVictoryMessage(username)
                        }
                    }
                }
            }
            type_progress_update -> {
                val username = message.getString(json_player)
                val progress = message.getInt(json_progress)
                state.participants.forEach{participant ->
                    if(participant.Username == username) {
                        participant.progress = progress
                        listener.update(state)
                        listener.handleMessage(username + " has completed " + progress + " steps!")
                        return
                    }
                }
            }
            type_victory -> {
                val username = message.getString(json_player)
                val out = username + " has won the race!"
                listener.handleMessage(out)
            }
            else -> {
                Log.d("RRModel", "Tried to handle invalid JSONObject!")
                Log.d("RRModel", message.toString())
                listener.handleMessage("Received an invalid JSONObject!")
            }
        }
    }

    private fun sendVictoryMessage(username: String) {
        val obj = JSONObject()
        obj.put(json_type, type_victory)
        obj.put(json_player, username)
        sendJSONObject(obj)
    }

    private fun sendProgressMessage(username: String) {
        state.participants.forEach{participant ->
            if(participant.Username == username) {
                participant.progress += 1
                val obj = JSONObject()
                obj.put(json_type, type_progress_update)
                obj.put(json_player, username)
                obj.put(json_progress, participant.progress)
                sendJSONObject(obj)
                return
            }
        }
    }

    override fun handleMessage(message: JSONArray) {
        val listener = listener ?: return
        Log.d("RRModel", "Tried to handle JSONArray!")
        Log.d("RRModel", message.toString())
        listener.handleMessage("Received a JSONArray!")
    }

    override fun sendData(data: RecipeRaceData) {
        val jsonParticipants = JSONArray()
        data.participants.forEach {participant ->
            val json_participant = JSONObject()
            json_participant.put(json_player, participant.Username)
            json_participant.put(json_userID, participant.UserID)
            json_participant.put(json_progress, participant.progress)
            json_participant.put(json_participant_total_progress, participant.totalProgress)
            jsonParticipants.put(json_participant)
        }
        val jsonIngredients = JSONArray()
        data.ingredients.forEach { ingredient ->
            jsonIngredients.put(ingredient.toJSONObject())
        }
        val obj = JSONObject()
        obj.put(json_participants, jsonParticipants)
        obj.put(json_running, data.running)
        obj.put(json_elapsedTime, data.elapsedTime)
        obj.put(json_recipeID, data.recipeID)
        obj.put(json_ingredients, data.ingredients)
        obj.put(json_type, type_data)
        sendJSONObject(obj)
    }

    private fun requestRecipeData() {
        if(state.recipeID == "null" || state.recipeID == "") return
        val url = recipe_request_url + state.recipeID
        volleyController.createJSONRequest(
            url,
            Request.Method.GET,
            "RecipeRace",
            null,
            {response->
                loadRecipeData(response)
            },
            null
        )
        val listener = listener ?: return
        listener.handleMessage("Loading recipe info...")
    }

    override fun loadRecipeData(data: JSONObject) {
        val stepsText = data.getString(json_recipe_steps)
        state.steps = stepsText
        val ingredients = data.getJSONArray(json_recipe_ingredients)
        for(i in 1..ingredients.length()) {
            val ingredient = ingredients[i - 1] as JSONObject
            val type = ingredient.getString(json_recipe_ingredient_name)
            val amount = ingredient.getDouble(json_recipe_ingredient_amount)
            val unit = ingredient.getString(json_recipe_ingredient_unit)
            state.ingredients.add(RRIngredient(type, amount, unit, false))
        }
        state.participants.forEach{participant ->
            participant.totalProgress = state.ingredients.size
            playerStates.put(participant.Username, Array(state.ingredients.size){false})
        }
        sendData(state)
        val listener = listener ?: return
        listener.update(state)
    }

    override fun completeIngredient(index: Int) {
        if(index < 0 || index >= state.ingredients.size) return
        state.ingredients[index].complete = true
        val obj = JSONObject()
        obj.put(json_type, type_complete_request)
        obj.put(json_player, Userinfo.username)
        obj.put(json_index, index)
        sendJSONObject(obj)
    }


    companion object RecipeRaceArguments {
        val url = Const.URL_VC5_WEBSOCKET

        const val json_message = "message"
        const val json_sender = "sender"
        const val json_player = "player"
        const val json_type = "type"

        const val json_index = "index"

        const val json_running = "runnning"
        const val json_elapsedTime = "elapsedTime"
        const val json_recipeID = "recipeID"
        const val json_ingredients = "ingredients"
        const val json_participants = "participants"
        const val json_userID = "u_id"
        const val json_progress = "progress"
        const val json_start_time = "startTime"

        const val json_participant_total_progress = "totalProgress"

        const val json_ingredient_type = "type"
        const val json_ingredient_unit = "unit"
        const val json_ingredient_amount = "amount"
        const val json_ingredient_complete = "complete"

        const val json_chrono_command = "cmd"
        const val json_chrono_start = "start"
        const val json_chrono_pause = "pause"
        const val json_chrono_reset = "reset"

        const val json_recipe_steps = "steps"
        const val json_recipe_ingredients = "ingredients"
        const val json_recipe_ingredient_name = "name"
        const val json_recipe_ingredient_amount = "amount"
        const val json_recipe_ingredient_unit = "unit"


        const val type_chat = "chat"
        const val type_info = "info"
        const val type_join = "join"
        const val type_leave = "leave"
        const val type_chrono = "chrono"
        const val type_create = "create"
        const val type_data = "data"
        const val type_complete_request = "cmp_req"
        const val type_progress_update = "prog_upd"
        const val type_victory = "victory"

        val recipe_request_url = Const.URL_VC5_RECIPES + "?recipeID="
    }
}