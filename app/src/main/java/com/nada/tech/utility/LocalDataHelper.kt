package com.nada.tech.utility

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nada.tech.model.BluethoothDevice
import com.nada.tech.model.PartModel
import com.nada.tech.model.PopupModel
import com.nada.tech.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// https://stackoverflow.com/questions/63643065/the-best-way-to-wrap-sharedpreference-object-in-hilt
@Singleton
class LocalDataHelper @Inject constructor(@ApplicationContext context: Context) {
    private var preference = context.getSharedPreferences("Nada-Teach-RFID", Context.MODE_PRIVATE)

    private fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    fun clearPreference() {
        login = false
        user = null

        val editor = preference.edit()
        editor.clear()
//        editor.remove(JwtToken)
//        editor.remove(UserData)
        editor.apply()
    }

    var login: Boolean
        get() = preference.getBoolean("is_login", false)
        set(value) = preference.edit { it.putBoolean("is_login", value) }

    var apiToken: String?
        get() = preference.getString("api_token", "")
        set(value) = preference.edit { it.putString("api_token", value) }

    var user: User?
        get() = preference.getString("user_details", "").fromJson(User::class.java)
        set(value) = preference.edit { it.putString("user_details", value.toJson()) }

    val userId: String get() = user?.UserId.orEmpty()

    var currentDevice: BluethoothDevice?
        get() = preference.getString("current_device", "").fromJson(BluethoothDevice::class.java)
        set(value) = preference.edit { it.putString("current_device", value.toJson()) }

    var fcmToken: String?
        get() = preference.getString("fcm_token", "")
        set(value) = preference.edit { it.putString("fcm_token", value) }

    var locations: List<PopupModel>
        get() {
            return Gson().fromJson(
                preference.getString("locations", ""),
                object : TypeToken<List<PopupModel>>() {}.type
            ) ?: emptyList()
        }
        set(value) = preference.edit { it.putString("locations", value.toJson()) }

    var partList: List<PartModel>
        get() {
            return Gson().fromJson(
                preference.getString("partList", ""),
                object : TypeToken<List<PartModel>>() {}.type
            ) ?: emptyList()
        }
        set(value) = preference.edit { it.putString("partList", value.toJson()) }

    var assetType: List<PopupModel>
        get() {
            return Gson().fromJson(
                preference.getString("assetType", ""),
                object : TypeToken<List<PopupModel>>() {}.type
            ) ?: emptyList()
        }
        set(value) = preference.edit { it.putString("assetType", value.toJson()) }
    var assetCategory: ArrayList<PopupModel>
        get() {
            return Gson().fromJson(
                preference.getString("assetCategory", ""),
                object : TypeToken<List<PopupModel>>() {}.type
            )
        }
        set(value) = preference.edit { it.putString("assetCategory", value.toJson()) }
    var manufacturer: ArrayList<PopupModel>
        get() {
            return Gson().fromJson(
                preference.getString("manufacturer", ""),
                object : TypeToken<ArrayList<PopupModel>>() {}.type
            )
        }
        set(value) = preference.edit { it.putString("manufacturer", value.toJson()) }

}
