package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BengaliUtils
import com.example.model.Paper
import com.example.pdf.PrintHelper
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PaperViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: PaperViewModel) {
    val questions by viewModel.allQuestions.collectAsState()
    val papers by viewModel.allPapers.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    val totalSubjects = remember(questions) { questions.map { it.sub }.filter { it.isNotBlank() }.distinct().size }
    val totalClasses = remember(questions) { questions.map { it.cls }.filter { it.isNotBlank() }.distinct().size }
    val questionMap = remember(questions) { questions.associateBy { it.id } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_banner")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Question Paper Maker",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "স্মার্ট প্রশ্নপত্র নির্মাতা",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Smart Question Paper Maker • অফলাইন ও দ্রুততম",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "প্রশ্ন সংখ্যা",
                    value = BengaliUtils.toBengaliDigits(questions.size),
                    subtitle = "Questions",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "মোট বিষয়",
                    value = BengaliUtils.toBengaliDigits(totalSubjects),
                    subtitle = "Subjects",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "মোট শ্রেণি",
                    value = BengaliUtils.toBengaliDigits(totalClasses),
                    subtitle = "Classes",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "সংরক্ষিত পেপার",
                    value = BengaliUtils.toBengaliDigits(papers.size),
                    subtitle = "Papers",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "দ্রুত কার্যক্রম (Quick Actions)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActionChip(
                            icon = Icons.Default.Add,
                            label = "নতুন প্রশ্ন যোগ",
                            onClick = { viewModel.startAddQuestion() }
                        )
                        ActionChip(
                            icon = Icons.Default.ContentPaste,
                            label = "বাল্ক পেস্ট (Bulk)",
                            onClick = { viewModel.navigateTo(AppScreen.BULK_PASTE) }
                        )
                        ActionChip(
                            icon = Icons.Default.School,
                            label = "বোর্ড পেস্ট (Board)",
                            onClick = { viewModel.navigateTo(AppScreen.BOARD_PASTE) }
                        )
                        ActionChip(
                            icon = Icons.Default.Article,
                            label = "প্রশ্নপত্র তৈরি (Paper)",
                            isPrimary = true,
                            onClick = { viewModel.startNewPaper() }
                        )
                        ActionChip(
                            icon = Icons.Default.Bookmark,
                            label = "সংরক্ষিত পেপার",
                            onClick = { viewModel.navigateTo(AppScreen.SAVED_PAPERS) }
                        )
                        ActionChip(
                            icon = Icons.Default.AutoAwesome,
                            label = "টেম্পলেট (Templates)",
                            onClick = { viewModel.navigateTo(AppScreen.TEMPLATES) }
                        )
                        ActionChip(
                            icon = Icons.Default.MenuBook,
                            label = "ইউজার গাইড",
                            onClick = { viewModel.navigateTo(AppScreen.USER_GUIDE) }
                        )
                    }
                }
            }
        }

        // Recent Papers
        item {
            Text(
                text = "সাম্প্রতিক প্রশ্নপত্র (Recent Papers)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (papers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "এখনো কোনো সংরক্ষিত প্রশ্নপত্র নেই।",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.startNewPaper() },
                            modifier = Modifier.testTag("create_first_paper_button")
                        ) {
                            Text("প্রথম প্রশ্নপত্র তৈরি করুন")
                        }
                    }
                }
            }
        } else {
            items(papers.take(5)) { paper ->
                PaperItemCard(
                    paper = paper,
                    onOpen = { viewModel.openPaper(paper) },
                    onPrint = {
                        activity?.let { PrintHelper.printPaper(it, paper, questionMap) }
                    },
                    onShare = {
                        PrintHelper.sharePdf(context, paper, questionMap)
                    }
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ActionChip(
    icon: ImageVector,
    label: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("action_${label.replace(" ", "_")}")
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 13.sp)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.testTag("action_${label.replace(" ", "_")}")
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 13.sp)
        }
    }
}

@Composable
fun PaperItemCard(
    paper: Paper,
    onOpen: () -> Unit,
    onPrint: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("paper_card_${paper.id}")
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = paper.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "v${paper.ver}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${paper.sub} • শ্রেণি: ${paper.cls} • পূর্ণমান: ${BengaliUtils.toBengaliDigits(paper.fullMarks)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(onClick = onPrint, modifier = Modifier.testTag("print_button_${paper.id}")) {
                Icon(
                    Icons.Default.Print,
                    contentDescription = "Print Paper",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onShare, modifier = Modifier.testTag("share_button_${paper.id}")) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share PDF",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
