package jrising.myapplication

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import jrising.myapplication.RecipeSearch.RecipeSearchFragment
import jrising.myapplication.RecipeSearch.recipeSummary
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.VolleyController
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner

@RunWith(Suite::class)
@Suite.SuiteClasses(
    RecipeSearchTest::class,
    RecipeSearchRobolectricTest::class
)
class RecipeSearchTests

@RunWith(MockitoJUnitRunner.Silent::class)
class RecipeSearchTest {
    lateinit var recipeSearchFragment: RecipeSearchFragment

    lateinit var mVolleyController: VolleyHandler

    val mFragmentManager = Mockito.mock(FragmentManager::class.java)

    @Before
    fun setUp() {
        recipeSearchFragment = RecipeSearchFragment()
        mVolleyController = Mockito.mock(VolleyController::class.java)
        recipeSearchFragment.volleyController = mVolleyController
        Mockito.`when`(mVolleyController.methodGet).thenReturn(0)
        recipeSearchFragment._childFragmentManager = mFragmentManager
    }

    @Test
    fun recipeSearch_userRecipeSearch_testURL() {
        val url = Const.URL_VC5_RECIPES + "/search?q=" + "test+terms+"
                Mockito.`when`(mVolleyController.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).
                    thenAnswer{
                        val args = it.arguments
                        var result = args[0] == url
                        result = result && (args[1] == 0)
                        result = result && (args[2].equals("UserRSearch"))
                assert(result)
            }
        recipeSearchFragment.requestSearchResults("User recipe search", arrayOf("test","terms"))
    }
}

@RunWith(RobolectricTestRunner::class)
class RecipeSearchRobolectricTest {
    val recipeSearchFragment = RecipeSearchFragment()

    @Mock
    lateinit var mVolleyController: VolleyController

    @Mock
    lateinit var mFragmentController: FragmentManager

    @Mock
    lateinit var mFragmentTransaction: FragmentTransaction

    @Before
    fun setupMocks() {
        MockitoAnnotations.initMocks(this)
    }

    @Before
    fun init() {
        recipeSearchFragment.volleyController = mVolleyController
        recipeSearchFragment._childFragmentManager = mFragmentController
    }

    fun generateTestJSONArray(): JSONArray {
        val testJSON = JSONArray()
        val match1 = JSONObject()
        match1.put("recipeID", "ID1")
        match1.put("recipeName", "name1")
        match1.put("userID", "user1")
        val match2 = JSONObject()
        match2.put("recipeID", "ID2")
        match2.put("recipeName", "name2")
        match2.put("userID", "user2")
        testJSON.put(match1)
        testJSON.put(match2)
        return testJSON
    }

    @Test
    fun recipeSearch_userRecipeSearch_full() {
        val fragments: MutableList<Fragment> = ArrayList()
        val testJSON = generateTestJSONArray()
        val url = Const.URL_VC5_RECIPES + "/search?q=" + "test+terms+"
        Mockito.`when`(mVolleyController.createJSONArrayRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).
            thenAnswer{
                val args = it.arguments
                if(args[0] != url) assert(false)
                if(args[1] != 0) assert(false)
                if(args[2] != "UserRSearch") assert(false)
                if(args[4] !is Function<*>) assert(false)
                val function = args[4] as (JSONArray)->Unit
                function(testJSON)
                assert(true)
            }

        Mockito.`when`(mFragmentController.beginTransaction()).thenReturn(mFragmentTransaction)

        Mockito.`when`(mFragmentTransaction.add(Mockito.anyInt(), TestHelper.any())).thenAnswer{
            val args = it.arguments
            if(args[0] != R.id.rSearch_resultContainer) assert(false)
            val fragment = args[1]
            if(fragment !is recipeSummary) assert(false)
            fragments += fragment as recipeSummary
            return@thenAnswer mFragmentTransaction
        }
        recipeSearchFragment.requestSearchResults("User recipe search", arrayOf("test", "terms"))

        assert(fragments.count() == 2)
        assert(fragments[0] is recipeSummary)
        assert(fragments[1] is recipeSummary)
        val rs1 = fragments[0] as recipeSummary
        val rs2 = fragments[1] as recipeSummary
        assert(rs1.id == "ID1")
        assert(rs1.name == "name1")
        assert(rs1.type == "user")
        assert(rs2.id == "ID2")
        assert(rs2.name == "name2")
        assert(rs2.type == "user")
    }

    @Test
    fun recipeSearch_yummlyRecipeSearch_full() {
        val fragments: MutableList<Fragment> = ArrayList()
        val testJSON = generateTestJSONObject()
        val testURL = "http://cs309-vc-5.misc.iastate.edu:8080/search/q=potato+"
        Mockito.`when`(mVolleyController.createJSONRequest(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), TestHelper.any(), TestHelper.any(), TestHelper.any())).thenAnswer{
            val args = it.arguments
            if(args[0] != testURL) assert(false)
            if(args[1] != 0) assert(false)
            if(args[2] != "YummlyRSearch") assert(false)
            if(args[4] !is Function<*>) assert(false)
            val function = args[4] as (JSONObject)->Unit
            function(testJSON)
            assert(true)
        }


        Mockito.`when`(mFragmentController.beginTransaction()).thenReturn(mFragmentTransaction)

        Mockito.`when`(mFragmentTransaction.add(Mockito.anyInt(), TestHelper.any())).thenAnswer{
            val args = it.arguments
            if(args[0] != R.id.rSearch_resultContainer) assert(false)
            val fragment = args[1]
            if(fragment !is recipeSummary) assert(false)
            fragments += fragment as recipeSummary
            return@thenAnswer mFragmentTransaction
        }

        recipeSearchFragment.requestSearchResults("Yummly recipe search", arrayOf("potato"))
        assert(testYummlyResults(fragments))
    }

    fun testYummlyResults(fragments: MutableList<Fragment>): Boolean {
        if(fragments.count() != 10) return false
        val summaries: MutableList<recipeSummary> = ArrayList()
        fragments.forEach{fragment ->
            if(fragment !is recipeSummary) return false
            if(fragment.type != "yummly") return false
            summaries += fragment
        }
        if (summaries[0].id != "Hasselback-Potatoes-2657286") return false
        if (summaries[0].name != "Hasselback Potatoes") return false

        if (summaries[1].id != "Upside-Down-Baked-Potatoes-2699024") return false
        if (summaries[1].name != "Upside Down Baked Potatoes") return false

        if (summaries[2].id != "Buttery-Chateau-Potatoes-2472337") return false
        if (summaries[2].name != "Buttery Chateau Potatoes") return false

        if (summaries[3].id != "Hasselback-Potatoes-1612919") return false
        if (summaries[3].name != "Hasselback Potatoes") return false

        if (summaries[4].id != "The-Best-Pan-Fried-Breakfast-Potatoes-2185030") return false
        if (summaries[4].name != "The Best Pan-Fried Breakfast Potatoes") return false

        if (summaries[5].id != "Moroccan-Stuffed-Sweet-Potatoes-2639030") return false
        if (summaries[5].name != "Moroccan Stuffed Sweet Potatoes") return false

        if (summaries[6].id != "Perfect-Mashed-Potatoes-2554979") return false
        if (summaries[6].name != "Perfect Mashed Potatoes") return false

        if (summaries[7].id != "Parmesan-Roasted-Potatoes-2538996") return false
        if (summaries[7].name != "Parmesan Roasted Potatoes") return false

        if (summaries[8].id != "Easy-Scalloped-Potatoes-2313008") return false
        if (summaries[8].name != "Easy Scalloped Potatoes") return false

        if (summaries[9].id != "Parmesan-Red-Potatoes-2335192") return false
        if (summaries[9].name != "Parmesan Red Potatoes") return false

        return true
    }

    fun generateTestJSONObject(): JSONObject {
        return JSONObject(testJSONObjectString)
    }
}

val testJSONObjectString = "{\n" +
        "    \"criteria\": {\n" +
        "        \"q\": \"potato\",\n" +
        "        \"allowedIngredient\": null,\n" +
        "        \"excludedIngredient\": null\n" +
        "    },\n" +
        "    \"matches\": [\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/UxhB70l7MR_7rsYdYhB7v-6ZQ3QJSqLvLv0uaPtlKu1g5cxIxeAyt1sNDupM_n3OpAFZhuaEK1Iw14edQtPsfQ=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Barefeet In The Kitchen\",\n" +
        "            \"ingredients\": [\n" +
        "                \"potatoes\",\n" +
        "                \"olive oil\",\n" +
        "                \"kosher salt\",\n" +
        "                \"freshly ground black pepper\"\n" +
        "            ],\n" +
        "            \"id\": \"Hasselback-Potatoes-2657286\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/vMhds14kMVSbWgpHXOndJ7ZxRlUUELkIUnX5PsRjUtvqTsYkMz58fC_jEQNTXvS2ZvXNe--GSGSc7nEtF8yX=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Hasselback Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 3600,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ],\n" +
        "                \"holiday\": [\n" +
        "                    \"Sunday Lunch\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": null,\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/_e5LXjcRiA75AFVdlNVPKrtoELEOTzrZwdPixYZHa46sp7wwQF2_NWWZjN0fpBbAMhKzeL_k38SfOkCW3NzVHA=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Fit Recipes\",\n" +
        "            \"ingredients\": [\n" +
        "                \"melted butter\",\n" +
        "                \"grated parmesan cheese\",\n" +
        "                \"medium potatoes\"\n" +
        "            ],\n" +
        "            \"id\": \"Upside-Down-Baked-Potatoes-2699024\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/t_3jlH9z0PixLbfF9XW-AjWbDxYA1vNq4BdppooG-bJ4SM19CxXY1W3-ZQ-rGBqUH2glmBdM38qwUdljppSkzi4=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Upside Down Baked Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 2700,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ],\n" +
        "                \"holiday\": [\n" +
        "                    \"Sunday Lunch\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0,\n" +
        "                \"meaty\": 1,\n" +
        "                \"bitter\": 1,\n" +
        "                \"sweet\": 1,\n" +
        "                \"sour\": 1,\n" +
        "                \"salty\": 1\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/OElM-a96ezP8mcTSxj6do5Q_V5qBKDP7K1nt_AKJlnrdrZFW-JNxnWy3I4SBZIfx5E7nAtIaJX-i40vIDumTgg=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Amuse Your Bouche\",\n" +
        "            \"ingredients\": [\n" +
        "                \"new potatoes\",\n" +
        "                \"garlic\",\n" +
        "                \"butter\",\n" +
        "                \"olive oil\",\n" +
        "                \"salt\",\n" +
        "                \"black pepper\",\n" +
        "                \"fresh parsley\"\n" +
        "            ],\n" +
        "            \"id\": \"Buttery-Chateau-Potatoes-2472337\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/PvX36QFKqHGdkM4SVF23OU_2TE_QVCLwRAPS3hNSaMPHEiSTfuxdkaoaPkNAnVQee5fyPnCbHwxRqaOi8V7wyw=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Buttery Chateau Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 3300,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ],\n" +
        "                \"holiday\": [\n" +
        "                    \"Sunday Lunch\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0,\n" +
        "                \"meaty\": 0.16666666666666666,\n" +
        "                \"bitter\": 0.3333333333333333,\n" +
        "                \"sweet\": 0.16666666666666666,\n" +
        "                \"sour\": 0.8333333333333334,\n" +
        "                \"salty\": 0.16666666666666666\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/3HeXqIC0JObGs1osIQkN4Ub7AIczfumZVGQ7AsVgGZmp13JqOD9Mtj-hCd_2n9qzHMo7ba71F7_vyPtO3ezXQA=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Grits and Pinecones\",\n" +
        "            \"ingredients\": [\n" +
        "                \"baking potatoes\",\n" +
        "                \"butter\",\n" +
        "                \"olive oil\",\n" +
        "                \"kosher salt\",\n" +
        "                \"pepper\",\n" +
        "                \"chives\"\n" +
        "            ],\n" +
        "            \"id\": \"Hasselback-Potatoes-1612919\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/XkKmCCcCsRS49oeJlExBOX0ERiJdZ1anpMmdNF22uUrkegM9I8ZdR44wMxGumjw6s_rLRuYpDFqEJxRpaYu9MQ=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Hasselback Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 3900,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0,\n" +
        "                \"meaty\": 0.16666666666666666,\n" +
        "                \"bitter\": 0.8333333333333334,\n" +
        "                \"sweet\": 0,\n" +
        "                \"sour\": 0.6666666666666666,\n" +
        "                \"salty\": 0.16666666666666666\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/Rv-SOs4izR7DltbDtoHAKuwp6ofI-GBe_5Z2sOVg7_9hL0Yfx5AR2WF2iWKPPjwhJ8YyyPYHOHQTR9VRm1JW=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"The Foodie Patootie\",\n" +
        "            \"ingredients\": [\n" +
        "                \"baking potatoes\",\n" +
        "                \"canola oil\",\n" +
        "                \"salt\",\n" +
        "                \"parsley\",\n" +
        "                \"parmesan cheese\"\n" +
        "            ],\n" +
        "            \"id\": \"The-Best-Pan-Fried-Breakfast-Potatoes-2185030\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/4qkYSuBjNwDINXqwm4TGCNv96pwaFBxU3By9jJhtFr4nbgD3ivZNK-BmydPGbWNeTHwNejE3wiHgg4TsotUF=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"The Best Pan-Fried Breakfast Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 1500,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Breakfast and Brunch\",\n" +
        "                    \"Side Dishes\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0,\n" +
        "                \"meaty\": 0.16666666666666666,\n" +
        "                \"bitter\": 0.8333333333333334,\n" +
        "                \"sweet\": 0,\n" +
        "                \"sour\": 0.6666666666666666,\n" +
        "                \"salty\": 0.16666666666666666\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/gCuBbAmlH3Gch373Ocidqs5Cu3Dj2ZjiQrq1igBwUkyYlOJ52XZEXh_KLYHpUp7b0aH42UL851VWXeVzDBy-xg=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Choosing Chia\",\n" +
        "            \"ingredients\": [\n" +
        "                \"eggplant\",\n" +
        "                \"olive oil\",\n" +
        "                \"garlic cloves\",\n" +
        "                \"tomato paste\",\n" +
        "                \"cumin\",\n" +
        "                \"paprika\",\n" +
        "                \"lemon juice\",\n" +
        "                \"salt\",\n" +
        "                \"pepper\",\n" +
        "                \"water\",\n" +
        "                \"chickpeas\",\n" +
        "                \"sweet potatoes\",\n" +
        "                \"Tahini\",\n" +
        "                \"pomegranate seeds\",\n" +
        "                \"cilantro\"\n" +
        "            ],\n" +
        "            \"id\": \"Moroccan-Stuffed-Sweet-Potatoes-2639030\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/c19GiDZD5x-3U0va3bxduV8K-QLsiZVGxkJ0M_LLmeHkDR7nzuJh-I_fnzcMiqIeF8f8alxWziff2sJUYuzaPQ=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Moroccan Stuffed Sweet Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 4200,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ],\n" +
        "                \"cuisine\": [\n" +
        "                    \"Moroccan\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0.16666666666666666,\n" +
        "                \"meaty\": 0.16666666666666666,\n" +
        "                \"bitter\": 0.6666666666666666,\n" +
        "                \"sweet\": 0.16666666666666666,\n" +
        "                \"sour\": 0.3333333333333333,\n" +
        "                \"salty\": 0.3333333333333333\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/0KQrim887YgevGsEd8GfncdOcSPDH4aQtuB29uqOpjRmcE5jG0Qm0QAr4onHGpsfCjsWeD_i2OoSWz0sJOhhYj4=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Southern Living\",\n" +
        "            \"ingredients\": [\n" +
        "                \"yukon gold potatoes\",\n" +
        "                \"salt\",\n" +
        "                \"butter\",\n" +
        "                \"half and half\",\n" +
        "                \"cream cheese\",\n" +
        "                \"ground pepper\"\n" +
        "            ],\n" +
        "            \"id\": \"Perfect-Mashed-Potatoes-2554979\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/mKTfV0RfWlVjoRDu5fmMW04EHHaVVMg385jIG18wIw6p_kbV2kCxbuPyyMM181a_CYsRyz2h42S2JHyHvonnhQ=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Perfect Mashed Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 2100,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ],\n" +
        "                \"cuisine\": [\n" +
        "                    \"American\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0.16666666666666666,\n" +
        "                \"meaty\": 0.16666666666666666,\n" +
        "                \"bitter\": 0.8333333333333334,\n" +
        "                \"sweet\": 0.16666666666666666,\n" +
        "                \"sour\": 0.6666666666666666,\n" +
        "                \"salty\": 0.8333333333333334\n" +
        "            },\n" +
        "            \"rating\": 3\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/iO7WjBsPAsc2sB3TYhfACl9cfLHv580SxpbjOweHPEZdTlNjxLaCDTm888Lo9UrURp5rH5iKdmy8O2oGarmXhQ=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"I Wash You Dry\",\n" +
        "            \"ingredients\": [\n" +
        "                \"gold potatoes\",\n" +
        "                \"olive oil\",\n" +
        "                \"bread crumbs\",\n" +
        "                \"Italian seasoning\",\n" +
        "                \"salt\",\n" +
        "                \"grated parmesan cheese\"\n" +
        "            ],\n" +
        "            \"id\": \"Parmesan-Roasted-Potatoes-2538996\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/_zCbBU0KRp55ZD_uO8jZRLlQVibWP4EA-43rMVYAa6O4nVPMiaO55stzpgRq1UA2JOf4HKkvnh4ofX8VqH9R=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Parmesan Roasted Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 1500,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ],\n" +
        "                \"holiday\": [\n" +
        "                    \"Sunday Lunch\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0,\n" +
        "                \"meaty\": 0.16666666666666666,\n" +
        "                \"bitter\": 0.8333333333333334,\n" +
        "                \"sweet\": 0.16666666666666666,\n" +
        "                \"sour\": 0.6666666666666666,\n" +
        "                \"salty\": 0.8333333333333334\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/OgR46gwKgW6XdCrhY5-y7YIKwqHUJ70MQY6qjH2DGlBqIlN55XEvdSkauFbYH0u83_Sz6hsGky9xzo3gXyScUc0=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Julia's Album\",\n" +
        "            \"ingredients\": [\n" +
        "                \"butter\",\n" +
        "                \"yellow potatoes\",\n" +
        "                \"salt\",\n" +
        "                \"garlic cloves\",\n" +
        "                \"parmesan cheese\",\n" +
        "                \"thyme\",\n" +
        "                \"heavy cream\"\n" +
        "            ],\n" +
        "            \"id\": \"Easy-Scalloped-Potatoes-2313008\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/Cml5cZVUFBPeCww99-O6NPZpJGCWgAaIamwdz_CiyctY6A196qWQssC1WdlVucGB83AnPcmjUUx7x7rt5RTG6w=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Easy Scalloped Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 5100,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ],\n" +
        "                \"holiday\": [\n" +
        "                    \"Sunday Lunch\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0,\n" +
        "                \"meaty\": 0.16666666666666666,\n" +
        "                \"bitter\": 0.8333333333333334,\n" +
        "                \"sweet\": 0,\n" +
        "                \"sour\": 0.6666666666666666,\n" +
        "                \"salty\": 0.3333333333333333\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        },\n" +
        "        {\n" +
        "            \"imageUrlsBySize\": {\n" +
        "                \"90\": \"https://lh3.googleusercontent.com/wNh47bDw6S_yM3YvxfXiuKi0t6AWz2LXJeIC0z-xPdPV1x_5ezOkX6qV4-UC8P95o-dh9J8VgNoO0DBdfujRYA=s90-c\"\n" +
        "            },\n" +
        "            \"sourceDisplayName\": \"Naturally Ella\",\n" +
        "            \"ingredients\": [\n" +
        "                \"red potato\",\n" +
        "                \"olive oil\",\n" +
        "                \"garlic\",\n" +
        "                \"dried oregano\",\n" +
        "                \"vegan parmesan cheese\",\n" +
        "                \"fresh chives\"\n" +
        "            ],\n" +
        "            \"id\": \"Parmesan-Red-Potatoes-2335192\",\n" +
        "            \"smallImageUrls\": [\n" +
        "                \"https://lh3.googleusercontent.com/EiADscZ3_ey65GHOAPEHPrDUEjSnDwlZgvFPCxiUB6Pim0xMh5dG-sdh0cR5abx614aPvKXBk_hMB_QCniml=s90\"\n" +
        "            ],\n" +
        "            \"recipeName\": \"Parmesan Red Potatoes\",\n" +
        "            \"totalTimeInSeconds\": 2400,\n" +
        "            \"attributes\": {\n" +
        "                \"course\": [\n" +
        "                    \"Side Dishes\"\n" +
        "                ]\n" +
        "            },\n" +
        "            \"flavors\": {\n" +
        "                \"piquant\": 0,\n" +
        "                \"meaty\": 1,\n" +
        "                \"bitter\": 1,\n" +
        "                \"sweet\": 1,\n" +
        "                \"sour\": 1,\n" +
        "                \"salty\": 1\n" +
        "            },\n" +
        "            \"rating\": 4\n" +
        "        }\n" +
        "    ],\n" +
        "    \"facetCounts\": {},\n" +
        "    \"totalMatchCount\": 126198,\n" +
        "    \"attribution\": {\n" +
        "        \"html\": \"Recipe search powered by <a href='http://www.yummly.co/recipes'><img alt='Yummly' src='https://static.yummly.co/api-logo.png'/></a>\",\n" +
        "        \"url\": \"http://www.yummly.co/recipes/\",\n" +
        "        \"text\": \"Recipe search powered by Yummly\",\n" +
        "        \"logo\": \"https://static.yummly.co/api-logo.png\"\n" +
        "    }\n" +
        "}"