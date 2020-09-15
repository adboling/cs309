package jrising.myapplication.net_utils

/**
 * An object class that keeps user information that is Used throughout the application
 */
object Userinfo {
 /**
  * The username of the signed in user, blank if guest
  */
   var username = ""
 /**
  * the user's id that is not publicly show to the endClient but used throughout the app
  * to easily distinguishes the user from other
  */
    var u_id = 0
 /**
  * The first name of the signed in user blank if guest
  */
    var firstName = ""
 /**
  * The last name of the signed in user blank if guest
  */
    var lastName = ""
 /**
  * the role of the the end user is "user" is normal user
  * "guest" for guest and "currator" for currator
  */
    var role = ""
 /**
  * Tells the app if the User is logged in
  */
    var bio = ""
    var loggedIn = false



}