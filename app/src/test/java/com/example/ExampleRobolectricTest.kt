package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BengaliUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Smart Question Paper Maker", appName)
  }

  @Test
  fun `verify bengali digits conversion`() {
    val englishNumber = "12345"
    val bengaliNumber = BengaliUtils.toBengaliDigits(englishNumber)
    assertEquals("১২৩৪৫", bengaliNumber)

    val backToArabic = BengaliUtils.toArabicDigits(bengaliNumber)
    assertEquals("12345", backToArabic)
  }

  @Test
  fun `verify question parsing logic`() {
    val sampleText = """
      ১। বাংলা গদ্যের জনক কে?
      ক) রবীন্দ্রনাথ ঠাকুর
      খ) ঈশ্বরচন্দ্র বিদ্যাসাগর
      গ) বঙ্কিমচন্দ্র চট্টোপাধ্যায়
      ঘ) মাইকেল মধুসূদন দত্ত
      উত্তর: খ
    """.trimIndent()

    val parsed = BengaliUtils.parseQuestions(sampleText, "বাংলা", "10", "1")
    assertEquals(1, parsed.size)
    val q = parsed[0]
    assertEquals("MCQ", q.type)
    assertTrue(q.text.contains("বাংলা গদ্যের জনক কে?"))
    assertEquals("1", q.ans) // index 1 for খ
  }
}
