package com.example.data

import com.example.model.Question
import com.example.model.SubQuestion

object SampleData {
    val starterQuestions: List<Question> = listOf(
        // MCQ 1 - Bangla
        Question(
            sub = "বাংলা",
            cls = "10",
            ch = "1",
            type = "MCQ",
            text = "বাংলা গদ্যের জনক বলা হয় কাকে?",
            stem = "",
            en = false,
            five = false,
            ans = "1",
            o = listOf("রবীন্দ্রনাথ ঠাকুর", "ঈশ্বরচন্দ্র বিদ্যাসাগর", "বঙ্কিমচন্দ্র চট্টোপাধ্যায়", "মাইকেল মধুসূদন দত্ত", ""),
            bq = true,
            bd = "ঢাকা",
            by = "2024",
            bx = "SSC"
        ),
        // MCQ 2 - Physics
        Question(
            sub = "Physics",
            cls = "10",
            ch = "2",
            type = "MCQ",
            text = "বলের এসআই (SI) একক কী?",
            stem = "",
            en = false,
            five = false,
            ans = "2",
            o = listOf("জুল (J)", "ওয়াট (W)", "নিউটন (N)", "প্যাসকেল (Pa)", ""),
            bq = true,
            bd = "রাজশাহী",
            by = "2023",
            bx = "SSC"
        ),
        // MCQ 3 - Mathematics
        Question(
            sub = "Mathematics",
            cls = "10",
            ch = "3",
            type = "MCQ",
            text = "x + 1/x = 3 হলে, x² + 1/x² এর মান কত?",
            stem = "",
            en = false,
            five = false,
            ans = "0",
            o = listOf("7", "9", "11", "5", ""),
            bq = true,
            bd = "কুমিল্লা",
            by = "2024",
            bx = "SSC"
        ),
        // MCQ 4 - Chemistry
        Question(
            sub = "Chemistry",
            cls = "10",
            ch = "4",
            type = "MCQ",
            text = "পর্যায় সারণির ক্ষার ধাতুসমূহ কোন গ্রুপে অবস্থিত?",
            stem = "",
            en = false,
            five = false,
            ans = "0",
            o = listOf("গ্রুপ-১", "গ্রুপ-২", "গ্রুপ-১৭", "গ্রুপ-১৮", ""),
            bq = true,
            bd = "চট্টগ্রাম",
            by = "2023",
            bx = "SSC"
        ),
        // MCQ 5 - English
        Question(
            sub = "English",
            cls = "10",
            ch = "1",
            type = "MCQ",
            text = "Which one is the correct passive form of 'He writes a letter'?",
            stem = "",
            en = true,
            five = false,
            ans = "1",
            o = listOf(
                "A letter is wrote by him",
                "A letter is written by him",
                "A letter was written by him",
                "A letter being written by him",
                ""
            ),
            bq = true,
            bd = "যশোর",
            by = "2024",
            bx = "SSC"
        ),
        // CQ 1 - Bangla
        Question(
            sub = "বাংলা",
            cls = "10",
            ch = "1",
            type = "CQ",
            text = "নিচের উদ্দীপকটি পড়ে প্রশ্নগুলোর উত্তর দাও:",
            stem = "রহিম একজন সৎ ও নিষ্ঠাবান কৃষক। শত অভাব-অনটনের মাঝেও সে কখনো পরের ধনে লোভ করে না। নিজের পরিশ্রমের অর্জিত ফসলই তার একমাত্র ভরসা।",
            s = listOf(
                SubQuestion(t = "উপন্যাসের প্রধান চরিত্র কে?", m = "1", k = "জ্ঞানমূলক"),
                SubQuestion(t = "সততা মানব জীবনের শ্রেষ্ঠ গুণ—ব্যাখ্যা করো।", m = "2", k = "অনুধাবন"),
                SubQuestion(t = "উদ্দীপকের রহিমের সাথে পাঠ্যবইয়ের চরিত্রের সাদৃশ্য তুলে ধরো।", m = "3", k = "প্রয়োগ"),
                SubQuestion(t = "\"উদ্দীপকের বিষয়টি কেবল আংশিক প্রতিফলিত হয়েছে\"—উক্তিটির যথার্থতা মূল্যায়ন করো।", m = "4", k = "উচ্চতর দক্ষতা")
            ),
            bq = true,
            bd = "ঢাকা",
            by = "2023",
            bx = "SSC"
        ),
        // CQ 2 - Physics
        Question(
            sub = "Physics",
            cls = "10",
            ch = "3",
            type = "CQ",
            text = "নিচের তথ্যটি লক্ষ্য করো এবং প্রশ্নগুলোর উত্তর দাও:",
            stem = "৫ কেজি ভরের একটি স্থির বস্তুর উপর ২০ নিউটন বল ৪ সেকেন্ড ধরে প্রয়োগ করা হলো। এরপর বল অপসারণ করা হলে বস্তুটি সমবেগে চলতে থাকে।",
            s = listOf(
                SubQuestion(t = "ত্বরণ কাকে বলে?", m = "1", k = "জ্ঞানমূলক"),
                SubQuestion(t = "ঘর্ষণ বল সবসময় গতির বিপরীতে কাজ করে কেন?", m = "2", k = "অনুধাবন"),
                SubQuestion(t = "প্রথম ৪ সেকেন্ডে বস্তুটি কত দূর অতিক্রম করবে নির্ণয় করো।", m = "3", k = "প্রয়োগ"),
                SubQuestion(t = "বল অপসারণের পর পরবর্তী ৬ সেকেন্ডে অতিক্রান্ত দূরত্ব বিশ্লেষণ করো।", m = "4", k = "উচ্চতর দক্ষতা")
            ),
            bq = true,
            bd = "বরিশাল",
            by = "2024",
            bx = "SSC"
        ),
        // Short 1 - Mathematics
        Question(
            sub = "Mathematics",
            cls = "10",
            ch = "2",
            type = "Short",
            text = "সেট ও উপসেটের মধ্যে পার্থক্য কী সংক্ষেপে লেখো।",
            bq = false
        ),
        // Short 2 - Biology
        Question(
            sub = "Biology",
            cls = "10",
            ch = "2",
            type = "Short",
            text = "উদ্ভিদ ও প্রাণিকোষের দুইটি প্রধান পার্থক্য উল্লেখ করো।",
            bq = true,
            bd = "দিনাজপুর",
            by = "2024",
            bx = "SSC"
        )
    )
}
