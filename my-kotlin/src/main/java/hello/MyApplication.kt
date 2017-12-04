package hello

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.Date
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

fun main(args: Array<String>) {
    SpringApplication.run(MyApplication::class.java, *args)
}

@SpringBootApplication
open class MyApplication: SpringBootServletInitializer() {
    override fun configure(builder: SpringApplicationBuilder) = builder.sources(MyApplication::class.java)
    @Bean open fun messageBean() = "Hello message"
    @Bean open fun helloServletRegistrationBean() = ServletRegistrationBean(HelloServlet(), "/hello-servlet")
    @Bean open fun jerseyRegistrationBean(): ServletRegistrationBean {
        val jersey = ServletContainer(ResourceConfig().register(HelloService::class.java))
        return ServletRegistrationBean(jersey, "/rest/*")
    }
}

//Jersey rest service
//@ApplicationPath("/rest") class ApplicationConfig : Application()

@Path("/hello/{username}")
class HelloService {
    @GET
    @Produces("text/html")
    fun hello(@PathParam("username") userName: String) = "<h1>Hello ${userName}</h1>"
}

//Spring controller
@Controller
class MyController {
    @Autowired lateinit var message: String

    @RequestMapping("/")
    @ResponseBody
    fun home() = message

    @RequestMapping("/hello")
    @ResponseBody
    fun hello() = "Hello"

    @RequestMapping(value="/user/{user}", method=arrayOf(RequestMethod.GET))
    @ResponseBody
    fun userInfo(@PathVariable("user") user: String,
            @RequestParam(value="date", required=false) date: String) = "Hello ${user} ${date}"

    @RequestMapping("/welcome")
    fun welcome(model: Model,
                @RequestParam(value="name", required=false, defaultValue="world") name: String): String {
        model.addAttribute("name", name)
        return "hello"
    }
}

//raw servlet
class HelloServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.write("<h1> Hello </h1>")
    }
}