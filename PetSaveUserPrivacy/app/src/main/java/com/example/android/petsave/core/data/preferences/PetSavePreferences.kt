package com.example.android.petsave.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.android.petsave.core.data.preferences.PreferencesConstants.KEY_LAST_LOGIN
import com.example.android.petsave.core.data.preferences.PreferencesConstants.KEY_TOKEN
import com.example.android.petsave.core.data.preferences.PreferencesConstants.KEY_TOKEN_EXPIRATION_TIME
import com.example.android.petsave.core.data.preferences.PreferencesConstants.KEY_TOKEN_TYPE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetSavePreferences @Inject constructor(
    @ApplicationContext context: Context
) : Preferences {

    companion object {
        const val PREFERENCES_NAME = "PET_SAVE_PREFERENCES"
    }

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val preferencesWrite = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun putToken(token: String) {
        edit { putString(KEY_TOKEN, token) }
    }

    override fun putTokenExpirationTime(time: Long) {
        edit { putLong(KEY_TOKEN_EXPIRATION_TIME, time) }
    }

    override fun putTokenType(tokenType: String) {
        edit { putString(KEY_TOKEN_TYPE, tokenType) }
    }

    override fun putLastLoggedInTime() {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        edit { putString(KEY_LAST_LOGIN, currentDateTimeString) }
    }

    private inline fun edit(block: SharedPreferences.Editor.() -> Unit) {
        with(preferencesWrite.edit()) {
            block()
            commit()
        }
    }

    override fun getToken(): String {
        return preferences.getString(KEY_TOKEN, "").orEmpty()
    }

    override fun getTokenExpirationTime(): Long {
        return preferences.getLong(KEY_TOKEN_EXPIRATION_TIME, -1)
    }

    override fun getTokenType(): String {
        return preferences.getString(KEY_TOKEN_TYPE, "").orEmpty()
    }

    override fun getLastLoggedIn(): String? {
        return preferences.getString(KEY_LAST_LOGIN, null)
    }

    override fun deleteTokenInfo() {
        edit {
            remove(KEY_TOKEN)
            remove(KEY_TOKEN_EXPIRATION_TIME)
            remove(KEY_TOKEN_TYPE)
        }
    }

    override fun clearPrefs() {
        preferences.edit().clear().apply()
        preferencesWrite.edit().clear().apply()
    }
}