package com.example.myapplication

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionChecker {

    companion object{
        const val ALL_PERMISSION_GRANTED = true
        const val NOT_ALL_PERMISSION_GRANTED = false
        const val MAIN_PERMISSION_REQUEST_CODE = 80
        const val CONTACT_PERMISSION_REQUEST_CODE = 81
        const val GALLERY_PERMISSION_REQUEST_CODE = 82

        fun checkAndRequestPermissons(fragment: Fragment, permissions: Array<String>, requestCode: Int): Boolean{
            val premissionInRequest: MutableList<String> = mutableListOf()

            fragment.context?.let{
                for(permission in permissions){
                    if(ContextCompat.checkSelfPermission(it, permission) != PackageManager.PERMISSION_GRANTED){
                        premissionInRequest.add(permission)
                    }
                }
            }

            if(premissionInRequest.isNotEmpty()){
                fragment.requestPermissions(permissions, requestCode)
                return NOT_ALL_PERMISSION_GRANTED
            }
            else{
                return ALL_PERMISSION_GRANTED
            }
        }
    }
}