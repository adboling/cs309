package jrising.myapplication.RecipeSearch

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import jrising.myapplication.R

import jrising.myapplication.app.AppController
import jrising.myapplication.net_utils.VolleyController
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONObject

import kotlinx.android.synthetic.main.fragment_recipe_search.*
import org.json.JSONArray




/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RecipeSearchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RecipeSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class RecipeSearchFragment : Fragment() {

    companion object {
        const val option_yummly = "Yummly recipe search"
        const val option_user = "User recipe search"
        const val option_ingredient = "Ingredient based search"

        const val bundle_yummly = "yummly"
        const val bundle_user = "user"
        const val bundle_type = "type"
        const val bundle_default = "default"
        const val bundle_terms = "terms"
        const val bundle_url = "url"
        const val bundle_tag = "tag"
        const val bundle_ingredient = "ingredient"
    }
    lateinit var _childFragmentManager: FragmentManager

    var searchHandler : IRecipeSearchHandler? = GeneralSearchHandler()
    lateinit var volleyController : VolleyHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_recipe_search, container, false)
        volleyController = AppController.volleyController
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rSearch_button.setOnClickListener { onSearchButtonPressed() }
        rSearch_spinner.onItemSelectedListener = RecipeSearchTypeSelectedListener()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this._childFragmentManager = getChildFragmentManager()
    }

    private fun onSearchButtonPressed() {
        // Grab the search result handler, exit if its null
        val searchResultHandler = searchHandler ?: return
        // Tokenize the search terms, and convert to a typed array
        val args = rsearch_input.text.split(" ").toTypedArray()
        // Variable for the spinner's selected option
        val spinnerOpt = rSearch_spinner.selectedItem.toString()
        // Switch on the selected option
        requestSearchResults(spinnerOpt, args)
    }

    fun requestSearchResults(spinnerOpt: String, terms: Array<String>) {
        val searchHandler = searchHandler ?: return
        var type = ""
        when(spinnerOpt) {
            option_yummly -> type = bundle_yummly
            option_user -> type = bundle_user
            option_ingredient -> type = bundle_ingredient
            else -> type = bundle_default
        }
        val requestData = searchHandler.getSearchRequest(type, terms)
        var url = requestData.url
        val tag = requestData.tag
        type = requestData.type
        when(type) {
            "user" -> {
                volleyController.createJSONArrayRequest(url, volleyController.methodGet, tag, null, {response -> searchResultsReceived(response)}, null)
            }
            // Make the yummly search request
            "yummly" -> {
                volleyController.createJSONRequest(url, volleyController.methodGet, tag, null, {response -> searchResultsReceived(response)}, null)
            }
            bundle_ingredient -> {
                volleyController.createJSONRequest(url, volleyController.methodGet, tag, null, { response -> searchResultsReceived(response) }, null)
            }
            else -> {
                Log.e("rSearch", "Trying to search a recipe with an unknown type!")
            }
        }
    }

    fun searchResultsReceived(arr : JSONArray) {
        Log.d("RecipeSearch", "Source:\n"+arr)
        val obj = JSONObject()
        obj.put("matches", arr)
        obj.put("type", "user")
        // If the searchResultHandler is null, we exit the function, otherwise we continue
        val searchResultHandler = searchHandler ?: return
        // Get the search results from the handler
        val results = searchResultHandler.getRecipeSearchResults(obj)
        // Get the matches from the results
        val matches = results.getStringArray("matches")
        // Get the type from the results
        val type = results.getString("type")

        val fm = _childFragmentManager
        val transaction = fm.beginTransaction()

        matches.forEach {match ->
            // Create a recipe summary fragment, and add it to the transaction
            val fragment = recipeSummary()
            fragment.init(match, type)
            transaction.add(R.id.rSearch_resultContainer, fragment)
        }
        transaction.commit()
    }

    fun searchResultsReceived(result : JSONObject) {
        // If the searchResultHandler is null, we exit the function, otherwise we continue
        val searchResultHandler = searchHandler ?: return
        // Get the search results from the handler
        val results = searchResultHandler.getRecipeSearchResults(result)
        // Get the matches from the results
        val matches = results.getStringArray("matches")
        // Get the type from the results
        val type = results.getString("type")

        val fm = _childFragmentManager
        val transaction = fm.beginTransaction()

        matches.forEach {match ->
            // Create a recipe summary fragment, and add it to the transaction
            val fragment = recipeSummary()
            fragment.init(match, type)
            transaction.add(R.id.rSearch_resultContainer, fragment)
        }
        transaction.commit()
    }
}



class RecipeSearchTypeSelectedListener : AdapterView.OnItemSelectedListener {
    var type : String? = null
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()
        when(item) {
            "User recipe search" -> type = "user"
            "Yummly search" -> type = "yummly"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}

