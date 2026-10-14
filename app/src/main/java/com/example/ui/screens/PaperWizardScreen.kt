package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BengaliUtils
import com.example.model.Paper
import com.example.model.PaperSection
import com.example.model.Question
import com.example.pdf.PrintHelper
import com.example.viewmodel.PaperViewModel
import com.example.viewmodel.WizardStep
import java.util.Locale

@Composable
fun PaperWizardScreen(viewModel: PaperViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val draft by viewModel.currentDraft.collectAsState()
    val activeStep by viewModel.activeWizardStep.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()
    val questionMap = remember(allQuestions) { allQuestions.associateBy { it.id } }

    val steps = listOf(
        WizardStep.SETUP to "১. সেটআপ",
        WizardStep.SECTIONS to "২. সেকশন",
        WizardStep.SELECTION to "৩. প্রশ্ন বাছাই",
        WizardStep.PREVIEW to "৪. প্রিভিউ ও প্রিন্ট"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Step Navigation Bar
        ScrollableTabRow(
            selectedTabIndex = steps.indexOfFirst { it.first == activeStep }.coerceAtLeast(0),
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            steps.forEachIndexed { index, (step, label) ->
                Tab(
                    selected = activeStep == step,
                    onClick = { viewModel.setWizardStep(step) },
                    text = { Text(label, fontSize = 13.sp) },
                    modifier = Modifier.testTag("wizard_step_tab_$index")
                )
            }
        }

        // Active Step Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeStep) {
                WizardStep.SETUP -> SetupStepView(draft = draft, onUpdate = viewModel::updateDraft)
                WizardStep.SECTIONS -> SectionsStepView(viewModel = viewModel, draft = draft)
                WizardStep.SELECTION -> SelectionStepView(
                    viewModel = viewModel,
                    draft = draft,
                    allQuestions = allQuestions,
                    questionMap = questionMap
                )
                WizardStep.PREVIEW -> PreviewStepView(
                    viewModel = viewModel,
                    draft = draft,
                    questionMap = questionMap,
                    activity = activity
                )
            }
        }

        // Bottom Navigation Bar (Previous / Next Step)
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stepIdx = steps.indexOfFirst { it.first == activeStep }

                if (stepIdx > 0) {
                    OutlinedButton(
                        onClick = { viewModel.setWizardStep(steps[stepIdx - 1].first) },
                        modifier = Modifier.testTag("btn_prev_step")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পূর্ববর্তী ধাপ")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (stepIdx < steps.size - 1) {
                    Button(
                        onClick = { viewModel.setWizardStep(steps[stepIdx + 1].first) },
                        modifier = Modifier.testTag("btn_next_step")
                    ) {
                        Text("পরবর্তী ধাপ")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.saveDraftAsVersion("Approved") { ver ->
                                Toast.makeText(context, "প্রশ্নপত্র সংরক্ষিত হয়েছে (v$ver)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("btn_save_final")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}

/* ---------------- STEP 1: SETUP ---------------- */
@Composable
fun SetupStepView(
    draft: Paper,
    onUpdate: ((Paper) -> Paper) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("প্রশ্নপত্রের মৌলিক তথ্য (Paper Setup)", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        OutlinedTextField(
            value = draft.name,
            onValueChange = { onUpdate { p -> p.copy(name = it) } },
            label = { Text("প্রশ্নপত্রের শিরোনাম (Paper Title / Draft Name)") },
            modifier = Modifier.fillMaxWidth().testTag("input_paper_name"),
            singleLine = true
        )

        OutlinedTextField(
            value = draft.inst,
            onValueChange = { onUpdate { p -> p.copy(inst = it) } },
            label = { Text("প্রতিষ্ঠানের নাম (Institution Name)") },
            modifier = Modifier.fillMaxWidth().testTag("input_institution_name"),
            singleLine = true
        )

        OutlinedTextField(
            value = draft.exam,
            onValueChange = { onUpdate { p -> p.copy(exam = it) } },
            label = { Text("পরীক্ষার নাম (Exam Name)") },
            placeholder = { Text("যেমন: অর্ধবার্ষিক পরীক্ষা ২০২৬") },
            modifier = Modifier.fillMaxWidth().testTag("input_exam_name"),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = draft.cls,
                onValueChange = { onUpdate { p -> p.copy(cls = it) } },
                label = { Text("শ্রেণি (Class)") },
                modifier = Modifier.weight(1f).testTag("input_paper_class"),
                singleLine = true
            )
            OutlinedTextField(
                value = draft.sub,
                onValueChange = { onUpdate { p -> p.copy(sub = it) } },
                label = { Text("বিষয় (Subject)") },
                modifier = Modifier.weight(1.5f).testTag("input_paper_subject"),
                singleLine = true
            )
            OutlinedTextField(
                value = draft.code,
                onValueChange = { onUpdate { p -> p.copy(code = it) } },
                label = { Text("বিষয় কোড") },
                modifier = Modifier.weight(1f).testTag("input_paper_code"),
                singleLine = true
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = draft.time,
                onValueChange = { onUpdate { p -> p.copy(time = it) } },
                label = { Text("সময় (Time)") },
                modifier = Modifier.weight(1.5f).testTag("input_paper_time"),
                singleLine = true
            )
            OutlinedTextField(
                value = draft.fullMarks,
                onValueChange = { onUpdate { p -> p.copy(fullMarks = it) } },
                label = { Text("পূর্ণমান (Full Marks)") },
                modifier = Modifier.weight(1f).testTag("input_paper_full_marks"),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = draft.instructions,
            onValueChange = { onUpdate { p -> p.copy(instructions = it) } },
            label = { Text("বিশেষ নির্দেশনা (Instructions, line by line)") },
            modifier = Modifier.fillMaxWidth().testTag("input_paper_instructions"),
            minLines = 3
        )

        // Layout settings
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("লেআউট সেটিংস (Layout Settings)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MCQ অপশন কলাম সংখ্যা:", fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1 to "১ কলাম", 2 to "২ কলাম").forEach { (col, label) ->
                            val isSel = draft.style.optColumns == col
                            if (isSel) {
                                Button(onClick = { onUpdate { p -> p.copy(style = p.style.copy(optColumns = col)) } }) {
                                    Text(label, fontSize = 12.sp)
                                }
                            } else {
                                OutlinedButton(onClick = { onUpdate { p -> p.copy(style = p.style.copy(optColumns = col)) } }) {
                                    Text(label, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("সেকশন শিরোনাম স্টাইল:", fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("bold" to "বোল্ড", "box" to "বক্স", "underline" to "আন্ডারলাইন").forEach { (st, label) ->
                            val isSel = draft.style.sectionHeaderStyle == st
                            if (isSel) {
                                Button(onClick = { onUpdate { p -> p.copy(style = p.style.copy(sectionHeaderStyle = st)) } }) {
                                    Text(label, fontSize = 11.sp)
                                }
                            } else {
                                OutlinedButton(onClick = { onUpdate { p -> p.copy(style = p.style.copy(sectionHeaderStyle = st)) } }) {
                                    Text(label, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = draft.style.watermarkText,
                    onValueChange = { onUpdate { p -> p.copy(style = p.style.copy(watermarkText = it)) } },
                    label = { Text("জলছাপ (Watermark text, optional)") },
                    placeholder = { Text("যেমন: CONFIDENTIAL বা স্কুলের নাম") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

/* ---------------- STEP 2: SECTIONS ---------------- */
@Composable
fun SectionsStepView(
    viewModel: PaperViewModel,
    draft: Paper
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.addSection() },
                modifier = Modifier.weight(1f).testTag("btn_add_section")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন সেকশন")
            }

            OutlinedButton(
                onClick = { viewModel.addStandardSections() },
                modifier = Modifier.weight(1.4f).testTag("btn_add_std_sections")
            ) {
                Text("স্ট্যান্ডার্ড সেকশন")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(draft.sections) { idx, sec ->
                SectionEditCard(
                    section = sec,
                    index = idx,
                    totalSections = draft.sections.size,
                    onUpdate = { updatedSec ->
                        viewModel.updateDraft { p ->
                            val list = p.sections.toMutableList()
                            list[idx] = updatedSec
                            p.copy(sections = list)
                        }
                    },
                    onMoveUp = { viewModel.moveSection(idx, -1) },
                    onMoveDown = { viewModel.moveSection(idx, 1) },
                    onDelete = { viewModel.deleteSection(idx) }
                )
            }
        }
    }
}

@Composable
fun SectionEditCard(
    section: PaperSection,
    index: Int,
    totalSections: Int,
    onUpdate: (PaperSection) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("section_edit_card_$index")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "সেকশন ${BengaliUtils.toBengaliDigits(index + 1)} (${section.title})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Row {
                    if (index > 0) {
                        IconButton(onClick = onMoveUp, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(18.dp))
                        }
                    }
                    if (index < totalSections - 1) {
                        IconButton(onClick = onMoveDown, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(18.dp))
                        }
                    }
                    if (totalSections > 1) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = section.title,
                onValueChange = { onUpdate(section.copy(title = it)) },
                label = { Text("সেকশন শিরোনাম (Title)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Type selector
                OutlinedTextField(
                    value = section.type,
                    onValueChange = { onUpdate(section.copy(type = it)) },
                    label = { Text("টাইপ (MCQ/CQ/Short)") },
                    modifier = Modifier.weight(1.2f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = section.marksPerQuestion.toString(),
                    onValueChange = {
                        val parsed = it.toFloatOrNull() ?: 1f
                        onUpdate(section.copy(marksPerQuestion = parsed))
                    },
                    label = { Text("প্রতি প্রশ্নে মান") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = section.totalMarks,
                    onValueChange = { onUpdate(section.copy(totalMarks = it)) },
                    label = { Text("মোট মান") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = section.instruction,
                onValueChange = { onUpdate(section.copy(instruction = it)) },
                label = { Text("সেকশন নির্দেশনা (যেমন: যেকোনো ৭টি প্রশ্নের উত্তর দাও)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "নির্বাচিত প্রশ্ন সংখ্যা: ${BengaliUtils.toBengaliDigits(section.items.size)} টি",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/* ---------------- STEP 3: SELECTION ---------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionStepView(
    viewModel: PaperViewModel,
    draft: Paper,
    allQuestions: List<Question>,
    questionMap: Map<String, Question>
) {
    val activeSecIdx by viewModel.activeSectionIndex.collectAsState()
    val secIndex = activeSecIdx.coerceIn(0, (draft.sections.size - 1).coerceAtLeast(0))
    val currentSec = draft.sections.getOrNull(secIndex) ?: return

    var searchBank by remember { mutableStateOf("") }

    val candidateQuestions = remember(allQuestions, draft, currentSec, searchBank) {
        val qStr = searchBank.trim().lowercase(Locale.ROOT)
        allQuestions.filter { q ->
            val matchType = currentSec.type == "Mixed" || q.type.equals(currentSec.type, ignoreCase = true)
            val matchSub = draft.sub.isBlank() || q.sub.isBlank() || q.sub.equals(draft.sub, ignoreCase = true)
            val matchCls = draft.cls.isBlank() || q.cls.isBlank() || q.cls == draft.cls
            val matchText = qStr.isEmpty() || q.text.lowercase(Locale.ROOT).contains(qStr) || q.stem.lowercase(Locale.ROOT).contains(qStr)
            matchType && matchSub && matchCls && matchText
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Switcher tabs
        ScrollableTabRow(
            selectedTabIndex = secIndex,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            draft.sections.forEachIndexed { i, s ->
                Tab(
                    selected = secIndex == i,
                    onClick = { viewModel.activeSectionIndex.value = i },
                    text = { Text("${s.title} (${BengaliUtils.toBengaliDigits(s.items.size)})", fontSize = 12.sp) }
                )
            }
        }

        // Section Questions list Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "${currentSec.title} এর নির্বাচিত প্রশ্ন (${BengaliUtils.toBengaliDigits(currentSec.items.size)}টি):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                if (currentSec.items.isEmpty()) {
                    Text(
                        text = "নিচের তালিকা থেকে প্রশ্ন যোগ করুন।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(currentSec.items) { itemIdx, it ->
                            val q = questionMap[it.qId]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${BengaliUtils.toBengaliDigits(itemIdx + 1)}. ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = q?.text?.take(60) ?: "(প্রশ্ন)",
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                IconButton(
                                    onClick = { viewModel.moveItemInSection(secIndex, itemIdx, -1) },
                                    enabled = itemIdx > 0,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = "Up", modifier = Modifier.size(14.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.moveItemInSection(secIndex, itemIdx, 1) },
                                    enabled = itemIdx < currentSec.items.size - 1,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = "Down", modifier = Modifier.size(14.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.removeQuestionFromSection(secIndex, itemIdx) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Available Bank Questions to Add
        OutlinedTextField(
            value = searchBank,
            onValueChange = { searchBank = it },
            placeholder = { Text("ব্যাংক থেকে খুঁজুন (Search bank...)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(candidateQuestions) { _, q ->
                val alreadyAdded = currentSec.items.any { it.qId == q.id }
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (alreadyAdded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TypeBadge(type = q.type)
                                if (q.ch.isNotBlank()) BadgeChip(text = "অধ্যায়: ${q.ch}")
                                if (q.bq) BadgeChip(text = "${q.bd} '${q.by.takeLast(2)}")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = q.text.ifBlank { q.stem },
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }

                        Button(
                            onClick = { viewModel.addQuestionToActiveSection(q.id) },
                            modifier = Modifier.testTag("add_q_to_sec_${q.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(if (alreadyAdded) "পুনরায় যোগ" else "যোগ করুন", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- STEP 4: PREVIEW & GENERATE ---------------- */
@Composable
fun PreviewStepView(
    viewModel: PaperViewModel,
    draft: Paper,
    questionMap: Map<String, Question>,
    activity: Activity?
) {
    val context = LocalContext.current
    var showTemplateDialog by remember { mutableStateOf(false) }
    var templateName by remember { mutableStateOf("") }

    val totalCalculatedMarks = remember(draft, questionMap) {
        draft.sections.sumOf { sec ->
            sec.items.sumOf { item ->
                (item.markOverride ?: sec.marksPerQuestion).toDouble()
            }
        }
    }

    val fullMarksInt = draft.fullMarks.toDoubleOrNull() ?: 0.0
    val isMarksMismatch = fullMarksInt > 0.0 && totalCalculatedMarks != fullMarksInt

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Marks verification badge
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isMarksMismatch) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isMarksMismatch) Icons.Default.Warning else Icons.Default.Check,
                    contentDescription = null,
                    tint = if (isMarksMismatch) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "মোট প্রশ্নের মান: ${BengaliUtils.toBengaliDigits(totalCalculatedMarks.toInt())} / পূর্ণমান: ${BengaliUtils.toBengaliDigits(draft.fullMarks)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isMarksMismatch) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                    )
                    if (isMarksMismatch) {
                        Text(
                            text = "সতর্কতা: পূর্ণমান ও নির্বাচিত প্রশ্নের মোট মানের মধ্যে অমিল রয়েছে।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Export Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    PrintHelper.sharePdf(context, draft, questionMap)
                },
                modifier = Modifier.weight(1f).testTag("btn_export_pdf")
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PDF ডাউনলোড", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    activity?.let { PrintHelper.printPaper(it, draft, questionMap) }
                },
                modifier = Modifier.weight(1f).testTag("btn_print_paper")
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("প্রিন্ট (Print)", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    PrintHelper.shareText(context, draft, questionMap)
                },
                modifier = Modifier.weight(1f).testTag("btn_export_txt")
            ) {
                Icon(Icons.Default.TextSnippet, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("TXT টেক্সট", fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { showTemplateDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("টেম্পলেট হিসেবে সেভ")
            }
        }

        // Realistic Printable Paper Preview
        Text(
            text = "প্রশ্নপত্রের বাস্তব প্রিভিউ (Live Preview):",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        PaperPreviewView(
            paper = draft,
            questionMap = questionMap,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showTemplateDialog = false },
            title = { Text("টেম্পলেট সংরক্ষণ") },
            text = {
                OutlinedTextField(
                    value = templateName,
                    onValueChange = { templateName = it },
                    label = { Text("টেম্পলেটের নাম (Template Name)") },
                    placeholder = { Text("যেমন: SSC বাংলা মডেল টেস্ট কাঠামো") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (templateName.isNotBlank()) {
                        viewModel.saveCurrentAsTemplate(templateName) {
                            Toast.makeText(context, "টেম্পলেট সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                            showTemplateDialog = false
                            templateName = ""
                        }
                    }
                }) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTemplateDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
