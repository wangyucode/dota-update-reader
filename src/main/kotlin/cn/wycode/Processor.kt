package cn.wycode

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

class Processor(private val currentData: CurrentData, private val databaseWriter: DatabaseWriter) {

    private val scanner = Scanner(System.`in`)

    fun process() {
        val lines = Files.readAllLines(Paths.get("D://update.txt"))
        var line: String
        var changeObject: Any? = null
        for (l in lines) {
            line = l.trim()
            if (line.isNotBlank()) {
                if (line.startsWith("-")) {
                    when (changeObject) {
                        is HeroDetail -> processHero(changeObject, line)
                        is DotaItem -> processItem(changeObject, line)
                        else -> println("跳过（$changeObject）-->$line！")
                    }
                } else if (line.startsWith("*")) {
                    print("全局修改-->$line")
                } else {
                    changeObject = processTitle(line)
                }
            }
        }
    }

    private fun processTitle(title: String): Any? {
        var obj: Any? = currentData.heroDetailList.find { it.name == title }
        if (obj != null) return obj
        obj = currentData.itemList.find { it.name == title }
        if (obj != null) return obj
        println("未知title-->$title！")
        return title
    }

    private fun processItem(item: DotaItem, change: String) {
        println(item.name + "-->" + change)
        //修复
        var pattern = Pattern.compile("- 修复(.*)的问题.*")
        var matcher = pattern.matcher(change)
        if (matcher.find()) {
            val tip = matcher.group(1)
            println("当前tip：${item.notes}")
            println("回车=跳过")
            if(scanner.nextLine().isEmpty()) return
        }
        //物品属性
        pattern = Pattern.compile("- (魔法恢复)|(攻击力)|(生命恢复)|(全属性)|(力量)|(敏捷)|(智力)|(护甲)|(攻击速度)|(生命值).*")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            val attrKey = matcher.group(1)
            val current = currentData.itemAttrList.find { it.key == item.key && it.attrKey == attrKey }
            if (current != null) {
                println("当前$attrKey=${current.attr}")
                println("输入修改值，不输=跳过")
                val value = scanner.nextLine()
                if(value.isNotEmpty()){
                    if(!updateItemAttr(item.key, attrKey, value)) processItem(item, change)
                }
            } else {
                println("未找到$attrKey")
                println("输入修改值，不输=跳过")
                val value = scanner.nextLine()
                if(value.isNotEmpty()){
                    if(!insertItemAttr(item.key, attrKey, value)) processItem(item, change)
                }

            }
            return
        }
        println("未知物品改动-->$change！")
        scanner.nextLine()
    }

    private fun insertItemAttr(key: String, attrKey: String, value: String): Boolean {
        val sql = "insert into DOTA_ITEM_ATTRS values('$key', '$value' '$attrKey')"
        return askForExecute(sql)
    }

    private fun updateItemAttr(key: String, attrKey: String, value: String): Boolean {
        val sql = "update DOTA_ITEM_ATTRS set attrs='$value' where dota_item_key='$key' and attrs_key='$attrKey'"
        return askForExecute(sql)
    }

    private fun processHero(heroDetail: HeroDetail, change: String) {
        println(heroDetail.name + "-->" + change)
        //天赋修改
        val pattern = Pattern.compile("- (\\d{2})级天赋从.*")
        val matcher = pattern.matcher(change)
        if (matcher.find()) {
            val level = matcher.group(1)
            println("当前${level}左=${getTalent(level, "left", heroDetail)}")
            println("当前${level}右=${getTalent(level, "right", heroDetail)}")
            println("选择天赋字段：1=左,2=右,3=跳过")
            when (scanner.nextLine()) {
                "1" -> {
                    println("输入修改值:")
                    val value = scanner.nextLine()
                    if (!updateTalent(heroDetail.name, level, "left", value)) {
                        processHero(heroDetail, change)
                    }
                }
                "2" -> {
                    println("输入修改值:")
                    val value = scanner.nextLine()
                    if (!updateTalent(heroDetail.name, level, "right", value)) {
                        processHero(heroDetail, change)
                    }
                }
                else -> println("选择了跳过！")
            }
            return
        }

        println("未知英雄改动-->$change！")
    }

    private fun getTalent(level: String, leftOrRight: String, heroDetail: HeroDetail): String {
        return when (level) {
            "10" -> if (leftOrRight == "left") heroDetail.talent10Left else heroDetail.talent10Right
            "15" -> if (leftOrRight == "left") heroDetail.talent15Left else heroDetail.talent15Right
            "20" -> if (leftOrRight == "left") heroDetail.talent20Left else heroDetail.talent20Right
            "25" -> if (leftOrRight == "left") heroDetail.talent25Left else heroDetail.talent25Right
            else -> {
                println("未知level->$level")
                scanner.nextLine()
            }
        }
    }

    private fun updateTalent(name: String, level: String, leftOrRight: String, value: String): Boolean {
        val sql = "update HERO_DETAIL set talent$level$leftOrRight = '$value' where name='$name'"
        return askForExecute(sql)
    }

    private fun askForExecute(sql: String): Boolean {
        println("确认sql:$sql")
        println("回车确认，任意字符拒绝")
        return if (scanner.nextLine() == "") {
            databaseWriter.execute(sql)
            true
        } else {
            false
        }
    }


}