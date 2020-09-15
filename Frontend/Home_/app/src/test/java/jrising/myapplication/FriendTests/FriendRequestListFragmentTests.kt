package jrising.myapplication.FriendTests

import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import jrising.myapplication.Friends.FriendRequestAdapter
import jrising.myapplication.Friends.FriendRequestListFragment
import jrising.myapplication.Friends.IFriendRequestController
import jrising.myapplication.TestHelper
import jrising.myapplication.app.User
import jrising.myapplication.net_utils.Userinfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FriendRequestListFragmentTests {
    @Mock
    lateinit var mTitle: TextView
    @Mock
    lateinit var mIncomingButton: Button
    @Mock
    lateinit var mOutgoingButton: Button
    @Mock
    lateinit var mIncomingList: RecyclerView
    @Mock
    lateinit var mOutgoingList: RecyclerView
    @Mock
    lateinit var mController: IFriendRequestController
    @Mock
    lateinit var mIncomingAdapter: FriendRequestAdapter
    @Mock
    lateinit var mOutgoingAdapter: FriendRequestAdapter

    lateinit var fragment: FriendRequestListFragment

    val testUser = User(
        "test avatar",
        "test background",
        "test bio",
        "test email",
        "John",
        "Smith",
        0,
        "user",
        "jsmith"
    )


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        fragment = FriendRequestListFragment()
        fragment.title = mTitle
        fragment.incomingButton = mIncomingButton
        fragment.outgoingButton = mOutgoingButton
        fragment.incomingList = mIncomingList
        fragment.outgoingList = mOutgoingList
        fragment.controller = mController
        fragment.incomingAdapter = mIncomingAdapter
        fragment.outgoingAdapter = mOutgoingAdapter
        Userinfo.username = "testuser"
        Userinfo.u_id = 15
    }

    @Test
    fun test_friendRequestAccepted() {
        var called = false
        Mockito.`when`(mController.acceptRequest(TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == testUser)
            called = true
            Unit
        }
        fragment.friendRequestAccepted(testUser)
        assert(called)
    }

    @Test
    fun test_friendRequestDenied() {
        var called = false
        Mockito.`when`(mController.declineRequest(TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == testUser)
            called = true
            Unit
        }
        fragment.friendRequestDeclined(testUser)
        assert(called)
    }

    @Test
    fun test_friendRequestCancelled() {
        var called = false
        Mockito.`when`(mController.cancelRequest(TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == testUser)
            called = true
            Unit
        }
        fragment.friendRequestCanceled(testUser)
        assert(called)
    }

    @Test
    fun test_updateIncomingRequests() {
        var updated = false
        Mockito.`when`(mIncomingAdapter.notifyDataSetChanged()).thenAnswer {
            updated = true
            Unit
        }
        fragment.updateIncomingRequests(listOf(testUser))
        assert(updated)
        assert(fragment.incomingRequests.size == 1)
        assert(fragment.incomingRequests[0] == testUser)
    }

    @Test
    fun test_updateOutgoingRequests() {
        var updated = false
        Mockito.`when`(mOutgoingAdapter.notifyDataSetChanged()).thenAnswer {
            updated = true
            Unit
        }
        fragment.updateOutgoingRequests(listOf(testUser))
        assert(updated)
        assert(fragment.outgoingRequests.size == 1)
        assert(fragment.outgoingRequests[0] == testUser)
    }
}