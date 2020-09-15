package jrising.myapplication.RecipeView

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.view.View
import android.widget.TextView
import jrising.myapplication.R
import org.json.JSONObject
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.NetworkImageView
import com.squareup.picasso.Picasso
import jrising.myapplication.RecipeRacer.RecipeRacerChooser
import jrising.myapplication.app.AppController
import jrising.myapplication.net_utils.Const

/**
 * An interface to abstract away the updating of UI from the recipeView fragment
 */
interface IRecipeViewHandler {
    var recipeID: String?
    var recipeName: String?
    /**
     * The ID for the layout used by the specific layout
     */
    val layoutID : Int
    /**
     * The function used to get the request url to get the recipe
     * @param recipeID The [String] ID for the recipe
     * @return The [String] URL to send a request to the server
     */
    fun getRequestUrl(recipeID : String) : String
    /**
     * The function used to populate the given layout, takes the view and the source JSON as arguments
     * @param view The [View] to display the results on
     * @param source The [JSONObject] source
     * @param context The [Context] for the fragment
     */
    fun updateUI(view: View, source: JSONObject, context: Context)
}

/**
 * The subclass used to show recipes from Yummly
 */
class YummlyRecipeViewHandler : IRecipeViewHandler {

    override var recipeID: String? = null
    override var recipeName: String? = null
    // We're using the yummly_recipe_view for the layout
    override val layoutID = R.layout.yummly_recipe_view

    override fun getRequestUrl(recipeID: String) : String {
        return Const.URL_VC5_RECIPE + recipeID
    }

    override fun updateUI(view: View, source: JSONObject, context: Context) {
        // Grab all the fields we need to populate
        val title : TextView = view.findViewById(R.id.yummly_title)
        val numberOfServings : TextView = view.findViewById(R.id.yummly_numberServings)
        val totalTime : TextView = view.findViewById(R.id.yummly_totalTime)
        val sourceView : TextView = view.findViewById(R.id.yummly_source)
        val ingredientsView : TextView = view.findViewById(R.id.yummly_ingredients)

        // Now we're going to populate them all
        recipeID = source.getString("id")
        recipeName = source.getString("name")
        title.text = recipeName

        val servingsText = context.getString(R.string.rView_numberServings) +
                source.getInt("numberOfServings")
        numberOfServings.text = servingsText

        val timeText = context.getString(R.string.rView_totalTime) + source.getString("totalTimeInSeconds")
        totalTime.text = timeText

        val sourceObject = source.getJSONObject("source")
        val sourceText = sourceObject.getString("sourceRecipeUrl")
        sourceView.text = sourceText

        val ingredientList = source.getJSONArray("ingredientLines")
        var ingredientText = "Ingredients:\n"
        for(i in 1..ingredientList.length()) {
            ingredientText += ingredientList.getString(i - 1) + "\n"
        }
        ingredientsView.text = ingredientText
    }
}

/**
 * The subclass used to show user created recipes
 */
class UserRecipeViewHandler : IRecipeViewHandler {
    override val layoutID = R.layout.user_recipe_view
    override var recipeID: String? = null
    override var recipeName: String? = null

    override fun getRequestUrl(recipeID: String): String {
        return Const.URL_VC5_RECIPES + "?recipeID=" + recipeID
    }


    override fun updateUI(view: View, source: JSONObject, context : Context) {
        // Grab all the fields
        val title : TextView = view.findViewById(R.id.userView_title)
        val description : TextView = view.findViewById(R.id.userView_description)
        val stepView : TextView = view.findViewById(R.id.userView_stepText)
        val dateView : TextView = view.findViewById(R.id.userView_date)
        val creatorView : TextView = view.findViewById(R.id.userView_creator)
        val servingsView : TextView = view.findViewById(R.id.userView_numberServings)
        val totalTimeView : TextView = view.findViewById(R.id.userView_totalTime)
        val prepTimeView : TextView = view.findViewById(R.id.userView_prepTime)
        val cookTimeView : TextView = view.findViewById(R.id.userView_cookTime)
        val ingredientsView : TextView = view.findViewById(R.id.rUserView_ingredients)
        val pictureContainer: ImageView = view.findViewById(R.id.userView_picture)
        val raceStart: Button = view.findViewById(R.id.userView_startRace)

        Log.d("UserRView", source.toString())

        recipeID = source.getString("recipeID")
        recipeName = source.getString("recipeName")

        title.text = recipeName
        description.text = source.getString("description")
        stepView.text = source.getString("steps")

        val timeString = source.getString("time")
        val dateText = context.getString(R.string.rView_date) + " " + timeString
        dateView.text = dateText

        val creatorText = context.getString(R.string.rView_creator) + " " + source.optString("userID", "User")
        creatorView.text = creatorText

        val servingsText = context.getString(R.string.rView_numberServings) + " " + source.optString("numberOfServings", "Not given")
        servingsView.text = servingsText

        val totalTimeText = context.getString(R.string.rView_totalTime) + " " + source.optString("totalTime", "Not given")
        totalTimeView.text = totalTimeText

        val prepTimeText = context.getString(R.string.rView_prepTime) + " " + source.optString("prepTime", "Not given")
        prepTimeView.text = prepTimeText

        val cookTimeText = context.getString(R.string.rView_cookTime) + " " + source.optString("cookTime", "Not given")
        cookTimeView.text = cookTimeText

        var ingredientText = ""
        val ingredients = source.getJSONArray("ingredients")
        for(i in 1..ingredients.length()) {
            val ingredient = ingredients.get(i - 1) as JSONObject
            val ingredientString = "Ingredient: " + ingredient.getString("name") + " Amount: " +
                    ingredient.getDouble("amount") + " Unit: " + ingredient.getString("unit")
            ingredientText += ingredientString + "\n"
        }
        ingredientsView.text = ingredientText

        val recipeID = recipeID
        if(recipeID != null) {
            raceStart.setOnClickListener {
                startRecipeRace(recipeID)
            }
        }

        var url = source.optString("picture")
        if(url == null || url == "") return
        url = url.replace("\\","")
        //Picasso.with(context).load(url).into(pictureContainer)
        AppController.volleyController.requestImage(pictureContainer, url)
    }

    private fun startRecipeRace(recipeID: String) {
        val fragment = RecipeRacerChooser()
        fragment.recipeID = recipeID
        AppController.eventBus.switchMainFragment(fragment)
    }
}