package jrising.myapplication.Friends

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import jrising.myapplication.R
import jrising.myapplication.app.User

interface IFriendRequestSelectedListener {
    fun friendRequestAccepted(user: User)
    fun friendRequestDeclined(user: User)
    fun friendRequestCanceled(user: User)
}

class FriendRequestAdapter(var users: List<User>, var listener: IFriendRequestSelectedListener, var isIncoming: Boolean): RecyclerView.Adapter<FriendRequestHolder>() {
    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_request_item, parent, false)
        return FriendRequestHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestHolder, position: Int) {
        val user = users[position]
        holder.usernameField.text = user.username
        if(isIncoming) {
            holder.acceptButton.visibility = View.VISIBLE
            holder.declineButton.visibility = View.VISIBLE
            holder.cancelButton.visibility = View.INVISIBLE
            holder.acceptButton.setOnClickListener{
                listener.friendRequestAccepted(user)
            }
            holder.declineButton.setOnClickListener{
                listener.friendRequestDeclined(user)
            }
        } else {
            holder.acceptButton.visibility = View.INVISIBLE
            holder.declineButton.visibility = View.INVISIBLE
            holder.cancelButton.visibility = View.VISIBLE
            holder.cancelButton.setOnClickListener {
                listener.friendRequestCanceled(user)
            }
        }
    }
}

class FriendRequestHolder(v: View): RecyclerView.ViewHolder(v) {
    val usernameField: TextView = v.findViewById(R.id.friendRequestItem_usernameField)
    val acceptButton: Button = v.findViewById(R.id.friendRequestItem_acceptButton)
    val declineButton: Button = v.findViewById(R.id.friendRequestItem_declineButton)
    val cancelButton: Button = v.findViewById(R.id.friendRequestItem_cancelButton)
}