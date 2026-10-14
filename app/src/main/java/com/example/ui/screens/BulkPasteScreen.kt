package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.Checkbox
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
fun BulkPasteScreen(viewModel: PaperViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    var subject by remember { mutableStateOf("বাংলা") }
    var className by remember { mutableStateOf("10") }
    var chapter by remember { mutableStateOf("1") }
    var rawText by remember { mutableStateOf("") }

    var isStemDetectionEnabled by remember { mutableStateOf(true) }
    var defaultFallbackType by remember { mutableStateOf("Short") }

    var parsedQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }

    fun doParse() {
        if (rawText.isBlank()) {
            Toast.makeText(context, "প্রথমে প্রশ্ন পেস্ট করুন (Paste questions first)", Toast.LENGTH_SHORT).show()
            return
        }
        val result = BengaliUtils.parseQuestions(
            text = rawText,
            subject = subject.trim(),
            className = className.trim(),
            chapter = chapter.trim(),
            defaultType = defaultFallbackType,
            isStemDetectEnabled = isStemDetectionEnabled
        )
        if (result.isEmpty()) {
            Toast.makeText(context, "কোনো প্রশ্ন চিহ্নিত করা যায়নি। ফরম্যাট পরীক্ষা করুন।", Toast.LENGTH_SHORT).show()
        } else {
            parsedQuestions = result
            selectedTab = 2 // Switch to Review Tab
            Toast.makeText(context, "${BengaliUtils.toBengaliDigits(result.size)}টি প্রশ্ন প্রস্তুত!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("১. পেস্ট এরিয়া", fontSize = 13.sp) },
                modifier = Modifier.testTag("tab_paste_area")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("২. পার্সিং নিয়ম", fontSize = 13.sp) },
                modifier = Modifier.testTag("tab_parsing_rules")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = if (parsedQuestions.isEmpty()) "৩. পর্যালোচনা" else "৩. পর্যালোচনা (${BengaliUtils.toBengaliDigits(parsedQuestions.size)})",
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_review_save")
            )
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Paste Area
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("বিষয় (Subject)") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = className,
                            onValueChange = { className = it },
                            label = { Text("শ্রেণি (Class)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = chapter,
                            onValueChange = { chapter = it },
                            label = { Text("অধ্যায় (Ch)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = rawText,
                        onValueChange = { rawText = it },
                        label = { Text("প্রশ্ন পেস্ট করুন (Paste questions here)") },
                        placeholder = {
                            Text("১। প্রশ্ন টেক্সট\nক) অপশন ১\nখ) অপশন ২\nগ) অপশন ৩\nঘ) অপশন ৪\nউত্তর: খ\n\n২। উদ্দীপক:\nরহিম একজন কৃষক...\nক) জ্ঞানমূলক প্রশ্ন\nখ) অনুধাবন প্রশ্ন...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("bulk_paste_textarea")
                    )

                    Button(
                        onClick = { doParse() },
                        modifier = Modifier.fillMaxWidth().testTag("parse_questions_button")
                    ) {
                        Text("প্রশ্নসমূহ পার্স করুন (Parse Questions)")
                    }
                }
            }

            1 -> {
                // Tab 1: Rules
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("স্বয়ংক্রিয় সনাক্তকরণ নিয়মাবলী", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isStemDetectionEnabled,
                                    onCheckedChange = { isStemDetectionEnabled = it }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("উদ্দীপক / Stem বা Passage থাকলে সৃজনশীল (CQ) হিসেবে গণ্য করো")
                            }

                            Text(
                                text = "• নম্বরযুক্ত লাইন (1. বা ১। বা Q1) নতুন প্রশ্ন হিসেবে শুরু হয়।\n" +
                                        "• ক) খ) গ) ঘ) অথবা A) B) C) D) লাইনগুলো বিকল্প অপশন হিসেবে যুক্ত হয়।\n" +
                                        "• লাইনের শেষে প্রশ্নবোধক চিহ্ন (?) থাকলে বা ব্যাখ্যামূলক নির্দেশ (করো, ব্যাখ্যা ইত্যাদি) থাকলে তা স্বয়ংক্রিয়ভাবে CQ সাব-প্রশ্ন হিসেবে গণ্য হয়।\n" +
                                        "• উত্তর: খ বা Ans: B থাকলে সঠিক উত্তর হিসেবে সেট হয়।",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Button(
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("পেস্ট এরিয়াতে ফিরে যান")
                    }
                }
            }

            2 -> {
                // Tab 2: Review and Save
                if (parsedQuestions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("এখনো কোনো প্রশ্ন পার্স করা হয়নি।", color = MaterialTheme.colorScheme.secondary)
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
                                text = "সনাক্তকৃত প্রশ্ন: ${BengaliUtils.toBengaliDigits(parsedQuestions.size)}টি",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Button(
                                onClick = {
                                    viewModel.saveMultipleQuestions(parsedQuestions) { count ->
                                        Toast.makeText(context, "${BengaliUtils.toBengaliDigits(count)}টি প্রশ্ন ব্যাংকে সংরক্ষিত হয়েছে!", Toast.LENGTH_LONG).show()
                                        viewModel.navigateTo(AppScreen.QUESTION_BANK)
                                    }
                                },
                                modifier = Modifier.testTag("save_all_parsed_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সবগুলো সংরক্ষণ করুন")
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
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "প্রশ্ন ${BengaliUtils.toBengaliDigits(idx + 1)}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                TypeBadge(type = q.type)
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            if (q.stem.isNotBlank()) {
                                                Text(
                                                    text = "উদ্দীপক: ${q.stem}",
                                                    fontSize = 12.sp,
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                    maxLines = 2
                                                )
                                            }

                                            Text(
                                                text = q.text.ifBlank { "(প্রশ্ন টেক্সট)" },
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )

                                            if (q.type == "MCQ") {
                                                val opts = q.o.filter { it.isNotBlank() }
                                                Text(
                                                    text = opts.joinToString(" • "),
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
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
