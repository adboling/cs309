package jrising.myapplication

import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import android.widget.Button
import com.android.volley.Request
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import jrising.myapplication.Pantry.*
import jrising.myapplication.app.Ingredient
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyController
import jrising.myapplication.net_utils.VolleyHandler
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

@RunWith(Suite::class)
@Suite.SuiteClasses(
    PantryFragmentTests::class,
    PantryModelTests::class
)
class PantryTests

@RunWith(RobolectricTestRunner::class)
class PantryFragmentTests {
    @Mock
    lateinit var mLLM: LinearLayoutManager
    @Mock
    lateinit var mAdapter: PantryIngredientAdapter
    @Mock
    lateinit var mModel: IPantryModel
    @Mock
    lateinit var mTypeIn: TextInputEditText
    @Mock
    lateinit var mAmountIn: TextInputEditText
    @Mock
    lateinit var mUnitIn: TextInputEditText
    @Mock
    lateinit var mAddButton: Button
    @Mock
    lateinit var mTypeGroup: TextInputLayout
    @Mock
    lateinit var mAmountGroup: TextInputLayout
    @Mock
    lateinit var mUnitGroup: TextInputLayout

    var fragment = PantryFragment()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        fragment.model = mModel
        fragment.adapter = mAdapter
        fragment.llm = mLLM
        fragment.typeIn = mTypeIn
        fragment.amountIn = mAmountIn
        fragment.unitIn = mUnitIn
        fragment.addButton = mAddButton
        fragment.typeGroup = mTypeGroup
        fragment.amountGroup = mAmountGroup
        fragment.unitGroup = mUnitGroup
    }

    @Test
    fun test_displayPantry_emptyList() {
        val testList = ArrayList<PantryItem>()
        val resultList = ArrayList<PantryItem>()
        Mockito.`when`(mAdapter.items).thenReturn(resultList)
        fragment.displayPantry(testList)
        assert(resultList.isEmpty()) { "Expected the result list to be empty!" }
    }

    @Test
    fun test_displayPantry_validList() {
        val testList = ArrayList<PantryItem>()
        testList.add(PantryItem(Ingredient("Test Ingredient", 1.0, "Test Unit"), 0))
        testList.add(PantryItem(Ingredient("Test Ingredient 2", 2.0,"Test Unit"), 1))
        var resultList: List<PantryItem>? = null
        mAdapter = mock {
            whenever(mAdapter.updateIngredients(TestHelper.any())).thenAnswer {
                resultList = it.arguments[0] as List<PantryItem>
                null
            }
        }
        fragment.displayPantry(testList)
        assert(resultList != null) { "Expected result list to exist! " }
        assert(resultList!!.isNotEmpty()) { "Expected the result list to be non-empty!" }
        assert(resultList!!.size == 2) { "Expceted the result list to have two elements!" }
        assert(resultList!!.contains(PantryItem(Ingredient("Test Ingredient", 1.0, "Test Unit"),0))) {
            "Expected result array to contain the first test ingredient!"
        }
        assert(resultList!!.contains(PantryItem(Ingredient("Test Ingredient 2", 2.0, "Test Unit"), 1))) {
            "Expected result array to contain the second test ingredient!"
        }
    }


    @Test
    fun test_addButtonPressed_notAdding() {
        fragment.addingIngredient = false
        var visible = false
        val spyFragment = Mockito.spy(fragment)
        doAnswer {
            assert(it.arguments.size == 1) { "Expected arguments to have a size of 1! "}
            assert(it.arguments[0] is Int) { "Expected argument 0 to be an Int!" }
            assert(it.arguments[0] == View.VISIBLE) { "Expected a call to setVisibilities(View.VISIBLE!" }
            visible = true
        }.whenever(spyFragment).setVisibilities(any())
        spyFragment.onAddButtonPressed()
        assert(visible) { "Expected views to be set to visible!" }
    }

    @Test
    fun test_addButtonPressed_Adding() {
        var added = false
        fragment.addingIngredient = true
        whenever(mTypeIn.text).thenReturn(Editable.Factory().newEditable("Test Ingredient"))
        whenever(mAmountIn.text).thenReturn(Editable.Factory().newEditable("1.0"))
        whenever(mUnitIn.text).thenReturn(Editable.Factory().newEditable("Test Unit"))
        whenever(mAdapter.addIngredient(any())).thenAnswer{
            assert(it.arguments.size == 1) { "Expected arguments to have a size of 1!" }
            assert(it.arguments[0] is Ingredient) { "Expected to get an ingredient!" }
            assert(it.arguments[0] == Ingredient("Test Ingredient", 1.0, "Test Unit"))
            added = true
            null
        }
        fragment.onAddButtonPressed()
        assert(added) { "Expected an ingredient to be added!" }
    }
}

@RunWith(RobolectricTestRunner::class)
class PantryModelTests {
    lateinit var mPantryListener: IPantryListener
    lateinit var mVolleyController: VolleyHandler

    lateinit var model: IPantryModel

    @Before
    fun setup() {
        mPantryListener = Mockito.mock(IPantryListener::class.java)
        mVolleyController = Mockito.mock(VolleyController::class.java)
        MockitoAnnotations.initMocks(this)
        model = PantryModel(mPantryListener, mVolleyController)
    }

    @Test
    fun test_requestPantry() {
        var requested = false
        Userinfo.u_id = 0
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/pantry?userID=0"
        Mockito.`when`(mVolleyController.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer{
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.GET)
            requested = true
            null
        }
        model.requestPantry()
        assert(requested)
    }

    @Test
    fun test_loadPantry_jsonArray() {
        var displayed = false
        val expectedList = ArrayList<PantryItem>()
        expectedList.add(PantryItem(Ingredient("potato", 1.0, "pound"), 0))
        val testObj = JSONObject()
        testObj.put("id", 0)
        testObj.put("ingredient", "potato")
        testObj.put("userID", 0)
        val testJSON = JSONArray()
        testJSON.put(testObj)
        Mockito.`when`(mPantryListener.displayPantry(TestHelper.any())).thenAnswer{
            assert(it.arguments[0] is List<*>)
            val list = it.arguments[0] as List<*>
            assert(list.size == 1)
            val item = list[0] as PantryItem
            assert(item.id == expectedList[0].id)
            assert(item.ingredient.type == expectedList[0].ingredient.type) { "Expected: " + expectedList[0].ingredient.type + "\nGot: " + item.ingredient.type }
            displayed = true
            null
        }
        model.loadPantry(testJSON)
        assert(displayed)
    }

    @Test
    fun test_onIngredientChanged() {
        var deleted = false
        var created = false
        Userinfo.u_id = 0
        val oldPantryItem = PantryItem(Ingredient("Test Ingredient", 1.0, "Yeet"), 0)
        val newIngredient = Ingredient("YEET Ingredient", 5.0, "Test")
        val newPantryItem = PantryItem(newIngredient, 5)
        Mockito.`when`(mVolleyController.createStringRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any())).thenAnswer {
            if(it.arguments[0] == "http://cs309-vc-5.misc.iastate.edu:8080/pantry/del?pantryID=0") {
                assert(it.arguments[1] == Request.Method.DELETE)
                val f = it.arguments[3] as (String) -> Unit
                f("\"deleted\"")
                deleted = true
            }
        }
        Mockito.`when`(mVolleyController.createJSONRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == "http://cs309-vc-5.misc.iastate.edu:8080/pantry")
            assert(it.arguments[1] == Request.Method.POST)
            assert(it.arguments[3] is JSONObject)
            val obj = it.arguments[3] as JSONObject
            assert(obj.getString("ingredient").toString() == newIngredient.type)
            assert(obj.getInt("userID") == 0)
            created = true
            null
        }
        model.onIngredientChanged(oldPantryItem, newPantryItem)
        assert(deleted)
        assert(created)
    }

    @Test
    fun test_onIngredientRemoved() {
        var deleted = false
        val testItem = PantryItem(Ingredient("Test Ingredient", 1.0, "Yote"), 5)
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/pantry/del?pantryID=5"
        Mockito.`when`(mVolleyController.createStringRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.DELETE)
            deleted = true
            null
        }
        model.onIngredientRemoved(testItem)
        assert(deleted)
    }

    @Test
    fun test_onIngredientAdded() {
        Userinfo.u_id = 9
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/pantry"
        val testIngredient = Ingredient("Test Ingredient", 1.0, "Yost")
        val testItem = PantryItem(testIngredient, 404)
        var created = false
        Mockito.`when`(mVolleyController.createJSONRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.POST)
            assert(it.arguments[3] is JSONObject)
            val obj = it.arguments[3] as JSONObject
            assert(obj.getInt("userID")==9)
            assert(obj.getString("ingredient") == testIngredient.type)
            created = true
            null
        }
        model.onIngredientAdded(testItem)
        assert(created)
    }
}