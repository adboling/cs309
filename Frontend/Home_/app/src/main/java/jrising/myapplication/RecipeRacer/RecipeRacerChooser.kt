package jrising.myapplication.RecipeRacer


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import jrising.myapplication.R
import jrising.myapplication.app.AppController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [RecipeRacerChooser.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RecipeRacerChooser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sessionIdTxt: EditText
    private lateinit var joinButton: Button
    private lateinit var createButton: Button

    var recipeID = ""

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

        var view = inflater.inflate(R.layout.fragment_recipe_racer_chooser, container, false)
        sessionIdTxt = view.findViewById(R.id.joinGame_txt)
        joinButton = view.findViewById(R.id.rr_joinBtn)
        createButton = view.findViewById(R.id.rr_createGame)

        joinButton.setOnClickListener { joinGame() }


        return view
    }

    private fun joinGame(){
        var sessionIdGrab = sessionIdTxt.text.toString()
        if(sessionIdGrab.isEmpty()){
            sessionIdTxt.error = "please enter a Session Id or try another option"
        }
        else {
            val sessionID = sessionIdGrab.toInt()
            val joinGame = RecipeRaceFragment.newInstance(sessionID, recipeID)
            AppController.eventBus.switchMainFragment(joinGame)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecipeRacerChooser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecipeRacerChooser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
