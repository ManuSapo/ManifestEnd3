package de.fiereu.openmmo.codegen.item

import java.io.File
import java.text.Normalizer
import java.util.Locale
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// A wire id is regionId * 1000 + index, and the client answers in the region 5 table.
private const val ITEM_REGION_BLOCK = 5000

private val PLACEHOLDER_NAMES = setOf("None", "?????", "???", "-", "?", "")

/** A name's position in `item_names.json` is the index `items.json` keys by. */
class ItemDataParser(private val dataDir: File) {

  private val json = Json { ignoreUnknownKeys = true }

  fun parseAll(): List<ParsedItem> {
    val itemsFile = File(dataDir, "items.json")
    val namesFile = File(dataDir, "item_names.json")
    require(itemsFile.exists() && namesFile.exists()) {
      "Item data not found at $dataDir (missing ${itemsFile.name} or ${namesFile.name})"
    }

    val names =
        json.parseToJsonElement(namesFile.readText()).jsonArray.map { it.jsonPrimitive.content }

    val rawItems =
        json.parseToJsonElement(itemsFile.readText()).jsonArray.map { entry ->
          val obj = entry.jsonObject
          RawItem(
              index = obj.getValue("index").jsonPrimitive.int,
              price = obj.getValue("price").jsonPrimitive.int,
              fieldUse = obj["field_use"]?.jsonPrimitive?.content ?: "",
              useClass = obj["use_class"]?.jsonPrimitive?.content ?: "0",
              kindIndex = obj["kind_index"]?.jsonPrimitive?.intOrNull ?: 0,
              amount = obj["amount"]?.jsonPrimitive?.intOrNull ?: 0,
              holdEffect = obj["hold_effect"]?.jsonPrimitive?.intOrNull ?: 0,
          )
        }

    return names
        .withIndex()
        .filterNot { (index, name) -> index == 0 || name.trim() in PLACEHOLDER_NAMES }
        .mapNotNull { (index, name) ->
          val identifier = identifierOf(name)
          if (identifier.isEmpty()) return@mapNotNull null
          val raw = rawItems.find { it.index == index } ?: return@mapNotNull null
          ItemEntry(identifier, name, index, raw)
        }
        .groupBy { it.identifier }
        .map { (identifier, group) ->
          val groupedNames = group.map { it.name }.distinct()
          check(groupedNames.size == 1) { "Items $groupedNames all derive the identifier $identifier" }
          val lowest = group.minBy { it.index }
          ParsedItem(
              identifier = identifier,
              name = lowest.name,
              price = lowest.raw.price,
              fieldUse = lowest.raw.fieldUse,
              useClass = lowest.raw.useClass,
              kindIndex = lowest.raw.kindIndex,
              amount = lowest.raw.amount,
              holdEffect = lowest.raw.holdEffect,
              ids = group.map { ITEM_REGION_BLOCK + it.index }.sorted(),
          )
        }
        .sortedBy { it.ids.first() }
  }

  private fun identifierOf(name: String): String =
      Normalizer.normalize(name, Normalizer.Form.NFD)
          .replace(Regex("\\p{Mn}+"), "")
          .uppercase(Locale.ROOT)
          .replace(Regex("[^A-Z0-9]+"), "_")
          .trim('_')
          .let { if (it.firstOrNull()?.isDigit() == true) "_$it" else it }

  private data class RawItem(
      val index: Int,
      val price: Int,
      val fieldUse: String,
      val useClass: String,
      val kindIndex: Int,
      val amount: Int,
      val holdEffect: Int,
  )

  private data class ItemEntry(
      val identifier: String,
      val name: String,
      val index: Int,
      val raw: RawItem,
  )
}
