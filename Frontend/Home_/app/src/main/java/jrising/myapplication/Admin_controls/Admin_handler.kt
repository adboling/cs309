package jrising.myapplication.Admin_controls

import android.content.Context
import android.util.Log
import android.widget.Toast
import jrising.myapplication.app.AppController
import jrising.myapplication.net_utils.Const
import org.json.JSONObject

interface Admin_handler{



fun deleteUser(user_id: Int, context: Context)
fun deleteComment(comment_id:Int, context: Context)
fun deleteTimelineObject(timeline_id:Int, context: Context)
fun changeRoles(username: String, context: Context)
}

class theAdminHandler: Admin_handler{

    override fun deleteUser(user_id: Int, context: Context) {
        var url = Const.URL_VC5_USER+"?userID="+user_id

        sendToServer(url, "user",context )
    }
    private fun handle_return(response:String, type:String, context: Context){
        Toast.makeText(context,"$type was deleted",
            Toast.LENGTH_SHORT).show()
    }
    private fun sendToServer(url:String, type: String, context: Context){
        AppController.volleyController.createStringRequest(url, AppController.volleyController.methodDelete, "deleteUserByID",
            { response-> handle_return(response,type, context)},
            {error-> Toast.makeText(context,"$error",
                Toast.LENGTH_LONG).show()})
    }

    override fun deleteComment(comment_id: Int, context: Context) {
        var url = Const.URL_VC5_COMMENT+"?commentID="+comment_id

       sendToServer(url, "comment", context)

    }

    override fun deleteTimelineObject(timeline_id: Int,context: Context) {
    var url= Const.URL_VC5_TIMELINES+"?timelineID="+timeline_id
        sendToServer(url,"timeline log", context)
    }
    override fun changeRoles(username: String, context: Context){
        var url = Const.URL_VC5_USER+"/modifyRole?role=admin&username="+username

        AppController.volleyController.createStringRequest(url,AppController.volleyController.methodPut,"changeToAdmin",
            {response-> Toast.makeText(context,"$response",
                Toast.LENGTH_LONG).show()},  {error-> Toast.makeText(context,"$error",
                Toast.LENGTH_LONG).show()})

    }

}