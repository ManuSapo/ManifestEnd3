package de.fiereu.openmmo.codegen.item

object RenderUtil {

  fun declaration(item: ParsedItem): String =
      "  /** ${item.ids.joinToString(", ")} */\n" +
          "  val ${item.identifier} = item(${escapeString(item.name)}, ${item.price}, ${escapeString(item.fieldUse)}, ${escapeString(item.useClass)}, ${item.kindIndex}, ${item.amount}, ${item.holdEffect})"

  fun register(item: ParsedItem): String =
      "reg.register(Items.${item.identifier}, ${item.ids.joinToString(", ")})"

  private fun escapeString(s: String): String =
      "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
