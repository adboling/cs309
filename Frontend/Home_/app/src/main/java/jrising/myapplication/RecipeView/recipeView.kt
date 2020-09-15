package jrising.myapplication.RecipeView


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import jrising.myapplication.MyTimeline.Fragment_createLog

import jrising.myapplication.R
import jrising.myapplication.RecipeComments.ViewRecipeCommentsFragment
import jrising.myapplication.app.AppController
import jrising.myapplication.app.EventBus
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyController
import org.json.JSONObject

import kotlinx.android.synthetic.main.fragment_recipe_view.*
import kotlinx.android.synthetic.main.homscreen_cards.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A [Fragment] subclass for displaying recipes
 *
 */
class recipeView : Fragment() {
    /**
     * The source [JSONObject] from the server
     */
    var source : JSONObject? = null
    /**
     * The ID for the recipe
     */
    var recipeID : String? = null
    /**
     * The name for the recipe
     */
    var recipeName : String? = null
    /**
     * A flag for the type of the recipe
     * "yummly" for yummly recipes
     * "user" for user created recipes
     */
    var type : String? = null
    var viewHandler : IRecipeViewHandler? = null
        private set
    var backToHome: Boolean =true

    /**
     * Override method generated by Android Studio
     * Creates the view during the [Fragment] lifecycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_view, container, false)
    }

    /**
     * Override method used during the [Fragment] lifecycle
     * Sets the onClick listener for the buttons
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(source != null) {
            Log.d("recipeView", "Source:" + source)
            recipeID = source?.optString("id")
            recipeName = source?.optString("name")
            if(recipeID == null) {
                recipeID = source?.getString("recipeID")
                recipeName = source?.getString("recipeName")
            }
        }
        rView_commentButton.setOnClickListener {
            Log.d("recipeView", "Comment button pressed")
            Log.d("recipeView", "RecipeSource: " + source.toString())
            Log.d("recipeView", "RecipeID: " + recipeID)
            val fragment = ViewRecipeCommentsFragment()
            if(recipeID != null) {
                fragment.init(recipeID)
                AppController.eventBus.switchMainFragment(fragment)
            } else if (source != null) {
                recipeID = source?.optString("id")
                recipeName = source?.optString("name")

                if (recipeID == null) {
                    recipeID = source?.getString("recipeID")
                    recipeName = source?.getString("recipeName")
                }
                if (recipeID != null) {
                    fragment.init(recipeID)
                    AppController.eventBus.switchMainFragment(fragment)
                }
            }
        }
        checkRole()
        rView_timelineButton.setOnClickListener {
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager?.beginTransaction()
            val Tfragment = Fragment_createLog()
            val args = Bundle()
            args.putString("recipeID", recipeID )
            args.putString("recipeName", recipeName)
            Tfragment.arguments = args
            fragmentTransaction?.replace(R.id.fragment_container, Tfragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }

        when (type) {
            "yummly", "user" -> {
                // We're going to do nothing here and do another check after this when statement
            }
            else -> {
                val source = source ?: return
                if (source.optString("type") != null) {
                    type = source.getString("type")
                } else {
                    var id = source.optString("id")
                    if (id != null) type = "yummmly"
                    else {
                        id = source.optString("recipeID")
                        if (id != null) type = "user"
                    }
                }
            }
        }
        when (type) {
            "user" -> viewHandler = UserRecipeViewHandler()
            "yummly" -> viewHandler = YummlyRecipeViewHandler()
        }
        val source = source
        if(source == null) {
            val viewHandler = viewHandler ?: return
            val recipeID = recipeID ?: return
            val url = viewHandler.getRequestUrl(recipeID)
            val req = JsonObjectRequest(url, null,
                Response.Listener { response ->
                    onRecipeReceived(response)
                }, Response.ErrorListener { error ->
                    AppController.volleyController.handleError(error, "RecipeView")
                })
            AppController.volleyController.addToRequestQueue(req)
        } else {
            onRecipeReceived(source)
        }
    }
    private fun checkRole(){
       if(Userinfo.role == "guest"){

                rView_commentButton.visibility = View.INVISIBLE
                rView_timelineButton.visibility = View.INVISIBLE
                backToHome = false



        }
    }



    private fun onRecipeReceived(source : JSONObject) {
        val viewHandler = viewHandler ?: return
        val view = view ?: return
        switchView(viewHandler.layoutID)
        val context = context ?: return
        viewHandler.updateUI(view, source, context)
        recipeName = viewHandler.recipeName
    }

    private fun switchView(id : Int) {
        rView_container.removeAllViews()
        val view = layoutInflater.inflate(id, rView_container, false)
        rView_container.addView(view)
    }

}
