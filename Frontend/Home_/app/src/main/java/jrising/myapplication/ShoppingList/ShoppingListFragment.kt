package jrising.myapplication.ShoppingList


import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.Ingredient
import kotlinx.android.synthetic.main.fragment_shopping_list.*
import kotlinx.android.synthetic.main.shopping_list_item.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A [Fragment] implementation which displays a list of ingredients the user wishes to purchase
 * Use the [ShoppingListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ShoppingListFragment : Fragment(), ShoppinglistItemSelectedListener, IShoppingListListener {
    lateinit var title: TextView
    lateinit var list: RecyclerView
    lateinit var nameGroup: ViewGroup
    lateinit var nameField: TextInputEditText
    lateinit var amountGroup: ViewGroup
    lateinit var amountField: TextInputEditText
    lateinit var unitGroup: ViewGroup
    lateinit var unitField: TextInputEditText
    lateinit var addIngredientButton: Button

    lateinit var adapter: ShoppinglistAdapter
    lateinit var controller: IShoppingListController

    var items: MutableList<ShoppingListItem> = ArrayList()

    var addingIngredient = false




    override fun onCreate(savedInstanceState: Bundle?) {
        controller = ShoppingListModel(this, AppController.volleyController)
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    fun initViews() {
        title = shoppingList_title
        list = shoppingList_list
        nameGroup = shoppingList_nameGroup
        nameField = shoppingList_nameField
        amountGroup = shoppingList_amountGroup
        amountField = shoppingList_amountField
        unitGroup = shoppingList_unitGroup
        unitField = shoppingList_unitField
        addIngredientButton = shoppingList_addIngredient


        addIngredientButton.setOnClickListener{ addIngredientPressed() }
        list.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        adapter = ShoppinglistAdapter(items, this)
        list.adapter = adapter

        controller.requestShoppingList()

        title.setOnLongClickListener { controller.requestShoppingList(); true }
    }

    private fun addIngredientPressed() {
        if(addingIngredient) {
            val nameText = nameField.text
            val amountText = amountField.text
            val unitText = unitField.text
            if(nameText == null || nameText.toString() == "") return invalidInput()
            val name = nameText.toString()
            val amount = amountText.toString().toDoubleOrNull() ?: return invalidInput()
            if(unitText == null || unitText.toString() == "") return invalidInput()
            val unit = unitText.toString()
            controller.addItem(Ingredient(name, amount, unit))
            nameGroup.visibility = View.GONE
            amountGroup.visibility = View.GONE
            unitGroup.visibility = View.GONE
            addingIngredient = false
        } else {
            nameGroup.visibility = View.VISIBLE
            amountGroup.visibility = View.VISIBLE
            unitGroup.visibility = View.VISIBLE
            addingIngredient = true
        }
    }

    private fun invalidInput() {
        Snackbar.make(addIngredientButton, "Invalid Input!", Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun displayShoppingList(shoppingList: List<ShoppingListItem>) {
        items = shoppingList.toMutableList()
        adapter.items = items
        adapter.notifyDataSetChanged()
    }

    override fun itemComplete(item: ShoppingListItem, complete: Boolean) {
        controller.editComplete(item, complete)
    }

    override fun itemEdited(old: ShoppingListItem, new: ShoppingListItem) {
        controller.editItem(old, new)
    }

    override fun itemRemoved(item: ShoppingListItem) {
        controller.removeItem(item)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShoppingListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ShoppingListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
