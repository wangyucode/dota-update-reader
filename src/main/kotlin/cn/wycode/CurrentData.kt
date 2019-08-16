package cn.wycode

import java.sql.Connection

class CurrentData(private val connection: Connection) {

    var heroDetailList = ArrayList<HeroDetail>()
    var itemList = ArrayList<DotaItem>()
    var itemAttrList = ArrayList<ItemAttr>()
    var itemDescList = ArrayList<ItemDesc>()
    var abilityList = ArrayList<Ability>()

    fun initData() {
        initHeroDetail()
        initItem()
        initItemAttr()
        initItemDesc()
        initAbility()
    }

    private fun initAbility() {
        val attrStatement = connection.prepareStatement("SELECT * FROM HERO_ABILITY  ")
        val resultSet = attrStatement.executeQuery()
        while (resultSet.next()) {
            val name = resultSet.getString("name")
            val annotation = resultSet.getString("annotation")
            val coolDown = resultSet.getString("cool_down")
            val description = resultSet.getString("description")
            val heroName = resultSet.getString("hero_name")
            val imageUrl = resultSet.getString("image_url")
            val magicConsumption = resultSet.getString("magic_consumption")
            val tips = resultSet.getString("tips")
            val num = resultSet.getInt("num")

            val ability = Ability(name, annotation, coolDown, description, heroName, imageUrl, magicConsumption, tips, num)
            abilityList.add(ability)
        }
    }

    private fun initItemDesc() {
        val attrStatement = connection.prepareStatement("SELECT * FROM DOTA_ITEM_DESC ")
        val resultSet = attrStatement.executeQuery()
        while (resultSet.next()) {
            val key = resultSet.getString("dota_item_key")
            val desc = resultSet.getString("desc")
            val descKey = resultSet.getString("desc_key")

            val item = ItemDesc(key, desc, descKey)
            itemDescList.add(item)
        }
    }

    private fun initItem() {
        val itemStatement = connection.prepareStatement("SELECT * FROM DOTA_ITEM ")
        val resultSet = itemStatement.executeQuery()
        while (resultSet.next()) {
            val key = resultSet.getString("key")
            val cd = resultSet.getInt("cd")
            val cname = resultSet.getString("cname")
            val cost = resultSet.getInt("cost")
            val img = resultSet.getString("img")
            val lore = resultSet.getString("lore")
            val mc = resultSet.getString("mc")
            val name = resultSet.getString("name")
            val notes = resultSet.getString("notes")
            val type = resultSet.getString("type")

            val item = DotaItem(key, type, cname, name, lore, img, notes, cost, mc, cd)
            itemList.add(item)
        }
    }

    private fun initItemAttr() {
        val attrStatement = connection.prepareStatement("SELECT * FROM DOTA_ITEM_ATTRS ")
        val resultSet = attrStatement.executeQuery()
        while (resultSet.next()) {
            val key = resultSet.getString("dota_item_key")
            val attr = resultSet.getString("attrs")
            val attrKey = resultSet.getString("attrs_key")

            val item = ItemAttr(key, attr, attrKey)
            itemAttrList.add(item)
        }
    }

    private fun initHeroDetail() {
        val heroDetailStatement = connection.prepareStatement("SELECT * FROM HERO_DETAIL")
        val resultSet = heroDetailStatement.executeQuery()
        while (resultSet.next()) {
            val name = resultSet.getString("name")
            val attackType = resultSet.getString("attack_type")
            val otherName = resultSet.getString("other_name")
            val strengthStart = resultSet.getInt("strength_start")
            val strengthGrow = resultSet.getString("strength_grow")
            val agilityStart = resultSet.getInt("agility_start")
            val agilityGrow = resultSet.getString("agility_grow")
            val intelligenceStart = resultSet.getInt("intelligence_start")
            val intelligenceGrow = resultSet.getString("intelligence_grow")
            val attackPower = resultSet.getInt("attack_power")
            val attackSpeed = resultSet.getInt("attack_speed")
            val armor = resultSet.getInt("armor")
            val speed = resultSet.getInt("speed")
            val story = resultSet.getString("story")
            val talent25Left = resultSet.getString("talent25left")
            val talent25Right = resultSet.getString("talent25right")
            val talent20Left = resultSet.getString("talent20left")
            val talent20Right = resultSet.getString("talent20right")
            val talent15Left = resultSet.getString("talent15left")
            val talent15Right = resultSet.getString("talent15right")
            val talent10Left = resultSet.getString("talent10left")
            val talent10Right = resultSet.getString("talent10right")

            val heroDetail = HeroDetail(
                name,
                attackType,
                otherName,
                story,
                strengthStart,
                strengthGrow,
                agilityStart,
                agilityGrow,
                intelligenceStart,
                intelligenceGrow,
                attackPower,
                attackSpeed,
                armor,
                speed,
                talent25Left,
                talent25Right,
                talent20Left,
                talent20Right,
                talent15Left,
                talent15Right,
                talent10Left,
                talent10Right
            )
            heroDetailList.add(heroDetail)
        }
    }
}