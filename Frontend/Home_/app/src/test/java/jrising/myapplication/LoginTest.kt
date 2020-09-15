import android.content.Context

import jrising.myapplication.TestHelper
import jrising.myapplication.login_signup.BasicLoginHandler
import jrising.myapplication.login_signup.LoginActivity
import jrising.myapplication.login_signup.LoginHandler
import jrising.myapplication.login_signup.signUpPage
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.VolleyController
import jrising.myapplication.net_utils.VolleyHandler
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.mock
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import javax.annotation.meta.When


public class LoginTests {

    lateinit var loginActivity: LoginActivity

    lateinit var mVolleyController: VolleyController
    lateinit var loginHandler: BasicLoginHandler
    lateinit var signUpPage: signUpPage

    @Before
    fun setup() {
        loginActivity = LoginActivity()
        mVolleyController = mock(VolleyController::class.java)
        loginActivity.volleyController = mVolleyController
        loginHandler = BasicLoginHandler()
        signUpPage= signUpPage()
        Mockito.`when`(mVolleyController.methodGet).thenReturn(0)

    }







    @Test
    fun loginHandler_validateInputs_resultsTrue() {
        val mock = mock(LoginHandler::class.java)
        var email = "test@gmail.com"
        var password = "test"


        var emailFail = "test"
        var emptyFail = ""
        var testURL = "" + Const.URL_VC5_LOGIN + "email=" + email + "&password=" + password
        val rEmail = "email"
        val rPassword = "password"

        var loghandler = Mockito.spy(loginHandler)

        Mockito.doReturn(true).`when`(loghandler).checkEmail(email)
        Mockito.doReturn(false).`when`(loghandler).checkEmail(emailFail)
        Mockito.doReturn(false).`when`(loghandler).checkEmail(emptyFail)


        Assert.assertEquals(testURL, loginHandler.validateInputs(email, password))
        Assert.assertEquals(rEmail, loginHandler.validateInputs(emptyFail, password))
        Assert.assertEquals(rPassword, loginHandler.validateInputs(email, emptyFail))

        // var return = loginHandler.validateInputs(email,password)
        //  Mockito.`when`(mock.validateInputs(email, password)).thenReturn(testURL)
        // Mockito.`when`(mock.validateInputs(emailFail, password)).thenReturn(rEmail)
        //Mockito.`when`(mock.validateInputs(email, passwordFail)).thenReturn(rEmail)

    }

    @Test
    fun signUp_checkforDub(){
        var testJson = Mockito.mock(JSONObject::class.java)
        var testEmailFail = "test@gmail.com"
        var testUnameFail = "aUsername"
        var SuccUsername = "notUname"
        var SuccEmail = "noTest@gmail.com"
        var SpySignup = Mockito.spy(signUpPage)
        Mockito.doReturn(true).`when`(SpySignup).setError("username")
        Mockito.doReturn(true).`when`(SpySignup).setError("email")

        Mockito.`when`(testJson.getString("email")).thenReturn("test@gmail.com")
        Mockito.`when`(testJson.getString("username")).thenReturn("aUsername")
        Mockito.`when`(testJson.getInt("id")).thenReturn(23)

        //Assert.assertEquals(false,SpySignup.matchCheck(testJson, SuccEmail,SuccUsername ))
        //Assert.assertEquals(true, SpySignup.matchCheck(testJson, testEmailFail,SuccUsername))
        //Assert.assertEquals(true, SpySignup.matchCheck(testJson, SuccEmail, testUnameFail))

    }

    @Test
    fun validate_signUp(){
        var testEmail = "test@gmail.com"
        var testUname = "aUsername"
        var SuccUsername = "notUname"
        var SuccEmail = "noTest@gmail.com"

        var  SpySignup1 = Mockito.spy(signUpPage)
        Mockito.doReturn(false).`when`(SpySignup1).setError("first")
        Mockito.doReturn(false).`when`(SpySignup1).setError("last")
        Mockito.doReturn(false).`when`(SpySignup1).setError("email")
        Mockito.doReturn(false).`when`(SpySignup1).setError("password")

       // Assert.assertEquals(true,SpySignup1.validate(testEmail, "Password123","test", "ATestName","username" ))
       // Assert.assertEquals(true, SpySignup1.matchCheck(testJson, testEmailFail,SuccUsername))
       // Assert.assertEquals(true, SpySignup1.matchCheck(testJson, SuccEmail, testUnameFail))

    }


}




    
