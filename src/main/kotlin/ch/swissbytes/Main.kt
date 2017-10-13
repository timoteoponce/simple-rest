package ch.swissbytes

import spark.Spark.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.flywaydb.core.Flyway
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import spark.Request
import java.sql.ResultSet
import java.util.concurrent.atomic.AtomicInteger


object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val usersRepo = UserRepository()

        path("/users") {

            get("") { req, res ->
                jacksonObjectMapper().writeValueAsString(usersRepo.list())
            }

            get("/:id") { req, res ->
                usersRepo.findById(req.params("id").toInt())
            }

            get("/email/:email") { req, res ->
                usersRepo.findByEmail(req.params("email"))
            }

            post("") { req, res ->
                usersRepo.save(name = req.queryParams("name"), email = req.queryParams("email"))
                res.status(201)
                "ok"
            }

            patch("/:id") { req, res ->
                usersRepo.update(
                        id = req.params("id").toInt(),
                        name = req.queryParams("name"),
                        email = req.queryParams("email")
                )
                "ok"
            }

            delete("/:id") { req, res ->
                usersRepo.delete(req.params("id").toInt())
                "ok"
            }

        }

    }

}

data class User(val name: String, val email: String, val id: Int)

class UserMapper : ResultSetMapper<User> {
    override fun map(row: Int, result: ResultSet?, context: StatementContext?): User {
        if (result != null) {
            return User(id = result.getInt("ID")
                    , name = result.getString("NAME")
                    , email = result.getString("EMAIL"))
        }
        throw IllegalStateException("Invalid result")
    }

}

class UserRepository {
    val dbUrl = "jdbc:postgresql://postgres/docker"
    val dbUser = "docker"
    val dbPwd = "docker"

    init {
        migrate()
    }

    fun migrate() {
        val flyway = Flyway()
        flyway.setDataSource(dbUrl, dbUser, dbPwd)
        flyway.migrate()
    }

    private fun dbHandle (): Handle? {
        return DBI(dbUrl, dbUser, dbPwd).open()
    }

    fun list(): List<User> {
        val result = arrayListOf<User>()
        dbHandle().use {
            result.addAll(
                    it!!.createQuery("SELECT * FROM USERS")
                            .map(UserMapper())
                            .list())
        }
        return result
    }


    fun save(name: String, email: String) {
        dbHandle().use {
            it!!.execute("INSERT INTO USERS(NAME,EMAIL) VALUES(?,?)", name, email)
        }
    }

    fun findById(id: Int): User? {
        var result:User? = null
        dbHandle().use {
            result = it!!.createQuery("SELECT * FROM USERS WHERE ID = :id")
                    .bind("id",id)
                    .map(UserMapper())
                    .first()
        }
        return result
    }

    fun findByEmail(email: String): User? {
        var result:User? = null
        dbHandle().use {
            result = it!!.createQuery("SELECT * FROM USERS WHERE EMAIL = :email")
                    .bind("email", email)
                    .map(UserMapper())
                    .first()
        }
        return result
    }

    fun update(id: Int, name: String, email: String) {
        dbHandle().use {
            it!!.execute("UPDATE USERS SET NAME = ?, EMAIL = ? WHERE ID = ?", name, email, id)
        }
    }

    fun delete(id: Int) {
        dbHandle().use {
            it!!.execute("DELETE FROM USERS WHERE ID = ?", id)
        }
    }
}
