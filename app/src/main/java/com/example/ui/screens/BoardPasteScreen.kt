package com.example.ui.screens

import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BengaliUtils
import com.example.model.Question
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PaperViewModel

@Composable
fun BoardPasteScreen(viewModel: PaperViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    var boardName by remember { mutableStateOf("ঢাকা") }
    var boardYear by remember { mutableStateOf("2024") }
    var subject by remember { mutableStateOf("বাংলা") }
    var className by remember { mutableStateOf("10") }
    var examType by remember { mutableStateOf("SSC") }
    var chapter by remember { mutableStateOf("1") }
    var rawText by remember { mutableStateOf("") }

    var parsedQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }

    fun doParseBoard() {
        if (boardName.isBlank() || boardYear.isBlank() || subject.isBlank() || className.isBlank()) {
            Toast.makeText(context, "বোর্ড, বছর, বিষয় এবং শ্রেণি পূরণ করুন", Toast.LENGTH_SHORT).show()
            return
        }
        if (rawText.isBlank()) {
            Toast.makeText(context, "প্রশ্ন টেক্সট পেস্ট করুন", Toast.LENGTH_SHORT).show()
            return
        }

        val parsed = BengaliUtils.parseQuestions(
            text = rawText,
            subject = subject.trim(),
            className = className.trim(),
            chapter = chapter.trim()
        )

        val withBoardData = parsed.map { q ->
            q.copy(
                bq = true,
                bd = boardName.trim(),
                by = BengaliUtils.toArabicDigits(boardYear.trim()),
                bx = examType.trim()
            )
        }

        if (withBoardData.isEmpty()) {
            Toast.makeText(context, "কোনো প্রশ্ন চিহ্নিত হয়নি", Toast.LENGTH_SHORT).show()
        } else {
            parsedQuestions = withBoardData
            selectedTab = 1
            Toast.makeText(context, "${BengaliUtils.toBengaliDigits(withBoardData.size)}টি বোর্ড প্রশ্ন তৈরি!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("১. বোর্ড তথ্য ও পেস্ট") },
                modifier = Modifier.testTag("tab_board_paste")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        if (parsedQuestions.isEmpty()) "২. পর্যালোচনা"
                        else "২. পর্যালোচনা (${BengaliUtils.toBengaliDigits(parsedQuestions.size)})"
                    )
                },
                modifier = Modifier.testTag("tab_board_review")
            )
        }

        when (selectedTab) {
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("বোর্ড পরীক্ষার তথ্য (Board Metadata)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = boardName,
                                    onValueChange = { boardName = it },
                                    label = { Text("বোর্ড *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = boardYear,
                                    onValueChange = { boardYear = it },
                                    label = { Text("সাল *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = examType,
                                    onValueChange = { examType = it },
                                    label = { Text("পরীক্ষা (SSC/HSC)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = subject,
                                    onValueChange = { subject = it },
                                    label = { Text("বিষয় *") },
                                    modifier = Modifier.weight(1.5f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = className,
                                    onValueChange = { className = it },
                                    label = { Text("শ্রেণি *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = chapter,
                                    onValueChange = { chapter = it },
                                    label = { Text("অধ্যায়") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = rawText,
                        onValueChange = { rawText = it },
                        label = { Text("বোর্ড প্রশ্ন পেস্ট করুন (Paste Board Questions)") },
                        placeholder = { Text("১। ঢাকা বোর্ড ২০২৪ এর প্রশ্ন এখানে হুবহু পেস্ট করুন...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("board_paste_textarea")
                    )

                    Button(
                        onClick = { doParseBoard() },
                        modifier = Modifier.fillMaxWidth().testTag("parse_board_button")
                    ) {
                        Text("বোর্ড প্রশ্ন পার্স করুন (Parse Board Questions)")
                    }
                }
            }

            1 -> {
                if (parsedQuestions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("কোনো প্রশ্ন পার্স করা হয়নি।", color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = { selectedTab = 0 }) {
                            Text("প্রশ্ন পেস্ট করতে যান")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "বোর্ড প্রশ্ন: ${BengaliUtils.toBengaliDigits(parsedQuestions.size)}টি",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Button(
                                onClick = {
                                    viewModel.saveMultipleQuestions(parsedQuestions) { count ->
                                        Toast.makeText(context, "${BengaliUtils.toBengaliDigits(count)}টি বোর্ড প্রশ্ন সংরক্ষিত!", Toast.LENGTH_LONG).show()
                                        viewModel.navigateTo(AppScreen.QUESTION_BANK)
                                    }
                                },
                                modifier = Modifier.testTag("save_all_board_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সবগুলো ব্যাংকে সংরক্ষণ করুন")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(parsedQuestions) { idx, q ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "প্রশ্ন ${BengaliUtils.toBengaliDigits(idx + 1)}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                TypeBadge(type = q.type)
                                                BadgeChip(
                                                    text = "${q.bd} '${q.by.takeLast(2)}",
                                                    color = Color(0xFF1D4E89)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            if (q.stem.isNotBlank()) {
                                                Text(
                                                    text = "উদ্দীপক: ${q.stem}",
                                                    fontSize = 12.sp,
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                                )
                                            }

                                            Text(
                                                text = q.text.ifBlank { "(প্রশ্ন টেক্সট)" },
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        IconButton(onClick = {
                                            parsedQuestions = parsedQuestions.toMutableList().apply { removeAt(idx) }
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Remove",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
