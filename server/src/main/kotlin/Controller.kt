package edu.uiowa.cs.team5

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider
import org.glassfish.jersey.server.ResourceConfig
import java.net.URI
import javax.ws.rs.ext.ContextResolver
class ServerController{
    fun startServer(){
        val resourceConfig = ResourceConfig.forApplication(SurveyApplication())
                .register(
                        ContextResolver<ObjectMapper> {
                            ObjectMapper().registerModule(KotlinModule())
                        }
                )
        val server = NettyHttpContainerProvider.createHttp2Server(URI.create("http://localhost:8080/"), resourceConfig, null)
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { server.close() }))
        println("Server Started")
    }

}




