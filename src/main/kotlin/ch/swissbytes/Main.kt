package ch.swissbytes

import spark.Spark.*


object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        get("/hello") { req, res -> "Hello World" }
    }


}
