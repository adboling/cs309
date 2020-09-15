package jrising.myapplication.login_signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.widget.Toast
import jrising.myapplication.MainActivity
import jrising.myapplication.app.AppController.Companion.volleyController
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import org.json.JSONObject
import java.lang.NumberFormatException
import java.util.regex.Pattern

/**
 * handler class to assist with logging a user in, calling the server via Andorid Volley
 * and validating results
 * @author Justin Kuennen
 *
 */
interface LoginHandler{

    fun Attemptlogin(url:String,context: Context)


  fun validateInputs(email: String,password: String):String
    fun userUpdate(resp: JSONObject, context: Context)

}

/**
 * Extension from the [LoginHandler] interface the implements validation logic
 * and calls the server checking if a user exist, updating the [Userinfo] if
 * credentials are correct
 */
class BasicLoginHandler : LoginHandler {

    /**
     * validates the email and password to see if there is not any obvious problems
     * like the email being in the right format or being empty
     * @param email passed in email to test
     * @param password passed in password to test
     * @return a url to try the with [Attemptlogin] or return email or password which will throw errors on screen
     *
      */
 override  fun validateInputs(email: String, password: String): String {
        var validVar = "" //return url for client if valid , email if email is wrong, and password if password is empty
        val testEmail = email
        val testPassword = password
        if (testEmail.isEmpty() ||!checkEmail(email)) {
            validVar =  "email"
        } else {
            if (testPassword.isEmpty()) {
                validVar ="password"
            } else {
                validVar =  "" + Const.URL_VC5_LOGIN + "email=" + email + "&password=" + password
            }
        }

        return validVar
    }


    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

     fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }


    /**
     * Will use the generated url and call the server to check the given credentials
     * with the servers credentials to find a match and calls[checklogin]
     * @param url the url of the server with the email and password added on
     * @param context context of the login activity kept so that we can go back to the activity or to the next activity
     */
   override fun Attemptlogin(url: String, context: Context) {
        var tag = "Login"


        volleyController.createStringRequest(
            url,
            volleyController.methodGet,
            tag,
            { response -> checklogin(response, context) },
            {error -> Toast.makeText(context,"Error Network Error",Toast.LENGTH_LONG).show()}
        )


    }

    /**
     * checks the response to see if it is -1 which indicates that the login failed otherwise then the response
     * will be the user_id and will be sent back to server to get the users information,
     * on response it will call [userUpdate]
     * @param response response back from the server
     * @param context context of the [loginActivity] kept so that we can return to it easily and switch to the next activity
     */
    fun checklogin(response: String, context: Context) {

        var responseTest = 0
        try {
            responseTest = response.toInt()
        } catch (nfe: NumberFormatException) {


        }

        if (responseTest <= -1) {
            Toast.makeText(context, "Email or Password is Incorrect try again", Toast.LENGTH_LONG).show()

        } else {
            var tag = "getId"
            var url = "" + Const.URL_VC5_USER_BY_ID + "id=" + responseTest

            volleyController.createJSONRequest(
                url,
                volleyController.methodGet,
                tag, null,
                { response -> userUpdate(response, context) },
                {error -> Toast.makeText(context,"Error Network Error",Toast.LENGTH_LONG).show()}
            )

        }
    }

     override fun userUpdate(resp: JSONObject, context: Context) {


        Userinfo.username = resp.getString("username")
        Userinfo.u_id = resp.getInt("id")
        Userinfo.firstName = resp.getString("firstname")
        Userinfo.lastName = resp.getString("lastname")
        Userinfo.role = resp.getString("role")
        Userinfo.u_id = resp.getInt("id")
        Userinfo.bio  = resp.getString("bio")

        Userinfo.loggedIn = true
        var intent = Intent(context, MainActivity::class.java)
        startActivity(context, intent, null)


    }

}

