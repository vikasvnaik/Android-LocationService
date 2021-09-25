package com.mvvm.data.repository

import android.content.SharedPreferences
import com.mvvm.domain.extention.get
import com.mvvm.domain.extention.set
import com.mvvm.domain.repository.UserDataRepo
import kotlinx.serialization.json.Json

class UserDataRepoImpl(private val sharedPreferences: SharedPreferences, private val json: Json) :
    UserDataRepo {


    override var isUserLoggedIn: Boolean
        get() {
            return sharedPreferences["isUserLoggedIn", false]
        }
        set(value) {
            sharedPreferences["isUserLoggedIn"] = value
        }

    override var userId: String?
        get() {
            return sharedPreferences["userId", ""]
        }
        set(value) {
            sharedPreferences["userId"] = value
        }

    override var mobile: String?
        get() = sharedPreferences["mobile", ""]
        set(value) {
            sharedPreferences["mobile"] = value
        }

    override var token: String?
        get() {
            return sharedPreferences["token", ""]
        }
        set(value) {
            sharedPreferences["token"] = value
        }

    override var distance: Float
        get() {
            return sharedPreferences["distance", 0.0F]
        }
        set(value) {
            sharedPreferences["distance"] = value
        }

    override var status: Boolean
        get() {
            return sharedPreferences["status", false]
        }
        set(value) {
            sharedPreferences["status"] = value
        }

}