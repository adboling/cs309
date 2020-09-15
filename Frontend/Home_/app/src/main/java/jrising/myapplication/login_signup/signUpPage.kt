
package jrising.myapplication.login_signup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import org.json.JSONObject
/**
 * Activity that is used to create Users and send the to the server as well as logging the user in
 * @author Justin Kuennen
 *
 */
class signUpPage : AppCompatActivity() {
    private lateinit var firstTxt: EditText
    private lateinit var lastTxt: EditText
    private lateinit var emailTxt: EditText
    private lateinit var passwordTxt: EditText
    private lateinit var signupBut: Button
    private lateinit var loginTxt: TextView
    private lateinit var usernameTxt: EditText
    private var loginHandler:BasicLoginHandler = BasicLoginHandler()
    private var id = 0
    private var firstTry = ""
    private var lastTry = ""
    private var emailTry = ""
    private var passwordTry = ""
    private var UnameTry = ""


    private var TAG = "signupActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        firstTxt = findViewById(R.id.input_Fname)
        lastTxt = findViewById(R.id.input_Lname)
        emailTxt = findViewById(R.id.input_email)
        passwordTxt = findViewById(R.id.input_password)
        signupBut = findViewById(R.id.btn_signup)
        loginTxt = findViewById(R.id.link_login)
        usernameTxt = findViewById(R.id.input_Uname)


        loginTxt.setOnClickListener{
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signupBut.setOnClickListener{
            signup()
        }

    }

    private fun signup(){

        Log.d(TAG, "Login")
        emailTry = emailTxt.text.toString()
        passwordTry = passwordTxt.text.toString()
        firstTry = firstTxt.text.toString()
        lastTry = lastTxt.text.toString()
        UnameTry = usernameTxt.text.toString()
        if(!validate(emailTry, passwordTry, firstTry, lastTry, UnameTry)){
            onSignupFailed()
            return
        }



        createAccount()




    }

    fun setError(errorTry: String):Boolean{

        if(errorTry == "username"){
            usernameTxt.error = "Username already taken try another"
        }
        else if(errorTry == "usernameShort"){
            usernameTxt.error = "at least 6 characters"
        }

        else if(errorTry == "Emptyemail"){
            emailTxt.error = "enter a valid email address"
        }
        else if(errorTry == "first"){
            firstTxt.error = "at least 3 characters"
        }
        else if(errorTry == "password"){
            passwordTxt.error = "password must be at least 8 characters"
        }
        else if(errorTry == "email") {
            emailTxt.error ="Account for email already exist please login"

        }
        else if(errorTry == "last")
            lastTxt.error = "at least 3 characters"
        return true

    }
    //TODO make validator
    fun validate(emailTry: String,passwordTry:String, firstTry:String, lastTry:String, UnameTry: String): Boolean {

        var isvalid: Boolean = true
        if(firstTry.isEmpty()|| firstTry.length < 3){
            setError("first")
            isvalid = false
        }

        if(lastTry.isEmpty()|| lastTry.length < 3){

            setError("last")
            isvalid = false
        }
        //TODO add prevention from adding coding characters together
        if(UnameTry.isEmpty()|| UnameTry.length < 6){
            setError("usernameShort")
            isvalid = false
        }


        if(emailTry.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailTry).matches()) {
            setError("Emptyemail")
            isvalid = false
        }

        if(passwordTry.isEmpty() || passwordTry.length < 8 || passwordTry.length > 50){
            setError("password")
            isvalid = false
        }

        return isvalid
    }






    //TODO
    private fun onSignupFailed(){
        Toast.makeText(baseContext, "Account creation failed", Toast.LENGTH_LONG).show()
        signupBut.isClickable = true
    }


    private fun createAccount(){

        var url = "" + Const.URL_VC5_USER
        var toSend = assembleSignup()

        AppController.volleyController.createJSONRequest(url,
            AppController.volleyController.methodPost,"Create Account",
            toSend,{ response -> loginHandler.userUpdate(response, this) }, { error-> Toast.makeText(this,"$error"
            ,Toast.LENGTH_LONG ).show()})
    }
    private fun assembleSignup():JSONObject{
        var turnin = JSONObject()
        turnin.put("email", emailTry)
        turnin.put("firstname", firstTry)
        turnin.put("lastname", lastTry)
        turnin.put("password", passwordTry)
        turnin.put("username", UnameTry)
        turnin.put("role", "user")
        return turnin
    }





}
