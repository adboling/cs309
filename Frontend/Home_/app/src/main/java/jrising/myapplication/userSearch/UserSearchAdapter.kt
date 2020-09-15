package jrising.myapplication.userSearch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import jrising.myapplication.R
import jrising.myapplication.app.User

/**
 * A class used to define a method used to notify a listener that a user has been selected
 */
interface IUserSelectedListener {
    fun handleUserSelected(user: User)
}

/**
 * A class used to control a [RecyclerView] to display a list of users
 * @param results The list of [User]s to display
 * @param listener The [IUserSelectedListener] to be notified when a user is selected
 */
class UserSearchAdapter(var results: List<User>, var listener: IUserSelectedListener) : RecyclerView.Adapter<UserSearchResultHolder>() {
    /**
     * A function to return the number of items to display
     */
    override fun getItemCount(): Int {
        return results.size
    }

    /**
     * A function used to handle an option being selected
     */
    private fun handleOptionSelected(index: Int) {
        listener.handleUserSelected(results[index])
    }

    /**
     * A function to create a [UserSearchResultHolder] to display search results
     * @param parent The view to hold the results
     * @return The viewHolder to display the results
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchResultHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_search_item, parent, false)
        return UserSearchResultHolder(view) { index: Int -> handleOptionSelected(index) }
    }

    /**
     * A function called when the [UserSearchResultHolder] is bound to update the fields shown
     */
    override fun onBindViewHolder(holder: UserSearchResultHolder, position: Int) {
        holder.usernameField.text = results[position].username
    }
}

/**
 * The class used to hold a couple views used for the UserSearch RecyclerView items
 * @param v The [View] used to display to the user
 * @param callback The function called when an option is selected
 */
class UserSearchResultHolder(v: View, callback: (Int)->Unit) : RecyclerView.ViewHolder(v) {
    val usernameField: TextView = v.findViewById(R.id.userSearchItem_username)
    val viewProfileButton: Button = v.findViewById(R.id.userSearchItem_viewProfileButton)
    init {
        viewProfileButton.setOnClickListener{
            callback(adapterPosition)
        }
    }

}