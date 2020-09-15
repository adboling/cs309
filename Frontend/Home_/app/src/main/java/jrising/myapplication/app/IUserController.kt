package jrising.myapplication.app

import android.view.Menu

/**
 * An interface defining the methods a UserController needs
 * The UserController is used to separate logic for different types of users
 */
interface IUserController {
    /**
     * A method to retrieve the navigation menu for different user types
     * @return The [Int] id for the proper navigation menu
     */
    fun navigationMenu() : Int
}