package jrising.myapplication.Pantry


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.Ingredient
import kotlinx.android.synthetic.main.fragment_pantry.*
import kotlin.Double as Double1


/**
 * A [Fragment] subclass used to show a list of ingredients a user has on hand
 * Use the [PantryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PantryFragment : Fragment(), IPantryListener {
    // A bunch of wrapper fields for some views
    lateinit var typeIn: TextInputEditText
    lateinit var amountIn: TextInputEditText
    lateinit var unitIn: TextInputEditText
    lateinit var addButton: Button
    lateinit var typeGroup: TextInputLayout
    lateinit var amountGroup: TextInputLayout
    lateinit var unitGroup: TextInputLayout

    lateinit var ingredientRecycler: RecyclerView
    lateinit var llm: LinearLayoutManager
    lateinit var adapter: PantryIngredientAdapter

    var volleyHandler = AppController.volleyController

    var model: IPantryModel = PantryModel(this, volleyHandler)

    /**
     * A flag marking whether an ingredient is currently being added
     */
    var addingIngredient = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pantry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()
        model.requestPantry()
    }

    override fun displayPantry(pantry: List<PantryItem>) {
        adapter.updateIngredients(pantry)
    }

    /**
     * A function used to initialize the fields created to allow abstraction of parts of the layout
     */
    private fun initializeFields() {
        typeIn = pantry_type_in
        amountIn = pantry_amount_in
        unitIn = pantry_unit_in

        typeGroup = pantry_type_tiet
        amountGroup = pantry_amount_tiet
        unitGroup = pantry_unit_tiet
        addButton = pantry_add
        addButton.setOnClickListener {
            onAddButtonPressed()
        }
        ingredientRecycler = pantry_list
        llm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        ingredientRecycler.layoutManager = llm
        adapter = PantryIngredientAdapter(ArrayList(), model)
        ingredientRecycler.adapter = adapter
    }

    /**
     * A function called whenever the "Add Ingredient" button is pressed
     * Checks if we're currently adding an ingredient.
     * If we're adding an ingredient, it will check validity, then hide the inputs and add the ingredient
     * If we're not adding an ingredient, it will show the inputs
     */
    fun onAddButtonPressed() {
        if(addingIngredient) {
            val newIngredient = createIngredient(
                typeIn.text.toString(),
                amountIn.text.toString(),
                unitIn.text.toString()
            )
            if(newIngredient == null) {
                Snackbar.make(addButton, "Invalid Ingredient!", Snackbar.LENGTH_LONG).show()
            } else {
                adapter.addIngredient(newIngredient)
                setVisibilities(View.GONE)
                addingIngredient = false
            }
        } else {
            setVisibilities(View.VISIBLE)
            addingIngredient = true
        }
    }

    /**
     * A helper method to set the visibility of three view groups which handle input that we don't want to view all the time.
     * Public for use during testing.
     */
    fun setVisibilities(visibility: Int) {
        if(visibility == View.VISIBLE || visibility == View.GONE || visibility == View.INVISIBLE) {
            typeGroup.visibility = visibility
            amountGroup.visibility = visibility
            unitGroup.visibility = visibility
        }
    }

    /**
     * A function called to create an ingredient with the given parameters
     * If the input is invalid, returns null
     */
    private fun createIngredient(type: String, amount: String, unit: String): Ingredient? {
        val newType: String = type
        if(type == "") return null
        val newAmount = amount.toDoubleOrNull()
        val newUnit = if(unit == "") null else unit
        return Ingredient(newType, newAmount, newUnit)
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         *
         * @return A new instance of fragment PantryFragment.
         */
        @JvmStatic
        fun newInstance() =
            PantryFragment().apply {
                arguments = Bundle().apply {

                }
            }
        const val TAG = "Pantry"
    }
}
