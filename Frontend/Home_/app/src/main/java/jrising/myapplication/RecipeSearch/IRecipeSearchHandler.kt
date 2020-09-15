package jrising.myapplication.RecipeSearch

import android.os.Bundle
import android.util.Log
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import org.json.JSONArray
import org.json.JSONObject


data class searchArguments(val url: String, val tag: String, val type: String)
/**
 * The interface used by fragments designed to handle search recipes. Handles talking to to the volley controller and
 * turning the different search result formats into a single unified bundle parameter
 */
interface IRecipeSearchHandler {
    /**
     * The function used to convert the JSONObject response from the server to a bundle to pass to different recipe classes
     * Inputs:
     *  results: The Json object received from the server
     *  output: The converted bundle version of the results
     */
    fun getRecipeSearchResults(results : JSONObject) : Bundle
    /**
     * This function is identical to the above function, but takes the results as a JSONArray
     * We just pack this up in a JSONObject and pass it to the above function
     */
    fun getRecipeSearchResults(results : JSONArray) : Bundle {
        val obj = JSONObject()
        obj.put("matches", results)
        return getRecipeSearchResults(obj)
    }

    /**
     * The function used to create different search calls depending on the desired search type
     * Inputs:
     *  criteria: A bundle containing the type as a string, and a string array terms
     * Output: A bundle containing a string tag, and a url, to send a request to the volley controller
     */
    fun getSearchRequest(criteria : Bundle) : Bundle

    fun getSearchRequest(type: String, terms: Array<String>) : searchArguments
}



/**
 * A general class which will take different handlers in a hash map to determine which handler to use
 * Its basically a meta handler
 */
class GeneralSearchHandler : IRecipeSearchHandler {
    // THe map of handlers
    val handlers : HashMap<String, IRecipeSearchHandler> = HashMap()
    var yummlyHandler: YummlySearchHandler = YummlySearchHandler()
    var userHandler: UserRecipeSearchHandler = UserRecipeSearchHandler()
    var ingredientRecipeHandler: ingredientRecipeSearchHandler = ingredientRecipeSearchHandler()
    // The default handler to use when the given type is not contained in the map
    var defaultHandler : IRecipeSearchHandler = DefaultRecipeSearchHandler()
    init {
        handlers.put("yummly", yummlyHandler)
        handlers.put("user", userHandler)
        handlers.put("default", defaultHandler)
        handlers.put("ingredient", ingredientRecipeHandler)
    }
    fun resetHandlers() {
        handlers.clear()
        handlers.put("yummly", yummlyHandler)
        handlers.put("user", userHandler)
        handlers.put("default", defaultHandler)
        handlers.put("ingredient", ingredientRecipeHandler)
    }

    override fun getRecipeSearchResults(results: JSONObject): Bundle {
        // Look for the desired search type
        val type = results.optString("type", "")
        // Grab the specified search type, or use the default handler
        val handler = handlers[type]
        if( handler == null) {
            // Check if the results contains a field "criteria", this is unique to the yummly search results
            if (results.optJSONObject("criteria") != null) {
                // We're dealing with a yummly search
                return handlers["yummly"]!!.getRecipeSearchResults(results)
            } else {
                // Grab the field "matches"
                val matches = results.optJSONArray("matches")
                // Switch on matches
                return when(matches) {
                    // If matches did not exist, we return the default recipe handler
                    null -> defaultHandler.getRecipeSearchResults(results)
                    // If matches exists, we return the user recipe handler
                    else -> handlers["user"]!!.getRecipeSearchResults(matches)
                }
            }
        } else {
            // return the handler's result
            return handler.getRecipeSearchResults(results)
        }
    }

    override fun getSearchRequest(criteria: Bundle): Bundle {
        val type = criteria.getString("type")
        val handler = handlers[type] ?: defaultHandler
        return handler.getSearchRequest(criteria)
    }

    override fun getSearchRequest(type: String, terms: Array<String>) : searchArguments {
        val handler = handlers[type] ?: defaultHandler
        return handler.getSearchRequest(type, terms)
    }


}

/**
 * A default recipe search handler for when the desired type does not exist. Does nothing
 */
class DefaultRecipeSearchHandler : IRecipeSearchHandler {
    override fun getRecipeSearchResults(results: JSONObject): Bundle {
        val result = Bundle()
        return result
    }

    override fun getSearchRequest(criteria: Bundle): Bundle {
        val result = Bundle()
        result.putString("type", "default")
        return result
    }


    override fun getSearchRequest(type: String, terms: Array<String>) : searchArguments {
        return searchArguments("default","default","default")
    }
}

/**
 * A recipe search handler to handle yummly recipe searches
 */
open class YummlySearchHandler : IRecipeSearchHandler {
    override fun getRecipeSearchResults(results: JSONObject): Bundle {
        val matches = results.getJSONArray("matches")
        val result = Bundle()
        val newMatches : ArrayList<String> = ArrayList()
        for(i in 1..matches.length()) {
            val match = matches.get(i - 1) as JSONObject
            val id = match.getString("id")
            val name = match.getString("recipeName")
            val sourceName = "Yummly API"
            val author = match.getString("sourceDisplayName")
            val newMatch = JSONObject()
            newMatch.put("id", id)
            newMatch.put("name", name)
            newMatch.put("sourceName", sourceName)
            newMatch.put("author", author)
            newMatches.add(newMatch.toString())
        }
        result.putStringArray("matches", newMatches.toTypedArray())
        result.putString("type", "yummly")
        return result
    }

    override fun getSearchRequest(criteria: Bundle): Bundle {
        val result = Bundle()
        result.putString("tag", "YummlyRSearch")
        var url = Const.URL_VC5_SEARCH + "q="
        val terms = criteria.getStringArray("terms")
        terms.forEach {term ->
            url += term + "+"
        }
       result.putString("url", url)
        result.putString("type", "yummly")
        return result
    }


    override fun getSearchRequest(type: String, terms: Array<String>) : searchArguments {
        val tag = "YummlyRSearch"
        var url = Const.URL_VC5_SEARCH + "q="
        terms.forEach {term ->
            url += term + "+"
        }
        return searchArguments(url, tag, type)
    }
}

class UserRecipeSearchHandler : IRecipeSearchHandler {
    override fun getRecipeSearchResults(results: JSONObject): Bundle {
        val matches = results.getJSONArray("matches")
        val result = Bundle()
        val newMatches : ArrayList<String> = ArrayList()
        for(i in 1..matches.length()) {
            val match = matches.get(i - 1) as JSONObject
            val id = match.getString("recipeID")
            val name = match.getString("recipeName")
            val sourceName = "User Created"
            val author = match.getString("userID")
            val newMatch = JSONObject()
            newMatch.put("id", id)
            newMatch.put("name", name)
            newMatch.put("sourceName", sourceName)
            newMatch.put("author", author)
            newMatches.add(newMatch.toString())
        }
        result.putStringArray("matches", newMatches.toTypedArray())
        result.putString("type", "user")
        return result
    }

    override fun getRecipeSearchResults(results: JSONArray): Bundle {
        val result = Bundle()
        val newMatches : ArrayList<String> = ArrayList()
        for(i in 1..results.length()) {
            val match = results.get(i - 1) as JSONObject
            val id = match.getString("recipeID")
            val name = match.getString("recipeName")
            val sourceName = "User Created"
            val author = match.getString("userID")
            val newMatch = JSONObject()
            newMatch.put("id", id)
            newMatch.put("name", name)
            newMatch.put("sourceName", sourceName)
            newMatch.put("author", author)
            newMatches.add(newMatch.toString())
        }
        result.putStringArray("matches", newMatches.toTypedArray())
        result.putString("type", "user")
        return result
    }

    override fun getSearchRequest(criteria: Bundle): Bundle {
        val result = Bundle()
        result.putString("tag", "UserRSearch")
        var url = Const.URL_VC5_RECIPES + "/search?q="
        val terms = criteria.getStringArray("terms")
        terms.forEach {term ->
            url += term + "+"
        }
        result.putString("url", url)
        result.putString("type", "user")
        return result
    }

    override fun getSearchRequest(type: String, terms: Array<String>): searchArguments {
        val tag = "UserRSearch"
        var url = Const.URL_VC5_RECIPES + "/search?q="
        terms.forEach{term ->
            url += term + "+"
        }
        return searchArguments(url, tag, type)
    }
}

class ingredientRecipeSearchHandler : YummlySearchHandler() {
    override fun getSearchRequest(criteria: Bundle): Bundle {
        val terms = criteria.getStringArray("terms")
        var term =""
        terms.forEach{
            if(it != "")
                term += "$it+"
        }
        val url = Const.URL_VC5_PANTRY + "/search?userID=" + Userinfo.u_id + "&q=" + term
        val tag = "irSearch"
        val type = "ingredient"
        val bundle = Bundle()
        bundle.putString("url", url)
        bundle.putString("tag", tag)
        bundle.putString("type", type)
        return bundle
    }

    override fun getSearchRequest(type: String, terms: Array<String>): searchArguments {
        var term = ""
        terms.forEach {
            if(it != "")
                term += "$it+"
        }
        val url = Const.URL_VC5_PANTRY + "/search?userID=" + Userinfo.u_id + "&q=" + term
        val tag = "irSearch"
        return searchArguments(url, tag, type)
    }

    override fun getRecipeSearchResults(results: JSONObject): Bundle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}