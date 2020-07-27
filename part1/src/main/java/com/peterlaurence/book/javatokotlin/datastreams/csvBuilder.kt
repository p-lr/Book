package com.peterlaurence.book.javatokotlin.datastreams

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Create CSV string of the data with the followings columns:
 * date | sn | charac1 | charac2 | .. | characN
 */
private fun createCsv(series: List<Serie>): String {
    data class CharacSerialKey(val serial: String, val charac: Charac, val date: LocalDateTime)

    val valuesMap = hashMapOf<CharacSerialKey, Point>()

    series.filter {
        it.charac.type == CharacType.CRITICAL || it.charac.type == CharacType.IMPORTANT
    }.forEach { serie ->
        serie.points.forEach { point ->
            val key = CharacSerialKey(point.sn, serie.charac, point.date)
            valuesMap[key] = point
        }
    }

    val characSet = valuesMap.keys.distinctBy { it.charac }.map { it.charac }.toSet()
    val dateSet = valuesMap.values.distinctBy { it.date }.map { it.date }.toSet().sorted()
    val csvHeader = "date;sn;" + characSet.joinToString(";") { it.name } + "\n"

    val uniqueSerials = valuesMap.keys.distinctBy { it.serial }.map { it.serial }
    val rows = dateSet.joinToString("") { date ->
        uniqueSerials.map { serial ->
            val characColumns = characSet.map { charac ->
                val value = valuesMap[CharacSerialKey(serial, charac, date)]
                value?.value?.toString() ?: " "
            }

            listOf(date.format(), serial) + characColumns
        }.joinToString(separator = "") {
            it.joinToString(";", postfix = "\n")
        }
    }

    return csvHeader + rows
}

private fun LocalDateTime.format(): String {
    return this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
}

fun main() {
    val dates = listOf<LocalDateTime>(
        LocalDateTime.parse("2020-07-27T15:45:00"),
        LocalDateTime.parse("2020-07-27T15:35:00"),
        LocalDateTime.parse("2020-07-27T15:25:00"),
        LocalDateTime.parse("2020-07-27T15:15:00")
    )

    val seriesExample = listOf(
        Serie(
            points = listOf(
                Point("HC127", dates[0], 0.1),
                Point("HC127", dates[3], 0.2),
                Point("HC100", dates[0], 2.0),
                Point("HC100", dates[3], 2.1)
            ),
            charac = Charac("AngleOfAttack", CharacType.CRITICAL)
        ),
        Serie(
            points = listOf(
                Point("HC127", dates[3], 0.5),
                Point("HC100", dates[3], 0.7)
            ),
            charac = Charac("ChordLength", CharacType.IMPORTANT)
        ),
        Serie(
            points = listOf(
                Point("HC127", dates[3], 0.5),
                Point("HC127", dates[3], 0.7)
            ),
            charac = Charac("PaintColor", CharacType.REGULAR)
        )
    )

    val csv = createCsv(seriesExample)
    println(csv)
}