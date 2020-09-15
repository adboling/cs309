package jrising.myapplication.Friends

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import jrising.myapplication.Profiles.HANDLER_BASIC
import jrising.myapplication.Profiles.UserProfile
import jrising.myapplication.Profiles.bundle_uname

import jrising.myapplication.R
import jrising.myapplication.app.AppController
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.android.synthetic.main.fragment_friends_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_FRIENDS_LIST = "friendsList"
const val ARG_ONCLICK = "onClick"

// arguments for ARG_ONCLICK to have different functionality
const val onClick_addFriend = "addFriend"
const val onClick_inviteRace = "inviteRace"
const val onClick_viewProfile = "viewProfile"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FriendsList.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FriendsList.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FriendsList : Fragment() {
    // TODO: Rename and change types of parameters
    private var friendsString: String? = null
    private var onClick: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            friendsString = it.getString(ARG_FRIENDS_LIST)
            onClick = it.getString(ARG_ONCLICK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val friendsList = JSONArray(friendsString)
        val usernameList: MutableList<String> = ArrayList()
        for(i in 1..friendsList.length()) {
            val friend = friendsList.get(i - 1) as JSONObject
            usernameList += friend.getString("username")
        }
        val adapter = ArrayAdapter(this.context, R.layout.friendslist_item, usernameList)
        friends_list.adapter = adapter
        friends_list.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val username = usernameList.get(i)
            handleClick(username)
        }
    }

    fun handleClick(username: String) {
        when(onClick) {
            onClick_addFriend -> {
                AppController.volleyController.addFriend(username) {
                    friendRequestSent()
                }
            }
            onClick_inviteRace -> {

            }
            onClick_viewProfile -> {
                val data = Bundle()
                data.putString(bundle_uname, username)
                val handler = HANDLER_BASIC

                val personal = false
                val fragment = UserProfile.newInstance(data, handler, personal)
                AppController.eventBus.switchMainFragment(fragment)
            }
        }
    }

    fun friendRequestSent() {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FriendsList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(friendsList: String, onClick: String) =
            FriendsList().apply {
                arguments = Bundle().apply {
                    putString(ARG_FRIENDS_LIST, friendsList)
                    putString(ARG_ONCLICK, onClick)
                }
            }
    }
}
