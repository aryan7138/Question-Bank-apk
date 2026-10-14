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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.SubQuestion
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PaperViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleQuestionScreen(viewModel: PaperViewModel) {
    val context = LocalContext.current
    val editing = viewModel.editingQuestion.collectAsState().value
    val allQuestions by viewModel.allQuestions.collectAsState()

    var subject by remember(editing) { mutableStateOf(editing?.sub ?: "বাংলা") }
    var className by remember(editing) { mutableStateOf(editing?.cls ?: "10") }
    var chapter by remember(editing) { mutableStateOf(editing?.ch ?: "1") }
    var questionType by remember(editing) { mutableStateOf(editing?.type ?: "MCQ") }

    var isBoardQuestion by remember(editing) { mutableStateOf(editing?.bq ?: false) }
    var boardName by remember(editing) { mutableStateOf(editing?.bd ?: "ঢাকা") }
    var boardYear by remember(editing) { mutableStateOf(editing?.by ?: "2024") }
    var examType by remember(editing) { mutableStateOf(editing?.bx ?: "SSC") }
    var sourceNote by remember(editing) { mutableStateOf(editing?.src ?: "") }

    var questionText by remember(editing) { mutableStateOf(editing?.text ?: "") }
    var stemText by remember(editing) { mutableStateOf(editing?.stem ?: "") }

    // MCQ specific
    var isEnglishLabels by remember(editing) { mutableStateOf(editing?.en ?: false) }
    var hasFiveOptions by remember(editing) { mutableStateOf(editing?.five ?: false) }
    var correctAnswerIndex by remember(editing) { mutableStateOf(editing?.ans ?: "") }
    var options by remember(editing) {
        val initial = editing?.o ?: listOf("", "", "", "", "")
        mutableStateOf(initial.toMutableList())
    }

    // CQ specific
    var subQuestions by remember(editing) {
        val initial = editing?.s ?: listOf(
            SubQuestion(m = "1", k = "জ্ঞানমূলক"),
            SubQuestion(m = "2", k = "অনুধাবন"),
            SubQuestion(m = "3", k = "প্রয়োগ"),
            SubQuestion(m = "4", k = "উচ্চতর দক্ষতা")
        )
        mutableStateOf(initial.toMutableList())
    }

    // Duplicate detection modal state
    var duplicateCandidate by remember { mutableStateOf<Question?>(null) }
    var pendingSaveQuestion by remember { mutableStateOf<Question?>(null) }

    fun buildQuestion(): Question {
        return Question(
            id = editing?.id ?: UUID.randomUUID().toString(),
            sub = subject.trim(),
            cls = className.trim(),
            ch = chapter.trim(),
            type = questionType,
            text = questionText.trim(),
            stem = stemText.trim(),
            en = isEnglishLabels,
            five = hasFiveOptions,
            ans = correctAnswerIndex,
            o = options,
            s = subQuestions,
            bq = isBoardQuestion,
            bd = if (isBoardQuestion) boardName.trim() else "",
            by = if (isBoardQuestion) boardYear.trim() else "",
            bx = if (isBoardQuestion) examType.trim() else "",
            src = if (isBoardQuestion) sourceNote.trim() else "",
            createdAt = editing?.createdAt ?: System.currentTimeMillis()
        )
    }

    fun performSave(q: Question) {
        viewModel.saveQuestion(q) {
            Toast.makeText(context, if (editing != null) "প্রশ্ন আপডেট হয়েছে" else "প্রশ্ন সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
            viewModel.navigateTo(AppScreen.QUESTION_BANK)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (editing != null) "প্রশ্ন সম্পাদনা (Edit Question)" else "একক প্রশ্ন সংযোজন (Single Entry)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Metadata: Subject, Class, Chapter
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("মৌলিক তথ্য (Basic Info)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("বিষয় (Subject)") },
                        modifier = Modifier.weight(1.5f).testTag("input_subject"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("শ্রেণি (Class)") },
                        modifier = Modifier.weight(1f).testTag("input_class"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = chapter,
                        onValueChange = { chapter = it },
                        label = { Text("অধ্যায় (Ch)") },
                        modifier = Modifier.weight(1f).testTag("input_chapter"),
                        singleLine = true
                    )
                }

                // Question Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("MCQ", "CQ", "Short").forEach { type ->
                        val isSelected = questionType == type
                        if (isSelected) {
                            Button(
                                onClick = { questionType = type },
                                modifier = Modifier.weight(1f).testTag("type_btn_$type")
                            ) {
                                Text(type)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { questionType = type },
                                modifier = Modifier.weight(1f).testTag("type_btn_$type")
                            ) {
                                Text(type)
                            }
                        }
                    }
                }
            }
        }

        // Board Questions Toggle & Details
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isBoardQuestion,
                        onCheckedChange = { isBoardQuestion = it },
                        modifier = Modifier.testTag("checkbox_board_question")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("বোর্ড প্রশ্ন (Previous Board Exam Question)", fontWeight = FontWeight.Medium)
                }

                if (isBoardQuestion) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = boardName,
                            onValueChange = { boardName = it },
                            label = { Text("বোর্ড (Board)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = boardYear,
                            onValueChange = { boardYear = it },
                            label = { Text("বছর (Year)") },
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
                }
            }
        }

        // CQ Stem (Passage)
        if (questionType == "CQ") {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("উদ্দীপক (Stem or Passage)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stemText,
                        onValueChange = { stemText = it },
                        placeholder = { Text("উদ্দীপকের অনুচ্ছেদ বা তথ্য এখানে লিখুন...") },
                        modifier = Modifier.fillMaxWidth().testTag("input_stem"),
                        minLines = 4
                    )
                }
            }
        }

        // Question Lead-in / Main Text
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (questionType == "CQ") "ভূমিকা টেক্সট (Lead-in text, optional)" else "মূল প্রশ্ন (Question Text)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    placeholder = {
                        Text(if (questionType == "CQ") "যেমন: নিচের উদ্দীপকটি পড়ে প্রশ্নগুলোর উত্তর দাও:" else "প্রশ্ন লিখুন...")
                    },
                    modifier = Modifier.fillMaxWidth().testTag("input_question_text"),
                    minLines = 3
                )
            }
        }

        // MCQ Options
        if (questionType == "MCQ") {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("বিকল্প উত্তরসমূহ (Options)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isEnglishLabels,
                                onCheckedChange = { isEnglishLabels = it }
                            )
                            Text("English (A, B, C, D)", fontSize = 13.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = hasFiveOptions,
                                onCheckedChange = { hasFiveOptions = it }
                            )
                            Text("৫ম অপশন যোগ", fontSize = 13.sp)
                        }
                    }

                    val labels = if (isEnglishLabels) BengaliUtils.ENGLISH_LETTERS else BengaliUtils.BENGALI_LETTERS
                    val count = if (hasFiveOptions) 5 else 4

                    for (j in 0 until count) {
                        OutlinedTextField(
                            value = options.getOrElse(j) { "" },
                            onValueChange = { newVal ->
                                val copy = options.toMutableList()
                                while (copy.size <= j) copy.add("")
                                copy[j] = newVal
                                options = copy
                            },
                            label = { Text("(${labels[j]}) অপশন") },
                            modifier = Modifier.fillMaxWidth().testTag("option_input_$j"),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // CQ Sub Questions
        if (questionType == "CQ") {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("সৃজনশীল উপ-প্রশ্ন (ক, খ, গ, ঘ)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    val labels = BengaliUtils.BENGALI_LETTERS
                    val classifications = listOf("জ্ঞানমূলক", "অনুধাবন", "প্রয়োগ", "উচ্চতর দক্ষতা")

                    for (j in 0..3) {
                        val sq = subQuestions.getOrElse(j) { SubQuestion() }
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "(${labels[j]}) উপ-প্রশ্ন • ${classifications[j]}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                OutlinedTextField(
                                    value = sq.t,
                                    onValueChange = { newT ->
                                        val copy = subQuestions.toMutableList()
                                        copy[j] = sq.copy(t = newT)
                                        subQuestions = copy
                                    },
                                    placeholder = { Text("প্রশ্ন লিখুন...") },
                                    modifier = Modifier.fillMaxWidth().testTag("cq_sub_input_$j"),
                                    minLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Save & Cancel Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.navigateTo(AppScreen.QUESTION_BANK) },
                modifier = Modifier.weight(1f).testTag("cancel_button")
            ) {
                Text("বাতিল (Cancel)")
            }

            Button(
                onClick = {
                    if (questionText.isBlank() && stemText.isBlank()) {
                        Toast.makeText(context, "প্রশ্নের বিবরণ বা টেক্সট লিখুন", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val newQ = buildQuestion()

                    // Check duplicate in bank (if new question)
                    if (editing == null) {
                        val duplicate = allQuestions.firstOrNull { existing ->
                            BengaliUtils.calculateSimilarity(existing, newQ) >= 0.70f
                        }
                        if (duplicate != null) {
                            duplicateCandidate = duplicate
                            pendingSaveQuestion = newQ
                            return@Button
                        }
                    }

                    performSave(newQ)
                },
                modifier = Modifier.weight(1.5f).testTag("save_question_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (editing != null) "পরিবর্তন সংরক্ষণ" else "ব্যাংকে সংরক্ষণ করুন")
            }
        }
    }

    // Duplicate prompt dialog
    duplicateCandidate?.let { duplicate ->
        val candidate = pendingSaveQuestion
        AlertDialog(
            onDismissRequest = {
                duplicateCandidate = null
                pendingSaveQuestion = null
            },
            title = { Text("অনুরূপ প্রশ্ন পাওয়া গেছে (Similar Question)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ব্যাংকে প্রায় একই ধরনের প্রশ্ন ইতিমধ্যে বিদ্যমান আছে:")
                    Text(
                        text = "বিদ্যমান: ${duplicate.text.take(120)}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text("আপনি কি উভয়টি সংরক্ষণ করতে চান?")
                }
            },
            confirmButton = {
                Button(onClick = {
                    candidate?.let { performSave(it) }
                    duplicateCandidate = null
                    pendingSaveQuestion = null
                }) {
                    Text("উভয়টি রাখুন (Keep both)")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    duplicateCandidate = null
                    pendingSaveQuestion = null
                }) {
                    Text("বাদ দিন (Skip)")
                }
            }
        )
    }
}
