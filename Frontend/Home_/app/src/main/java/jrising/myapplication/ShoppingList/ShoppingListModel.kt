package jrising.myapplication.ShoppingList

import android.util.Log
import com.android.volley.Request
import jrising.myapplication.app.Ingredient
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONArray
import org.json.JSONObject

/**
 * The interface used to define the methods a shopping list view needs to be able to handle the data from the model
 */
interface IShoppingListListener {
    /**
     * The method used to have the view display a list of ingredients in the shopping list with the given [ShoppingListItem]s
     */
    fun displayShoppingList(shoppingList: List<ShoppingListItem>)
}

/**
 * The interface used to define the methods the ShoppingListModel needs to handle the data and requests from the fragment
 * The Model will send requests through the [jrising.myapplication.net_utils.VolleyController] to the server to load
 * the shopping list. It will also handle parsing the responses back, and then sending the data to the listener
 */
interface IShoppingListController {
    /**
     * The method used to send a request to the [jrising.myapplication.net_utils.VolleyController] to load the shopping list
     * Calls [loadShoppingList] when successful
     */
    fun requestShoppingList()

    /**
     * The method used to parse the response from the server into a list of ingredients to display as a shopping list.
     * Calls [IShoppingListListener.displayShoppingList] when successful
     * @param response The JSONObject response back from the server
     */
    fun loadShoppingList(response: JSONArray)

    fun loadShoppingList(response: String)

    /**
     * The method used to generate a request to add the selected [ShoppingListItem] to the list stored on the server
     * @param item the [ShoppingListItem] to add
     */
    fun addItem(item: Ingredient)

    /**
     * The method used to replace one [ShoppingListItem] with another
     * @param old The original [ShoppingListItem] to remove
     * @param new The new [ShoppingListItem] to add
     */
    fun editItem(old: ShoppingListItem, new: ShoppingListItem)

    /**
     * The method used to remove a [ShoppingListItem] from the server
     * @param item the [ShoppingListItem] to remove
     */
    fun removeItem(item: ShoppingListItem)

    /**
     * The method used to modify the status of a [ShoppingListItem] to the given status
     * @param item The [ShoppingListItem] to modify
     * @param complete The new [Boolean] status
     */
    fun editComplete(item: ShoppingListItem, complete: Boolean)
}

class ShoppingListModel(var listener: IShoppingListListener, var volleyHandler: VolleyHandler) : IShoppingListController {

    override fun addItem(item: Ingredient) {
        val url = Const.URL_VC5_SHOPPING_LIST + "/add?userID=" + Userinfo.u_id
        val method = Request.Method.POST
        val obj = JSONObject()
        obj.put(json_ingredient_name, item.type)
        obj.put(json_ingredient_amount, item.amount)
        obj.put(json_ingredient_unit, item.unit)
        val arr = JSONArray()
        arr.put(obj)
        volleyHandler.createJSONArrayRequest(url, method, TAG, arr, {  }, null)
    }

    override fun editItem(old: ShoppingListItem, new: ShoppingListItem) {
        val deleteUrl = Const.URL_VC5_SHOPPING_LIST + "/delete?entryID=" + old.id
        volleyHandler.createStringRequest(deleteUrl, Request.Method.DELETE, TAG, { }, null)
        addItem(new.ingredient)
    }

    override fun removeItem(item: ShoppingListItem) {
        val deleteUrl = Const.URL_VC5_SHOPPING_LIST + "/delete?entryID=" + item.id
        volleyHandler.createStringRequest(deleteUrl, Request.Method.DELETE, TAG, { requestShoppingList() }, null)
    }

    override fun requestShoppingList() {
        val url = Const.URL_VC5_SHOPPING_LIST + "/get?userID=" + Userinfo.u_id
        volleyHandler.createJSONArrayRequest(url, Request.Method.GET, TAG, null, { response -> loadShoppingList(response) }, null)
    }

    override fun loadShoppingList(response: String) {
        val arr = JSONArray(response)
        loadShoppingList(arr)
    }

    override fun loadShoppingList(response: JSONArray) {
        val result = ArrayList<ShoppingListItem>()
        for(i in 1..response.length()) {
            val it = response[i - 1] as? JSONObject ?: return
            val name = it.getString(json_ingredient_name)
            val amount = it.getDouble(json_ingredient_amount)
            val unit = it.getString(json_ingredient_unit)
            val ingredient = Ingredient(name, amount, unit)
            val id = it.getInt(json_entry_id)
            val complete = it.getInt(json_complete) != 0
            result.add(ShoppingListItem(complete, ingredient, id))
        }
        listener.displayShoppingList(result)
    }

    override fun editComplete(item: ShoppingListItem, complete: Boolean) {
        var url = Const.URL_VC5_SHOPPING_LIST + "/modifyComplete?complete=" + if(complete) 1 else 0
        url += "&entryID=" + item.id
        volleyHandler.createStringRequest(url, Request.Method.PUT, TAG, { requestShoppingList() }, null)
    }

    companion object {
        const val TAG = "ShoppingList"
        const val json_ingredient_name = "name"
        const val json_ingredient_amount = "amount"
        const val json_ingredient_unit = "unit"
        const val json_entry_id = "entryID"
        const val json_complete = "complete"
    }
}