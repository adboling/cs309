package jrising.myapplication.Pantry

import android.util.Log
import com.android.volley.Request
import jrising.myapplication.app.AppController
import jrising.myapplication.app.Ingredient
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONArray
import org.json.JSONObject

/**
 * The interface used to define the methods the cookbook view has to implement
 */
interface IPantryListener {
    /**
     * The method used to update the view's display of the pantry
     * @param pantry The list of ingredients contained in the pantry to list
     */
    fun displayPantry(pantry: List<PantryItem>)
}

/**
 * The interface used to represent the required methods for the PantryModel
 * The PantryModel handles sending messages through the [jrising.myapplication.net_utils.VolleyController] to the server
 * as well as handling responses in return
 */
interface IPantryModel : IIngredientListListener {
    /**
     * The method used to generate a request using [jrising.myapplication.net_utils.VolleyController] to load the pantry
     * list from the server. Calls [loadPantry] with the response
     */
    fun requestPantry()
    /**
     * The method used to load a list of ingredients after receiving the response back from the server
     * Calls [IPantryListener.displayPantry] when successful
     * @param response The [JSONArray] response received from the server
     */
    fun loadPantry(response: JSONArray)
}

class PantryModel(var listener: IPantryListener, var volleyHandler: VolleyHandler) : IPantryModel {

    override fun requestPantry() {
        val url = Const.URL_VC5_PANTRY + "?userID=" + Userinfo.u_id
        Log.d(TAG, "URL: $url")
        volleyHandler.createJSONArrayRequest(url, Request.Method.GET, TAG, null, { response -> loadPantry(response) }, null)
        Log.d(TAG, "Pantry requested!")
    }

    override fun loadPantry(response: JSONArray) {
        val result = ArrayList<PantryItem>()
        for(i in 1..response.length()) {
            val item = response[i - 1] as? JSONObject ?: return
            val ingredientString = item.getString(json_ingredient)
            val key = item.getInt(json_key)
            val ingredient = Ingredient(ingredientString, null, null)
            result.add(PantryItem(ingredient, key))
        }
        listener.displayPantry(result)
    }

    override fun onIngredientChanged(old: PantryItem, new: PantryItem) {
        val del_url = Const.URL_VC5_PANTRY + "/del?pantryID=" + old.id
        val new_url = Const.URL_VC5_PANTRY
        val obj = JSONObject()
        obj.put(json_ingredient, new.ingredient.type)
        obj.put(json_uid, Userinfo.u_id)
        volleyHandler.createStringRequest(del_url, Request.Method.DELETE, TAG, {response ->
            Log.d(TAG, "Response: " + response)
            if(response == "\"deleted\"") {
                volleyHandler.createJSONRequest(new_url, Request.Method.POST, TAG, obj, { requestPantry() }, null)
            }
        }, null)
    }

    override fun onIngredientRemoved(item: PantryItem) {
        val key: Int = item.id
        val url = Const.URL_VC5_PANTRY + "/del?pantryID="+key
        volleyHandler.createStringRequest(url, Request.Method.DELETE, TAG, {response -> Log.d(TAG, response) ; if(response == "\"deleted\"") Log.d(TAG, "Ingredient removed.") ; requestPantry()}, null)
        Log.d(TAG, "Ingredient removed: " + item.ingredient.toString())
    }

    override fun onIngredientAdded(item: PantryItem) {
        val obj = JSONObject()
        obj.put(json_uid, Userinfo.u_id)
        val str = item.ingredient.type
        obj.put(json_ingredient, str)
        Log.d(TAG, "JSONObject Out: $obj")
        val url = Const.URL_VC5_PANTRY
        volleyHandler.createJSONRequest(url, Request.Method.POST, TAG, obj, { requestPantry() }, null)
        Log.d(TAG, "Ingredient added: " + item.ingredient.toString())
    }
    companion object {
        const val TAG: String = "PantryModel"

        const val json_ingredient = "ingredient"
        const val json_uid = "userID"
        const val json_key = "id"
    }
}