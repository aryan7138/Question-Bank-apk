package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BengaliUtils
import com.example.model.Question
import com.example.viewmodel.PaperViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(viewModel: PaperViewModel) {
    val questions by viewModel.allQuestions.collectAsState()
    val search by viewModel.searchQuery.collectAsState()
    val filterSub by viewModel.filterSubject.collectAsState()
    val filterCls by viewModel.filterClass.collectAsState()
    val filterTy by viewModel.filterType.collectAsState()
    val sort by viewModel.sortBy.collectAsState()
    val selectedIds by viewModel.selectedQuestionIds.collectAsState()

    var showMoveDialog by remember { mutableStateOf(false) }
    var moveSubject by remember { mutableStateOf("") }
    var moveClass by remember { mutableStateOf("") }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Filter and sort questions
    val filteredQuestions = remember(questions, search, filterSub, filterCls, filterTy, sort) {
        val qQuery = search.trim().lowercase(Locale.ROOT)
        var list = questions.filter { q ->
            val matchText = qQuery.isEmpty() ||
                    q.text.lowercase(Locale.ROOT).contains(qQuery) ||
                    q.stem.lowercase(Locale.ROOT).contains(qQuery) ||
                    q.sub.lowercase(Locale.ROOT).contains(qQuery) ||
                    q.bd.lowercase(Locale.ROOT).contains(qQuery)
            val matchSub = filterSub.isEmpty() || q.sub.equals(filterSub, ignoreCase = true)
            val matchCls = filterCls.isEmpty() || q.cls == filterCls
            val matchTy = filterTy.isEmpty() || q.type == filterTy
            matchText && matchSub && matchCls && matchTy
        }

        list = when (sort) {
            "at" -> list.sortedByDescending { it.createdAt }
            "ty" -> list.sortedBy { it.type }
            "az" -> list.sortedBy { it.text }
            else -> list.sortedWith(
                compareBy<Question> { it.sub }
                    .thenBy { it.cls }
                    .thenBy { it.ch }
            )
        }
        list
    }

    val availableSubjects = remember(questions) {
        listOf("All") + questions.map { it.sub }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val availableClasses = remember(questions) {
        listOf("All") + questions.map { it.cls }.filter { it.isNotBlank() }.distinct().sorted()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startAddQuestion() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_question")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Question")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text("প্রশ্ন খুঁজুন (Search question...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_question_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Type Filter
                FilterDropdown(
                    label = "টাইপ",
                    options = listOf("All", "MCQ", "CQ", "Short"),
                    selected = if (filterTy.isEmpty()) "All" else filterTy,
                    onSelected = { viewModel.filterType.value = if (it == "All") "" else it },
                    modifier = Modifier.weight(1f)
                )

                // Subject Filter
                FilterDropdown(
                    label = "বিষয়",
                    options = availableSubjects,
                    selected = if (filterSub.isEmpty()) "All" else filterSub,
                    onSelected = { viewModel.filterSubject.value = if (it == "All") "" else it },
                    modifier = Modifier.weight(1.3f)
                )

                // Class Filter
                FilterDropdown(
                    label = "শ্রেণি",
                    options = availableClasses,
                    selected = if (filterCls.isEmpty()) "All" else filterCls,
                    onSelected = { viewModel.filterClass.value = if (it == "All") "" else it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Batch selection toolbar
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedIds.isNotEmpty())
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedIds.isEmpty())
                            "মোট: ${BengaliUtils.toBengaliDigits(filteredQuestions.size)} / ${BengaliUtils.toBengaliDigits(questions.size)} টি প্রশ্ন"
                        else
                            "${BengaliUtils.toBengaliDigits(selectedIds.size)} টি নির্বাচিত",
                        fontSize = 13.sp,
                        fontWeight = if (selectedIds.isNotEmpty()) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (selectedIds.size == filteredQuestions.size) {
                                viewModel.clearQuestionSelection()
                            } else {
                                viewModel.selectAllQuestions(filteredQuestions.map { it.id })
                            }
                        },
                        modifier = Modifier.testTag("select_all_button")
                    ) {
                        Icon(
                            Icons.Default.SelectAll,
                            contentDescription = "Select All",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (selectedIds.isNotEmpty()) {
                        IconButton(
                            onClick = { showMoveDialog = true },
                            modifier = Modifier.testTag("move_batch_button")
                        ) {
                            Icon(
                                Icons.Default.DriveFileMove,
                                contentDescription = "Move Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("delete_batch_button")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Selected",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of questions
            if (filteredQuestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো প্রশ্ন পাওয়া যায়নি। নতুন প্রশ্ন যোগ করুন।",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredQuestions, key = { it.id }) { q ->
                        QuestionBankItemCard(
                            question = q,
                            isSelected = selectedIds.contains(q.id),
                            onToggleSelect = { viewModel.toggleQuestionSelection(q.id) },
                            onEdit = { viewModel.startEditQuestion(q) },
                            onDelete = { viewModel.deleteQuestion(q.id) }
                        )
                    }
                }
            }
        }
    }

    // Move dialog
    if (showMoveDialog) {
        AlertDialog(
            onDismissRequest = { showMoveDialog = false },
            title = { Text("নির্বাচিত প্রশ্ন স্থানান্তর (Move)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = moveSubject,
                        onValueChange = { moveSubject = it },
                        label = { Text("নতুন বিষয় (New Subject)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = moveClass,
                        onValueChange = { moveClass = it },
                        label = { Text("নতুন শ্রেণি (New Class)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.moveSelectedQuestions(moveSubject, moveClass)
                    showMoveDialog = false
                    moveSubject = ""
                    moveClass = ""
                }) {
                    Text("স্থানান্তর করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMoveDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Delete confirm dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("প্রশ্ন মুছে ফেলা") },
            text = {
                Text("নির্বাচিত ${BengaliUtils.toBengaliDigits(selectedIds.size)}টি প্রশ্ন ডিলিট করতে চান? এই ক্রিয়াটি অপরিবর্তনীয়।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSelectedQuestions()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("ডিলিট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelected(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun QuestionBankItemCard(
    question: Question,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() }
            .testTag("question_item_${question.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                modifier = Modifier.testTag("checkbox_${question.id}")
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Badges Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TypeBadge(type = question.type)

                    if (question.ch.isNotBlank()) {
                        BadgeChip(text = "অধ্যায়: ${question.ch}")
                    }

                    if (question.bq || question.bd.isNotBlank()) {
                        val boardStr = listOf(question.bd, question.by).filter { it.isNotBlank() }.joinToString(" ")
                        BadgeChip(
                            text = "বোর্ড: ${if (boardStr.isBlank()) "✓" else boardStr}",
                            color = Color(0xFF1D4E89)
                        )
                    }

                    Text(
                        text = "${question.sub} (শ্রেণি: ${question.cls})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (question.stem.isNotBlank()) {
                    Text(
                        text = "উদ্দীপক: ${question.stem}",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = question.text.ifBlank { "(প্রশ্ন টেক্সট)" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3
                )

                // Sub details snippet for MCQ or CQ
                if (question.type == "MCQ") {
                    val validOpts = question.o.filter { it.isNotBlank() }
                    if (validOpts.isNotEmpty()) {
                        Text(
                            text = validOpts.take(4).joinToString(" • "),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1
                        )
                    }
                }
            }

            Column {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_q_${question.id}")) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_q_${question.id}")) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypeBadge(type: String) {
    val (bg, fg) = when (type) {
        "MCQ" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "CQ" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        else -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = type, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

@Composable
fun BadgeChip(text: String, color: Color = MaterialTheme.colorScheme.secondary) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.Normal, color = color)
    }
}
