package jrising.myapplication.RecipeView


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jrising.myapplication.R
import org.json.JSONObject

import kotlinx.android.synthetic.main.fragment_view_ingredient.*

/**
 * A simple [Fragment] subclass to display information about an ingredient.
 * Should be replaced using a RecyclerView
 *
 */
class IngredientFragment : Fragment() {

    /**
     * A [JSONObject] containing the name, amount, and unit for the recipe
     * "name" is for the name,
     * "unit" is for the unit,
     * "amount" is for the amount
     */
    var source : JSONObject? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_ingredient, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val source = source
        val name = source?.getString("name")
        val unit = source?.getString("unit")
        val amount = source?.getDouble("amount")
        val ingredient = "name: " + name + ", amount: " + amount + ", unit: " + unit
        rIngredient_text.text = ingredient
    }
}
