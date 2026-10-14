package com.example.model

import java.util.UUID

data class SubQuestion(
    val t: String = "", // text
    val m: String = "", // marks
    val k: String = ""  // classification: জ্ঞানমূলক, অনুধাবন, প্রয়োগ, উচ্চতর দক্ষতা
)

data class Question(
    val id: String = UUID.randomUUID().toString(),
    val sub: String = "",
    val cls: String = "",
    val ch: String = "",
    val type: String = "MCQ", // MCQ, CQ, Short
    val text: String = "",
    val stem: String = "", // উদ্দীপক
    val en: Boolean = false, // English labels (A,B,C,D) vs Bengali (ক,খ,গ,ঘ)
    val five: Boolean = false, // 5 options vs 4 options
    val ans: String = "", // Correct option index (0, 1, 2, 3, 4)
    val o: List<String> = listOf("", "", "", "", ""), // Options
    val s: List<SubQuestion> = listOf(
        SubQuestion(),
        SubQuestion(),
        SubQuestion(),
        SubQuestion()
    ), // CQ sub questions ক, খ, গ, ঘ
    val bq: Boolean = false, // Board question flag
    val bd: String = "", // Board name
    val by: String = "", // Board year
    val bx: String = "", // Exam type (SSC, HSC, etc.)
    val src: String = "", // Source / Note
    val img: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
