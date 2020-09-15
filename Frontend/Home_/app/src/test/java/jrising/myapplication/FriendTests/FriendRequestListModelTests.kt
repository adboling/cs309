package jrising.myapplication.FriendTests

import com.android.volley.Request
import jrising.myapplication.Friends.FriendRequestModel
import jrising.myapplication.Friends.IFriendRequestListener
import jrising.myapplication.TestHelper
import jrising.myapplication.app.EventBus
import jrising.myapplication.app.User
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FriendRequestListModelTests {
    @Mock
    lateinit var mListener: IFriendRequestListener
    @Mock
    lateinit var mVolleyHandler: VolleyHandler
    @Mock
    lateinit var mEventBus: EventBus

    lateinit var model: FriendRequestModel

    val testSender = User(
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        "",
    "john_smith")
    val testReceiver = User(
        "",
        "",
        "",
        "",
        "",
        "",
        15,
        "",
        "mary_sue"
    )
    var expectedObj = JSONObject()
    var testJSON = JSONArray()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        model = FriendRequestModel(mListener, mVolleyHandler, mEventBus)
        expectedObj.put("senderID", testSender.id)
        expectedObj.put("senderUsername", testSender.username)
        expectedObj.put("recipientID", testReceiver.id)
        expectedObj.put("recipientUsername", testReceiver.username)
        testJSON.put(testSender.toJSONObject())
        testJSON.put(testReceiver.toJSONObject())
    }
    @Test
    fun test_AcceptRequest() {
        Userinfo.u_id = testReceiver.id!!
        Userinfo.username = testReceiver.username!!
        var requestSent = false
        val expectedUrl="http://cs309-vc-5.misc.iastate.edu:8080/friends/acceptRequest"
        Mockito.`when`(mVolleyHandler.createJSONRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.POST)
            // We don't test argument 2 because its only used for logging
            assert(it.arguments[3].toString() == expectedObj.toString()) { "Expected: " + expectedObj.toString() + "\nGot: " + it.arguments[3].toString()}
            // We don't test argument 4 because the callback doesn't matter
            // We don't test argument 5 because the error handler doesn't matter
            requestSent = true
            Unit
        }
        model.acceptRequest(testSender)
        assert(requestSent)
    }
    @Test
    fun test_cancelRequest() {
        Userinfo.u_id = testSender.id!!
        Userinfo.username = testSender.username!!
        var requestSent = false
        var expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/friends/deleteFriend?friendID=15&userID=0"
        Mockito.`when`(mVolleyHandler.createJSONRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.DELETE)
            // We don't test any other arguments because they don't matter
            requestSent = true
            Unit
        }
        model.cancelRequest(testReceiver)
        assert(requestSent)
    }
    @Test
    fun test_declineRequest() {
        Userinfo.u_id = testReceiver.id!!
        Userinfo.username = testReceiver.username!!
        var requestSent = false
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/friends/declineRequest"
        Mockito.`when`(mVolleyHandler.createJSONRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.POST)
            // We don't test argument 2 because its only used for logging
            assert(it.arguments[3].toString() == expectedObj.toString()) { "Expected: " + expectedObj.toString() + "\nGot: " + it.arguments[3].toString()}
            // We don't test arguments 4 or 5 because the callback and error handler don't matter
            requestSent = true
            Unit
        }
        model.declineRequest(testSender)
        assert(requestSent)
    }
    @Test
    fun test_requestIncoming() {
        Userinfo.u_id = 1
        Userinfo.username = "testuser"
        val expectedUrlID = "http://cs309-vc-5.misc.iastate.edu:8080/friends/getIncomingByUserID?userID=1"
        val expectedUrlUsername = "http://cs309-vc-5.misc.iastate.edu:8080/friends/getIncomingByUsername?username=testuser"
        var requestSent = false
        Mockito.`when`(mVolleyHandler.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrlID || it.arguments[0] == expectedUrlUsername)
            assert(it.arguments[1] == Request.Method.GET)
            // We don't test arguments 2, 3, 4, or 5 because they don't matter in this case
            requestSent = true
            Unit
        }
        model.requestIncoming()
        assert(requestSent)
    }

    @Test
    fun test_requestOutgoing() {
        Userinfo.u_id = 1
        Userinfo.username = "testuser"
        val expectedUrlID = "http://cs309-vc-5.misc.iastate.edu:8080/friends/getOutgoingByUserID?userID=1"
        val expectedUrlUsername = "http://cs309-vc-5.misc.iastate.edu:8080/friends/getOutgoingByUsername?username=testuser"
        var requestSent = false
        Mockito.`when`(mVolleyHandler.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrlID || it.arguments[0] == expectedUrlUsername) { "Expected: " + expectedUrlID + " or " + expectedUrlUsername + "\nGot: " + it.arguments[0]}
            assert(it.arguments[1] == Request.Method.GET)
            // We don't test arguments 2, 3, 4, or 5 because they don't matter in this case
            requestSent = true
            Unit
        }
        model.requestOutgoing()
        assert(requestSent)
    }

    @Test
    fun test_handleIncoming() {
        var actualList: List<User>? = null
        Mockito.`when`(mListener.updateIncomingRequests(TestHelper.any())).thenAnswer {
            actualList = it.arguments[0] as List<User>
            Unit
        }
        model.handleIncoming(testJSON)
        assert(actualList != null)
        assert(actualList!!.size == 2)
        assert(actualList!!.contains(testReceiver))
        assert(actualList!!.contains(testSender))
    }

    @Test
    fun test_handleOutgoing() {
        var actualList: List<User>? = null
        Mockito.`when`(mListener.updateOutgoingRequests(TestHelper.any())).thenAnswer {
            actualList = it.arguments[0] as List<User>
            Unit
        }
        model.handleOutgoing(testJSON)
        assert(actualList != null)
        assert(actualList!!.size == 2)
        assert(actualList!!.contains(testReceiver))
        assert(actualList!!.contains(testSender))
    }

}