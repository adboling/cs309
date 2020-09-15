package jrising.myapplication.RecipeRacer

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import jrising.myapplication.R

/**
 * An adapter for a RecyclerView using a layout for [RRParticipant]s
 */
class RRParticipantAdapter(val participants: MutableList<RRParticipant>) : RecyclerView.Adapter<RRParticipantAdapter.RRParticipantHolder>() {

    /**
     * The method used to get the number of items in the array
     */
    override fun getItemCount(): Int {
        return participants.size
    }

    /**
     * The method used to create a view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RRParticipantHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rr_participant_item, parent, false)
        val holder = RRParticipantHolder(view)
        return holder
    }

    /**
     * The method used to bind a view holder, and populate it
     */
    override fun onBindViewHolder(holder: RRParticipantHolder, position: Int) {
        holder.name.text = participants[position].Username
        val progressText = participants[position].progress.toString() + " / " + participants[position].totalProgress
        holder.progress.text = progressText
    }

    /**
     * A class used to bind the different views in the RR_Participant view
     */
    inner class RRParticipantHolder(v: View): RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.rr_participant_name)
        val progress: TextView = v.findViewById(R.id.rr_participant_progress)
    }
}

/**
 * An adapter for a recycler view using RRIngredients
 */
class RRIngredientAdapter(val ingredients: MutableList<RRIngredient>, var callback: (Int)->Unit) : RecyclerView.Adapter<RRIngredientAdapter.RRIngredientHolder>() {

    /**
     * A method to get the item count
     */
    override fun getItemCount(): Int {
        return ingredients.size
    }

    /**
     * A method called when the view holder is created
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RRIngredientHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rr_ingredient_item, parent, false)
        val holder = RRIngredientHolder(view)
        return holder
    }

    /**
     * A method called when the view holder is bound
     */
    override fun onBindViewHolder(holder: RRIngredientHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.type.text = ingredient.type
        holder.amount.text = ingredient.amount.toString()
        holder.unit.text = ingredient.unit
        holder.itemView.setOnClickListener{
            callback(position)
        }
    }

    /**
     * A class used to bind the different views in a RRIngredient view
     */
    inner class RRIngredientHolder(v: View): RecyclerView.ViewHolder(v) {
        val type: TextView = v.findViewById(R.id.rr_ingredient_type)
        val amount: TextView = v.findViewById(R.id.rr_ingredient_amount)
        val unit: TextView = v.findViewById(R.id.rr_ingredient_unit)
    }
}