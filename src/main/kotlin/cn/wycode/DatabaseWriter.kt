package cn.wycode

import java.sql.Connection

class DatabaseWriter(private val connection: Connection) {

    fun execute(sql: String) {
        println("已执行：$sql")
    }
}