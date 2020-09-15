package jrising.myapplication.app

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.FragmentManager
import android.util.Base64
import android.util.Log
import jrising.myapplication.net_utils.VolleyController
import jrising.myapplication.net_utils.VolleyHandler
import java.io.ByteArrayOutputStream

/**
 * The class which acts as a singleton for some methods and controllers
 * @property TAG The debugging tag for the controller, depreciated
 * @property instance The global instance for the AppController
 * @property userController The [IUserController] for the app
 * @property volleyController The [jrising.myapplication.net_utils.VolleyController] for the app
 * @property eventBus The global [EventBus] for the app
 */
class AppController : Application() {
    companion object {
        val TAG = "AppController"
        lateinit var instance: AppController
            private set
        lateinit var userController : IUserController
        lateinit var volleyController: VolleyHandler
        lateinit var eventBus: EventBus
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        volleyController = VolleyController.getInstance(applicationContext)
        eventBus = EventBus()
    }

    /**
     * The method used to get the Int ID for the navigation menu for the specified user type
     * @return The [Int] ID for the navigation menu
     */
    fun getNavigationMenu() : Int? {
        return userController.navigationMenu()
    }

    /**
     * The method used to set the UserController for a given user type
     * @param userType The [String] user type for the app
     */
    fun setUserController(userType : String) {
        when(userType) {
            "test" ->
                userController = TestUserController()
            //TODO: Implement new UserControllers for different user types
            else ->
                userController = StandardUserController()
        }
    }

    // Function which takes a uri filepath, converts it to a bitmap, compresses it to JPEG, and encodes it in base 64
    /**
     * A function which takes a uri filepath, converts it to a bitmap, compresses it to JPEG, and encodes it in base64
     * @param uri The [Uri] filepath for the picture to compress to send
     * @return The [String] base 64 encoding for the picture
     */
    fun convertUriToBitmap(uri : Uri) : String {
        Log.d("AppController", "URI:\n" + uri.toString())
        // Get the bitmap for the uri
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        var lastBitmap : Bitmap? = null
        lastBitmap = bitmap
        // create a byte array output stream
        val baos = ByteArrayOutputStream()
        // compress the bitmap to JPEG
        lastBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        // get the binary output
        val imageBytes = baos.toByteArray()
        // Encode the binary to base 64, and return it
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    /**
     * A function which takes a bitmap, compresses it to JPEG, and encodes it into base 64
     * @param bitmap The [Bitmap] to compress and encode
     * @return The [String] base 64 encoding of the picture
     */
    fun convertBitmapToBase64(bitmap: Bitmap) : String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }

    /**
     * A function to remove all fragments from a fragment container
     * @param fm The [FragmentManager] to clear
     */
    fun removeAllFragments(fm : FragmentManager) {
        val fragments = fm.fragments
        val transaction = fm.beginTransaction()
        fragments.forEach {fragment ->
            transaction.remove(fragment)
        }
        transaction.commit()
    }

    /**
     * A function to split a long log message into smaller pieces that can be printed in [Log]
     * [Log] can only handle messages of about 4096 characters, and we had to break up some message for debugging
     * a bitmap
     */
    fun longLog(tag : String, msg : String) {
        if (msg.length > 4000) {
            Log.d(tag, msg.substring(0, 4000))
            longLog(tag, msg.substring(4000))
        } else {
            Log.d(tag, msg)
        }
    }
}
