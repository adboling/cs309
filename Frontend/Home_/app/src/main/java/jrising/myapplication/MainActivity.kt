package jrising.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment

import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import jrising.myapplication.Friends.FriendRequestListFragment
import jrising.myapplication.Admin_controls.Admin_Control
import jrising.myapplication.MyTimeline.MyTImelLineFragment
import jrising.myapplication.Pantry.PantryFragment
import jrising.myapplication.Profiles.ARG_DATA
import jrising.myapplication.Profiles.ARG_HANDLER
import jrising.myapplication.Profiles.HANDLER_BASIC
import jrising.myapplication.Profiles.UserProfile
import jrising.myapplication.RecipeCreate.RecipeCreateFragment
import jrising.myapplication.RecipeRacer.RecipeRacerChooser
import jrising.myapplication.RecipeSearch.RecipeSearchFragment
import jrising.myapplication.ShoppingList.ShoppingListFragment
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.app.AppController
import jrising.myapplication.app.mainFragmentContainer
import jrising.myapplication.homeScreen.homeScreen
import jrising.myapplication.login_signup.LoginActivity
import jrising.myapplication.userSearch.UserSearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

/**
 * Main Activity for the app that acts like a switch board
 * holding the appbar and having other fragments overlay over a blank user space
 * @Vc-5 (Kitchen Korner)
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    mainFragmentContainer {
    var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppController.eventBus.registerMainFragmentContainer(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //make the user login before continuing
        OnReturn()


    }

    /**
     *
     */
    fun OnReturn() {
        if (!Userinfo.loggedIn) {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            onLogin()
        }

    }


    /**
     * After logging in to the app this will check your user role and then direct you to the correct spot in the app
     * */
    fun onLogin() {
        if (Userinfo.role == "guest") {
            getNextView("guest")
        } else {
            getNextView("homescreen")
        }
        AppController.instance.setUserController(Userinfo.role)
        val id = AppController.instance.getNavigationMenu()
        if (id != null) {
            nav_view.menu.clear()
            nav_view.inflateMenu(id)
            nav_view.setNavigationItemSelectedListener {
                onNavigationItemSelected(it)
            }

        }
    }


    /**
     * replaces main activity with homescreen after login
     */
    private fun getNextView(nextView: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        var fragment: Fragment
        if (nextView == "guest") {
            fragment = RecipeSearchFragment()
        } else fragment = homeScreen()

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    /**
     * handles the what happens when the back button is pressed so that it doesn't break the app there are
     * different setting for different fragments open
     */
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * inflates the appbar menu to navigate around the app
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * will will open up profile page if selected
     * @return the setting that was selected in this case only profile is clickable
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return when(item.itemId) {

            R.id.profile_link -> {
                goToProfile()
                false


            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    /**
     * when profile icon is pressed will go and move to user profile fragement
     * takes in Username and a boolean that is if it the clients own profile
     */
    private fun goToProfile(){

        val fragment = UserProfile()
        var args = Bundle()
        args.putString("uName", Userinfo.username)
        args.putBoolean("personalProfile", true) //used to for extra features that would not be there normally
        args.putBundle(ARG_DATA, Bundle())
        args.putString(ARG_HANDLER, HANDLER_BASIC)
        fragment.arguments = args
        AppController.eventBus.switchMainFragment(fragment)
    }

    /**
     * this function is a big switch case that will open up the fragment that was selected
     * @param item the item that was selected from the appbar menu
     *
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.getHomeScreen ->{
                val fragment = homeScreen()
                AppController.eventBus.switchMainFragment(fragment)
            }
            R.id.rSearch -> {
                val fragment = RecipeSearchFragment()
                AppController.eventBus.switchMainFragment(fragment)
            }
            R.id.rCreate -> {
                val fragment = RecipeCreateFragment()
                AppController.eventBus.switchMainFragment(fragment)
            }
            R.id.mTimeline -> {
                //opens myTimeLineFragement from menu
                val myTimeLine = MyTImelLineFragment()
                var args = Bundle()
                args.putString("uName", Userinfo.username)
                args.putBoolean("isOwner", true)
                myTimeLine.arguments = args
                AppController.eventBus.switchMainFragment(myTimeLine)
            }

            R.id.rr_select -> {
                val recipeRacer = RecipeRacerChooser()
                AppController.eventBus.switchMainFragment(recipeRacer)

            }
            R.id.logout -> {
                clearUserdata()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.Admin -> {
                val fragment = Admin_Control()
                AppController.eventBus.switchMainFragment(fragment)

            }
            R.id.pantry -> {
                val pantry = PantryFragment()
                AppController.eventBus.switchMainFragment(pantry)
            }
            R.id.userSearch -> {
                val fragment = UserSearchFragment.newInstance(null)
                AppController.eventBus.switchMainFragment(fragment)
            }
            R.id.friendRequest -> {
                val fragment = FriendRequestListFragment.newInstance()
                AppController.eventBus.switchMainFragment(fragment)
            }
            R.id.shoppingList -> {
                val fragment = ShoppingListFragment.newInstance()
                AppController.eventBus.switchMainFragment(fragment)
            }


            /*  R.id.mCookbook -> {
                  // Don't have this fragment yet
              }
              R.id.slist -> {
                  // Don't have this fragment yet
              }
              R.id.util -> {
                  // Don't have this fragment yet
              }
              R.id.settings -> {
                  // Don't have this fragment yet
              }
              */



        }

        fragmentTransaction.commit()
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }



        private  fun clearUserdata(){
        Userinfo.username = ""
        Userinfo.u_id = 0
        Userinfo.firstName = ""
        Userinfo.lastName = ""
        Userinfo.role = ""
        Userinfo.loggedIn = false

    }

    /**
     * handle switching framgnets from various points in the class
     * @param fragment takes in the fragment you want to switch to
     */
    override fun handleFragmentSwitch(fragment : Fragment) {
        // Save a snapshot of the current fragment
        val oldFragment = currentFragment
        // Check if the currentFragment is null (none have been created) or the new fragment is a different type
        if(oldFragment == null || (fragment::class != oldFragment::class)) {
            // Save the new fragment to the activity
            currentFragment = fragment
            // Get the fragment manager
            val fragmentManager = supportFragmentManager
            // Start a fragment transaction
            val fragmentTransaction = fragmentManager.beginTransaction()
            // Replace the old fragment with the new fragment
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack(null)
            // Commit the transaction
            fragmentTransaction.commit()
        }
    }
}
