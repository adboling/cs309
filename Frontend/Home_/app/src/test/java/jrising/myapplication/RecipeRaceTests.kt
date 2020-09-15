package jrising.myapplication

import android.app.Activity
import android.content.res.Resources
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.View
import android.widget.*
import jrising.myapplication.RecipeRacer.*
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.socketHandler
import junit.framework.Assert.assertEquals

import junit.framework.Assert.fail
import junit.framework.TestSuite
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(Suite::class)
@Suite.SuiteClasses(
    RecipeRaceModelTests::class,
    RecipeRaceFragmentTests::class
)
class RecipeRaceTests

/**********************************************************************************************************************
 * Some basic tests for the RecipeRaceFragment                                                                        *
 * Honestly, its more like tests for the mocking than anything else because the fragment just passes everything to    *
 * the controller anyway, but whatever. More tests = more better                                                      *
 **********************************************************************************************************************/
@RunWith(RobolectricTestRunner::class)
class RecipeRaceFragmentTests {
    @Mock
    lateinit var mRRController: RecipeRaceController

    lateinit var recipeRaceFragment: RecipeRaceFragment

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        recipeRaceFragment = RecipeRaceFragment()
        recipeRaceFragment.controller = mRRController
        Userinfo.username = "test user"
        Userinfo.u_id = 0
    }

    @Test
    fun rrController_testConnect_noSession() {
        recipeRaceFragment.recipeID = "test-recipe-id"
        recipeRaceFragment.sessionID = 0
        // If the fragment tries to call the connect method in the controller, fail
        // It shouldn't try to connect if the ID is 0
        Mockito.`when`(mRRController.connect(Mockito.anyInt())).thenAnswer{
            fail()
        }
        // Call the method that gets called when the connect button is pressed
        recipeRaceFragment.connectButtonPressed()
    }

    @Test
    fun rrController_testConnect_validSession() {
        recipeRaceFragment.recipeID = "test-recipe-id"
        recipeRaceFragment.sessionID = 1
        val mButton = Mockito.mock(Button::class.java)
        val mResources = Mockito.mock(Resources::class.java)
        recipeRaceFragment._rr_connectDisconnect = mButton
        recipeRaceFragment._resources = mResources
        Mockito.`when`(mRRController.connect(Mockito.anyInt())).thenAnswer{
            val args = it.arguments
            if(args[0] !is Int) fail()
            assertEquals(1, args[0])
            return@thenAnswer null
        }
        Mockito.`when`(mRRController.setSessionID(Mockito.anyInt())).thenAnswer{
            val args = it.arguments
            if(args[0] !is Int) fail()
            return@thenAnswer null
        }
        recipeRaceFragment.connectButtonPressed()
    }

    @Test
    fun rrController_testDisconnect() {
        val mActivity = Mockito.mock(Activity::class.java)
        recipeRaceFragment._activity = mActivity
        var called = false
        recipeRaceFragment.connected = true
        Mockito.`when`(mRRController.disconnect()).thenAnswer{
            called = true
            return@thenAnswer null
        }
        recipeRaceFragment.connectButtonPressed()
        assert(called)
    }

    @Test
    fun rrController_testSendMessage() {
        recipeRaceFragment.connected = true
        recipeRaceFragment.sessionID = 1
        recipeRaceFragment.recipeID = "test-recipe-id"
        val mEditText = Mockito.mock(TextInputEditText::class.java)
        val mText = Mockito.mock(Editable::class.java)
        recipeRaceFragment._rr_input_message = mEditText
        Mockito.`when`(mEditText.text).thenReturn(mText)
        Mockito.`when`(mText.toString()).thenReturn("test message")
        Mockito.`when`(mRRController.sendMessage("test message")).thenAnswer {
            val args = it.arguments
            if(args[0] !is String) assert(false)
            val message = args[0] as String
            assertEquals("test message", message)
        }
        recipeRaceFragment.sendMessage()
    }

    @Test
    fun rrFragment_testHandleChat() {
        val mListView: ListView = Mockito.mock(ListView::class.java)
        val mActivity: Activity = Mockito.mock(Activity::class.java)
        val mMainLayout: RelativeLayout = Mockito.mock(RelativeLayout::class.java)
        var updated = false
        val input = "test message"
        val spyFragment = Mockito.spy(recipeRaceFragment)
        spyFragment._rr_chatlist = mListView
        spyFragment._rr_main_layout = mMainLayout
        spyFragment._activity = mActivity

        Mockito.doCallRealMethod().`when`(spyFragment).handleMessage(Mockito.anyString())
        Mockito.doAnswer{
            val view = it.arguments[0] as View
            if(view == mListView) {
                updated = true
            }
            return@doAnswer null
        }.`when`(spyFragment).updateView(TestHelper.any())
        Mockito.`when`(mListView.postInvalidate()).thenAnswer{
            updated = true
            return@thenAnswer null
        }


        spyFragment.handleMessage(input)
        assert(updated) {"expected RecipeRaceFragment.updateView() to be called!"}
        assertEquals("Expected chatList to have length of 1!", 1, spyFragment.chatList.size)
        assertEquals("test message", spyFragment.chatList[0])
    }

    @Test
    fun rrFragment_testUpdate() {
        val mChrono = Mockito.mock(Chronometer::class.java)
        val mSession = Mockito.mock(TextView::class.java)
        val mStartStop = Mockito.mock(Button::class.java)
        val mConnectDisconnect = Mockito.mock(Button::class.java)
        val mParticipantList = Mockito.mock(RecyclerView::class.java)
        val mIngredientList = Mockito.mock(RecyclerView::class.java)
        val mSteps = Mockito.mock(TextView::class.java)
        val mChatList = Mockito.mock(ListView::class.java)
        val mInputMessage = Mockito.mock(TextInputEditText::class.java)
        val mSend = Mockito.mock(Button::class.java)
        val mActivity = Mockito.mock(Activity::class.java)
        val mMainLayout = Mockito.mock(RelativeLayout::class.java)
        val mResources = Mockito.mock(Resources::class.java)
        recipeRaceFragment._rr_chrono = mChrono
        recipeRaceFragment._rr_sessionID = mSession
        recipeRaceFragment._rr_startStop = mStartStop
        recipeRaceFragment._rr_connectDisconnect = mConnectDisconnect
        recipeRaceFragment._rr_participantList = mParticipantList
        recipeRaceFragment._rr_ingredientList = mIngredientList
        recipeRaceFragment._rr_steps = mSteps
        recipeRaceFragment._rr_chatlist = mChatList
        recipeRaceFragment._rr_input_message = mInputMessage
        recipeRaceFragment._rr_send = mSend
        recipeRaceFragment._rr_main_layout = mMainLayout
        recipeRaceFragment._resources = mResources

        val participants = HashSet<RRParticipant>()
        participants.add(RRParticipant("User1", 1, 0, 7))
        participants.add(RRParticipant("User2", 2, 5, 7))
        val ingredients = ArrayList<RRIngredient>()
        val l: Long = 500
        val input = RecipeRaceData(
            0,
            true,
            15,
            3,
            true,
            "testID",
            ingredients,
            participants,
            "test steps"
        )

        var chronoUpdated = false
        var layoutUpdated = false

        recipeRaceFragment._activity = mActivity
        Mockito.`when`(mResources.getString(Mockito.anyInt())).thenReturn("Session:")

        val spyFragment = Mockito.spy(recipeRaceFragment)
        Mockito.`when`(mChrono.start()).thenAnswer{
            chronoUpdated = true
            return@thenAnswer null
        }
        Mockito.doAnswer{
            val args = it.arguments
            if(args.size != 2) fail("Expected exactly two arguments for setChrono!")
            if(args[0] !is Long) fail("Expected argument one of setChrono to be a long!")
            if(args[1] !is Boolean) fail("Expected argument two of setChrono to be a boolean!")
            val actual = args[0] as Long
            val expected = l - 15
            assertEquals("setChrono base did not match!", expected, actual)
            val actual1 = args[1] as Boolean
            val expected1 = true
            assertEquals("setChrono argument 2 should be true!", actual1, expected1)
            return@doAnswer null
        }.`when`(spyFragment).setChrono(Mockito.anyLong(), Mockito.anyBoolean())

        Mockito.doAnswer{
            val args = it.arguments
            when(args[0]) {
                mSession -> {
                    val expected = "Session:3"
                    val actual = args[1] as String
                    assertEquals("Session text didn't match!", expected, actual)
                }
                mSteps -> {
                    val expected = "test steps"
                    val actual = args[1] as String
                    assertEquals("Steps text didn't match!", expected, actual)
                }
            }
        }.`when`(spyFragment).setText(TestHelper.any(), Mockito.anyString())

        Mockito.doAnswer{
            if(it.arguments[0] !is View) fail("Expected to get a View in updateView!")
            val view = it.arguments[0] as View
            if(view == mMainLayout) {
                layoutUpdated = true
            }
        }.`when`(spyFragment).updateView(TestHelper.any())
        Mockito.doCallRealMethod().`when`(spyFragment).update(TestHelper.any())
        Mockito.doReturn(l).`when`(spyFragment).getTime()
        spyFragment.update(input)

        assertEquals(spyFragment.participantList, participants.toMutableList())
        assertEquals(spyFragment.ingredientList, ingredients)
        assert(layoutUpdated){"Expected main layout to get updated!"}
    }
}

@RunWith(RobolectricTestRunner::class)
class RecipeRaceModelTests {
    lateinit var model: RecipeRaceModel

    @Mock
    lateinit var mListener: IRecipeRaceListener

    @Mock
    lateinit var mSocketHandler: socketHandler

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        model = RecipeRaceModel()
        model.listener = mListener
        model.sc = mSocketHandler
    }

    @Test
    fun RRModel_SendMessageTest() {
        Userinfo.username = "test user"
        Mockito.`when`(mSocketHandler.send(Mockito.anyString())).thenAnswer{
            val args = it.arguments
            if(args[0] !is String) fail()
            val actual = args[0] as String
            val expected = "{\"" + RecipeRaceModel.json_type + "\":\"" + RecipeRaceModel.type_chat + "\",\"" + RecipeRaceModel.json_message + "\":\"test message\",\"" +
                    RecipeRaceModel.json_sender + "\":\"test user\"}"
            assertEquals(expected, actual)
        }
        model.sendChat("test message")
    }

    @Test
    fun RRModel_toggleTimeTest() {
        model.state.running = false
        Mockito.`when`(mListener.update(TestHelper.any())).thenAnswer{
            val args = it.arguments
            if(args[0] !is RecipeRaceData) fail()
            val data = args[0] as RecipeRaceData
            assertEquals(true, data.running)
        }
    }

    @Test
    fun RRModel_handleStringMessageTest() {
        val input = "test message"
        Mockito.`when`(mListener.handleMessage(TestHelper.any())).thenAnswer{
            val args = it.arguments
            if(args[0] !is String) fail("Expected to get a String!")
            val actual = args[0] as String
            val expected = "test message"
            assertEquals(expected, actual)
        }
        model.handleMessage(input)
    }

    @Test
    fun RRModel_handleJSONArrayMessage() {
        val input = JSONArray()
        input.put("test")
        input.put("values")
        Mockito.`when`(mListener.handleMessage(TestHelper.any ())).thenAnswer{
            val args = it.arguments
            if(args[0] !is String) fail("Expected to get a String")
            val actual = args[0] as String
            val expected = "Received a JSONArray!"
            assertEquals(expected, actual)
        }
        model.handleMessage(input)
    }

    @Test
    fun RRModel_handleJSONObjectMessage() {
        val input = JSONObject()
        input.put("arg1", "test")
        input.put("arg2", "values")
        Mockito.`when`(mListener.handleMessage(Mockito.anyString())).thenAnswer{
            val args = it.arguments
            if(args[0] !is String) fail("Expected to get a String!")
            val actual = args[0] as String
            val expected = "Received an invalid JSONObject!"
            assertEquals(expected, actual)
        }
        model.handleMessage(input)
    }

    @Test
    fun RRModel_handlePlayerJoin_isHost() {
        var sentData = false
        var sentMessage = false
        val input = JSONObject()
        input.put(RecipeRaceModel.json_type, RecipeRaceModel.type_join)
        input.put(RecipeRaceModel.json_player, "Test Player")
        model.isHost = true
        Mockito.`when`(mSocketHandler.send(Mockito.anyString())).thenAnswer{
            val args = it.arguments
            if(args.size != 1) fail("Improper number of arguments!")
            if(args[0] !is String) fail("Expected to get a String!")
            val actual = args[0] as String
            val expected = "{\"participants\":[{\"player\":\"Test Player\",\"u_id\":0,\"progress\":0,\"totalProgress\":0}],\"runnning\":false,\"elapsedTime\":0,\"recipeID\":\"\",\"ingredients\":\"[]\",\"type\":\"data\"}"
            assertEquals(expected, actual)
            sentData = true
            return@thenAnswer null
        }
        Mockito.`when`(mListener.handleMessage(Mockito.anyString())).thenAnswer{
            val args = it.arguments
            if(args.size != 1) fail("Improper number of arguments!")
            if(args[0] !is String) fail("Expected to get a String!")
            val actual = args[0] as String
            val expected = "Test Player has joined the game!"
            assertEquals(actual, expected)
            sentMessage = true
            return@thenAnswer null
        }
        model.handleMessage(input)
        assert(sentData && sentMessage)
    }
    @Test
    fun RRModel_handlePlayerJoin_isNotHost() {
        var sent = false
        val input = JSONObject()
        input.put(RecipeRaceModel.json_type, RecipeRaceModel.type_join)
        input.put(RecipeRaceModel.json_player, "Test Player")
        model.isHost = false
        Mockito.`when`(mSocketHandler.send(Mockito.anyString())).thenAnswer{
            fail("Model should not be sending any data when a player joins if it is not the host!")
        }
        Mockito.`when`(mListener.handleMessage(Mockito.anyString())).thenAnswer{
            val args = it.arguments
            if(args.size != 1) fail("Improper number of arguments!")
            if(args[0] !is String) fail("Expected to get a String!")
            val actual = args[0] as String
            val expected = "Test Player has joined the game!"
            assertEquals(actual, expected)
            sent = true
            return@thenAnswer null
        }
        model.handleMessage(input)
        assert(sent)

    }

    @Test
    fun RRModel_handleDataMessage() {
        var sent = false

        val participants = JSONArray()
        val participant1 = JSONObject()
        participant1.put(RecipeRaceModel.json_player, "User1")
        participant1.put(RecipeRaceModel.json_userID, 1)
        participant1.put(RecipeRaceModel.json_progress, 0)
        participant1.put(RecipeRaceModel.json_participant_total_progress, 7)
        val participant2 = JSONObject()
        participant2.put(RecipeRaceModel.json_player, "User2")
        participant2.put(RecipeRaceModel.json_userID, 2)
        participant2.put(RecipeRaceModel.json_progress, 7)
        participant2.put(RecipeRaceModel.json_participant_total_progress, 7)
        participants.put(participant1)
        participants.put(participant2)

        val ingredients = JSONArray()
        val ingredient1 = JSONObject()
        ingredient1.put(RecipeRaceModel.json_ingredient_type, "ingredient1")
        ingredient1.put(RecipeRaceModel.json_ingredient_amount, 1.0)
        ingredient1.put(RecipeRaceModel.json_ingredient_unit, "cup")
        ingredient1.put(RecipeRaceModel.json_ingredient_complete, false)
        ingredients.put(ingredient1)
        val ingredient2 = JSONObject()
        ingredient2.put(RecipeRaceModel.json_ingredient_type, "ingredient2")
        ingredient2.put(RecipeRaceModel.json_ingredient_amount, 5.0)
        ingredient2.put(RecipeRaceModel.json_ingredient_unit, "oz")
        ingredient2.put(RecipeRaceModel.json_ingredient_complete, true)
        ingredients.put(ingredient2)

        val input = JSONObject()
        input.put(RecipeRaceModel.json_type, RecipeRaceModel.type_data)
        input.put(RecipeRaceModel.json_elapsedTime, 5)
        input.put(RecipeRaceModel.json_running, false)
        input.put(RecipeRaceModel.json_recipeID, "testID")
        input.put(RecipeRaceModel.json_ingredients, ingredients)
        input.put(RecipeRaceModel.json_participants, participants)


        Mockito.`when`(mListener.update(TestHelper.any())).thenAnswer{
            val args = it.arguments
            if(args.size != 1) fail("Improper number of arguments!")
            if(args[0] !is RecipeRaceData) fail ("Expected recipe race data!")
            val actual = args[0] as RecipeRaceData
            assertEquals("Connected did not match!", false, actual.connected)
            assertEquals("Elapsed time did not match!", 5, actual.elapsedTime)
            assertEquals("RecipeID did not match!","testID", actual.recipeID)
            assertEquals("SessionID did not match!",0, actual.sessionID)
            assertEquals("Ingredients size did not match!", 2, actual.ingredients.size)
            val expectedIngredient1 = RRIngredient("ingredient1", 1.0, "cup", false)
            val expectedIngredient2 = RRIngredient("ingredient2", 5.0, "oz", true)
            assert(actual.ingredients.contains(expectedIngredient1)) {"Ingredient1 was not included!"}
            assert(actual.ingredients.contains(expectedIngredient2)) {"Ingredient2 was not included!"}
            assertEquals("Participants size did not match!",2, actual.participants.size)
            val expectedParticipant1 = RRParticipant("User1", 1, 0, 7)
            val expectedParticipant2 = RRParticipant("User2", 2, 7, 7)
            assert(actual.participants.contains(expectedParticipant1)) {"Participant1 was not included!"}
            assert(actual.participants.contains(expectedParticipant2)) {"Participant2 was not included!"}
            sent = true

            return@thenAnswer(null)
        }
        model.handleMessage(input)
        assert(sent, {"Listener was not updated!"})
    }

}