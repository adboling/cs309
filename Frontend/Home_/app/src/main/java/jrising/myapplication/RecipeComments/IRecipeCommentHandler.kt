package jrising.myapplication.RecipeComments

import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * The interface used to define a contract for handling RecipeCommments
 * The interface provides a layoutID, refreshURL, a method for refreshing comments, and a method to post a comment
 */
interface IRecipeCommentHandler {
    /**
     * The LayoutID in [R] for the comment view
     */
    val LayoutID : Int

    /**
     * A method to provide a URL to refresh comments for a given recipeID
     * @param recipeID The [String] recipeID to refresh comments for
     */
    fun getRefreshUrl(recipeID : String) : String

    /**
     * A method to refresh the comments in a given view for a given recipe
     * @param recipeID The [String] recipeID to refresh comments for
     * @param view The [View] to display to
     */
    fun refreshComments(recipeID: String, view : View)

    /**
     * A method to send a request to the server to post a given comment
     * @param recipeID The [String] recipeID to post a comment on
     * @param content The [String] message for the comment
     * @param viwe The source [View] the for input
     */
    fun postComment(recipeID: String, content: String, view: View)
}

/**
 * A class to handle displaying comments for a the [ViewRecipeCommentsFragment]
 */
class BasicRecipeCommmentHandler : IRecipeCommentHandler {
    override val LayoutID = 0
    override fun getRefreshUrl(recipeID: String) : String {
        return Const.URL_VC5_COMMENT + "?recipeID=" + recipeID
    }

    override fun refreshComments(recipeID: String, view: View) {
        val url = getRefreshUrl(recipeID)
        val req = JsonArrayRequest(url,
            Response.Listener{ response->
                populateComments(view, response)
            }, Response.ErrorListener { error ->
                AppController.volleyController.handleError(error, "rCommentRefresh")
            })
        AppController.volleyController.addToRequestQueue(req)
    }

    private fun populateComments(view: View, source: JSONArray) {
        Log.d("rComment", "Source: " + source.toString())
        var out = ""
        for(i in 1..source.length()) {
            val obj : JSONObject = source.getJSONObject(i - 1)
            out += obj.getString("comment") + "\n"
        }
        val commentView : TextView = view.findViewById(R.id.rcomment_debug_comment)
        commentView.text = out
    }

    override fun postComment(recipeID: String, content: String, view: View) {
        val comment = JSONObject()
        comment.put("recipeID", recipeID)
        comment.put("userID", Userinfo.u_id)
        comment.put("comment", content)
        val url = Const.URL_VC5_COMMENT
        val req = JsonObjectRequest(url, comment,
            Response.Listener {
                refreshComments(recipeID, view)
            }, Response.ErrorListener {error ->
                AppController.volleyController.handleError(error, "rCommentPost")
            })
        AppController.volleyController.addToRequestQueue(req)
    }
}