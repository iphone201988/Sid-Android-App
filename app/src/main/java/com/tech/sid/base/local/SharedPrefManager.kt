package com.tech.sid.base.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.ui.auth.AuthModelLogin
import org.json.JSONObject
import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val IS_FIRST = "LOGIN_DATA"
    }
    fun setLoginData(isFirst: Any) {
        val gson = Gson()
        val json = gson.toJson(isFirst)
        val editor = sharedPreferences.edit()
        editor.putString(KEY.IS_FIRST, json)
        editor.apply()
    }

    fun getLoginData(): String? {
        val json: String? = sharedPreferences.getString(KEY.IS_FIRST, "")
        return json
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
    fun getProfileData(): AuthModelLogin? {
        val rawJsonString: String = getLoginData() ?: ""
        if (rawJsonString.isEmpty() || "".equals(rawJsonString) || rawJsonString == null) {
            return null
        }
        val cleanJsonString = if (rawJsonString.startsWith("\"") && rawJsonString.endsWith("\"")) {
            rawJsonString.substring(1, rawJsonString.length - 1).replace("\\\"", "\"")
        } else {
            rawJsonString
        }
        val jsonObject = JSONObject(cleanJsonString)
        return BindingUtils.parseJson<AuthModelLogin?>(jsonObject.toString())
    }

      fun getTokenFromPref(): Boolean {
        val rawJsonString: String = getLoginData() ?: ""
        if(rawJsonString.isEmpty()|| "".equals(rawJsonString) || rawJsonString==null){
            return false
        }
        val cleanJsonString = if (rawJsonString.startsWith("\"") && rawJsonString.endsWith("\"")) {
            rawJsonString.substring(1, rawJsonString.length - 1).replace("\\\"", "\"")
        } else {
            rawJsonString
        }
        val jsonObject = JSONObject(cleanJsonString)
        val loginModel: AuthModelLogin? =
            BindingUtils.parseJson(jsonObject.toString())
        return  if(loginModel?.token==null) false else true
    }
}