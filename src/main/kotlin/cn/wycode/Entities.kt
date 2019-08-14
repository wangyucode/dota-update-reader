package cn.wycode

data class HeroDetail(
    val name: String = "",
    val attackType: String = "",
    val otherName: String = "",
    val story: String = "",
    val strengthStart: Int = 0,
    val strengthGrow: String = "",
    val agilityStart: Int = 0,
    val agilityGrow: String = "",
    val intelligenceStart: Int = 0,
    val intelligenceGrow: String = "",
    val attackPower: Int = 0,
    val attackSpeed: Int = 0,
    val armor: Int = 0,
    val speed: Int = 0,
    val talent25Left: String = "",
    val talent25Right: String = "",
    val talent20Left: String = "",
    val talent20Right: String = "",
    val talent15Left: String = "",
    val talent15Right: String = "",
    val talent10Left: String = "",
    val talent10Right: String = ""
)

data class DotaItem(
    val key: String = "",
    var type: String = "",
    var cname: String = "",
    var name: String = "",
    val lore: String? = null,
    var img: String = "",
    val notes: String? = null,
    var cost: Int? = null,
    val mc: String? = null,
    val cd: Int? = null
)