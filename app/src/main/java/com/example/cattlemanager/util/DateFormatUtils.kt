package com.example.cattlemanager.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object DateFormatUtils {

    private val backendFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val spanishFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es-ES"))

    fun toSpanishDisplay(date: String): String {
        return try {
            LocalDate.parse(date, backendFormatter).format(spanishFormatter)
        } catch (_: DateTimeParseException) {
            date
        }
    }

    fun toBackend(date: String): String {
        return try {
            LocalDate.parse(date, spanishFormatter).format(backendFormatter)
        } catch (_: DateTimeParseException) {
            date
        }
    }
}
