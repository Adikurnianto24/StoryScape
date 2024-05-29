package com.adiandroid.storyscape.data.datastore

import android.content.Context
import com.adiandroid.storyscape.data.model.user.UserModel

class UserPreference(context: Context) {

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setLogin(value: UserModel) {
        val editor = preferences.edit()
        editor.putString(NAME, value.name)
        editor.putString(USER_ID, value.userId)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getUser(): UserModel {
        val name = preferences.getString(NAME, null)
        val userId = preferences.getString(USER_ID, null)
        val token = preferences.getString(TOKEN, null)
        return UserModel(userId, name, token)
    }

    fun removeUser() {
        val editor = preferences.edit().clear()
        editor.apply()
    }
    fun isUserLoggedIn(): Boolean {
        return preferences.contains(TOKEN)
    }
    companion object {
        private const val PREF_NAME = "userPref"
        private const val NAME = "name"
        private const val USER_ID = "userId"
        private const val TOKEN = "token"
    }
}