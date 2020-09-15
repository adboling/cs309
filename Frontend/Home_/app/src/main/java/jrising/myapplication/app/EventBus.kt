package jrising.myapplication.app

import android.content.Context
import android.support.v4.app.Fragment
import jrising.myapplication.RecipeView.recipeView
import org.json.JSONArray
import org.json.JSONObject

/**
 * An interface defining what a timeline handler has to do
 */
interface timelineHandler{
    /**
     * A method to pass a JSON response to the listener
     * @param response The [JSONObject] response from the server
     */
    fun handleTimelineResults(response: JSONArray, isOwner: Boolean)
}

/**
 * An interface defining the methods the mainFragmentContainer requires
 */
interface mainFragmentContainer {
    /**
     * A function to switch the main container to a different fragment
     * @param fragment The [Fragment] to switch to
     */
    fun handleFragmentSwitch(fragment : Fragment)
}
interface createLogsHandler{
    fun handleLogSubmition()
}

interface homescreenHandler{
    fun handleTheHomescreen(jsonArray: JSONArray)
}

/**
 * A central message bus for different methods to communicate
 */
class EventBus {
    /**
     * The container for the main fragment
     */
     var mainFragmentContainer : mainFragmentContainer? = null

    /**
     * A method to set the main fragment container
     * @param handler The [mainFragmentContainer] to handle the switches
     */
    fun registerMainFragmentContainer(handler: mainFragmentContainer) {
        mainFragmentContainer = handler
    }

    /**
     * A function used to switch the main fragment from some other class
     * @param fragment The [Fragment] to switch to
     */
    fun switchMainFragment(fragment : Fragment) {
        mainFragmentContainer?.handleFragmentSwitch(fragment)
    }

    /**
     * A function called whenever a recipe is selected in the recipe search fragment
     * @param id The RecipeID for the recipe
     * @param source A [JSONObject] with some information for the fragment to display
     * @param type The type of the recipe: User or Yummly
     */
    @Deprecated("Unused argument: source.", ReplaceWith("recipeSelected(id, type)"))
    fun recipeSelected(id : String, source : JSONObject, type : String) {
        val fragment = recipeView()
        fragment.recipeID = id
        fragment.type = type
        switchMainFragment(fragment)
    }
    /**
     * A function called whenever a recipe is selected in the recipe search fragment
     * @param id The RecipeID for the recipe
     * @param type The type of the recipe: User or Yummly
     */
    fun recipeSelected(id: String, type: String) {
        val fragment = recipeView()
        fragment.recipeID = id
        fragment.type = type
        switchMainFragment(fragment)
    }

    /**
     * to be used with recycler view, populates items in the timeline using the JSONarray
     */
    fun handleTimelineResults(response: JSONArray, handler: timelineHandler , isOwner: Boolean){
       handler.handleTimelineResults(response, isOwner)

    }

    /**
     * goes to a function that will move to the timeline fragment
     */
    fun handleLogSubmission(handler: createLogsHandler){
        handler.handleLogSubmition()
    }

    /**
     * points to a function that will populate the home screen
     */
    fun handleTheHomescreen(response: JSONArray, handler: homescreenHandler){
        handler.handleTheHomescreen(response)
    }


}