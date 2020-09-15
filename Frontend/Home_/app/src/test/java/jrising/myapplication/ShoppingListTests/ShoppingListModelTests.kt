package jrising.myapplication.ShoppingListTests

import com.android.volley.Request
import jrising.myapplication.ShoppingList.IShoppingListListener
import jrising.myapplication.ShoppingList.ShoppingListItem
import jrising.myapplication.ShoppingList.ShoppingListModel
import jrising.myapplication.TestHelper
import jrising.myapplication.app.Ingredient
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.internal.runners.statements.Fail
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShoppingListModelTests {
    @Mock
    lateinit var mListener: IShoppingListListener
    @Mock
    lateinit var mVolleyHandler: VolleyHandler

    lateinit var model: ShoppingListModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        model = ShoppingListModel(mListener, mVolleyHandler)
    }

    @Test
    fun testAddItem() {
        Userinfo.u_id = 0
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/add?userID=0"
        val expectedGetUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/get?userID=0"
        val expectedObj = JSONObject()
        expectedObj.put("amount", 5.0)
        expectedObj.put("name", "test ingredient")
        expectedObj.put("unit", "test unit")
        val testIngredient = Ingredient("test ingredient", 5.0, "test unit")
        var called = false
        Mockito.`when`(mVolleyHandler.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            if(it.arguments[0] == expectedGetUrl) return@thenAnswer Unit
            assert(it.arguments[0] == expectedUrl) { "Expected: " + expectedUrl + "\nGot: " + it.arguments[0]}
            assert(it.arguments[1] == Request.Method.POST)
            // We don't test the third argument because its only used for logging
            val arr: JSONArray? = it.arguments[3] as? JSONArray
            if(arr == null || arr.length() == 0) { assert(false) } else {
                val obj: JSONObject? = arr[0] as? JSONObject
                if (obj == null) {
                    assert(false)
                } else {
                    if (obj.optDouble("amount") != 5.0) assert(false)
                    if (obj.optString("name") != "test ingredient") assert(false)
                    if (obj.optString("unit") != "test unit") assert(false)
                }
            }
            // We don't test args 4 or 5 because they don't matter to this test
            called = true
            Unit
        }
        model.addItem(testIngredient)
        assert(called)
    }

    @Test
    fun testModifyItem() {
        Userinfo.u_id = 0
        val testOldItem = ShoppingListItem(false, Ingredient("Test Ingredient", 5.0, "Test Unit"), 0)
        val testNewItem = ShoppingListItem(false, Ingredient("Potato", 1.0, "Pound"), 0)
        val expectedDeleteUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/delete?entryID=0"
        val expectedCreateUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/add?userID=0"
        val expectedGetUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/get?userID=0"
        val expectedObj = JSONObject()
        expectedObj.put("name", "Potato")
        expectedObj.put("amount", 1.0)
        expectedObj.put("unit", "Pound")

        var deleted = false
        var created = false

        Mockito.`when`(mVolleyHandler.createStringRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any())).thenAnswer {
            val actualUrl = it.arguments[0] as? String
            if(actualUrl == expectedGetUrl) return@thenAnswer Unit
            assert(actualUrl == expectedDeleteUrl) {"\nExpected: " + expectedDeleteUrl + "\nGot      " + actualUrl }
            assert(it.arguments[1] == Request.Method.DELETE)
            // We don't test any other arguments because they don't matter
            deleted = true
            Unit
        }

        Mockito.`when`(mVolleyHandler.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            if(it.arguments[0] == expectedGetUrl) return@thenAnswer Unit
            assert(it.arguments[0] == expectedCreateUrl)
            assert(it.arguments[1] == Request.Method.POST)

            val arr = it.arguments[3] as? JSONArray
            if(arr == null || arr.length() == 0) { assert(false) } else  {
                val obj = arr[0] as? JSONObject
                if(obj == null) { assert(false) } else {
                    if (obj.optDouble("amount") != 1.0) assert(false)
                    if (obj.optString("name") != "Potato") assert(false)
                    if (obj.optString("unit") != "Pound") assert(false)
                }
            }
            created = true
            Unit

        }
        model.editItem(testOldItem, testNewItem)
        assert(deleted)
        assert(created)
    }

    @Test
    fun testRemoveItem() {
        val testItem = ShoppingListItem(false, Ingredient("", 0.0, ""), 5)
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/delete?entryID=5"
        var deleted = false
        Mockito.`when`(mVolleyHandler.createStringRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.DELETE)
            deleted = true
            Unit
        }
        model.removeItem(testItem)
        assert(deleted)
    }

    @Test
    fun requestShoppingList() {
        var requested = false
        Userinfo.u_id = 0
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/get?userID=0"
        Mockito.`when`(mVolleyHandler.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl)
            assert(it.arguments[1] == Request.Method.GET)
            val f = it.arguments[4] as? (JSONObject)->Unit
            if(f == null) assert(false)
            requested = true
            // We don't test anything else because they don't matter for the test
            Unit
        }
        model.requestShoppingList()
        assert(requested)
    }

    @Test
    fun testLoadShoppingList() {
        var loaded = false
        val testString = "[\n" +
                "  {\n" +
                "    \"entryID\": 6,\n" +
                "    \"userID\": 0,\n" +
                "    \"name\": \"potato\",\n" +
                "    \"unit\": \"pound\",\n" +
                "    \"amount\": 1.0,\n" +
                "    \"complete\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"entryID\": 7,\n" +
                "    \"userID\": 0,\n" +
                "    \"name\": \"milk\",\n" +
                "    \"unit\": \"cup\",\n" +
                "    \"amount\": 1.5,\n" +
                "    \"complete\": 1\n" +
                "  }\n" +
                "]"
        val testInput = JSONArray(testString)
        val expectedList: MutableList<ShoppingListItem> = ArrayList()
        expectedList.add(ShoppingListItem(false, Ingredient("potato", 1.0, "pound"), 6))
        expectedList.add(ShoppingListItem(true, Ingredient("milk", 1.5, "cup"), 7))
        Mockito.`when`(mListener.displayShoppingList(TestHelper.any())).thenAnswer {
            val list: List<ShoppingListItem>? = it.arguments[0] as? List<ShoppingListItem>
            if(list == null) assert(false)
            expectedList.forEach{
                assert(list!!.contains(it))
            }
            loaded = true
            Unit
        }
        model.loadShoppingList(testInput)
        assert(loaded)
    }

    @Test
    fun testEditComplete() {
        var edit = false
        val testItem = ShoppingListItem(true, Ingredient("", 0.0, ""), 1)
        val testComplete = false
        val expectedUrl = "http://cs309-vc-5.misc.iastate.edu:8080/shoppinglist/modifyComplete?complete=0&entryID=1"
        Mockito.`when`(mVolleyHandler.createStringRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any())).thenAnswer {
            assert(it.arguments[0] == expectedUrl) { "Expected: " + expectedUrl + "\nGot: " + it.arguments[0] }
            assert(it.arguments[1] == Request.Method.PUT)
            // All the other parameters don't actually matter
            edit = true
            Unit
        }
        model.editComplete(testItem, testComplete)
        assert(edit)
    }
}