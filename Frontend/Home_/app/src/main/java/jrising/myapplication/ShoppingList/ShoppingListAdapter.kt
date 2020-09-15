package jrising.myapplication.ShoppingList

import android.content.res.Resources
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import jrising.myapplication.R
import jrising.myapplication.app.Ingredient

interface ShoppinglistItemSelectedListener {
    fun itemEdited(old: ShoppingListItem, new: ShoppingListItem)
    fun itemRemoved(item: ShoppingListItem)
    fun itemComplete(item: ShoppingListItem, complete: Boolean)
}

data class ShoppingListItem(
    val complete: Boolean,
    val ingredient: Ingredient,
    val id: Int
    )

class ShoppinglistAdapter(var items: List<ShoppingListItem>, var listener: ShoppinglistItemSelectedListener) : RecyclerView.Adapter<ShoppingListItemHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_list_item, parent, false)
        return ShoppingListItemHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ShoppingListItemHolder, position: Int) {
        val item = items[position]
        holder.item = item
        holder.reset()
    }
}

class ShoppingListItemHolder(var v: View, var listener: ShoppinglistItemSelectedListener): RecyclerView.ViewHolder(v) {
    val textView: TextView = v.findViewById(R.id.shoppingListItem_textView)
    val completeButton: Button = v.findViewById(R.id.shoppingListItem_completeButton)
    val editButton: Button = v.findViewById(R.id.shoppingListItem_editButton)
    val removeButton: Button = v.findViewById(R.id.shoppingListItem_removeButton)
    val saveButton: Button = v.findViewById(R.id.shoppingListItem_saveButton)
    val cancelButton: Button = v.findViewById(R.id.shoppingListItem_cancelButton)

    val ingredientNameField: TextInputEditText = v.findViewById(R.id.shoppingListItem_ingredientNameField)
    val ingredientAmountField: TextInputEditText = v.findViewById(R.id.shoppingListItem_ingredientAmountField)
    val ingredientUnitField: TextInputEditText = v.findViewById(R.id.shoppingListItem_ingredientUnitField)


    val ingredientNameGroup: ViewGroup = v.findViewById(R.id.shoppingListItem_ingredientNameGroup)
    val ingredientAmountGroup: ViewGroup = v.findViewById(R.id.shoppingListItem_ingredientAmountGroup)
    val ingredientUnitGroup: ViewGroup = v.findViewById(R.id.shoppingListItem_ingredientUnitGroup)

    var expanded = false

    lateinit var item: ShoppingListItem

    init {
        v.setOnLongClickListener { onLongClick(); true }
        editButton.setOnClickListener { onEditClicked() }
        removeButton.setOnClickListener { onRemoveClicked() }
        cancelButton.setOnClickListener { onCancelClicked() }
        saveButton.setOnClickListener { onSaveClicked() }
        completeButton.setOnClickListener { onCompleteClicked() }
    }

    fun reset() {
        resetVisibilites()
        ingredientNameField.text = Editable.Factory().newEditable(item.ingredient.type)
        ingredientAmountField.text = Editable.Factory().newEditable(item.ingredient.amount.toString())
        ingredientUnitField.text = Editable.Factory().newEditable(item.ingredient.unit)
        textView.text = item.ingredient.toString()
        completeButton.text = if(item.complete) "Undo" else "Complete"
    }

    private fun setVisibilities(views: Array<View>, visibility: Int) {
        if(visibility != View.VISIBLE && visibility != View.INVISIBLE && visibility != View.GONE) return
        views.forEach {view ->
            view.visibility = visibility
        }
    }

    fun resetVisibilites() {
        setVisibilities(arrayOf(textView, completeButton), View.VISIBLE)
        setVisibilities(arrayOf(editButton, removeButton, saveButton, cancelButton, ingredientNameGroup, ingredientAmountGroup, ingredientUnitGroup), View.GONE)
        expanded = false
    }

    fun onLongClick() {
        expanded = if(expanded) {
            setVisibilities(arrayOf(textView, completeButton), View.VISIBLE)
            setVisibilities(arrayOf(editButton, removeButton, saveButton, cancelButton, ingredientNameGroup, ingredientAmountGroup, ingredientUnitGroup), View.GONE)
            false
        } else {
            setVisibilities(arrayOf(textView, editButton, removeButton), View.VISIBLE)
            setVisibilities(arrayOf(saveButton, cancelButton, ingredientNameGroup, ingredientAmountGroup, ingredientUnitGroup), View.GONE)
            true
        }
    }

    fun onCompleteClicked() {
        listener.itemComplete(item, !item.complete)
    }

    fun onEditClicked() {
        setVisibilities(arrayOf(saveButton, cancelButton, ingredientNameGroup, ingredientAmountGroup, ingredientUnitGroup), View.VISIBLE)
        setVisibilities(arrayOf(textView, completeButton, editButton, removeButton), View.GONE)
    }

    fun onRemoveClicked() {
        resetVisibilites()
        listener.itemRemoved(item)
    }

    fun onSaveClicked() {
        resetVisibilites()
        val nameText = ingredientNameField.text
        val amountText = ingredientAmountField.text
        val unitText = ingredientUnitField.text
        if(nameText == null || nameText.toString() == "") return invalidInput()
        val name = nameText.toString()
        val amount = amountText.toString().toDoubleOrNull() ?: return invalidInput()
        if(unitText == null || unitText.toString() == "") return invalidInput()
        val unit = unitText.toString()
        val ingredient = Ingredient(name, amount, unit)
        listener.itemEdited(item, ShoppingListItem(item.complete, ingredient, 0))
    }

    fun invalidInput() {
        Snackbar.make(v, "Invalid input!", Snackbar.LENGTH_SHORT).show()
    }

    fun onCancelClicked() {
        resetVisibilites()
    }

}