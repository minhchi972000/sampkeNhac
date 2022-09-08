package wee.digital.library.util

import android.util.Log
import com.google.gson.JsonObject
import org.json.JSONObject
import kotlin.reflect.KClass

class Logger {

    private val tag: String

    constructor(string: String) {
        this.tag = if (string.length > 23) string.substring(0, 22) else string
    }

    constructor(cls: KClass<*>) {
        val s = cls.java.simpleName
        this.tag = if (s.length > 23) s.substring(0, 22) else s
    }

    fun d(s: JsonObject?) {
        if (s == null) {
            d("null")
            return
        }
        val json = s.toString()
        d(JSONObject(json).toString(2))
    }

    fun d(any: String?) {
        if (enable) Log.d(tag, any ?: "null")
    }

    fun d(throwable: Throwable?) {
        d(throwable?.message)
    }

    fun i(any: String?) {
        if (enable) Log.i(tag, any ?: "null")
    }

    fun i(throwable: Throwable?) {
        i(throwable?.message)
    }

    fun e(any: String?) {
        if (enable) Log.e(tag, any ?: "null")
    }

    fun e(throwable: Throwable?) {
        e(throwable?.message)
    }

    fun w(any: String?) {
        if (enable) Log.w(tag, any ?: "null")
    }

    fun w(throwable: Throwable?) {
        w(throwable?.message)
    }

    fun wtf(any: String?) {
        if (enable) Log.wtf(tag, any ?: "null")
    }

    fun wtf(throwable: Throwable?) {
        wtf(throwable?.message)
    }

    companion object {

        var enable: Boolean = true

        fun breakpoint() {}

        fun error() {
            arrayOf(true)[-1]
        }

        fun crash() {
            throw RuntimeException("crash")
        }
    }

}