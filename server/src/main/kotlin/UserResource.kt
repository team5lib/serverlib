package edu.uiowa.cs.team5

import edu.uiowa.cs.team5.Patron.Companion.patronList
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("users")  // when this runs, try http://localhost:8080/users/Iowa for the "GET" request
@Produces(MediaType.APPLICATION_JSON)
class UserResource {
    private val fileHandler = FileHandler()

    init {
        patronList = fileHandler.readPatrons()
        try{
            patronList += User("user", "user")
            patronList += Admin("admin", "admin")
        }catch (e:Error){
            //do nothing
        }
    }

    @POST @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    fun login(loginRequest: LoginRequest):Response {
        val response: LoginResponse
        println("Login Request: " + "${loginRequest.username}")
        if (patronList[loginRequest.username]?.password.equals(loginRequest.password)){
            println("Login Success: " + "${loginRequest.username}")
            val p = patronList[loginRequest.username]!!
            response = LoginResponse("Welcome!", p,p is Admin)
            fileHandler.writeLoginHistory(loginRequest.username,true)
        }else{
            response = LoginResponse("Wrong password or Username not exist!",null,false)
            fileHandler.writeLoginHistory(loginRequest.username,false)
        }
        return Response.status(Response.Status.OK).entity(response).build()
    }

    @POST @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    fun create(createRequest:CreateRequest):Response{
        var response: CreateResponse
        val u = createRequest.username
        val p = createRequest.password
        val user: User
        try{
            user = User(u,p)
            patronList += user
            println("Created: ${u}")
            fileHandler.writePatrons()
            response = CreateResponse("Created!", patronList[u]!!)
            fileHandler.writeCreateHistory(u,true)
        }catch(e:Error){
            response = CreateResponse(e.message.toString(),null)
        }
        return Response.status(Response.Status.OK).entity(response).build()
    }



    /*
    @GET @Path("{username}")
            // Test in browser with http://localhost:8080/users/Iowa
    fun getUser(@PathParam("username") username: String): User? {
        println("Get " + "$username")
        return users[username]
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