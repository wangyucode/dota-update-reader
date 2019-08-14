package cn.wycode

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

class Processor(private val currentData: CurrentData) {

    fun process() {
        val lines = Files.readAllLines(Paths.get("D://update.txt"))
        var line: String
        var changeObject: Any? = null
        for (l in lines) {
            line = l.trim()
            if (line.isNotBlank()) {
                if (line.startsWith("-")) {
                    if (changeObject is HeroDetail) {
                        processHero(changeObject, line)
                    } else {
                        println("跳过-->$line")
                    }
                } else {
                    changeObject = findTitle(line)
                }
            }
        }
    }

    private fun findTitle(title: String): Any? {
        var obj: Any? = currentData.heroDetailList.find { it.name == title }
        if (obj != null) return obj
        obj = currentData.itemList.find { it.name == title }
        if (obj != null) return obj
        println("未知title-->$title")
        return null
    }

    private fun processHero(heroDetail: HeroDetail, change: String) {
        println(heroDetail.name + "-->" + change)
        //天赋修改
        val pattern = Pattern.compile("- (\\d{2})级天赋从([+|\\-]\\d+[%|秒]? .+)改为(.*)")
        val matcher = pattern.matcher(change)
        if (matcher.find()) { //匹配器进行匹配

            val level = matcher.group(1)
            println("天赋等级：$level")
            val v1 = matcher.group(2)
            println("原来：$v1")
            val v2 = matcher.group(3)
            println("改为：$v2")
            return
        }


    }
}