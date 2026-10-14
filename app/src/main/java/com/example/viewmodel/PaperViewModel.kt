package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PaperEntity
import com.example.data.QuestionEntity
import com.example.data.TemplateEntity
import com.example.model.BengaliUtils
import com.example.model.Paper
import com.example.model.PaperSection
import com.example.model.PaperTemplate
import com.example.model.Question
import com.example.model.SectionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class AppScreen {
    DASHBOARD,
    QUESTION_BANK,
    SINGLE_QUESTION,
    BULK_PASTE,
    BOARD_PASTE,
    PAPER_WIZARD,
    SAVED_PAPERS,
    TEMPLATES,
    USER_GUIDE
}

enum class WizardStep {
    SETUP,
    SECTIONS,
    SELECTION,
    PREVIEW
}

class PaperViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val questionDao = db.questionDao()
    private val paperDao = db.paperDao()
    private val templateDao = db.templateDao()

    val allQuestions: StateFlow<List<Question>> = questionDao.getAllFlow()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPapers: StateFlow<List<Paper>> = paperDao.getAllFlow()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTemplates: StateFlow<List<PaperTemplate>> = templateDao.getAllFlow()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenHistory = mutableListOf<AppScreen>()

    // Current draft paper for wizard
    private val _currentDraft = MutableStateFlow(Paper())
    val currentDraft: StateFlow<Paper> = _currentDraft.asStateFlow()

    // Wizard active step
    private val _activeWizardStep = MutableStateFlow(WizardStep.SETUP)
    val activeWizardStep: StateFlow<WizardStep> = _activeWizardStep.asStateFlow()

    // Question editing state
    private val _editingQuestion = MutableStateFlow<Question?>(null)
    val editingQuestion: StateFlow<Question?> = _editingQuestion.asStateFlow()

    // Question Bank Filters & Search
    val searchQuery = MutableStateFlow("")
    val filterSubject = MutableStateFlow("")
    val filterClass = MutableStateFlow("")
    val filterType = MutableStateFlow("") // MCQ, CQ, Short, or ""
    val filterBoard = MutableStateFlow("")
    val filterYear = MutableStateFlow("")
    val sortBy = MutableStateFlow("ch") // ch, at, ty, az
    val selectedQuestionIds = MutableStateFlow<Set<String>>(emptySet())

    // Selection step active section index
    val activeSectionIndex = MutableStateFlow(0)

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
            return true
        }
        return false
    }

    fun setWizardStep(step: WizardStep) {
        _activeWizardStep.value = step
    }

    fun startNewPaper() {
        _currentDraft.value = Paper(
            id = UUID.randomUUID().toString(),
            rootId = UUID.randomUUID().toString(),
            name = "Untitled Question Paper"
        )
        _activeWizardStep.value = WizardStep.SETUP
        navigateTo(AppScreen.PAPER_WIZARD)
    }

    fun updateDraft(update: (Paper) -> Paper) {
        _currentDraft.value = update(_currentDraft.value)
    }

    fun startAddQuestion() {
        _editingQuestion.value = null
        navigateTo(AppScreen.SINGLE_QUESTION)
    }

    fun startEditQuestion(question: Question) {
        _editingQuestion.value = question
        navigateTo(AppScreen.SINGLE_QUESTION)
    }

    fun saveQuestion(question: Question, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            questionDao.insert(QuestionEntity.fromDomain(question))
            onFinished(true)
        }
    }

    fun saveMultipleQuestions(questions: List<Question>, onFinished: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val entities = questions.map { QuestionEntity.fromDomain(it) }
            questionDao.insertAll(entities)
            onFinished(questions.size)
        }
    }

    fun deleteQuestion(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            questionDao.delete(id)
        }
    }

    fun deleteSelectedQuestions() {
        val ids = selectedQuestionIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            questionDao.deleteBatch(ids)
            selectedQuestionIds.value = emptySet()
        }
    }

    fun moveSelectedQuestions(newSubject: String, newClass: String) {
        val ids = selectedQuestionIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            if (newSubject.isNotBlank()) questionDao.updateSubjectBatch(ids, newSubject)
            if (newClass.isNotBlank()) questionDao.updateClassBatch(ids, newClass)
            selectedQuestionIds.value = emptySet()
        }
    }

    fun toggleQuestionSelection(id: String) {
        val current = selectedQuestionIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        selectedQuestionIds.value = current
    }

    fun selectAllQuestions(ids: List<String>) {
        selectedQuestionIds.value = ids.toSet()
    }

    fun clearQuestionSelection() {
        selectedQuestionIds.value = emptySet()
    }

    // Paper Section Operations
    fun addSection(type: String = "Short", marks: Float = 2f) {
        val draft = _currentDraft.value
        val newSec = PaperSection(
            id = UUID.randomUUID().toString(),
            title = "Section ${('A' + draft.sections.size)}",
            type = type,
            marksPerQuestion = marks,
            numbering = "bn",
            instruction = ""
        )
        _currentDraft.value = draft.copy(sections = draft.sections + newSec)
    }

    fun addStandardSections() {
        val draft = _currentDraft.value
        val stdSecs = listOf(
            PaperSection(title = "ক বিভাগ - বহুনির্বাচনী প্রশ্ন", type = "MCQ", marksPerQuestion = 1f, instruction = "সকল প্রশ্নের উত্তর দাও"),
            PaperSection(title = "খ বিভাগ - সৃজনশীল প্রশ্ন", type = "CQ", marksPerQuestion = 10f, instruction = "যেকোনো ৭টি প্রশ্নের উত্তর দাও"),
            PaperSection(title = "গ বিভাগ - সংক্ষিপ্ত উত্তর", type = "Short", marksPerQuestion = 2f, instruction = "সংক্ষেপে উত্তর দাও")
        )
        _currentDraft.value = draft.copy(sections = draft.sections + stdSecs)
    }

    fun moveSection(index: Int, direction: Int) {
        val draft = _currentDraft.value
        val target = index + direction
        if (target in draft.sections.indices) {
            val list = draft.sections.toMutableList()
            val item = list.removeAt(index)
            list.add(target, item)
            _currentDraft.value = draft.copy(sections = list)
        }
    }

    fun deleteSection(index: Int) {
        val draft = _currentDraft.value
        if (draft.sections.size <= 1) return
        val list = draft.sections.toMutableList()
        list.removeAt(index)
        _currentDraft.value = draft.copy(sections = list)
        if (activeSectionIndex.value >= list.size) {
            activeSectionIndex.value = list.size - 1
        }
    }

    fun addQuestionToActiveSection(questionId: String) {
        val draft = _currentDraft.value
        val secIdx = activeSectionIndex.value.coerceIn(0, draft.sections.size - 1)
        val sec = draft.sections[secIdx]
        val updatedSec = sec.copy(items = sec.items + SectionItem(qId = questionId))
        val updatedSections = draft.sections.toMutableList()
        updatedSections[secIdx] = updatedSec
        _currentDraft.value = draft.copy(sections = updatedSections)
    }

    fun removeQuestionFromSection(sectionIndex: Int, itemIndex: Int) {
        val draft = _currentDraft.value
        if (sectionIndex !in draft.sections.indices) return
        val sec = draft.sections[sectionIndex]
        if (itemIndex !in sec.items.indices) return
        val updatedItems = sec.items.toMutableList().apply { removeAt(itemIndex) }
        val updatedSections = draft.sections.toMutableList().apply {
            this[sectionIndex] = sec.copy(items = updatedItems)
        }
        _currentDraft.value = draft.copy(sections = updatedSections)
    }

    fun moveItemInSection(sectionIndex: Int, itemIndex: Int, direction: Int) {
        val draft = _currentDraft.value
        if (sectionIndex !in draft.sections.indices) return
        val sec = draft.sections[sectionIndex]
        val target = itemIndex + direction
        if (target in sec.items.indices) {
            val updatedItems = sec.items.toMutableList()
            val item = updatedItems.removeAt(itemIndex)
            updatedItems.add(target, item)
            val updatedSections = draft.sections.toMutableList().apply {
                this[sectionIndex] = sec.copy(items = updatedItems)
            }
            _currentDraft.value = draft.copy(sections = updatedSections)
        }
    }

    fun saveDraftAsVersion(vname: String, onSaved: (Int) -> Unit) {
        val draft = _currentDraft.value
        viewModelScope.launch(Dispatchers.IO) {
            val existingPapers = paperDao.getAll().filter { it.rootId == draft.rootId }
            val nextVer = existingPapers.size + 1
            val paperToSave = draft.copy(
                id = UUID.randomUUID().toString(),
                ver = nextVer,
                vname = vname,
                createdAt = System.currentTimeMillis()
            )
            paperDao.insert(PaperEntity.fromDomain(paperToSave))
            _currentDraft.value = paperToSave
            onSaved(nextVer)
        }
    }

    fun openPaper(paper: Paper) {
        _currentDraft.value = paper
        _activeWizardStep.value = WizardStep.SETUP
        navigateTo(AppScreen.PAPER_WIZARD)
    }

    fun duplicatePaper(paper: Paper) {
        viewModelScope.launch(Dispatchers.IO) {
            val duplicated = paper.copy(
                id = UUID.randomUUID().toString(),
                rootId = UUID.randomUUID().toString(),
                name = "${paper.name} (Copy)",
                ver = 1,
                createdAt = System.currentTimeMillis()
            )
            paperDao.insert(PaperEntity.fromDomain(duplicated))
        }
    }

    fun deletePaper(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            paperDao.delete(id)
        }
    }

    fun saveCurrentAsTemplate(templateName: String, onSaved: () -> Unit) {
        val draft = _currentDraft.value
        viewModelScope.launch(Dispatchers.IO) {
            val emptySecs = draft.sections.map { it.copy(items = emptyList()) }
            val template = PaperTemplate(
                id = UUID.randomUUID().toString(),
                name = templateName,
                style = draft.style,
                sections = emptySecs
            )
            templateDao.insert(TemplateEntity.fromDomain(template))
            onSaved()
        }
    }

    fun applyTemplate(template: PaperTemplate) {
        val draft = _currentDraft.value
        _currentDraft.value = draft.copy(
            style = template.style,
            sections = template.sections.map { it.copy(id = UUID.randomUUID().toString(), items = emptyList()) }
        )
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            templateDao.delete(id)
        }
    }
}
