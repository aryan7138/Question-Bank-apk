package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BengaliUtils
import com.example.model.Paper
import com.example.model.Question

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaperPreviewView(
    paper: Paper,
    questionMap: Map<String, Question>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Outer double border of Bangladeshi question sheet
                .padding(6.dp)
                .border(1.5.dp, Color.Black)
                .padding(3.dp)
                .border(0.75.dp, Color.Black)
                .padding(14.dp)
        ) {
            // Watermark
            if (paper.style.watermarkText.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = paper.style.watermarkText,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.06f),
                        modifier = Modifier.rotate(-30f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Institution Name
                Text(
                    text = paper.inst.ifBlank { "শিক্ষা প্রতিষ্ঠানের নাম" },
                    fontSize = (paper.style.headerFontSize).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "(জাতীয় শিক্ষাক্রম বাংলা ও ইংরেজি ভার্সন)",
                    fontSize = 11.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                if (paper.exam.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = paper.exam,
                        fontSize = (paper.style.fontSize + 2).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Meta Row: Left (Time, notes) • Center (Class, Subject) • Right (Code, Full marks)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "সময়: ${paper.time}",
                            fontSize = paper.style.fontSize.sp,
                            color = Color.Black
                        )
                        if (paper.date.isNotBlank()) {
                            Text(
                                text = "তারিখ: ${paper.date}",
                                fontSize = (paper.style.fontSize - 1).sp,
                                color = Color.Black
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "শ্রেণি: ${paper.cls}",
                            fontSize = paper.style.fontSize.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = "বিষয়: ${paper.sub}",
                            fontSize = paper.style.fontSize.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (paper.code.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "বিষয় কোড: ",
                                    fontSize = 11.sp,
                                    color = Color.Black
                                )
                                SubjectCodeBoxes(code = paper.code)
                            }
                        }
                        Text(
                            text = "পূর্ণমান: ${BengaliUtils.toBengaliDigits(paper.fullMarks)}",
                            fontSize = paper.style.fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = Color.Black, thickness = 0.75.dp)
                Spacer(modifier = Modifier.height(6.dp))

                // Instructions
                val instructions = paper.instructions.lines().filter { it.isNotBlank() }
                if (instructions.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        instructions.forEach { line ->
                            Text(
                                text = line,
                                fontSize = (paper.style.fontSize - 1).sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Sections & Questions
                var globalQNumber = 1

                paper.sections.forEachIndexed { secIdx, sec ->
                    Spacer(modifier = Modifier.height(10.dp))

                    // Section Heading
                    SectionHeadingView(
                        title = sec.title,
                        instruction = sec.instruction,
                        totalMarks = sec.totalMarks,
                        style = paper.style.sectionHeaderStyle,
                        fontSize = paper.style.fontSize + 1
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (sec.items.isEmpty()) {
                        Text(
                            text = "[ এই সেকশনে এখনো কোনো প্রশ্ন যোগ করা হয়নি ]",
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        sec.items.forEach { it ->
                            val q = questionMap[it.qId]
                            if (q != null) {
                                val mark = it.markOverride ?: sec.marksPerQuestion
                                QuestionItemPreview(
                                    qNumber = globalQNumber,
                                    question = q,
                                    mark = mark,
                                    fontSize = paper.style.fontSize,
                                    optColumns = paper.style.optColumns,
                                    showMarks = paper.style.marksDisplay != "hide"
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                globalQNumber++
                            }
                        }
                    }
                }

                if (paper.style.footerText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = paper.style.footerText,
                        fontSize = 10.sp,
                        color = Color.Black.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectCodeBoxes(code: String) {
    val chars = code.replace(" ", "").take(5).padEnd(3, ' ')
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        chars.forEach { ch ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .border(0.75.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (ch != ' ') BengaliUtils.toBengaliDigits(ch.toString()) else "",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun SectionHeadingView(
    title: String,
    instruction: String,
    totalMarks: String,
    style: String,
    fontSize: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val titleText = if (totalMarks.isNotBlank()) "$title (পূর্ণমান: ${BengaliUtils.toBengaliDigits(totalMarks)})" else title
        when (style) {
            "box" -> {
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black)
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = titleText,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            "underline" -> {
                Text(
                    text = titleText,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    color = Color.Black
                )
            }
            else -> {
                Text(
                    text = titleText,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        if (instruction.isNotBlank()) {
            Text(
                text = "($instruction)",
                fontSize = (fontSize - 2).sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun QuestionItemPreview(
    qNumber: Int,
    question: Question,
    mark: Float,
    fontSize: Int,
    optColumns: Int,
    showMarks: Boolean
) {
    val numStr = BengaliUtils.toBengaliDigits(qNumber) + "। "
    val markStr = if (showMarks && question.type != "CQ") " [${BengaliUtils.toBengaliDigits(mark)}]" else ""

    Column(modifier = Modifier.fillMaxWidth()) {
        // Main Question line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = numStr + question.text,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            if (markStr.isNotBlank()) {
                Text(
                    text = markStr,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }

        // Stem (Passage)
        if (question.stem.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.padding(start = 12.dp)) {
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .background(Color.Black)
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "উদ্দীপক: " + question.stem,
                    fontSize = fontSize.sp,
                    color = Color.Black
                )
            }
        }

        // Options for MCQ
        if (question.type == "MCQ") {
            Spacer(modifier = Modifier.height(4.dp))
            val labels = if (question.en) BengaliUtils.ENGLISH_LETTERS else BengaliUtils.BENGALI_LETTERS
            val count = if (question.five) 5 else 4

            if (optColumns == 2) {
                // 2 columns
                for (j in 0 until count step 2) {
                    val opt1 = question.o.getOrNull(j) ?: ""
                    val opt2 = question.o.getOrNull(j + 1) ?: ""

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 2.dp)
                    ) {
                        Text(
                            text = if (opt1.isNotBlank()) "(${labels[j]}) $opt1" else "",
                            fontSize = fontSize.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (opt2.isNotBlank() && (j + 1 < count)) "(${labels[j + 1]}) $opt2" else "",
                            fontSize = fontSize.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // 1 column stacked
                for (j in 0 until count) {
                    val opt = question.o.getOrNull(j) ?: ""
                    if (opt.isNotBlank()) {
                        Text(
                            text = "(${labels[j]}) $opt",
                            fontSize = fontSize.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }
                }
            }
        } else if (question.type == "CQ") {
            // Sub-questions ক, খ, গ, ঘ
            Spacer(modifier = Modifier.height(4.dp))
            for (j in 0 until minOf(4, question.s.size)) {
                val sq = question.s[j]
                if (sq.t.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "(${BengaliUtils.BENGALI_LETTERS[j]}) ${sq.t}",
                            fontSize = fontSize.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        if (showMarks && sq.m.isNotBlank()) {
                            Text(
                                text = " [${BengaliUtils.toBengaliDigits(sq.m)}]",
                                fontSize = fontSize.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
