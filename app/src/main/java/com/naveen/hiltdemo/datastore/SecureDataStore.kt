package com.naveen.hiltdemo.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_preferences")
        private const val PREF_NAME = "secure_shared_prefs"
    }
    
    // Master key for encryption
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    // Encrypted SharedPreferences for sensitive data
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    // DataStore for regular preferences
    private val dataStore = context.dataStore
    
    // Keys for different data types
    object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_AGE = intPreferencesKey("user_age")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val LAST_LOGIN_TIME = longPreferencesKey("last_login_time")
        val USER_SETTINGS = stringPreferencesKey("user_settings")
    }
    
    // Write operations
    suspend fun writeString(key: String, value: String, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().putString(key, value).apply()
        } else {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
    }
    
    suspend fun writeInt(key: String, value: Int, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().putInt(key, value).apply()
        } else {
            dataStore.edit { preferences ->
                preferences[intPreferencesKey(key)] = value
            }
        }
    }
    
    suspend fun writeBoolean(key: String, value: Boolean, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().putBoolean(key, value).apply()
        } else {
            dataStore.edit { preferences ->
                preferences[booleanPreferencesKey(key)] = value
            }
        }
    }
    
    suspend fun writeLong(key: String, value: Long, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().putLong(key, value).apply()
        } else {
            dataStore.edit { preferences ->
                preferences[longPreferencesKey(key)] = value
            }
        }
    }
    
    // Read operations
    fun readString(key: String, defaultValue: String = "", isSensitive: Boolean = false): Flow<String> {
        return if (isSensitive) {
            // For encrypted data, we need to return a flow that reads from SharedPreferences
            kotlinx.coroutines.flow.flow {
                emit(encryptedPrefs.getString(key, defaultValue) ?: defaultValue)
            }
        } else {
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[stringPreferencesKey(key)] ?: defaultValue
                }
        }
    }
    
    fun readInt(key: String, defaultValue: Int = 0, isSensitive: Boolean = false): Flow<Int> {
        return if (isSensitive) {
            kotlinx.coroutines.flow.flow {
                emit(encryptedPrefs.getInt(key, defaultValue))
            }
        } else {
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[intPreferencesKey(key)] ?: defaultValue
                }
        }
    }
    
    fun readBoolean(key: String, defaultValue: Boolean = false, isSensitive: Boolean = false): Flow<Boolean> {
        return if (isSensitive) {
            kotlinx.coroutines.flow.flow {
                emit(encryptedPrefs.getBoolean(key, defaultValue))
            }
        } else {
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[booleanPreferencesKey(key)] ?: defaultValue
                }
        }
    }
    
    fun readLong(key: String, defaultValue: Long = 0L, isSensitive: Boolean = false): Flow<Long> {
        return if (isSensitive) {
            kotlinx.coroutines.flow.flow {
                emit(encryptedPrefs.getLong(key, defaultValue))
            }
        } else {
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[longPreferencesKey(key)] ?: defaultValue
                }
        }
    }
    
    // Delete operations
    suspend fun deleteString(key: String, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().remove(key).apply()
        } else {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
            }
        }
    }
    
    suspend fun deleteInt(key: String, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().remove(key).apply()
        } else {
            dataStore.edit { preferences ->
                preferences.remove(intPreferencesKey(key))
            }
        }
    }
    
    suspend fun deleteBoolean(key: String, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().remove(key).apply()
        } else {
            dataStore.edit { preferences ->
                preferences.remove(booleanPreferencesKey(key))
            }
        }
    }
    
    suspend fun deleteLong(key: String, isSensitive: Boolean = false) {
        if (isSensitive) {
            encryptedPrefs.edit().remove(key).apply()
        } else {
            dataStore.edit { preferences ->
                preferences.remove(longPreferencesKey(key))
            }
        }
    }
    
    // Clear all data
    suspend fun clearAll() {
        // Clear DataStore
        dataStore.edit { preferences ->
            preferences.clear()
        }
        // Clear EncryptedSharedPreferences
        encryptedPrefs.edit().clear().apply()
    }
    
    // Convenience methods for common operations
    suspend fun saveUserData(name: String, email: String, phone: String, age: Int, isSensitive: Boolean = true) {
        if (isSensitive) {
            writeString(Keys.USER_NAME.name, name, true)
            writeString(Keys.USER_EMAIL.name, email, true)
            writeString(Keys.USER_PHONE.name, phone, true)
            writeInt(Keys.USER_AGE.name, age, true)
        } else {
            writeString(Keys.USER_NAME.name, name, false)
            writeString(Keys.USER_EMAIL.name, email, false)
            writeString(Keys.USER_PHONE.name, phone, false)
            writeInt(Keys.USER_AGE.name, age, false)
        }
    }
    
    suspend fun setLoginStatus(isLoggedIn: Boolean, timestamp: Long = System.currentTimeMillis()) {
        writeBoolean(Keys.IS_LOGGED_IN.name, isLoggedIn, false)
        writeLong(Keys.LAST_LOGIN_TIME.name, timestamp, false)
    }
    
    suspend fun saveUserSettings(settings: String, isSensitive: Boolean = false) {
        writeString(Keys.USER_SETTINGS.name, settings, isSensitive)
    }
    
    // Read user data
    fun getUserName(isSensitive: Boolean = true): Flow<String> = readString(Keys.USER_NAME.name, "", isSensitive)
    fun getUserEmail(isSensitive: Boolean = true): Flow<String> = readString(Keys.USER_EMAIL.name, "", isSensitive)
    fun getUserPhone(isSensitive: Boolean = true): Flow<String> = readString(Keys.USER_PHONE.name, "", isSensitive)
    fun getUserAge(isSensitive: Boolean = true): Flow<Int> = readInt(Keys.USER_AGE.name, 0, isSensitive)
    fun getLoginStatus(): Flow<Boolean> = readBoolean(Keys.IS_LOGGED_IN.name, false, false)
    fun getLastLoginTime(): Flow<Long> = readLong(Keys.LAST_LOGIN_TIME.name, 0L, false)
    fun getUserSettings(isSensitive: Boolean = false): Flow<String> = readString(Keys.USER_SETTINGS.name, "", isSensitive)
}
