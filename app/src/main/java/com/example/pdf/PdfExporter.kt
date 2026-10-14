package com.example.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.BengaliUtils
import com.example.model.Paper
import com.example.model.Question
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    private const val PAGE_WIDTH = 595 // A4 standard width in points at 72dpi
    private const val PAGE_HEIGHT = 842 // A4 standard height in points at 72dpi
    private const val MARGIN = 36f // 0.5 inch margin

    fun exportToPdf(context: Context, paper: Paper, questionMap: Map<String, Question>): File {
        val document = PdfDocument()

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = paper.style.fontSize.toFloat().coerceIn(10f, 16f)
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }

        val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = paper.style.fontSize.toFloat().coerceIn(10f, 16f)
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }

        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = paper.style.headerFontSize.toFloat().coerceIn(15f, 22f)
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val subHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = (paper.style.fontSize + 1).toFloat()
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        val thinBorderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.75f
        }

        val watermarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            alpha = 40
            textSize = 48f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        var currentPageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        fun drawBorders(c: Canvas) {
            // Double border frame
            c.drawRect(MARGIN - 10, MARGIN - 10, PAGE_WIDTH - MARGIN + 10, PAGE_HEIGHT - MARGIN + 10, borderPaint)
            c.drawRect(MARGIN - 6, MARGIN - 6, PAGE_WIDTH - MARGIN + 6, PAGE_HEIGHT - MARGIN + 6, thinBorderPaint)

            // Watermark
            if (paper.style.watermarkText.isNotBlank()) {
                c.save()
                c.rotate(-30f, PAGE_WIDTH / 2f, PAGE_HEIGHT / 2f)
                c.drawText(paper.style.watermarkText, PAGE_WIDTH / 2f, PAGE_HEIGHT / 2f, watermarkPaint)
                c.restore()
            }
        }

        drawBorders(canvas)

        var curY = MARGIN + 16f

        // Draw Header
        canvas.drawText(paper.inst, PAGE_WIDTH / 2f, curY, headerPaint)
        curY += headerPaint.textSize + 4f

        if (paper.exam.isNotBlank()) {
            canvas.drawText(paper.exam, PAGE_WIDTH / 2f, curY, subHeaderPaint)
            curY += subHeaderPaint.textSize + 6f
        }

        // Sub info row: Class, Subject, Code, Time, Marks
        val leftX = MARGIN + 8f
        val rightX = PAGE_WIDTH - MARGIN - 8f

        val line1Left = "শ্রেণি: ${paper.cls}   বিষয়: ${paper.sub}"
        val line1Right = if (paper.code.isNotBlank()) "বিষয় কোড: ${paper.code}" else ""
        canvas.drawText(line1Left, leftX, curY, boldPaint)
        val rWidth1 = boldPaint.measureText(line1Right)
        canvas.drawText(line1Right, rightX - rWidth1, curY, boldPaint)
        curY += boldPaint.textSize + 4f

        val line2Left = "সময়: ${paper.time}"
        val line2Right = "পূর্ণমান: ${BengaliUtils.toBengaliDigits(paper.fullMarks)}"
        canvas.drawText(line2Left, leftX, curY, textPaint)
        val rWidth2 = textPaint.measureText(line2Right)
        canvas.drawText(line2Right, rightX - rWidth2, curY, boldPaint)
        curY += textPaint.textSize + 6f

        // Horizontal line under header
        canvas.drawLine(leftX, curY, rightX, curY, thinBorderPaint)
        curY += 10f

        // Instructions
        val instLines = paper.instructions.lines().filter { it.isNotBlank() }
        if (instLines.isNotEmpty()) {
            for (inst in instLines) {
                canvas.drawText(inst, leftX, curY, textPaint)
                curY += textPaint.textSize + 3f
            }
            curY += 6f
        }

        var globalQIndex = 1

        fun checkNewPage(neededHeight: Float) {
            if (curY + neededHeight > PAGE_HEIGHT - MARGIN - 20) {
                document.finishPage(page)
                currentPageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                drawBorders(canvas)
                curY = MARGIN + 16f
            }
        }

        fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
            val words = text.split(" ")
            val lines = mutableListOf<String>()
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) <= maxWidth) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) lines.add(currentLine)
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) lines.add(currentLine)
            return lines
        }

        // Draw Sections & Questions
        for (sec in paper.sections) {
            checkNewPage(40f)

            // Section Header
            val secTitle = sec.title
            val secTitleWidth = boldPaint.measureText(secTitle)
            val secX = (PAGE_WIDTH - secTitleWidth) / 2f
            canvas.drawText(secTitle, secX, curY, boldPaint)
            curY += boldPaint.textSize + 4f

            if (sec.instruction.isNotBlank()) {
                val insText = "(${sec.instruction})"
                val insWidth = textPaint.measureText(insText)
                canvas.drawText(insText, (PAGE_WIDTH - insWidth) / 2f, curY, textPaint)
                curY += textPaint.textSize + 6f
            }

            for (it in sec.items) {
                val q = questionMap[it.qId] ?: continue
                val mark = it.markOverride ?: sec.marksPerQuestion
                val numStr = BengaliUtils.toBengaliDigits(globalQIndex) + "। "
                val markStr = if (q.type != "CQ") " [${BengaliUtils.toBengaliDigits(mark)}]" else ""

                val qHeading = numStr + q.text + markStr
                val wrappedQ = wrapText(qHeading, boldPaint, rightX - leftX)

                checkNewPage(wrappedQ.size * 18f + 30f)

                for (line in wrappedQ) {
                    canvas.drawText(line, leftX, curY, boldPaint)
                    curY += boldPaint.textSize + 4f
                }

                // Stem (Passage)
                if (q.stem.isNotBlank()) {
                    val stemLines = wrapText("উদ্দীপক: " + q.stem, textPaint, rightX - leftX - 16f)
                    val stemStartY = curY - 2f
                    for (sLine in stemLines) {
                        canvas.drawText(sLine, leftX + 16f, curY, textPaint)
                        curY += textPaint.textSize + 3f
                    }
                    // Draw vertical line beside stem
                    canvas.drawLine(leftX + 8f, stemStartY, leftX + 8f, curY - 2f, borderPaint)
                    curY += 4f
                }

                // Options for MCQ
                if (q.type == "MCQ") {
                    val labels = if (q.en) BengaliUtils.ENGLISH_LETTERS else BengaliUtils.BENGALI_LETTERS
                    val count = if (q.five) 5 else 4
                    val colWidth = (rightX - leftX - 20f) / 2f

                    for (j in 0 until count step 2) {
                        val opt1 = q.o.getOrNull(j) ?: ""
                        val opt2 = q.o.getOrNull(j + 1) ?: ""

                        if (opt1.isNotBlank()) {
                            val text1 = "(${labels[j]}) $opt1"
                            canvas.drawText(text1, leftX + 16f, curY, textPaint)
                        }
                        if (opt2.isNotBlank() && (j + 1 < count)) {
                            val text2 = "(${labels[j + 1]}) $opt2"
                            canvas.drawText(text2, leftX + 16f + colWidth, curY, textPaint)
                        }
                        curY += textPaint.textSize + 4f
                    }
                } else if (q.type == "CQ") {
                    // Sub questions
                    for (j in 0 until minOf(4, q.s.size)) {
                        val sq = q.s[j]
                        if (sq.t.isNotBlank()) {
                            val label = "(${BengaliUtils.BENGALI_LETTERS[j]}) "
                            val markText = if (sq.m.isNotBlank()) " [${BengaliUtils.toBengaliDigits(sq.m)}]" else ""
                            val fullSub = label + sq.t + markText
                            val subLines = wrapText(fullSub, textPaint, rightX - leftX - 16f)
                            for (sLine in subLines) {
                                canvas.drawText(sLine, leftX + 16f, curY, textPaint)
                                curY += textPaint.textSize + 3f
                            }
                        }
                    }
                }

                curY += 6f
                globalQIndex++
            }
        }

        document.finishPage(page)

        // Save PDF to cache
        val cleanName = (paper.name.ifBlank { "question-paper" })
            .replace(Regex("[\\\\/:*?\"<>|]"), "-")
            .take(40)
        val file = File(context.cacheDir, "$cleanName.pdf")
        val out = FileOutputStream(file)
        document.writeTo(out)
        out.flush()
        out.close()
        document.close()

        return file
    }
}
