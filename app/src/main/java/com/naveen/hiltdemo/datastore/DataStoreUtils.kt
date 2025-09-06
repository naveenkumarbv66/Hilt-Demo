package com.naveen.hiltdemo.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Utility class for easy DataStore operations that can be called from anywhere in the app
 * This provides a simple interface without requiring dependency injection
 */
object DataStoreUtils {
    
    private const val PREF_NAME = "app_preferences"
    private const val ENCRYPTED_PREF_NAME = "encrypted_preferences"
    
    // Extension property for DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREF_NAME)
    
    // Master key for encryption
    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    
    // Encrypted SharedPreferences
    private fun getEncryptedPrefs(context: Context): EncryptedSharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREF_NAME,
            getMasterKey(context),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }
    
    // Write operations
    suspend fun writeString(context: Context, key: String, value: String, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().putString(key, value).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
    }
    
    suspend fun writeInt(context: Context, key: String, value: Int, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().putInt(key, value).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences[intPreferencesKey(key)] = value
            }
        }
    }
    
    suspend fun writeBoolean(context: Context, key: String, value: Boolean, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().putBoolean(key, value).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences[booleanPreferencesKey(key)] = value
            }
        }
    }
    
    suspend fun writeLong(context: Context, key: String, value: Long, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().putLong(key, value).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences[longPreferencesKey(key)] = value
            }
        }
    }
    
    // Read operations
    fun readString(context: Context, key: String, defaultValue: String = "", encrypted: Boolean = false): Flow<String> {
        return if (encrypted) {
            kotlinx.coroutines.flow.flow {
                emit(getEncryptedPrefs(context).getString(key, defaultValue) ?: defaultValue)
            }
        } else {
            context.dataStore.data
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
    
    fun readInt(context: Context, key: String, defaultValue: Int = 0, encrypted: Boolean = false): Flow<Int> {
        return if (encrypted) {
            kotlinx.coroutines.flow.flow {
                emit(getEncryptedPrefs(context).getInt(key, defaultValue))
            }
        } else {
            context.dataStore.data
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
    
    fun readBoolean(context: Context, key: String, defaultValue: Boolean = false, encrypted: Boolean = false): Flow<Boolean> {
        return if (encrypted) {
            kotlinx.coroutines.flow.flow {
                emit(getEncryptedPrefs(context).getBoolean(key, defaultValue))
            }
        } else {
            context.dataStore.data
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
    
    fun readLong(context: Context, key: String, defaultValue: Long = 0L, encrypted: Boolean = false): Flow<Long> {
        return if (encrypted) {
            kotlinx.coroutines.flow.flow {
                emit(getEncryptedPrefs(context).getLong(key, defaultValue))
            }
        } else {
            context.dataStore.data
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
    suspend fun deleteString(context: Context, key: String, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().remove(key).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
            }
        }
    }
    
    suspend fun deleteInt(context: Context, key: String, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().remove(key).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences.remove(intPreferencesKey(key))
            }
        }
    }
    
    suspend fun deleteBoolean(context: Context, key: String, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().remove(key).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences.remove(booleanPreferencesKey(key))
            }
        }
    }
    
    suspend fun deleteLong(context: Context, key: String, encrypted: Boolean = false) {
        if (encrypted) {
            getEncryptedPrefs(context).edit().remove(key).apply()
        } else {
            context.dataStore.edit { preferences ->
                preferences.remove(longPreferencesKey(key))
            }
        }
    }
    
    // Clear all data
    suspend fun clearAll(context: Context) {
        // Clear DataStore
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
        // Clear EncryptedSharedPreferences
        getEncryptedPrefs(context).edit().clear().apply()
    }
    
    // Convenience methods for common operations
    suspend fun saveUserToken(context: Context, token: String, encrypted: Boolean = true) {
        writeString(context, "user_token", token, encrypted)
    }
    
    suspend fun getUserToken(context: Context, encrypted: Boolean = true): Flow<String> {
        return readString(context, "user_token", "", encrypted)
    }
    
    suspend fun saveUserId(context: Context, userId: String, encrypted: Boolean = true) {
        writeString(context, "user_id", userId, encrypted)
    }
    
    suspend fun getUserId(context: Context, encrypted: Boolean = true): Flow<String> {
        return readString(context, "user_id", "", encrypted)
    }
    
    suspend fun saveAppSettings(context: Context, settings: String, encrypted: Boolean = false) {
        writeString(context, "app_settings", settings, encrypted)
    }
    
    suspend fun getAppSettings(context: Context, encrypted: Boolean = false): Flow<String> {
        return readString(context, "app_settings", "{}", encrypted)
    }
    
    suspend fun setFirstLaunch(context: Context, isFirstLaunch: Boolean) {
        writeBoolean(context, "first_launch", isFirstLaunch, false)
    }
    
    suspend fun isFirstLaunch(context: Context): Flow<Boolean> {
        return readBoolean(context, "first_launch", true, false)
    }
    
    suspend fun setLastSyncTime(context: Context, timestamp: Long) {
        writeLong(context, "last_sync_time", timestamp, false)
    }
    
    suspend fun getLastSyncTime(context: Context): Flow<Long> {
        return readLong(context, "last_sync_time", 0L, false)
    }
}
