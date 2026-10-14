package com.example.model

import java.util.Locale
import java.util.UUID

object BengaliUtils {
    val BENGALI_LETTERS = listOf("ক", "খ", "গ", "ঘ", "ঙ")
    val ENGLISH_LETTERS = listOf("A", "B", "C", "D", "E")
    val ROMAN_NUMERALS = listOf(
        "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
        "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX"
    )

    private val BENGALI_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBengaliDigits(input: Any?): String {
        if (input == null) return ""
        val s = input.toString()
        val sb = StringBuilder()
        for (ch in s) {
            if (ch in '0'..'9') {
                sb.append(BENGALI_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toArabicDigits(input: String?): String {
        if (input.isNullOrEmpty()) return ""
        val sb = StringBuilder()
        for (ch in input) {
            val idx = BENGALI_DIGITS.indexOf(ch)
            if (idx >= 0) {
                sb.append(idx)
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatNumber(index: Int, style: String): String {
        return when (style.lowercase(Locale.ROOT)) {
            "bn" -> toBengaliDigits(index + 1)
            "ar" -> (index + 1).toString()
            "ro" -> if (index < ROMAN_NUMERALS.size) ROMAN_NUMERALS[index] else (index + 1).toString()
            "al" -> ('A' + (index % 26)).toString()
            "bl" -> BENGALI_LETTERS[index % BENGALI_LETTERS.size]
            else -> toBengaliDigits(index + 1)
        }
    }

    private val QMK = Regex("[?？]$")
    private val CQW = Regex(
        "(?:^|[\\s,(])(?:করো|করুন|লেখো|দেখাও|ব্যাখ্যা|বিশ্লেষণ|নির্ণয়|প্রমাণ|বর্ণনা|আলোচনা|মূল্যায়ন|বুঝিয়ে|কেন|কীভাবে|কিভাবে|কাকে|বলতে|explain|describe|define|find|prove|write|discuss|justify|analyze|calculate|determine|evaluate|why|how|what)(?=$|[\\s,।?!.)])",
        RegexOption.IGNORE_CASE
    )

    private fun stripMarks(s: String): String {
        return s.replace(
            Regex("\\s*[\\[(（]?\\s*[0-9০-৯]{1,2}\\s*(?:নম্বর|marks?|মার্কস?)?[\\])）]?\\s*$", RegexOption.IGNORE_CASE),
            ""
        ).trim()
    }

    private fun kindOf(oo: List<String>, stem: String, defaultType: String): String {
        if (stem.isNotBlank()) return "CQ"
        val n = oo.size
        if (n == 0) return defaultType
        val clean = oo.map { it.trim() }
        val avgLen = clean.sumOf { it.length } / n.toDouble()

        if (clean.any { QMK.containsMatchIn(stripMarks(it)) }) return "CQ"
        if (avgLen > 50) return "CQ"

        val dandaCount = clean.count { stripMarks(it).endsWith("।") }
        val keywordCount = clean.count { CQW.containsMatchIn(it) }
        val markCount = clean.count { Regex("\\s[\\[(（]\\s*[1-9১-৯]\\s*[\\])）]$").containsMatchIn(it) }

        val score = (if (keywordCount >= 2) 2 else 0) +
                (if (markCount >= 2) 2 else 0) +
                (if (dandaCount >= 2 && keywordCount >= 1) 2 else 0) +
                (if (keywordCount >= 1 && n >= 3 && avgLen > 30) 2 else 0)

        return if (score >= 2) "CQ" else if (n >= 2) "MCQ" else defaultType
    }

    private class ParsedItem(
        var text: String = "",
        var stem: String = "",
        val oo: MutableList<String> = mutableListOf(),
        var ans: String = "",
        var isEnglish: Boolean = false
    )

    /**
     * Parses bulk raw text into Questions using standard Bangladeshi examination format.
     */
    fun parseQuestions(
        text: String,
        subject: String = "",
        className: String = "",
        chapter: String = "",
        defaultType: String = "Short",
        forceType: String? = null,
        isStemDetectEnabled: Boolean = true
    ): List<Question> {
        val out = mutableListOf<ParsedItem>()

        val numberRegex = Regex("^(?:Q\\.?\\s*)?(?:\\(([0-9০-৯]+)\\)|([0-9০-৯]+)\\s*[.।):])\\s*(.*)$", RegexOption.IGNORE_CASE)
        val optionRegex = Regex("^\\(?([কখগঘঙ]|[A-Ea-e])\\s*[).।]\\s*(.*)$")
        val stemRegex = Regex("^(উদ্দীপক|stem|passage)(?:\\s*[:：-]\\s*|\\s+)(.*)$", RegexOption.IGNORE_CASE)
        val answerRegex = Regex("^(?:উত্তর|সঠিক\\s*উত্তর|ans(?:wer)?)\\s*[:：\\-–.]?\\s*\\(?\\s*([কখগঘঙ]|[A-Ea-e])\\s*\\)?\\s*[.।]?\\s*$", RegexOption.IGNORE_CASE)

        var cur: ParsedItem? = null
        var mode = ""
        var pendingStem = ""

        fun newItem(t: String, st: String): ParsedItem {
            val item = ParsedItem(text = t, stem = st)
            out.add(item)
            return item
        }

        val lines = text.lines()
        for (raw in lines) {
            val ln = raw.trim()
            if (ln.isEmpty()) continue

            val numMatch = numberRegex.find(ln)
            if (numMatch != null) {
                val qText = numMatch.groupValues[3]
                cur = newItem(qText, pendingStem)
                pendingStem = ""
                mode = "t"
                continue
            }

            if (isStemDetectEnabled) {
                val stemMatch = stemRegex.find(ln)
                if (stemMatch != null) {
                    val sText = stemMatch.groupValues[2]
                    if (cur != null && cur.oo.isEmpty() && cur.stem.isEmpty()) {
                        cur.stem = sText
                        mode = "s"
                    } else {
                        pendingStem = sText
                        mode = "ps"
                    }
                    continue
                }
            }

            val ansMatch = answerRegex.find(ln)
            if (cur != null && ansMatch != null) {
                val letter = ansMatch.groupValues[1]
                val idx = if (BENGALI_LETTERS.contains(letter)) {
                    BENGALI_LETTERS.indexOf(letter)
                } else {
                    ENGLISH_LETTERS.indexOf(letter.uppercase(Locale.ROOT))
                }
                if (idx in 0..4) {
                    cur.ans = idx.toString()
                }
                mode = "a"
                continue
            }

            val optMatch = optionRegex.find(ln)
            if (optMatch != null) {
                if (cur == null) {
                    cur = newItem("", pendingStem)
                    pendingStem = ""
                }
                cur.oo.add(optMatch.groupValues[2])
                if (cur.oo.size == 1) {
                    cur.isEnglish = Regex("[A-Ea-e]").matches(optMatch.groupValues[1])
                }
                mode = "o"
                continue
            }

            if (cur == null) {
                cur = newItem("", pendingStem)
                pendingStem = ""
            }

            if (mode == "a") continue
            if (mode == "o" && ln.length < 80) {
                if (cur.oo.isNotEmpty()) {
                    val lastIdx = cur.oo.size - 1
                    cur.oo[lastIdx] = cur.oo[lastIdx] + " " + ln
                }
            } else if (mode == "s") {
                cur.stem = if (cur.stem.isEmpty()) ln else "${cur.stem} $ln"
            } else if (mode == "ps") {
                pendingStem = if (pendingStem.isEmpty()) ln else "$pendingStem $ln"
            } else {
                cur.text = if (cur.text.isEmpty()) ln else "${cur.text} $ln"
                mode = "t"
            }
        }

        if (pendingStem.isNotEmpty()) {
            if (out.isEmpty()) {
                newItem("", pendingStem)
            } else if (out.last().stem.isEmpty() && out.last().oo.isEmpty()) {
                out.last().stem = pendingStem
            }
        }

        return out.map { item ->
            val n = item.oo.size
            val detectedType = if (!forceType.isNullOrBlank() && forceType != "Auto") {
                forceType
            } else {
                kindOf(item.oo, item.stem, defaultType)
            }

            val opts = mutableListOf("", "", "", "", "")
            for (i in 0 until minOf(5, item.oo.size)) {
                opts[i] = item.oo[i]
            }

            val subQuestions = if (detectedType == "CQ") {
                listOf(
                    SubQuestion(t = item.oo.getOrNull(0) ?: "", m = "1", k = "জ্ঞানমূলক"),
                    SubQuestion(t = item.oo.getOrNull(1) ?: "", m = "2", k = "অনুধাবন"),
                    SubQuestion(t = item.oo.getOrNull(2) ?: "", m = "3", k = "প্রয়োগ"),
                    SubQuestion(t = item.oo.getOrNull(3) ?: "", m = "4", k = "উচ্চতর দক্ষতা")
                )
            } else {
                listOf(SubQuestion(), SubQuestion(), SubQuestion(), SubQuestion())
            }

            val cleanText = if (detectedType == "Short" && item.oo.isNotEmpty()) {
                (listOf(item.text) + item.oo).filter { it.isNotBlank() }.joinToString("\n")
            } else {
                item.text
            }

            Question(
                id = UUID.randomUUID().toString(),
                sub = subject,
                cls = className,
                ch = chapter,
                type = detectedType,
                text = cleanText,
                stem = item.stem,
                en = item.isEnglish,
                five = n >= 5,
                ans = if (detectedType == "MCQ") item.ans else "",
                o = opts,
                s = subQuestions
            )
        }.filter { q ->
            q.text.isNotBlank() || q.stem.isNotBlank() || q.o.any { it.isNotBlank() } || q.s.any { it.t.isNotBlank() }
        }
    }

    /**
     * Similarity metric between two questions for duplicate detection.
     */
    fun calculateSimilarity(q1: Question, q2: Question): Float {
        val s1 = "${q1.stem} ${q1.text} ${q1.o.joinToString(" ")}".lowercase(Locale.ROOT)
            .split(Regex("[\\s,.;:()\\[\\]?!।]+"))
            .filter { it.isNotBlank() }
            .toSet()
        val s2 = "${q2.stem} ${q2.text} ${q2.o.joinToString(" ")}".lowercase(Locale.ROOT)
            .split(Regex("[\\s,.;:()\\[\\]?!।]+"))
            .filter { it.isNotBlank() }
            .toSet()

        if (s1.isEmpty() || s2.isEmpty()) return 0f
        val intersection = s1.intersect(s2).size
        val union = (s1 + s2).size
        return intersection.toFloat() / union.toFloat()
    }

    /**
     * Generates plain-text representation of a question paper for sharing.
     */
    fun paperToFormattedText(paper: Paper, questionMap: Map<String, Question>): String {
        val divider = "=".repeat(60)
        val sb = StringBuilder()
        sb.append(divider).append("\n")
        sb.append(paper.inst).append("\n")
        if (paper.exam.isNotBlank()) sb.append(paper.exam).append("\n")
        sb.append(divider).append("\n")
        sb.append("শ্রেণি: ").append(paper.cls).append("    বিষয়: ").append(paper.sub)
        if (paper.code.isNotBlank()) sb.append("    বিষয় কোড: ").append(paper.code)
        sb.append("\n")
        sb.append("সময়: ").append(paper.time)
        if (paper.fullMarks.isNotBlank()) sb.append("    পূর্ণমান: ").append(toBengaliDigits(paper.fullMarks))
        sb.append("\n\n")

        val instList = paper.instructions.lines().filter { it.isNotBlank() }
        if (instList.isNotEmpty()) {
            sb.append("বিশেষ নির্দেশাবলী:\n")
            instList.forEach { sb.append("  ").append(it).append("\n") }
            sb.append("\n")
        }

        var globalIndex = 1
        for (sec in paper.sections) {
            sb.append("\n[ ").append(sec.title).append(" ]")
            if (sec.totalMarks.isNotBlank()) {
                sb.append("  (পূর্ণমান: ").append(toBengaliDigits(sec.totalMarks)).append(")")
            }
            sb.append("\n")
            if (sec.instruction.isNotBlank()) {
                sb.append("  (").append(sec.instruction).append(")\n")
            }

            for (it in sec.items) {
                val q = questionMap[it.qId] ?: continue
                val mark = it.markOverride ?: sec.marksPerQuestion
                val num = toBengaliDigits(globalIndex) + "। "

                sb.append(num).append(q.text)
                if (q.type != "CQ") {
                    sb.append(" [মান: ").append(toBengaliDigits(mark)).append("]")
                }
                sb.append("\n")

                if (q.stem.isNotBlank()) {
                    sb.append("   উদ্দীপক: ").append(q.stem).append("\n")
                }

                if (q.type == "MCQ") {
                    val labels = if (q.en) ENGLISH_LETTERS else BENGALI_LETTERS
                    val count = if (q.five) 5 else 4
                    for (j in 0 until count) {
                        val opt = q.o.getOrNull(j) ?: ""
                        if (opt.isNotBlank()) {
                            sb.append("   (").append(labels[j]).append(") ").append(opt).append("\n")
                        }
                    }
                } else if (q.type == "CQ") {
                    for (j in 0 until minOf(4, q.s.size)) {
                        val sq = q.s[j]
                        if (sq.t.isNotBlank()) {
                            sb.append("   (").append(BENGALI_LETTERS[j]).append(") ").append(sq.t)
                            if (sq.m.isNotBlank()) {
                                sb.append(" [").append(toBengaliDigits(sq.m)).append("]")
                            }
                            sb.append("\n")
                        }
                    }
                }
                sb.append("\n")
                globalIndex++
            }
        }
        return sb.toString()
    }
}
