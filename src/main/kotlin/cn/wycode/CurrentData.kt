package cn.wycode

import java.sql.Connection

class CurrentData(private val connection: Connection) {

    var heroDetailList = ArrayList<HeroDetail>()
    var itemList = ArrayList<DotaItem>()

    fun initData() {
        initHeroDetail()
        initItem()
    }

    private fun initItem() {
//        val itemStatement = connection.prepareStatement("SELECT * FROM DOTA_ITEM ")
//        val resultSet = itemStatement.executeQuery()
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