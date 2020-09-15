package jrising.myapplication.UserSearchTests

import com.android.volley.Request
import jrising.myapplication.Profiles.ARG_DATA
import jrising.myapplication.Profiles.ARG_PERSONAL
import jrising.myapplication.Profiles.UserProfile
import jrising.myapplication.Profiles.bundle_uname
import jrising.myapplication.TestHelper
import jrising.myapplication.app.EventBus
import jrising.myapplication.app.User
import jrising.myapplication.net_utils.VolleyHandler
import jrising.myapplication.userSearch.IUserSearchListener
import jrising.myapplication.userSearch.UserSearchModel
import org.json.JSONArray
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserSearchModelTests  {
    @Mock
    lateinit var mListener: IUserSearchListener
    @Mock
    lateinit var mVolleyHandler: VolleyHandler
    @Mock
    lateinit var mEventBus: EventBus

    lateinit var model: UserSearchModel

    val santa = User(
        "http://cs309-vc-5.misc.iastate.edu/images/avatars/SuperAdmin.jpg",
        "",
        "How many times do I have to tell you Timmy, stop giving your dogs chocolate!",
        "claus.santa@xmas.com", "Santa", "Claus", 1, "admin", "SuperAdmin"
    )
    val masterChef = User(
        "",
        "",
        "Welcome to my Profile",
        "dinedrivedive@food.net",
        "Guy",
        "Ferrari",
        2,
        "curator",
        "MasterChef"
    )
    val herpderp = User(
        "",
        "",
        "",
        "herpderp@test.com",
        "herp",
        "derp",
        55,
        "user",
        "herpderp"
    )
    val testUsers = arrayListOf(santa, masterChef, herpderp)


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        model = UserSearchModel(mListener, mVolleyHandler, mEventBus)
    }

    @Test
    fun testSearch() {
        var called = false
        val testString = "testUsername"
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/users/search?username=testUsername"
        Mockito.`when`(mVolleyHandler.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(),TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.GET)
            // We won't test arguments[2] because the TAG only matters for reporting errors.
            // We won't test arguments[3] because the params don't matter when its using GET
            // We won't test arguments[4] because the callback doesn't really matter for this test
            // We won't test arguments[5] because the errorHandler doesn't really matter for this test
            called = true
            Unit
        }
        model.search(testString)
        assert(called)
    }

    private fun generateTestJSON(): JSONArray {
        val result = JSONArray()
        testUsers.forEach {
            result.put(it.toJSONObject())
        }
        return result
    }

    @Test
    fun test_handleSearchResults() {
        val testResponse = generateTestJSON()
        val expectedUsers = testUsers
        var actualUsers: List<User>? = null
        Mockito.`when`(mListener.updateResults(TestHelper.any())).thenAnswer{
            assert(it.arguments[0] is List<*>)
            val list = it.arguments[0] as List<*>
            list.forEach{item ->
                assert(item is User)
            }
            actualUsers = it.arguments[0] as List<User>
            Unit
        }
        model.handleSearchResults(testResponse)
        assert(actualUsers != null)
        assert(actualUsers?.size == expectedUsers.size)
        actualUsers!!.forEach{
            assert(expectedUsers.contains(it))
        }
    }

    private fun testUserProfile(userProfile: UserProfile, expectedUsername: String): Boolean {
        val args = userProfile.arguments ?: return false
        val data = args.getBundle(ARG_DATA) ?: return false
        if(data.getString(bundle_uname) != expectedUsername) return false
        if(args.getBoolean(ARG_PERSONAL)) return false
        return true
    }

    @Test
    fun test_resultSelected_user() {
        var switched = false
        val testUser = santa
        Mockito.`when`(mEventBus.switchMainFragment(TestHelper.any())).thenAnswer{
            assert(it.arguments[0] is UserProfile)
            val up = it.arguments[0] as UserProfile
            switched = testUserProfile(up, testUser.username!!)
            Unit
        }
        model.resultSelected(testUser)
        assert(switched)
    }

    @Test
    fun test_resultSelected_username() {
        var switched = false
        val testUsername = "test user"
        Mockito.`when`(mEventBus.switchMainFragment(TestHelper.any())).thenAnswer{
            assert(it.arguments[0] is UserProfile)
            val up = it.arguments[0] as UserProfile
            switched = testUserProfile(up, testUsername)
            Unit
        }
        model.resultSelected(testUsername)
        assert(switched)
    }
}