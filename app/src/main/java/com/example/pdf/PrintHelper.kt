package com.example.pdf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.BengaliUtils
import com.example.model.Paper
import com.example.model.Question
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object PrintHelper {

    fun printPaper(activity: Activity, paper: Paper, questionMap: Map<String, Question>) {
        val printManager = activity.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(activity, "Print service is not available on this device", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfFile = PdfExporter.exportToPdf(activity, paper, questionMap)
        val jobName = paper.name.ifBlank { "QuestionPaper" }

        printManager.print(
            jobName,
            object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }

                    val info = PrintDocumentInfo.Builder("$jobName.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                        .build()

                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    if (destination == null) {
                        callback?.onWriteFailed("Destination is null")
                        return
                    }

                    try {
                        val input = FileInputStream(pdfFile)
                        val output = FileOutputStream(destination.fileDescriptor)
                        input.copyTo(output)
                        input.close()
                        output.close()
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    }
                }
            },
            PrintAttributes.Builder().build()
        )
    }

    fun sharePdf(context: Context, paper: Paper, questionMap: Map<String, Question>) {
        try {
            val pdfFile = PdfExporter.exportToPdf(context, paper, questionMap)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, paper.name)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Question Paper (PDF)"))
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to share PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun shareText(context: Context, paper: Paper, questionMap: Map<String, Question>) {
        try {
            val textContent = BengaliUtils.paperToFormattedText(paper, questionMap)
            val cleanName = (paper.name.ifBlank { "question-paper" })
                .replace(Regex("[\\\\/:*?\"<>|]"), "-")
                .take(40)
            val txtFile = File(context.cacheDir, "$cleanName.txt")
            txtFile.writeText(textContent, Charsets.UTF_8)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                txtFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, textContent)
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, paper.name)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Question Paper (Text)"))
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to share text: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
