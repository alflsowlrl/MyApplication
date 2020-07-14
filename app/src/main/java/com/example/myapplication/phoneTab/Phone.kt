package com.example.myapplication.phoneTab

data class Phone(var id: Int, var name: String, var phoneNumber: String)

class PhonePriority{
    companion object{
        const val LOW_PRIORITY = 100
        const val HIGH_PRIORITY = 0
    }
}