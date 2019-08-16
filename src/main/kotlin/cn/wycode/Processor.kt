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
                when {
                    line.startsWith("-") -> {
                        println()
                        println("-----------------------------------------------------")
                        println()
                        when (changeObject) {
                            is HeroDetail -> processHero(changeObject, line)
                            is DotaItem -> processItem(changeObject, line)
                            else -> println("跳过（$changeObject）-->$line！")
                        }
                    }
                    line.startsWith("*") -> print("全局修改-->$line")
                    else -> changeObject = processTitle(line)
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
        println("${item.name}-->${item.key}-->$change")
        //修复
        var pattern = Pattern.compile("- 修复(.*)的问题.*")
        var matcher = pattern.matcher(change)
        if (matcher.find()) {
            println("当前tip：${item.notes}")
            println("回车=跳过")
            if (scanner.nextLine().isEmpty()) return
        }
        //物品属性
        pattern = Pattern.compile("- (魔法恢复|攻击力|生命恢复|全属性|力量|敏捷|智力|护甲|攻击速度|生命值|移动速度).*")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            println("匹配到属性修改")
            val attrKey = matcher.group(1)
            val current = currentData.itemAttrList.find { it.key == item.key && it.attrKey == attrKey }
            if (current != null) {
                println("当前$attrKey=${current.attr}")
                println("修改值？回车=继续匹配")
                val value = scanner.nextLine()
                if (value.isNotEmpty()) {
                    if (!updateItemAttr(item.key, attrKey, value)) processItem(item, change)
                    return
                }
            } else {
                println("未找到$attrKey！")
                println("选择？继续匹配=1，新增属性=2，直接跳过=回车")
                when (val value = scanner.nextLine()) {
                    "1" -> {
                    }
                    "2" -> {
                        if (!insertItemAttr(item.key, attrKey, value)) processItem(item, change)
                        return
                    }
                    else -> return
                }
            }
        }
        //物品价格
        pattern = Pattern.compile("- 价格从\\d+(减少|增加)至(\\d+)")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            println("匹配到价格修改")
            val price = matcher.group(2)
            println("任意值=更新为${price}金，回车=跳过")
            val value = scanner.nextLine()
            if (value.isNotEmpty()) {
                if (!updatePrice(item.key, price.toInt())) processItem(item, change)
            }
            return
        }
        //物品描述
        pattern = Pattern.compile("- .*(减缓|降低|减速|减少|加成|提升|冷却).*")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            println("匹配到描述修改")
            var i = 0
            val descs = currentData.itemDescList.filter {
                if (it.key == item.key) {
                    println("第${i++}个属性-->${it.descKey}")
                    println(it.desc)
                    true
                } else {
                    false
                }
            }
            println("选择第几个？回车=跳过")
            val index = scanner.nextLine()
            val current = try {
                descs[index.toInt()]
            } catch (e: NumberFormatException) {
                println("出错，跳过！")
                return
            }
            println("输入修改值，回车=跳过")
            val value = scanner.nextLine()
            if (value.isNotEmpty()) {
                if (!updateItemDesc(item.key, current.descKey, value)) processItem(item, change)
            }
            return
        }
        //物品描述
        pattern = Pattern.compile("- 现在需要(\\d+)金的图纸")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            println("匹配到价格修改")
            val designMap = matcher.group(1)
            println("任意值=增加${designMap}金，回车=跳过")
            val value = scanner.nextLine()
            if (value.isNotEmpty()) {
                val price = item.cost + designMap.toInt()
                if (!updatePrice(item.key, price)) processItem(item, change)
            }
            return
        }
        println("未知物品改动-->$change")
        scanner.nextLine()
    }

    private fun updatePrice(key: String, price: Int): Boolean {
        val sql = "update DOTA_ITEM set cost='$price' where key='$key'"
        return askForExecute(sql)
    }

    private fun updateItemDesc(key: String, descKey: String, value: String): Boolean {
        val v = value.replace("\\n", "\n")
        val sql = "update DOTA_ITEM_DESC  set desc='$v' where dota_item_key='$key' and desc_key='$descKey'"
        return askForExecute(sql)
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
        var pattern = Pattern.compile("- (\\d{2})级天赋从.*")
        var matcher = pattern.matcher(change)
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
        //攻击速度
        pattern = Pattern.compile("- 基础攻击速度.*")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            println("匹配到攻击速度，不显示，跳过！")
            return
        }
        //技能魔法消耗
        pattern = Pattern.compile("- (.*)的(魔法消耗)从.*至(.*)点")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            val abilityName = matcher.group(1)
            val abilityAttr = matcher.group(2)
            val to = matcher.group(3)
            val ability = currentData.abilityList.find { it.name == abilityName }
            if (ability != null) {
                println("匹配到${abilityName}的${abilityAttr}改动到$to")
                println("任意值=更新，回车=跳过")
                val value = scanner.nextLine()
                if (value.isNotEmpty()) {
                    when (abilityAttr) {
                        "魔法消耗" -> if (!updateAbilityAttr(abilityName,"magic_consumption", to)) processHero(heroDetail, change)
                    }
                }
            }
        }
        //技能施法距离
        pattern = Pattern.compile("- (.*)的(施法距离)从.*至(.*)")
        matcher = pattern.matcher(change)
        if (matcher.find()) {
            val abilityName = matcher.group(1)
            val attrKey = matcher.group(2)
            val to = matcher.group(3)
            val ability = currentData.abilityList.find { it.name == abilityName }
            if (ability != null) {
                println("匹配到${abilityName}的${attrKey}改动到$to")
                println("技能说明：")
                println(ability.description)
                println("任意值=更新描述，回车=跳过")
                var value = scanner.nextLine()
                if (value.isNotEmpty()) {
                    if (!updateAbilityAttr(abilityName,"desc", value)) processHero(heroDetail, change)
                }
                println("技能提示：")
                println(ability.tips)
                println("任意值=更新提示，回车=跳过")
                value = scanner.nextLine()
                if (value.isNotEmpty()) {
                    if (!updateAbilityAttr(abilityName,"tips", value)) processHero(heroDetail, change)
                }

            }
        }
        println("未知英雄改动-->$change")
        scanner.nextLine()
    }


    private fun updateAbilityAttr(name: String,key: String, value: String): Boolean {
        val sql = "update HERO_ABILITY set $key='$value' where name='$name'"
        return askForExecute(sql)
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