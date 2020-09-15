package jrising.myapplication.RecipeCreate

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import jrising.myapplication.app.AppController
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * The interface to define what a recipe creation handler needs to do
 */
interface IRecipeCreateHandler {
    /**
     * The [JSONObject] representation for the recipe
     */
    val recipe: JSONObject

    /**
     * The method to send a request to the server to post the recipe
     * Performs checks to ensure the recipe is valid, and then sends a POST request
     */
    fun submitRecipe()

    /**
     * Creates an ingredient with the given name, unit, and amound, and then returns it
     * @param name The name of the ingredient
     * @param unit The unit type for the ingredient
     * @param amount The amount for the ingredient
     * @return The [JSONObject] representation for the ingredient
     */
    fun addIngredient(name: String, unit: String, amount: Double) : JSONObject

    /**
     * Adds the given name to the recipe
     * @param name The [String] Title for the recipe
     */
    fun addName(name: String)

    /**
     * Adds the given description to the recipe
     * @param desc The [String] description for the recipe
     */
    fun addDescription(desc: String)

    /**
     * Adds the given steps to the recipe
     * @param steps The [String] steps for the recipe
     */
    fun addSteps(steps: String)

    /**
     * Adds the number of servings to the recipe
     * @param numberServings The [Int] number of servings
     */
    fun addNumberOfServings(numberServings: Int)

    /**
     * Adds the preptime to the recipe
     * @param prepTime The [Double] prep time for the recipe
     */
    fun addPrepTime(prepTime: Double)

    /**
     * Adds the total time to the recipe
     * @param totalTime The [Double] total time to prepare the recipe
     */
    fun addTotalTime(totalTime: Double)

    /**
     * Adds the given tags to the recipe
     * @param tags The [String] tags for the recipe
     */
    fun addTags(tags: String)

    /**
     * Converts the given image to base 64, and then attaches it to the recipe as the main image
     * @param path The [Uri] filepath for the image
     */
    fun addImageFromPath(path: Uri?)
    /**
     * Converts the given image to base 64, and then attaches it to the recipe as a thumbnail
     * @param path The [Uri] filepath for the image
     */
    fun addThumbnailFromPath(path: Uri?)
    /**
     * Attaches the given base 64 JPEG encoding of an image to the recipe as the main image
     * @param b64 The [String] base 64 JPEG encoding of the image
     */
    fun addImageFromBase64(b64: String)
    /**
     * Attaches the given base 64 JPEG encoding of an image to the recipe as a thumbnail
     * @param b64 The [String] base 64 JPEG encoding of the image
     */
    fun addThumbnailFromBase64(b64: String)
    /**
     * Converts the given bitmap to base 64, then attaches it to the recipe as the main image
     * @param bitmap The [bitmap] image to convert
     */
    fun addImage(bitmap: Bitmap)
}

/**
 * The class used to handle creating User recipes
 * Keeps a separate [JSONArray] for the ingredients
 */
class BasicRecipeCreateHandler : IRecipeCreateHandler {
    override val recipe: JSONObject = JSONObject()
    val ingredients = JSONArray()

    override fun submitRecipe() {
        recipe.put("ingredients", ingredients)
        recipe.put("userID", Userinfo.u_id)
        Log.d("BrCreate", "Recipe: " + recipe.toString())
        if(!recipe.has("recipeName")) return
        if(!recipe.has("description")) recipe.put("description", "no description given")
        if(!recipe.has("steps")) recipe.put("steps", "no steps given")
        val TAG = "RecipeCreate"
        val url = Const.URL_VC5_RECIPES
        val req = JsonObjectRequest(url, recipe,
            Response.Listener { response ->
                AppController.eventBus.recipeSelected(response.getString("recipeID"), response, "user")
            }, Response.ErrorListener {error ->
                AppController.volleyController.handleError(error, TAG)
            })
        AppController.volleyController.addToRequestQueue(req)
    }
    override fun addIngredient(name: String, unit: String, amount: Double) : JSONObject {
        val ingredient = JSONObject()
        ingredient.put("name", name)
        ingredient.put("unit", unit)
        ingredient.put("amount", amount)
        ingredients.put(ingredient)
        return ingredient
    }
    override fun addName(name: String) {
        recipe.put("recipeName", name)
    }
    override fun addDescription(desc: String) {
        recipe.put("description", desc)
    }
    override fun addSteps(steps: String) {
        recipe.put("steps", steps)
    }
    override fun addNumberOfServings(numberServings: Int) {
        recipe.put("numberServings", numberServings)
    }
    override fun addPrepTime(prepTime: Double) {
        recipe.put("prepTime", prepTime)
    }
    override fun addTotalTime(totalTime: Double) {
        recipe.put("totalTime", totalTime)
    }
    override fun addTags(tags: String) {
        recipe.put("tags", tags)
    }
    override fun addImageFromPath(path: Uri?) {
        Log.d("RecipeCreate", "Image Path:\n" + path.toString())
        val path = path
        if(path == null) {
            recipe.put("picture", "")
        } else {
            val b64 = AppController.instance.convertUriToBitmap(path)
            Log.d("RecipeCreate", "Image B64:\n" + b64)
            //recipe.put("picture", b64)
        }
    }
    override fun addThumbnailFromPath(path: Uri?) {
        if(path == null) {
            recipe.put("thumbnail", "")
        } else {
            val b64 = AppController.instance.convertUriToBitmap(path)
            recipe.put("thumbnail", b64)
        }
    }
    override fun addImageFromBase64(b64: String) {
        recipe.put("picture", b64)
    }
    override fun addThumbnailFromBase64(b64: String) {
        recipe.put("thumbnail", b64)
    }

    override fun addImage(bitmap: Bitmap) {
        val b64 = AppController.instance.convertBitmapToBase64(bitmap)
        //AppController.instance.longLog("ImageB64", b64)
        recipe.put("picture",b64)
    }
}
