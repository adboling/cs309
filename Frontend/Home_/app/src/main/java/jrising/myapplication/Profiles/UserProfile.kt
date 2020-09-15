package jrising.myapplication.Profiles


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import jrising.myapplication.Friends.*
import jrising.myapplication.MyTimeline.MyTImelLineFragment
import org.json.JSONObject

import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.AppController.Companion.volleyController
import jrising.myapplication.app.User
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyController
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.json.JSONArray



// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_DATA = "data"
const val ARG_HANDLER = "handler"
const val ARG_INITIALIZED = "initialized"
const val ARG_PERSONAL = "personal"

const val HANDLER_BASIC = "basic"

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfile.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserProfile : Fragment() {
    lateinit var data: Bundle
    private lateinit var handler: String

    private var personal :Boolean = false
    private lateinit var profileHandler: IPRofileHander
    private val TAG = "Profile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getBundle(ARG_DATA)
            handler = it.getString(ARG_HANDLER)

            personal = it.getBoolean(ARG_PERSONAL)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_user_profile, container, false)
        val args = arguments


        //inits the profile handler by defualt it is basic type
        profileHandler = when(handler) {
            HANDLER_BASIC -> BasicProfileHandler()
            else -> {
                Log.e(TAG, "Tried to create a handler with an invalid type!\nProvided type: " + handler +
                        "\nDefaulting to a BasicProfileHandler")
                BasicProfileHandler()
            }
        }
        //grab the users information
            val ID = Userinfo.u_id
            val uname = data.getString(bundle_uname)
            val url = if(uname != null) {
                profileHandler.generateURL(uname)
            } else {
                profileHandler.generateURL(ID)
            }
        AppController.volleyController.createJSONRequest(url,volleyController.methodGet,"ProfileGet",null,{response: JSONObject ->
        val bundle = profileHandler.handleSource(response,personal )
        populateProfile(bundle)},{error->
            AppController.volleyController.handleError(error, TAG)})

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_friend.setOnClickListener {
            val uname = data.getString(bundle_uname)
            if(uname != Userinfo.username) {
                addFriend(uname)
            }
        }
        uProfile_friends.setOnClickListener {
            val friendsString = data.getString(bundle_friends)
            val friendsList = JSONArray(friendsString)
            openFriendsList(friendsList)
        }
        profile_timelineBtn.setOnClickListener {
            val myTimeLine = MyTImelLineFragment()
            var args = Bundle()
            args.putString("uName",data.getString(bundle_uname))
            myTimeLine.arguments = args
            AppController.eventBus.switchMainFragment(myTimeLine)
           /* val timelineString = data.getString(bundle_timeline)
            val timeline = JSONArray(timelineString)
            openTimeline(timeline)*/
        }
        //will open up edit profile if you one this profile otherwise I shouldn't show a button
        edit_profile.setOnClickListener{

            goToEditProfile(data)

        }
    }

    /**
     * will go to Edit profile page where you can change or add a bio and then change the profile picture
     * @param data will take in the information from the profile to populate the next page
     */
    private fun goToEditProfile(data: Bundle){
        val fragment = EditProfile()
        var bioCheck = profileHandler.checkofEmptyBio( data.getString(bundle_bio))
       var sendwith = Bundle()
        sendwith.putString(bundle_fname,data.getString(bundle_fname))
        sendwith.putString(bundle_lname,data.getString(bundle_lname))
        sendwith.putString(bundle_uname,data.getString(bundle_uname))
        sendwith.putString(bundle_avatar,data.getString(bundle_avatar))
        sendwith.putString(bundle_background,data.getString(bundle_background))


        sendwith.putString(bundle_bio, bioCheck)
        fragment.arguments = sendwith

        AppController.eventBus.switchMainFragment(fragment)
    }

    private fun populateProfile(data: Bundle) {

        val lname = data.getString(bundle_lname)
        val flname = data.getString(bundle_fname) +" "+ lname
        First_lastname.text = flname
        val friends = data.getString(bundle_friends)
        val friendsList = JSONArray(friends)
        val friendsText = "Friends: " + friendsList.length()
        uProfile_friends.text = friendsText
        user_profile_short_bio.text = data.getString(bundle_bio)
        val avatar = data.getString(bundle_avatar)

        if( avatar.isNotEmpty() ) {
            if (URLUtil.isValidUrl(avatar))
                AppController.volleyController.requestImage(user_profile_photo, avatar!!)
            else {
                Log.d("Timeline", "Invalid image url: " + avatar)
            }
        }
        // If this is not a personal profile, hide the button but leave the room for it
        val id = data.getInt(bundle_id)
        val url = Const.URL_VC5_FRIENDS + "/getFriendsByUserID?userID=" + id
        volleyController.createJSONArrayRequest(url, Request.Method.GET, "profile", null, { response -> handleFriendsList(response) }, null)
        if(data.getBoolean(ARG_PERSONAL) || id == Userinfo.u_id) add_friend.visibility = View.INVISIBLE
        this.data = data
    }

    private fun handleFriendsList(friends: JSONArray) {
        val set: MutableSet<Int> = HashSet()
        for(i in 1..friends.length()) {
            val it = friends[i - 1]
            val obj = it as? JSONObject
            if(obj != null) {
                val friend = User(obj)
                set.add(friend.id!!)
            }
        }
        if(set.contains(Userinfo.u_id)) {
            add_friend.visibility = View.INVISIBLE
        } else {
            add_friend.visibility = View.VISIBLE
        }
    }

    private fun addFriend(uname: String) {
        add_friend.visibility = View.INVISIBLE
        AppController.volleyController.addFriend(uname) {
            friendRequestSent()
        }
    }

    private fun openFriendsList(friendsList: JSONArray) {
        val fragment = FriendsList.newInstance(friendsList.toString(), onClick_viewProfile)
        AppController.eventBus.switchMainFragment(fragment)
    }

    private fun openTimeline(timeline: JSONArray) {

    }

    private fun friendRequestSent() {
        Toast.makeText(context, "Friend Request Sent!", Toast.LENGTH_LONG).show()
    }

    companion object {

        @JvmStatic
        fun newInstance(data: Bundle, handler: String, personal: Boolean) =
            UserProfile().apply {
                arguments = Bundle().apply {
                    putBundle(ARG_DATA, data)
                    putString(ARG_HANDLER, handler)

                    putBoolean(ARG_PERSONAL, personal)
                }
            }
    }
}
