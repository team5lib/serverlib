package edu.uiowa.cs.team5

import javax.ws.rs.core.Application

class SurveyApplication: Application() {
    override fun getSingletons(): MutableSet<Any> {
        return mutableSetOf(MainResource())
    }
}