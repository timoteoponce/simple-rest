package ch.swissbytes

import spark.Spark.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import java.util.concurrent.atomic.AtomicInteger


object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val usersRepo = UserRepository()

        path("/users") {

            get("") { req, res ->
                jacksonObjectMapper().writeValueAsString(usersRepo.users)
            }

            get("/:id") { req, res ->
                usersRepo.findById(req.params("id").toInt())
            }

            get("/email/:email") { req, res ->
                usersRepo.findByEmail(req.params("email"))
            }

            post("/create") { req, res ->
                usersRepo.save(name = req.queryParams("name"), email = req.queryParams("email"))
                res.status(201)
                "ok"
            }

            patch("/update/:id") { req, res ->
                usersRepo.update(
                        id = req.params("id").toInt(),
                        name = req.queryParams("name"),
                        email = req.queryParams("email")
                )
                "ok"
            }

            delete("/delete/:id") { req, res ->
                usersRepo.delete(req.params("id").toInt())
                "ok"
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

    var lastId: AtomicInteger = AtomicInteger(users.size - 1)

    fun save(name: String, email: String) {
        val id = lastId.incrementAndGet()
        users.put(id, User(name = name, email = email, id = id))
    }

    fun findById(id: Int): User? {
        return users[id]
    }

    fun findByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }

    fun update(id: Int, name: String, email: String) {
        users.put(id, User(name = name, email = email, id = id))
    }

    fun delete(id: Int) {
        users.remove(id)
    }
}
