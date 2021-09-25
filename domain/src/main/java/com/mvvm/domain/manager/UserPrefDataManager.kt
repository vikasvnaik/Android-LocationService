package com.mvvm.domain.manager

import com.mvvm.domain.repository.UserDataRepo


class UserPrefDataManager(private val userDataRepo: UserDataRepo) : UserDataRepo {

    override var isUserLoggedIn: Boolean
        get() = userDataRepo.isUserLoggedIn
        set(value) {
            userDataRepo.isUserLoggedIn = value
        }

    override var userId: String?
        get() = userDataRepo.userId
        set(value) {
            userDataRepo.userId = value
        }

    override var mobile: String?
        get() = userDataRepo.mobile
        set(value) {
            userDataRepo.mobile = value
        }

    override var token: String?
        get() = userDataRepo.token
        set(value) {
            userDataRepo.token = value
        }

    override var distance: Float
        get() = userDataRepo.distance
        set(value) {
            userDataRepo.distance = value
        }

    override var status: Boolean
        get() = userDataRepo.status
        set(value) {
            userDataRepo.status = value
        }
}