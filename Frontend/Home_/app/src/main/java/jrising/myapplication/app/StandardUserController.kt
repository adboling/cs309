package jrising.myapplication.app

import android.view.Menu
import jrising.myapplication.R
import jrising.myapplication.net_utils.Userinfo


/**
 * A user controller for a standard user. Has most of the functionality
 */
class StandardUserController : IUserController {
    override fun navigationMenu() : Int {
        var menuReturn: Int = if(Userinfo.role == "guest"){
            R.menu.guest_view_menu
        }
        else if(Userinfo.role == "admin"){
            R.menu.admin_view_drawer
        }
        else R.menu.activity_main_drawer
        return menuReturn
    }
}

@Deprecated("Not implemented")
class TestUserController : IUserController {
    override fun navigationMenu(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}