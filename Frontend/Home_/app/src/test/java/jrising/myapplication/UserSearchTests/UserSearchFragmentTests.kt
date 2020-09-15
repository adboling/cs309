package jrising.myapplication.UserSearchTests

import android.support.design.widget.TextInputEditText
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.widget.Button
import jrising.myapplication.TestHelper
import jrising.myapplication.app.User
import jrising.myapplication.userSearch.IUserSearchController
import jrising.myapplication.userSearch.UserSearchAdapter
import jrising.myapplication.userSearch.UserSearchFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(Suite::class)
@Suite.SuiteClasses(
    UserSearchFragmentTests::class,
    UserSearchModelTests::class)
class UserSearchTests

@RunWith(RobolectricTestRunner::class)
class UserSearchFragmentTests {
    @Mock
    lateinit var mSearchInput: TextInputEditText
    @Mock
    lateinit var mSearchButton: Button
    @Mock
    lateinit var mSearchResultContainer: RecyclerView
    @Mock
    lateinit var mController: IUserSearchController
    @Mock
    lateinit var mAdapter: UserSearchAdapter

    lateinit var fragment: UserSearchFragment

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        fragment = UserSearchFragment()
        fragment.controller = mController
        fragment.searchButton = mSearchButton
        fragment.searchInput = mSearchInput
        fragment.searchResultContainer = mSearchResultContainer
        fragment.adapter = mAdapter
    }

    @Test
    fun test_handleUserSelected() {
        var called = false
        val testUser = User("test avatar", "test background", "test bio", "test email", "test fname", "test lname", 0, "test role", "test username")
        Mockito.`when`(mController.resultSelected(TestHelper.any<User>())).thenAnswer{
            assert(it.arguments[0] == testUser)
            called = true
            null
        }
        Mockito.`when`(mController.resultSelected(Mockito.anyString())).thenAnswer{
            assert(it.arguments[0] == "test username")
            called = true
            null
        }
        fragment.handleUserSelected(testUser)
        assert(called)
    }

    @Test
    fun test_UpdateResults() {
        val testInput = ArrayList<User>()
        testInput.add(
            User("test avatar", "test background", "test bio", "test email", "test fname", "test lname", 0, "test role", "test username")
        )
        testInput.add(
            User("test avatar 2", "test background", "test bio 2", "test@test.com", "fname", "lname", 1, "user", "test")
        )
        var results:MutableList<User>? = null
        Mockito.`when`(mAdapter.notifyDataSetChanged()).thenAnswer {
            results = fragment.results
            null
        }
        fragment.updateResults(testInput)
        assert(results != null)
        assert(results!!.containsAll(testInput))
    }

    @Test
    fun test_SearchButtonPressed() {
        val testString = "test"
        var called = false
        Mockito.`when`(mSearchInput.text).thenReturn(Editable.Factory().newEditable(testString))
        Mockito.`when`(mController.search(Mockito.anyString())).thenAnswer{
            assert(it.arguments[0] == testString)
            called = true
            Unit
        }
        fragment.searchButtonPressed()
        assert(called)
    }
}