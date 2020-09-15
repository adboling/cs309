package jrising.myapplication.defultMVP

abstract class BasePresentrer<V>{
    protected var view : V?= null
    fun attachView(view:V){
        this.view = view
    }
    fun detachView(){
        this.view = null
    }
}