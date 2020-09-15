package jrising.myapplication.login_signup
import android.content.Context
import android.content.Intent
import android.icu.text.DisplayContext
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import jrising.myapplication.MainActivity
import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.AppController.Companion.volleyController
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import jrising.myapplication.net_utils.VolleyController
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONObject
import java.lang.NumberFormatException
import java.util.regex.Pattern

/**
 * Activity used as a starting point in the app.
 * You can either sign in to get a normal user view
 * or currator view depending on your profile's role.
 * or you can sign in as a guest for limited use
 * @author Justin Kuennen
 */
class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var signLink: TextView
    private lateinit var guestButton: Button
    lateinit var volleyController: VolleyHandler

    private lateinit var handleLogin: BasicLoginHandler

    /**
     * function is called on activity creation, sets up all of the layout widgets
     * will attempt to signin the user on loginButton Click calling the onSubmit() function
     * will start the [signupPage] on signLink button press. As well as switching to guest mode
     * on guestButton click
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //init layout inputs

        emailText = findViewById(R.id.input_email)
        passwordText = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.btn_login)
        signLink = findViewById(R.id.link_signup)
        volleyController = VolleyController(this)
        handleLogin = BasicLoginHandler()
        guestButton = findViewById(R.id.guestbtn)
        if (Userinfo.loggedIn) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        signLink.setOnClickListener() {
            var intent = Intent(this, signUpPage::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            var emailTry = emailText.text.toString()
            var passwordTry = passwordText.text.toString()
            onSubmit(emailTry, passwordTry)

        }


        guestButton.setOnClickListener{
            goToGuestView()
        }

    }
    private fun goToGuestView(){

        Userinfo.loggedIn = true
        Userinfo.role = "guest"
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    /**
     * Will start the login process first calling the helper class [handleLogin]
     * to validate the two parameters before calling the server
     * @param email the email to attempt to login in with
     * @param password the password to attempt to login with
     */
    fun onSubmit(email: String, password: String) {



        var validResults = handleLogin.validateInputs(email, password)

        if (validResults == "email") {
            emailText.error = "email format not valid try again"
        } else if (validResults == "password") {
            passwordText.error = "password entry is blank"
        } else {
            emailText.error = null
            passwordText.error = null

            handleLogin.Attemptlogin(validResults, this)

        }
    }}











/*
    private fun checkLogin() {
        var url = "" + Const.URL_VC5_LOGIN + "email=" + emailTry + "&password=" + passwordTry


        val strReg = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                checkL(response)
            }, Response.ErrorListener { error ->
                VolleyLog.d(TAG, "Error: " + error.message)
            }
        )

        // Adding request to request queue
        AppController.volleyController.addToRequestQueue(strReg, TAG)
    }*/


/*
    private fun checkUser(id: Int) {
        Userinfo.u_id = id

        var url = "" + Const.URL_VC5_USER_BY_ID + "id=" + id


        val JSONReq = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                userUpdate(response)
            }, Response.ErrorListener { error ->
                VolleyLog.d(TAG, "Error: " + error.message)
            }
        )

        // Adding request to request queue
        AppController.volleyController.addToRequestQueue(JSONReq, TAG)
    }
*/



