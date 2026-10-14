package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.BoardPasteScreen
import com.example.ui.screens.BulkPasteScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PaperWizardScreen
import com.example.ui.screens.QuestionBankScreen
import com.example.ui.screens.SavedPapersScreen
import com.example.ui.screens.SingleQuestionScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.UserGuideScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PaperViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: PaperViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateBack()
    }

    val screenTitle = when (currentScreen) {
        AppScreen.DASHBOARD -> "স্মার্ট প্রশ্নপত্র নির্মাতা"
        AppScreen.QUESTION_BANK -> "প্রশ্ন ব্যাংক (Question Bank)"
        AppScreen.SINGLE_QUESTION -> "প্রশ্ন সম্পাদনা (Question Entry)"
        AppScreen.BULK_PASTE -> "বাল্ক পেস্ট (Bulk Paste)"
        AppScreen.BOARD_PASTE -> "বোর্ড পেস্ট (Board Paste)"
        AppScreen.PAPER_WIZARD -> "প্রশ্নপত্র নির্মাতা (Paper Wizard)"
        AppScreen.SAVED_PAPERS -> "সংরক্ষিত প্রশ্নপত্র (Saved Papers)"
        AppScreen.TEMPLATES -> "টেম্পলেট (Templates)"
        AppScreen.USER_GUIDE -> "ব্যবহার নির্দেশিকা (User Guide)"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    if (currentScreen != AppScreen.DASHBOARD) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("topbar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.testTag("app_top_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("ড্যাশবোর্ড", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_dashboard")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.QUESTION_BANK,
                    onClick = { viewModel.navigateTo(AppScreen.QUESTION_BANK) },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "Question Bank") },
                    label = { Text("প্রশ্ন ব্যাংক", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_question_bank")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.PAPER_WIZARD,
                    onClick = { viewModel.startNewPaper() },
                    icon = { Icon(Icons.Default.Article, contentDescription = "Create Paper") },
                    label = { Text("তৈরি করুন", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_create_paper")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.SAVED_PAPERS,
                    onClick = { viewModel.navigateTo(AppScreen.SAVED_PAPERS) },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved Papers") },
                    label = { Text("সংরক্ষিত", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_saved_papers")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.USER_GUIDE,
                    onClick = { viewModel.navigateTo(AppScreen.USER_GUIDE) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Guide") },
                    label = { Text("গাইড", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_guide")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppScreen.QUESTION_BANK -> QuestionBankScreen(viewModel = viewModel)
                AppScreen.SINGLE_QUESTION -> SingleQuestionScreen(viewModel = viewModel)
                AppScreen.BULK_PASTE -> BulkPasteScreen(viewModel = viewModel)
                AppScreen.BOARD_PASTE -> BoardPasteScreen(viewModel = viewModel)
                AppScreen.PAPER_WIZARD -> PaperWizardScreen(viewModel = viewModel)
                AppScreen.SAVED_PAPERS -> SavedPapersScreen(viewModel = viewModel)
                AppScreen.TEMPLATES -> TemplatesScreen(viewModel = viewModel)
                AppScreen.USER_GUIDE -> UserGuideScreen()
            }
        }
    }
}
