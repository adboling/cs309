package jrising.myapplication.RecipeSearch

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import jrising.myapplication.R
import jrising.myapplication.app.AppController
import kotlinx.android.synthetic.main.fragment_recipe_summary.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [recipeSummary.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [recipeSummary.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class recipeSummary : Fragment() {
    private var source: JSONObject? = null

    var id : String? = null
        private set

    var name : String? = null
        private set
    var type : String? = null
        private set

    fun init(source : JSONObject, type : String) {
        this.source = source
        when(type) {
            "yummly"-> {
                name = source.getString("sourceDisplayName")
                id = source.getString("id")
            }
            "user" -> {
                name = source.getString("recipeName")
                id = source.getString("recipeID")
            }
        }
        this.type = type
    }

    fun init(source : String, type : String) {
        val obj = JSONObject(source)
        id = obj.getString("id")
        name = obj.getString("name")
        this.type = type
        this.source = obj
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the name on the card to the given name
        rSummary_name.text = name
        // Set the id on the card to the given id
        rSummary_id.text = id
        // Set the onClick listener
        rSummary_card.setOnClickListener { onClick() }
    }

    fun onClick() {
        // Get a copy of the id and source so its thread safe
        val id = id
        val source = source
        val type = type
        // Make sure the id and source are not null
        if(id != null && source != null && type != null)
            // Send the id and source to the eventBus
            AppController.eventBus.recipeSelected(id, source, type)
    }
}
