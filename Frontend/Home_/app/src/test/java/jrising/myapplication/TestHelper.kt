package jrising.myapplication

import org.mockito.Mockito

object TestHelper {
    fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T
}