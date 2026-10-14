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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BengaliUtils
import com.example.model.Paper
import com.example.pdf.PrintHelper
import com.example.viewmodel.PaperViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedPapersScreen(viewModel: PaperViewModel) {
    val papers by viewModel.allPapers.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()
    val questionMap = remember(allQuestions) { allQuestions.associateBy { it.id } }
    val context = LocalContext.current
    val activity = context as? Activity

    var search by remember { mutableStateOf("") }
    var paperToDelete by remember { mutableStateOf<Paper?>(null) }

    val filteredPapers = remember(papers, search) {
        val q = search.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) papers
        else papers.filter {
            it.name.lowercase(Locale.ROOT).contains(q) ||
                    it.sub.lowercase(Locale.ROOT).contains(q) ||
                    it.cls.contains(q) ||
                    it.vname.lowercase(Locale.ROOT).contains(q)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "সংরক্ষিত প্রশ্নপত্রসমূহ (Saved Papers)",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("নাম, বিষয় বা শ্রেণি দিয়ে খুঁজুন...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("search_saved_papers"),
            singleLine = true
        )

        if (filteredPapers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (papers.isEmpty()) "এখনো কোনো প্রশ্নপত্র সংরক্ষিত নেই। 'প্রশ্নপত্র তৈরি' থেকে নতুন প্রশ্নপত্র তৈরি করুন।"
                    else "কোনো প্রশ্নপত্র মেলেনি।",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPapers, key = { it.id }) { paper ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openPaper(paper) }
                            .testTag("saved_paper_card_${paper.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = paper.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
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
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        if (paper.vname.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${paper.vname})",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "${paper.sub} • শ্রেণি: ${paper.cls} • পূর্ণমান: ${BengaliUtils.toBengaliDigits(paper.fullMarks)}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(paper.createdAt))
                                    Text(
                                        text = "সংরক্ষণকাল: $dateStr",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { viewModel.openPaper(paper) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        activity?.let { PrintHelper.printPaper(it, paper, questionMap) }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = "Print", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        PrintHelper.sharePdf(context, paper, questionMap)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        PrintHelper.shareText(context, paper, questionMap)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.TextSnippet, contentDescription = "TXT", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.duplicatePaper(paper)
                                        Toast.makeText(context, "কপি তৈরি হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { paperToDelete = paper },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    paperToDelete?.let { p ->
        AlertDialog(
            onDismissRequest = { paperToDelete = null },
            title = { Text("প্রশ্নপত্র মুছে ফেলতে চান?") },
            text = { Text("'${p.name}' স্থায়ীভাবে ডিলিট করতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePaper(p.id)
                        paperToDelete = null
                        Toast.makeText(context, "মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("ডিলিট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { paperToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
