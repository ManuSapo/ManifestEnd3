package de.fiereu.openmmo.codegen.item

/** [ids] is a list because the table lists some items in more than one slot. */
data class ParsedItem(
    val identifier: String,
    val name: String,
    val price: Int,
    val fieldUse: String,
    val useClass: String,
    val kindIndex: Int,
    val amount: Int,
    val holdEffect: Int,
    val ids: List<Int>,
)
