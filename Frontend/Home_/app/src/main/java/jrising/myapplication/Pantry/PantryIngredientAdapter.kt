package jrising.myapplication.Pantry

import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import jrising.myapplication.R
import jrising.myapplication.app.Ingredient

/**
 * The interface used to communicate changes to the stored array
 */
interface IIngredientListListener {
    /**
     * The function called when the given element is removed
     */
    fun onIngredientRemoved(item: PantryItem)

    /**
     * The function called when the given element is modified
     */
    fun onIngredientChanged(old: PantryItem, new: PantryItem)

    /**
     * The function called when the given element is added
     */
    fun onIngredientAdded(item: PantryItem)
}

/**
 * A data class which encapsulates an ingredient with a Pantry item id
 */
data class PantryItem(
    val ingredient: Ingredient,
    val id: Int
) {
    override fun equals(other: Any?): Boolean {
        if(other !is PantryItem) return false
        if(other.id != id) return false
        if(other.ingredient != ingredient) return false
        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

class PantryIngredientAdapter(var items: MutableList<PantryItem>, val listener: IIngredientListListener): RecyclerView.Adapter<PantryIngredientAdapter.PantryIngredientHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }

    fun addIngredient(type: String, amount: String, unit: String): Boolean {
        if(type == "") return false
        if(amount.toDoubleOrNull() == null) return false
        if(unit == "") return false
        val newIngredient = Ingredient(type, amount.toDouble(), unit)
        listener.onIngredientAdded(PantryItem(newIngredient, 0))
        return true
    }

    fun addIngredient(ingredient: Ingredient) {
        listener.onIngredientAdded(PantryItem(ingredient, 0))
    }

    fun updateIngredients(items: List<PantryItem>) {
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryIngredientHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pantry_item, parent, false)
        val holder = PantryIngredientHolder(view, this)
        return holder
    }

    override fun onBindViewHolder(holder: PantryIngredientHolder, position: Int) {
        holder.textView.text = items[position].ingredient.toString()
    }

    fun onItemEdit(type: String, amountStr: String, unitStr: String, position: Int) {
        var amount: Double? = null
        var unit: String? = null
        if(position == RecyclerView.NO_POSITION) return
        val old = items[position]
        if(type == "" || type.contains('=')) return
        amount = amountStr.toDoubleOrNull()
        unit = if(unitStr == "") null else unitStr
        val new = Ingredient(type, amount, unit)
        listener.onIngredientChanged(old, PantryItem(new, 0))
    }

    fun onItemRemove(position: Int) {
        if(position == RecyclerView.NO_POSITION) return
        listener.onIngredientRemoved(items[position])
    }

    inner class PantryIngredientHolder(val v: View, val adapter: PantryIngredientAdapter): RecyclerView.ViewHolder(v) {
        val textView: TextView = v.findViewById(R.id.pantry_item_text)
        val editButton: Button = v.findViewById(R.id.pantry_item_edit)
        val removeButton: Button = v.findViewById(R.id.pantry_item_remove)
        val saveButton: Button = v.findViewById(R.id.pantry_item_save)
        val cancelButton: Button = v.findViewById(R.id.pantry_item_cancel)
        val typeIn: TextInputEditText = v.findViewById(R.id.pantry_item_type_in)
        val amountIn: TextInputEditText = v.findViewById(R.id.pantry_item_amount_in)
        val unitIn: TextInputEditText = v.findViewById(R.id.pantry_item_unit_in)
        val typeGroup: TextInputLayout = v.findViewById(R.id.pantry_item_type_tiet)
        val amountGroup: TextInputLayout = v.findViewById(R.id.pantry_item_amount_tiet)
        val unitGroup: TextInputLayout = v.findViewById(R.id.pantry_item_unit_tiet)
        var expanded = false

        init {
            v.setOnLongClickListener{ onLongClick(); true }
            editButton.setOnClickListener { onEditClicked() }
            removeButton.setOnClickListener { onRemoveClicked() }
            saveButton.setOnClickListener { onSaveClicked() }
            cancelButton.setOnClickListener { onCancelClicked() }
        }

        private fun setVisibilities(views: Array<View>, visibility: Int) {
            if(visibility != View.VISIBLE && visibility != View.INVISIBLE && visibility != View.GONE) return
            views.forEach{ view ->
                view.visibility = visibility
            }
        }

        private fun onLongClick() {
            expanded = if(expanded) {
                setVisibilities(arrayOf(textView), View.VISIBLE)
                setVisibilities(arrayOf(editButton, removeButton, saveButton, cancelButton, typeGroup, amountGroup, unitGroup), View.GONE)
                false
            } else {
                setVisibilities(arrayOf(textView, editButton, removeButton), View.VISIBLE)
                setVisibilities(arrayOf(saveButton, cancelButton, typeGroup, amountGroup, unitGroup), View.GONE)
                true
            }

        }

        private fun onEditClicked() {
            setVisibilities(arrayOf(saveButton, cancelButton, typeGroup, amountGroup, unitGroup), View.VISIBLE)
            setVisibilities(arrayOf(textView, editButton, removeButton), View.GONE)
        }

        private fun onRemoveClicked() {
            adapter.onItemRemove(adapterPosition)
            v.visibility = View.GONE
        }

        private fun onSaveClicked() {
            setVisibilities(arrayOf(textView), View.VISIBLE)
            setVisibilities(arrayOf(editButton, removeButton, saveButton, cancelButton, typeGroup, amountGroup, unitGroup), View.GONE)
            adapter.onItemEdit(
                typeIn.text.toString(),
                amountIn.text.toString(),
                unitIn.text.toString(),
                adapterPosition)
        }

        private fun onCancelClicked() {
            setVisibilities(arrayOf(textView), View.VISIBLE)
            setVisibilities(arrayOf(editButton, removeButton, saveButton, cancelButton, typeGroup, amountGroup, unitGroup), View.GONE)
        }
    }
}