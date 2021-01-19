package com.onramp.android.takehome.main

import android.os.Handler

class MainPresenter(
        view: MainContract.View

) : MainContract.Presenter {

    private var view: MainContract.View? = view

    override fun start(){

            view?.setUpUINotLoggedIn()

    }

    override fun startLoggedIn(user: String, units: String){
        view?.setUpUILoggedIn(user, units)
    }

    override fun onBtnClick(){

        if(view?.checkFields() == false){
            view?.createSnackbar()
        } else {
            val user = view?.getName()

            val units = view?.getUnits()
            //delay screen a bit
            val handler = Handler()
            handler.postDelayed({
                view?.changeUiOnBtnClick(user!!, units!!)
            }, 1500)

        }

    }

    override fun startWeatherActivity(){

        view?.getPermissions()

    }


}