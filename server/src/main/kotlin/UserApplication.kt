package edu.uiowa.cs.team5

import javax.ws.rs.core.Application

class SurveyApplication: Application() {
    override fun getSingletons(): MutableSet<Any> {
        //there should be other resourves like SurveyResource
        return mutableSetOf(UserResource())
    }
}