package com.rymo.felfel.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.rymo.felfel.data.preferences.FatherPreferences
import java.lang.reflect.Type


/**
 * Created by Javad Vatan on 2/28/2018.
 */
@SuppressLint("StaticFieldLeak")
object GlobalPreferences : FatherPreferences() {

    private val mSharedPreferences: SharedPreferences = mContext.getSharedPreferences(
        Constants.SHP_NAME, Context.MODE_PRIVATE
    )

    fun getBoolean(key: String?): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, defValue)
    }

    fun setBoolean(key: String?, aValue: Boolean) {
        mSharedPreferences.edit().putBoolean(key, aValue).apply()
    }

    fun setInt(key: String?, aValue: Int) {
        mSharedPreferences.edit().putInt(key, aValue).apply()
    }

    fun getInt(key: String?): Int {
        return mSharedPreferences.getInt(key, 0)
    }

    fun getInt(key: String?, defValue: Int): Int {
        return mSharedPreferences.getInt(key, defValue)
    }

    fun setString(key: String?, `val`: String?) {
        mSharedPreferences.edit().putString(key, `val`).apply()
    }

    fun getString(key: String?): String? {
        return mSharedPreferences.getString(key, "")
    }

    fun getString(key: String?, defValue: String): String? {
        return mSharedPreferences.getString(key, defValue)
    }

    fun setLong(key: String?, aValue: Long) {
        mSharedPreferences.edit().putLong(key, aValue).apply()
    }

    fun putListInt(key: String, value: List<Int>) {
        return mSharedPreferences.edit().putString(
            key, value.joinToString(
                separator = ",",
                transform = { it.toString() })
        ).apply()
    }

    fun getListInt(key: String): List<Int> {
        with(mSharedPreferences.getString(key, "")) {
            with(
                if (this != null && this.isNotEmpty())
                    split(',')
                else
                    return mutableListOf()
            ) {

                return MutableList(count()) { this[it].toInt() }
            }
        }
    }

    fun putModel(model: Any?) {
        requireNotNull(model) { "model is null" }
        mSharedPreferences.edit().putString(model.javaClass.canonicalName, mGson.toJson(model))
            .apply()
    }

    fun <T> getModel(a: Class<T>): T? {
        val json = mSharedPreferences.getString(a.canonicalName, null) ?: return null
        return try {
            mGson.fromJson(json, a)
        } catch (e: Exception) {
            throw IllegalArgumentException("Model stored with key " + a.canonicalName + " is instanceof other class")
        }
    }

    fun putList(key: String, model: List<Any?>) {
        requireNotNull(model) { "model is null" }
        val json = mGson.toJson(model)
        Log.d("putList", json)
        mSharedPreferences.edit().putString(key, json).apply()
    }

    fun <T> getList(key: String, listType: Type): MutableList<T>? {
        val json = mSharedPreferences.getString(key, null) ?: return null
        return try {
            mGson.fromJson(json, listType)
        } catch (e: Exception) {
            throw IllegalArgumentException("Model stored with key " + listType.javaClass.canonicalName + " is instanceof other class")
        }
    }

    fun <T> clearModel(a: Class<T>) {
        mSharedPreferences.edit().remove(a.canonicalName).apply()
    }

    fun getLong(key: String?): Long {
        return mSharedPreferences.getLong(key, 0)
    }

    fun getLong(key: String?, defValue: Long): Long {
        return mSharedPreferences.getLong(key, defValue)
    }

    fun clearPreferences() {
        mSharedPreferences.edit().clear().apply()
    }
}
