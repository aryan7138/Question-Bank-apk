package com.example.model

import java.util.UUID

data class PaperStyle(
    val fontChoice: String = "Serif",
    val fontSize: Int = 13,
    val headerFontSize: Int = 18,
    val lineSpacing: Float = 1.4f,
    val optColumns: Int = 2, // 1, 2, or 4
    val marksDisplay: String = "end", // "end", "margin", "hide"
    val sectionHeaderStyle: String = "bold", // "bold", "underline", "box"
    val showLogo: Boolean = false,
    val watermarkText: String = "",
    val footerText: String = ""
)

data class SectionItem(
    val qId: String,
    val markOverride: Float? = null
)

data class PaperSection(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "ক বিভাগ",
    val type: String = "MCQ", // MCQ, CQ, Short, Mixed
    val marksPerQuestion: Float = 1f,
    val numbering: String = "bn", // bn, ar, ro, al, bl
    val instruction: String = "",
    val totalMarks: String = "",
    val items: List<SectionItem> = emptyList()
)

data class Paper(
    val id: String = UUID.randomUUID().toString(),
    val rootId: String = UUID.randomUUID().toString(),
    val name: String = "Untitled Question Paper",
    val inst: String = "BAZLUL HUQ KHAN SCHOOL AND COLLEGE",
    val exam: String = "Half-Yearly Examination",
    val cls: String = "10",
    val sub: String = "বাংলা",
    val code: String = "101",
    val date: String = "",
    val time: String = "২ ঘণ্টা ৩০ মিনিট",
    val fullMarks: String = "100",
    val instructions: String = "১. প্রতিটি প্রশ্নের মান সমান।\n২. প্রয়োজনীয় ক্ষেত্রে চিত্র অঙ্কন করো।",
    val style: PaperStyle = PaperStyle(),
    val sections: List<PaperSection> = listOf(
        PaperSection(
            title = "ক বিভাগ - বহুনির্বাচনী প্রশ্ন",
            type = "MCQ",
            marksPerQuestion = 1f,
            numbering = "bn",
            instruction = "সকল প্রশ্নের উত্তর দাও",
            totalMarks = "30"
        ),
        PaperSection(
            title = "খ বিভাগ - সৃজনশীল প্রশ্ন",
            type = "CQ",
            marksPerQuestion = 10f,
            numbering = "bn",
            instruction = "যেকোনো ৭টি প্রশ্নের উত্তর দাও",
            totalMarks = "70"
        )
    ),
    val ver: Int = 1,
    val vname: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
