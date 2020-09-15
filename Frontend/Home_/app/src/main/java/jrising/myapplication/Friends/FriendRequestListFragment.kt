package jrising.myapplication.Friends


import android.os.Bundle
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
import jrising.myapplication.app.User

import kotlinx.android.synthetic.main.fragment_friend_request_list.*

/**
 * A simple [Fragment] subclass.
 * Use the [FriendRequestListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FriendRequestListFragment : Fragment(), IFriendRequestListener, IFriendRequestSelectedListener {

    var controller: IFriendRequestController = FriendRequestModel(this, AppController.volleyController, AppController.eventBus)
    lateinit var title: TextView
    lateinit var incomingButton: Button
    lateinit var outgoingButton: Button
    lateinit var incomingList: RecyclerView
    lateinit var outgoingList: RecyclerView

    lateinit var incomingAdapter: FriendRequestAdapter
    lateinit var outgoingAdapter: FriendRequestAdapter

    var incomingRequests: MutableList<User> = ArrayList()
    var outgoingRequests: MutableList<User> = ArrayList()


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
        return inflater.inflate(R.layout.fragment_friend_request_list, container, false)
    }

    private fun initFields() {
        title = friendRequest_title
        incomingButton = friendRequest_incomingButton
        outgoingButton = friendRequest_outgoingButton
        incomingList = friendRequest_incomingList
        outgoingList = friendRequest_outgoingList

        incomingList.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        outgoingList.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        incomingAdapter = FriendRequestAdapter(incomingRequests, this, true)
        outgoingAdapter = FriendRequestAdapter(outgoingRequests, this, false)
        incomingList.adapter = incomingAdapter
        outgoingList.adapter = outgoingAdapter

        incomingButton.setOnClickListener { switchToIncomingList() }
        outgoingButton.setOnClickListener { switchToOutgoingList() }
    }

    private fun switchToIncomingList() {
        outgoingList.visibility = View.INVISIBLE
        incomingList.visibility = View.VISIBLE
        controller.requestIncoming()
    }

    private fun switchToOutgoingList() {
        outgoingList.visibility = View.VISIBLE
        incomingList.visibility = View.INVISIBLE
        controller.requestOutgoing()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields()
    }

    override fun friendRequestAccepted(user: User) {
        controller.acceptRequest(user)
    }

    override fun friendRequestCanceled(user: User) {
        controller.cancelRequest(user)
    }

    override fun friendRequestDeclined(user: User) {
        controller.declineRequest(user)
    }

    override fun updateIncomingRequests(users: List<User>) {
        incomingRequests = users.toMutableList()
        incomingAdapter.users = incomingRequests
        incomingAdapter.notifyDataSetChanged()
    }

    override fun updateOutgoingRequests(users: List<User>) {
        outgoingRequests = users.toMutableList()
        outgoingAdapter.users = outgoingRequests
        outgoingAdapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FriendRequestListFragment.
         */
        @JvmStatic
        fun newInstance() =
            FriendRequestListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
