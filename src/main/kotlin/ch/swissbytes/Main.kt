package ch.swissbytes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Spark.get
import spark.Spark.path


object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val usersRepo = UserRepository()
        path("/users") {

            get("") { req, res ->
                jacksonObjectMapper().writeValueAsString(usersRepo.users)
            }
        }
    }

}

data class User(val name: String, val email: String, val id: Int)

class UserRepository {
    val users = hashMapOf(
            0 to User(name = "Alice", email = "alice@alice.kt", id = 0),
            1 to User(name = "Bob", email = "bob@bob.kt", id = 1),
            2 to User(name = "Carol", email = "carol@carol.kt", id = 2),
            3 to User(name = "Dave", email = "dave@dave.kt", id = 3)
    )

}
