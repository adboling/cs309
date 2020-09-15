package jrising.myapplication.Cookbooks

import jrising.myapplication.net_utils.VolleyController
import org.json.JSONObject


/**
 * A class used by [CookbookModel] and [CookbookFragment] to transfer information back and forth
 * @constructor The method used to generate the CookbookItem
 * @param recipeID The [String] recipeID for the CookbookItem
 * @param recipeName The [String] name for the CookbookItem
 * @property recipeID The [String] recipeID for the CookbookItem
 * @property recipeName The [String] name for the CookbookItem
 */
data class CookbookItem(
    val recipeID: String,
    val recipeName: String)

/**
 * The interface used to define what the cookbook view needs to properly display the cookbook list
 */
interface ICookbookListener {
    /**
     * This method is called by the [ICookbookModel] to send the cookbook to the listener
     * @param cookbook [List]<[CookbookItem]> The list of cookbooks to send to the listener
     */
    fun displayCookbook(cookbook: List<CookbookItem>)
}

/**
 * The interface defining what the cookbook model should do.
 * This class is used by the [CookBookFragment] to handle data and all the logic
 */
interface ICookbookModel {
    /**
     * Method used to generate a request for cookbook data to the server through the [jrising.myapplication.net_utils.VolleyHandler]
     * @param cookbookID The ID for the cookbook to load
     */
    fun requestCookbook(cookbookID: String)

    /**
     * Method used to parse the response from the server into a data type the [jrising.myapplication.net_utils.VolleyHandler]
     * can use
     * @param response The response from the server to parse
     */
    fun loadCookbook(response: JSONObject)

    /**
     * Method used to send a request to the server through the [jrising.myapplication.net_utils.VolleyHandler] to
     * delete a given cookbook
     * @param cookbookID The ID for the cookbook to delete
     */
    fun removeCookbook(cookbookID: String)
}

class CookbookModel : ICookbookModel {
    init { VolleyController}

    override fun requestCookbook(cookbookID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadCookbook(response: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeCookbook(cookbookID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}