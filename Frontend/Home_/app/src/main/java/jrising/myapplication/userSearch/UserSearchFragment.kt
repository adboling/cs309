package jrising.myapplication.userSearch


import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.User

/**
 * A simple [Fragment] subclass used to facilitate searching for users
 * Use the [UserSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserSearchFragment : Fragment(), IUserSearchListener, IUserSelectedListener {
    /**
     * A bunch of fields
     */
    lateinit var searchInput: TextInputEditText
    lateinit var searchButton: Button
    lateinit var searchResultContainer: RecyclerView
    lateinit var adapter: UserSearchAdapter

    var results: MutableList<User> = ArrayList()

    /**
     * The saved username search term
     */
    var username: String? = null

    /**
     * The controller used to handle commands
     */
    var controller: IUserSearchController = UserSearchModel(this, AppController.volleyController, AppController.eventBus)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_PARAM_USERNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_search, container, false)
    }

    private fun setupFields(v: View) {
        searchInput = v.findViewById(R.id.userSearch_searchInput)
        searchButton = v.findViewById(R.id.userSearch_searchButton)
        searchResultContainer = v.findViewById(R.id.userSearch_resultContainer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFields(view)
        searchResultContainer.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        adapter = UserSearchAdapter(results, this)
        searchResultContainer.adapter = adapter
        searchButton.setOnClickListener{
            searchButtonPressed()
        }
    }

    override fun handleUserSelected(user: User) {
        controller.resultSelected(user)
    }

    override fun updateResults(users: List<User>) {
        results = users.toMutableList()
        adapter.results = results
        adapter.notifyDataSetChanged()
    }

    fun searchButtonPressed() {
        val searchText = searchInput.text.toString()
        controller.search(searchText)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param terms the list of search terms to begin with
         * @return A new instance of fragment UserSearchFragment.
         */
        @JvmStatic
        fun newInstance(username: String?) =
            UserSearchFragment().apply {
                arguments = Bundle().apply {
                    arguments?.putString(ARG_PARAM_USERNAME, username)
                }
            }
        const val ARG_PARAM_USERNAME = "username"
    }
}
