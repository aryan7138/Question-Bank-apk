package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.model.Paper
import com.example.model.PaperSection
import com.example.model.PaperStyle
import com.example.model.PaperTemplate
import com.example.model.Question
import com.example.model.SectionItem
import com.example.model.SubQuestion
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val sub: String,
    val cls: String,
    val ch: String,
    val type: String,
    val text: String,
    val stem: String,
    val en: Boolean,
    val five: Boolean,
    val ans: String,
    val oJson: String,
    val sJson: String,
    val bq: Boolean,
    val bd: String,
    val by: String,
    val bx: String,
    val src: String,
    val img: String,
    val createdAt: Long
) {
    fun toDomain(): Question {
        val options = mutableListOf<String>()
        try {
            val arr = JSONArray(oJson)
            for (i in 0 until arr.length()) {
                options.add(arr.optString(i, ""))
            }
        } catch (_: Exception) {}
        while (options.size < 5) options.add("")

        val subQuestions = mutableListOf<SubQuestion>()
        try {
            val arr = JSONArray(sJson)
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i)
                if (obj != null) {
                    subQuestions.add(
                        SubQuestion(
                            t = obj.optString("t", ""),
                            m = obj.optString("m", ""),
                            k = obj.optString("k", "")
                        )
                    )
                }
            }
        } catch (_: Exception) {}
        while (subQuestions.size < 4) subQuestions.add(SubQuestion())

        return Question(
            id = id,
            sub = sub,
            cls = cls,
            ch = ch,
            type = type,
            text = text,
            stem = stem,
            en = en,
            five = five,
            ans = ans,
            o = options,
            s = subQuestions,
            bq = bq,
            bd = bd,
            by = by,
            bx = bx,
            src = src,
            img = img,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(q: Question): QuestionEntity {
            val oArr = JSONArray()
            q.o.forEach { oArr.put(it) }

            val sArr = JSONArray()
            q.s.forEach { sq ->
                val sObj = JSONObject()
                sObj.put("t", sq.t)
                sObj.put("m", sq.m)
                sObj.put("k", sq.k)
                sArr.put(sObj)
            }

            return QuestionEntity(
                id = q.id,
                sub = q.sub,
                cls = q.cls,
                ch = q.ch,
                type = q.type,
                text = q.text,
                stem = q.stem,
                en = q.en,
                five = q.five,
                ans = q.ans,
                oJson = oArr.toString(),
                sJson = sArr.toString(),
                bq = q.bq,
                bd = q.bd,
                by = q.by,
                bx = q.bx,
                src = q.src,
                img = q.img,
                createdAt = q.createdAt
            )
        }
    }
}

@Entity(tableName = "papers")
data class PaperEntity(
    @PrimaryKey val id: String,
    val rootId: String,
    val name: String,
    val inst: String,
    val exam: String,
    val cls: String,
    val sub: String,
    val code: String,
    val date: String,
    val time: String,
    val fullMarks: String,
    val instructions: String,
    val styleJson: String,
    val sectionsJson: String,
    val ver: Int,
    val vname: String,
    val createdAt: Long
) {
    fun toDomain(): Paper {
        var style = PaperStyle()
        try {
            val obj = JSONObject(styleJson)
            style = PaperStyle(
                fontChoice = obj.optString("fontChoice", "Serif"),
                fontSize = obj.optInt("fontSize", 13),
                headerFontSize = obj.optInt("headerFontSize", 18),
                lineSpacing = obj.optDouble("lineSpacing", 1.4).toFloat(),
                optColumns = obj.optInt("optColumns", 2),
                marksDisplay = obj.optString("marksDisplay", "end"),
                sectionHeaderStyle = obj.optString("sectionHeaderStyle", "bold"),
                showLogo = obj.optBoolean("showLogo", false),
                watermarkText = obj.optString("watermarkText", ""),
                footerText = obj.optString("footerText", "")
            )
        } catch (_: Exception) {}

        val sectionsList = mutableListOf<PaperSection>()
        try {
            val arr = JSONArray(sectionsJson)
            for (i in 0 until arr.length()) {
                val sObj = arr.optJSONObject(i) ?: continue
                val itemsArr = sObj.optJSONArray("items") ?: JSONArray()
                val itemsList = mutableListOf<SectionItem>()
                for (j in 0 until itemsArr.length()) {
                    val itObj = itemsArr.optJSONObject(j) ?: continue
                    val qId = itObj.optString("qId", "")
                    val markOverride = if (itObj.has("markOverride") && !itObj.isNull("markOverride")) {
                        itObj.optDouble("markOverride").toFloat()
                    } else null
                    itemsList.add(SectionItem(qId = qId, markOverride = markOverride))
                }

                sectionsList.add(
                    PaperSection(
                        id = sObj.optString("id", ""),
                        title = sObj.optString("title", "Section"),
                        type = sObj.optString("type", "MCQ"),
                        marksPerQuestion = sObj.optDouble("marksPerQuestion", 1.0).toFloat(),
                        numbering = sObj.optString("numbering", "bn"),
                        instruction = sObj.optString("instruction", ""),
                        totalMarks = sObj.optString("totalMarks", ""),
                        items = itemsList
                    )
                )
            }
        } catch (_: Exception) {}

        return Paper(
            id = id,
            rootId = rootId,
            name = name,
            inst = inst,
            exam = exam,
            cls = cls,
            sub = sub,
            code = code,
            date = date,
            time = time,
            fullMarks = fullMarks,
            instructions = instructions,
            style = style,
            sections = sectionsList,
            ver = ver,
            vname = vname,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(p: Paper): PaperEntity {
            val sObj = JSONObject()
            sObj.put("fontChoice", p.style.fontChoice)
            sObj.put("fontSize", p.style.fontSize)
            sObj.put("headerFontSize", p.style.headerFontSize)
            sObj.put("lineSpacing", p.style.lineSpacing.toDouble())
            sObj.put("optColumns", p.style.optColumns)
            sObj.put("marksDisplay", p.style.marksDisplay)
            sObj.put("sectionHeaderStyle", p.style.sectionHeaderStyle)
            sObj.put("showLogo", p.style.showLogo)
            sObj.put("watermarkText", p.style.watermarkText)
            sObj.put("footerText", p.style.footerText)

            val secArr = JSONArray()
            p.sections.forEach { sec ->
                val secObj = JSONObject()
                secObj.put("id", sec.id)
                secObj.put("title", sec.title)
                secObj.put("type", sec.type)
                secObj.put("marksPerQuestion", sec.marksPerQuestion.toDouble())
                secObj.put("numbering", sec.numbering)
                secObj.put("instruction", sec.instruction)
                secObj.put("totalMarks", sec.totalMarks)

                val itemsArr = JSONArray()
                sec.items.forEach { item ->
                    val itObj = JSONObject()
                    itObj.put("qId", item.qId)
                    if (item.markOverride != null) {
                        itObj.put("markOverride", item.markOverride.toDouble())
                    }
                    itemsArr.put(itObj)
                }
                secObj.put("items", itemsArr)
                secArr.put(secObj)
            }

            return PaperEntity(
                id = p.id,
                rootId = p.rootId,
                name = p.name,
                inst = p.inst,
                exam = p.exam,
                cls = p.cls,
                sub = p.sub,
                code = p.code,
                date = p.date,
                time = p.time,
                fullMarks = p.fullMarks,
                instructions = p.instructions,
                styleJson = sObj.toString(),
                sectionsJson = secArr.toString(),
                ver = p.ver,
                vname = p.vname,
                createdAt = p.createdAt
            )
        }
    }
}

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val styleJson: String,
    val sectionsJson: String
) {
    fun toDomain(): PaperTemplate {
        var style = PaperStyle()
        try {
            val obj = JSONObject(styleJson)
            style = PaperStyle(
                fontChoice = obj.optString("fontChoice", "Serif"),
                fontSize = obj.optInt("fontSize", 13),
                optColumns = obj.optInt("optColumns", 2),
                marksDisplay = obj.optString("marksDisplay", "end"),
                sectionHeaderStyle = obj.optString("sectionHeaderStyle", "bold"),
                watermarkText = obj.optString("watermarkText", ""),
                footerText = obj.optString("footerText", "")
            )
        } catch (_: Exception) {}

        val sectionsList = mutableListOf<PaperSection>()
        try {
            val arr = JSONArray(sectionsJson)
            for (i in 0 until arr.length()) {
                val sObj = arr.optJSONObject(i) ?: continue
                sectionsList.add(
                    PaperSection(
                        id = sObj.optString("id", ""),
                        title = sObj.optString("title", "Section"),
                        type = sObj.optString("type", "MCQ"),
                        marksPerQuestion = sObj.optDouble("marksPerQuestion", 1.0).toFloat(),
                        numbering = sObj.optString("numbering", "bn"),
                        instruction = sObj.optString("instruction", ""),
                        totalMarks = sObj.optString("totalMarks", "")
                    )
                )
            }
        } catch (_: Exception) {}

        return PaperTemplate(
            id = id,
            name = name,
            style = style,
            sections = sectionsList
        )
    }

    companion object {
        fun fromDomain(t: PaperTemplate): TemplateEntity {
            val sObj = JSONObject()
            sObj.put("fontChoice", t.style.fontChoice)
            sObj.put("fontSize", t.style.fontSize)
            sObj.put("optColumns", t.style.optColumns)
            sObj.put("marksDisplay", t.style.marksDisplay)
            sObj.put("sectionHeaderStyle", t.style.sectionHeaderStyle)
            sObj.put("watermarkText", t.style.watermarkText)
            sObj.put("footerText", t.style.footerText)

            val secArr = JSONArray()
            t.sections.forEach { sec ->
                val secObj = JSONObject()
                secObj.put("id", sec.id)
                secObj.put("title", sec.title)
                secObj.put("type", sec.type)
                secObj.put("marksPerQuestion", sec.marksPerQuestion.toDouble())
                secObj.put("numbering", sec.numbering)
                secObj.put("instruction", sec.instruction)
                secObj.put("totalMarks", sec.totalMarks)
                secArr.put(secObj)
            }

            return TemplateEntity(
                id = t.id,
                name = t.name,
                styleJson = sObj.toString(),
                sectionsJson = secArr.toString()
            )
        }
    }
}
