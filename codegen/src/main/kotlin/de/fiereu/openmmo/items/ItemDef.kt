package de.fiereu.openmmo.items

class ItemDef(
    val name: String,
    val price: Int,
    val fieldUse: String,
    val useClass: String,
    val kindIndex: Int,
    val amount: Int,
    val holdEffect: Int,
) {
  override fun toString(): String = name
}
