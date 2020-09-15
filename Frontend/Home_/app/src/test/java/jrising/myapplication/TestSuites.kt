package jrising.myapplication

import jrising.myapplication.FriendTests.FriendTests
import jrising.myapplication.ShoppingListTests.ShoppingListTests
import jrising.myapplication.UserSearchTests.UserSearchTests
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    RecipeRaceTests::class,
    RecipeSearchTests::class,
    PantryTests::class,
    UserSearchTests::class,
    FriendTests::class,
    ShoppingListTests::class
)
class TotalTestSuite