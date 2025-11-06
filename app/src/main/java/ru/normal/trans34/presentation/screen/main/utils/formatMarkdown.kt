package ru.normal.trans34.presentation.screen.main.utils

import ru.normal.trans34.presentation.model.FormattedLine
import ru.normal.trans34.presentation.model.Style


fun formatMarkdown(text: String): List<FormattedLine> {
    return text.lines().map { line ->
        when {
            line.startsWith("# ") -> FormattedLine(line.removePrefix("# "), Style.HEADER1)
            line.startsWith("## ") -> FormattedLine(line.removePrefix("## "), Style.HEADER2)
            line.startsWith("### ") -> FormattedLine(line.removePrefix("## "), Style.HEADER3)
            line.startsWith("- ") || line.startsWith("* ") ->
                FormattedLine(line.drop(2), Style.LIST_ITEM)
            else -> FormattedLine(line, Style.NORMAL)
        }
    }
}
