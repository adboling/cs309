package jrising.myapplication.ShoppingListTests

import android.support.design.widget.TextInputEditText
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import jrising.myapplication.ShoppingList.IShoppingListController
import jrising.myapplication.ShoppingList.ShoppingListFragment
import jrising.myapplication.ShoppingList.ShoppingListItem
import jrising.myapplication.ShoppingList.ShoppinglistAdapter
import jrising.myapplication.TestHelper
import jrising.myapplication.app.Ingredient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShoppingListFragmentTests {
    @Mock
    lateinit var mTitle: TextView
    @Mock
    lateinit var mList: RecyclerView
    @Mock
    lateinit var mNameGroup: ViewGroup
    @Mock
    lateinit var mNameField: TextInputEditText
    @Mock
    lateinit var mAmountGroup: ViewGroup
    @Mock
    lateinit var mAmountField: TextInputEditText
    @Mock
    lateinit var mUnitGroup: ViewGroup
    @Mock
    lateinit var mUnitField: TextInputEditText
    @Mock
    lateinit var mAddIngredientButton: Button

    @Mock
    lateinit var mAdapter: ShoppinglistAdapter
    @Mock
    lateinit var mController: IShoppingListController

    lateinit var fragment: ShoppingListFragment

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        fragment = ShoppingListFragment()
        fragment.title = mTitle
        fragment.list = mList
        fragment.nameGroup = mNameGroup
        fragment.nameField = mNameField
        fragment.amountGroup = mAmountGroup
        fragment.amountField = mAmountField
        fragment.unitGroup = mUnitGroup
        fragment.unitField = mUnitField
        fragment.addIngredientButton = mAddIngredientButton
        fragment.adapter = mAdapter
        fragment.controller = mController
    }

    @Test
    fun testDisplayShoppingList() {
        val testList: MutableList<ShoppingListItem> = ArrayList()
        testList.add(ShoppingListItem(false, Ingredient("test ingredient", 1.0, "test unit"), 0))
        testList.add(ShoppingListItem(true, Ingredient("herp", 5.0, "derp"), 1))
        var resultList: MutableList<ShoppingListItem>? = null
        Mockito.`when`(mAdapter.notifyDataSetChanged()).thenAnswer {
            resultList = fragment.items
            Unit
        }
        fragment.displayShoppingList(testList)
        assert(resultList != null)
        assert(resultList!!.containsAll(testList))
    }

    @Test
    fun testItemComplete() {
        val testIngredient = Ingredient("test ingredient", 1.0, "test unit")
        val testItem = ShoppingListItem(true, testIngredient, 0)
        var called = false
        Mockito.`when`(mController.editComplete(TestHelper.any(), Mockito.anyBoolean())).thenAnswer {
            val old = it.arguments[0] as? ShoppingListItem ?: assert(false) { "Expected argument 0 to be a ShoppingListItem!" }
            val bool = it.arguments[1] as? Boolean ?: assert(false) { "Expected argument 1 to be a boolean! " }
            assert(old == testItem)
            assert(bool == false)
            called = true
            Unit
        }
        fragment.itemComplete(testItem, false)
        assert(called)
    }

    @Test
    fun testItemEdited() {
        val testOldItem = ShoppingListItem(false, Ingredient("test ingredient", 1.0, "test unit"), 0)
        val testNewItem = ShoppingListItem(true, Ingredient("herp", 5.0, "derp"), 0)
        var called = false
        Mockito.`when`(mController.editItem(TestHelper.any(), TestHelper.any())).thenAnswer {
            val old = it.arguments[0] as? ShoppingListItem ?: assert(false) { "Expected argument 0 to be a shoppingListItem!" }
            val new = it.arguments[1] as? ShoppingListItem ?: assert(false) { "Expected arguemnt 1 to be a shoppingListItem!" }
            assert(old == testOldItem)
            assert(new == testNewItem)
            called = true
            Unit
        }
        fragment.itemEdited(testOldItem, testNewItem)
        assert(called)
    }

    @Test
    fun testItemRemoved() {
        val testItem = ShoppingListItem(false, Ingredient("test ingredient", 1.0, "test unit"), 0)
        var called = false
        Mockito.`when`(mController.removeItem(TestHelper.any())).thenAnswer {
            val item = it.arguments[0] as? ShoppingListItem ?: assert(false) { "Expected the argument to be a shoppingListItem!" }
            assert(item == testItem)
            called = true
            Unit
        }
        fragment.itemRemoved(testItem)
        assert(called)
    }
}