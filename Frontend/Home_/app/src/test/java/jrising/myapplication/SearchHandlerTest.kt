package jrising.myapplication

import android.os.Bundle
import jrising.myapplication.RecipeSearch.GeneralSearchHandler
import jrising.myapplication.RecipeSearch.RecipeSearchFragment
import jrising.myapplication.RecipeSearch.UserRecipeSearchHandler
import jrising.myapplication.RecipeSearch.YummlySearchHandler
import jrising.myapplication.net_utils.VolleyController
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class SearchHandlerTest {
    // The search handler we're testing
    lateinit var generalSearchHandler: GeneralSearchHandler

    // A pair of stubbed classes used by the above class
    val mUserSearchHandler = Mockito.mock(UserRecipeSearchHandler::class.java)
    val mYummlySearchHandler = Mockito.mock(YummlySearchHandler::class.java)

    // A stubbed volley controller to use for testing
    val mVolleyHandler = Mockito.mock(VolleyController::class.java)

    @Before
    fun setUp() {
        // Initialize the search handler
        generalSearchHandler = GeneralSearchHandler()
        // Add the mocked handlers to the general search handler
        generalSearchHandler.userHandler = mUserSearchHandler
        generalSearchHandler.yummlyHandler = mYummlySearchHandler
        generalSearchHandler.defaultHandler = mUserSearchHandler
        // Have the search handler reset its hashmap
        generalSearchHandler.resetHandlers()
    }

    @Test
    fun generalSearchHandler_yummlyRecipeSearchResults() {
        // Create a stubbed json object
        val testJSON = Mockito.mock(JSONObject::class.java)
        // Create a stub that will always fail when its called
        Mockito.`when`(mUserSearchHandler.getRecipeSearchResults(testJSON)).thenAnswer{
            assert(false)
            return@thenAnswer Bundle()
        }
        // Create a stub which will always pass when called
        Mockito.`when`(mYummlySearchHandler.getRecipeSearchResults(testJSON)).thenAnswer{
            assert(true)
            return@thenAnswer Bundle()
        }
        // Have the json object stub give the given value with the given arguments
        Mockito.`when`(testJSON.optString("type", "")).thenReturn("yummly")

        // Have the handler get search results
        generalSearchHandler.getRecipeSearchResults(testJSON)
    }

    @Test
    fun generalSearchHandler_yummlyRecipeSearch_NoType() {
        val testJSON = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(testJSON.optString("type","")).thenReturn("")
        Mockito.`when`(testJSON.optJSONObject("criteria")).thenReturn(testJSON)

        Mockito.`when`(mUserSearchHandler.getRecipeSearchResults(testJSON)).thenAnswer {
            assert(false)
            return@thenAnswer Bundle()
        }
        Mockito.`when`(mYummlySearchHandler.getRecipeSearchResults(testJSON)).thenAnswer {
            assert(true)
            return@thenAnswer Bundle()
        }

        generalSearchHandler.getRecipeSearchResults(testJSON)
    }

    @Test
    fun generalSearchHandler_userRecipeSearch_WithType() {
        val testJSON = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(testJSON.optString("type","")).thenReturn("user")
        Mockito.`when`(mUserSearchHandler.getRecipeSearchResults(testJSON)).thenAnswer {
            assert(true)
            return@thenAnswer Bundle()
        }
        Mockito.`when`(mYummlySearchHandler.getRecipeSearchResults(testJSON)).thenAnswer {
            assert(false)
            return@thenAnswer Bundle()
        }
        generalSearchHandler.defaultHandler = mYummlySearchHandler
        generalSearchHandler.resetHandlers()
        generalSearchHandler.getRecipeSearchResults(testJSON)
    }

    @Test
    fun generalSearchHandler_userRecipeSearch_NoType() {
        val testJSON = Mockito.mock(JSONObject::class.java)
        val testArr = Mockito.mock(JSONArray::class.java)
        Mockito.`when`(testJSON.optString("type","")).thenReturn("")
        Mockito.`when`(testJSON.optJSONObject("criteria")).thenReturn(null)
        Mockito.`when`(testJSON.optJSONArray("matches")).thenReturn(testArr)
        Mockito.`when`(mUserSearchHandler.getRecipeSearchResults(testArr)).thenAnswer {
            assert(true)
            return@thenAnswer Bundle()
        }
        Mockito.`when`(mYummlySearchHandler.getRecipeSearchResults(testJSON)).thenAnswer {
            assert(false)
            return@thenAnswer Bundle()
        }
    }

}