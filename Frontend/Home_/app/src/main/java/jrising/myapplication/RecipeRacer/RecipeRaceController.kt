package jrising.myapplication.RecipeRacer

import org.json.JSONArray
import org.json.JSONObject

/**
 * A class used to decouple the RecipeRaceModel from the RecipeRaceFragment
 * It really just forwards commands
 * We should take it out
 */
class RecipeRaceController(sessionID: Int, recipeID: String)
{
    var model: IRecipeRaceModel = RecipeRaceModel(sessionID, recipeID)

    constructor(recipeID: String) : this(0, recipeID)
    constructor(sessionID: Int) : this(sessionID, "")
    constructor() : this(0, "")

    /**
     * A method to send a startTime command to the [RecipeRaceModel]
     */
    fun startTime() {
        model.startTime()
    }

    /**
     * A method to send a pauseTime command to the [RecipeRaceModel]
     */
    fun pauseTime() {
        model.pauseTime()
    }

    /**
     * A method to send a resetTime command to the [RecipeRaceModel]
     */
    fun resetTime() {
        model.resetTime()
    }

    /**
     * A method to send a connect command to the model
     */
    fun connect(sessionID: Int) {
        model.connect(sessionID)
    }

    /**
     * A method to send a disconnect command to the model
     */
    fun disconnect() {
        model.disconnect()
    }

    /**
     * A method used to set the sessionID of the model
     */
    fun setSessionID(session: Int) {
        model.setSessionId(session)
    }

    /**
     * A method used to send a message to the controller to forward to the server and other clients
     */
    fun sendMessage(message: String) {
        model.sendChat(message)
    }

    /**
     * A method to set the [IRecipeRaceListener] for the model
     */
    fun setListener(listener: IRecipeRaceListener) {
        model.listener= listener
    }

    /**
     * A method to complete an ingredient in the model
     */
    fun completeIngredient(index: Int) {
        model.completeIngredient(index)
    }
}