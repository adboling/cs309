package jrising.myapplication.MyTimeline

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.timelineHandler
import org.json.JSONArray


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var linearLayoutManager: LinearLayoutManager



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyTImelLineFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyTImelLineFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MyTImelLineFragment : Fragment(), timelineHandler {



    private var adapter: RecyclerView.Adapter<MytimeLineRecycleAdaptor.logHolder>? = null
    private lateinit var logdata: JSONArray
    private var done: Boolean = false

    /**
     * returning from getting the timeline objects from the server from the response JSON Array this
     * function will pass the Array to the [MytimeLineRecycleAdaptor] and starts the recyclerview]
     */
    override fun handleTimelineResults(response: JSONArray, isOwner: Boolean) {
       logdata = response
        done = true
        //go to create logs
        var recyclerView = view?.findViewById<RecyclerView>(R.id.recyleTimeLine) //init the recyclerView to hold logs


        //this area is for init linearLayout as well as formatting for better looks
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.stackFromEnd = true




        recyclerView?.layoutManager = linearLayoutManager
        adapter = MytimeLineRecycleAdaptor(logdata , isOwner)
        recyclerView?.adapter = adapter



    }

    /**
     * initalizes the view and makes a volley call calling
     * [AppController.volleyController.getUserLogs] to get a users timeline
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_my_timel_line, container, false)

        var args = arguments
        var uName = args?.getString("uName", "")!!
        var isOwner = args?.getBoolean("isOwner")
        if(!isOwner){
            isOwner = false
        }

         AppController.volleyController.getUserLogs(uName, this, isOwner)



    return view
    }



}
