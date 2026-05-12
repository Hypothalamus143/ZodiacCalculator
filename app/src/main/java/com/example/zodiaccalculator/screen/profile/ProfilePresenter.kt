package com.example.zodiaccalculator.screen.profile


import com.example.zodiaccalculator.data.models.User
import com.example.zodiaccalculator.screen.profile.ProfileContract
import com.example.zodiaccalculator.screen.profile.ProfileModel

class ProfilePresenter(private val view: ProfileContract.View, private val model: ProfileModel) : ProfileContract.Presenter {
    init {
        val user : User? = model.getUserData();
        if(user == null) view.navigateToLogin();
        else
            view.setUserDetails(user);
//        view.setUserDetails(view.getUsername()?: "", userdata?.get("firstname")?:"",  userdata?.get("middlename")?:"", userdata?.get("lastname")?:"")
    }
    override fun onDashboardClicked() {
        view.navigateToDashboard()
    }
}