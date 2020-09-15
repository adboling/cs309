package jrising.myapplication.Admin_controls


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import jrising.myapplication.MyTimeline.MytimeLineRecycleAdaptor
import jrising.myapplication.R
import kotlinx.android.synthetic.main.fragment_admin__control.*
import kotlin.Function as Function1001


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Admin_Control.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Admin_Control : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var user_id: EditText
    private lateinit var deleteUser: Button
    private lateinit var comment_id: EditText
    private lateinit var deleteComment: Button
    private lateinit var timelineId: EditText
    private lateinit var deleteTimeline:Button
    private lateinit var username_role: EditText
    private lateinit var roleChangeBtn: Button
    private var adminHandler: theAdminHandler = theAdminHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_admin__control, container, false)
        user_id = view.findViewById(R.id.user_id)
        deleteUser = view.findViewById(R.id.deleteUserBtn)
        comment_id = view.findViewById(R.id.comment_id)
        deleteComment = view.findViewById(R.id.deleteCommentBtn)
        timelineId= view.findViewById(R.id.timeline_object_id)
        deleteTimeline = view.findViewById(R.id.deleteTimelineBtn)
        username_role = view.findViewById(R.id.username)
        roleChangeBtn = view.findViewById(R.id.changeRole)


        deleteUser.setOnClickListener {
            if (user_id.text.isNotEmpty()) {
                removeDialog(user_id.text.toString().toInt(), "", "user", view.context)
            } else user_id.error = "Enter A User ID to delete"
        }
        deleteComment.setOnClickListener {
            if (comment_id.text.isNotEmpty()) {
                removeDialog(comment_id.text.toString().toInt(), "", "comment", view.context)
            } else comment_id.error = "Enter A Comment ID to delete"
        }
        deleteTimeline.setOnClickListener {
            if (timelineId.text.isNotEmpty()) {
                removeDialog(timelineId.text.toString().toInt(), "", "timeline log", view.context)
            } else timelineId.error = "Enter A timeline Object ID to delete"
        }
        roleChangeBtn.setOnClickListener {
            if (username_role.text.isNotEmpty()) {
                removeDialog(0, username_role.text.toString(), "admin", view.context)
            } else username_role.error = "Enter A timeline Object ID to delete"


        }




        return view
    }

    private fun removeDialog(pass_in: Int, username: String, Type: String, context: Context) {
        val alertDialog: AlertDialog? = view.let {
            val builder = AlertDialog.Builder(context)
            builder.apply {
                if (Type != "admin") {
                    setMessage("Delete $Type?\nDeleting this cannot be undone")
                } else setMessage("Change to $Type?\n this cannot be undone")


                setPositiveButton(
                    R.string.confirm
                ) { _, _ ->
                    //if you choose yes we will start the proccess of deleting that timeline object
                    deleteAction(pass_in, username, Type, context)


                }
                setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    // User cancelled the dialog
                }
            }


            // Create the AlertDialog
            builder.show()
        }
    }

    private fun deleteAction(pass_in: Int, username: String, type: String, context: Context) {

        when (type) {
            "user" -> adminHandler.deleteUser(pass_in, context)
            "comment" -> adminHandler.deleteComment(pass_in, context)
            "timeline log" -> adminHandler.deleteTimelineObject(pass_in, context)
            "admin" -> adminHandler.changeRoles(username, context)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Admin_Control.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Admin_Control().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
