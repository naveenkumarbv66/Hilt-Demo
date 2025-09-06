package com.naveen.hiltdemo.service

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class GreetingService @Inject constructor() {
    
    open fun getGreeting(): String {
        return "Hello Hilt!"
    }

    open fun getPersonalizedGreeting(name: String): String {
        return "Hello $name from Hilt!"
    }
}
