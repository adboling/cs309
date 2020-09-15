package jrising.myapplication.homeScreen

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.homescreenHandler
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [homeScreen.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [homeScreen.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class homeScreen : Fragment(), homescreenHandler {
    /**
     * after calling Volley to user the homescreen's JSON array of timeline
     * objects this function passes that array to [homescreenAdaptor] for use in a recyclerview
     */
    override fun handleTheHomescreen(jsonArray: JSONArray) {

        var recyclerView = view?.findViewById<RecyclerView>(R.id.recycleHome) //init the recyclerView to hold logs


        //this area is for init linearLayout as well as formatting for better looks
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        var itemDecor = DividerItemDecoration(context, 1)



        recyclerView?.layoutManager = linearLayoutManager
        adapter = homescreenAdaptor(jsonArray)
        recyclerView?.adapter = adapter
        //recyclerView?.addItemDecoration(itemDecor)
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var adapter: RecyclerView.Adapter<homescreenAdaptor.cardHolder>? = null

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
        var view = inflater.inflate(R.layout.fragment_home_screen, container, false)
        AppController.volleyController.getHomeScreen(this)

        return view

    }


}