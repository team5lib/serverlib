package edu.uiowa.cs.team5

import edu.uiowa.cs.team5.Patron.Companion.adminList
import edu.uiowa.cs.team5.Patron.Companion.patronList
import edu.uiowa.cs.team5.Survey.Companion.surveyList
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Path("main")  // when this runs, try http://localhost:8080/users/Iowa for the "GET" request
@Produces(MediaType.APPLICATION_JSON)
class MainResource {
    private val fileHandler = FileHandler()
    private val gson = Gson()
    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    init {
        patronList = fileHandler.readPatrons()
        adminList = fileHandler.readAdmins()
        try{
            patronList += Patron("user", "user",false)
            patronList += Patron("admin", "admin",true)
            fileHandler.writePatrons()
            fileHandler.writeAdmins()

        }catch (e:Error){
            //do nothing
        }
    }

    @POST @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    fun login(loginRequestString: String):Response {
        val loginRequest = gson.fromJson<LoginRequest>(loginRequestString)
        val response: String
        println("Login Request: " + "${loginRequest.username}")
        if (patronList[loginRequest.username]?.password.equals(loginRequest.password)){
            println("Login Success: " + "${loginRequest.username}")
            val p = patronList[loginRequest.username]!!
            val isAdmin = adminList.containsKey(p.username)
            response = gson.toJson(LoginResponse("Welcome!", p, isAdmin))
            fileHandler.writeLoginHistory(loginRequest.username,true)
        }else{
            response = gson.toJson(LoginResponse("Wrong password or Username not exist!",null,false))
            fileHandler.writeLoginHistory(loginRequest.username,false)
        }
        return Response.status(Response.Status.OK).entity(response).build()
    }

    @POST @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    fun create(createRequestString:String):Response{
        val createRequest = gson.fromJson<CreateRequest>(createRequestString)
        var response: String
        val u = createRequest.username
        val p = createRequest.password
        val patron: Patron
        try{
            patron = Patron(u,p,false)
            patronList += patron
            println("Created: ${u}")
            fileHandler.writePatrons()
            response = gson.toJson(CreateResponse("Created!", patronList[u]!!))
            fileHandler.writeCreateHistory(u,true)
        }catch(e:Error){
            response = gson.toJson(CreateResponse(e.message.toString(),null))
        }
        return Response.status(Response.Status.OK).entity(response).build()
    }

    @POST @Path("submit")
    @Consumes(MediaType.APPLICATION_JSON)
    fun submit(submitRequestString:String):Response{
        val submitRequest = gson.fromJson<SubmitRequest>(submitRequestString)
        var response: String
        var username = submitRequest.username
        var data = submitRequest.surveylist
        try {
            patronList[username]!!.surveyList = data
            if (adminList.containsKey(username)){
                println("Starting to update...")
                for((username,patron) in patronList){
                    for(i in 0..data.size-1){
                        if (patron.surveyList.size<(i+1)){
                            println("adding survey: ${data[i].title} to $username")
                            patron.surveyList.add(data[i])
                        }else {
                            println("updating survey: ${data[i].title} to $username")
                            patron.surveyList[i].title = data[i].title
                            for (j in 0..data[i].questionList.size-1){
                                patron.surveyList[i].questionList[j].question = data[i].questionList[j].question
                            }
                        }
                    }
                    println("$username updated")
                }
                println("update finished")
            }
            fileHandler.writePatrons()
            response = gson.toJson(SubmitResponse("Submitted!", patronList[username]!!))
            println("Submitted from: ${username}")
        }catch (e:Error){
            response = gson.toJson(SubmitResponse(e.message.toString(),null))
        }
        return Response.status(Response.Status.OK).entity(response).build()
    }


    /*
    @GET @Path("{username}")
            // Test in browser with http://localhost:8080/users/Iowa
    fun getUser(@PathParam("username") username: String): String {
        println("Get " + "$username")
        return gson.toJson(patronList[username])
        // note the return type is User? which means it will either
        // be null (no user found) or a User object -- but then
        // that user object is "serialized" into JSON, because the
        // annotation above says @Produces(APPLICATION_JSON)
    }

    /** A duplicate of getUser, but with a different @Path **/
    @GET @Path("get/{username}")  // Test in browser with http://localhost:8080/users/get/Iowa
    fun getUser2(@PathParam("username") username: String): User? {
        println("Get2 " + "$username")
        return users[username]
    }




    @PUT @Path("{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun updateUser(@PathParam("username") username: String, user: User) {
        users -= username
        users += user.username to user
    }

    @DELETE @Path("{username}")
    fun deleteUser(@PathParam("username") username: String): User? {
        return users.remove(username)
    }
    */
}