package cn.wycode

import java.sql.Connection
import java.sql.DriverManager
import java.util.*

fun main() {
    val connection = getDatabaseConnection() ?: return
    val currentData = CurrentData(connection)
    val databaseWriter = DatabaseWriter(connection)
    currentData.initData()
    Processor(currentData,databaseWriter).process()
}


fun getDatabaseConnection(): Connection? {
    //连接数据库
    Class.forName("org.h2.Driver")
    val resourceBundle = ResourceBundle.getBundle("application")
    val url = resourceBundle.getString("datasource.url")
    val username = resourceBundle.getString("datasource.username")
    val password = resourceBundle.getString("datasource.password")
    return try {
        DriverManager.getConnection(url, username, password)
    } catch (se: Exception) {
        se.printStackTrace()
        null
    }
}