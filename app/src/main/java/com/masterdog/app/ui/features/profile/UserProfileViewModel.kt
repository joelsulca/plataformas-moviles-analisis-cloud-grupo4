package com.masterdog.app.ui.features.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.masterdog.app.mock.MockData
import com.masterdog.app.mock.UserUi

class UserProfileViewModel : ViewModel() {
    var user by mutableStateOf(MockData.currentUser)
        private set

    fun updateUser(updated: UserUi) {
        user = updated
    }

    fun logout() {
        user = UserUi(
            id = "",
            firstName = "",
            lastName = "",
            email = "",
            phone = "",
            address = ""
        )
    }
}
