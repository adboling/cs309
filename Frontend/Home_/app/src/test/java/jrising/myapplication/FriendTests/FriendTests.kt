package jrising.myapplication.FriendTests

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    FriendRequestListFragmentTests::class,
    FriendRequestListModelTests::class
)
class FriendTests