package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY createdAt DESC")
    suspend fun getAll(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: String): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Update
    suspend fun update(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM questions WHERE id IN (:ids)")
    suspend fun deleteBatch(ids: List<String>)

    @Query("UPDATE questions SET sub = :newSub WHERE id IN (:ids)")
    suspend fun updateSubjectBatch(ids: List<String>, newSub: String)

    @Query("UPDATE questions SET cls = :newCls WHERE id IN (:ids)")
    suspend fun updateClassBatch(ids: List<String>, newCls: String)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int
}

@Dao
interface PaperDao {
    @Query("SELECT * FROM papers ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers ORDER BY createdAt DESC")
    suspend fun getAll(): List<PaperEntity>

    @Query("SELECT * FROM papers WHERE id = :id")
    suspend fun getById(id: String): PaperEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paper: PaperEntity)

    @Update
    suspend fun update(paper: PaperEntity)

    @Query("DELETE FROM papers WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates")
    fun getAllFlow(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates")
    suspend fun getAll(): List<TemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: TemplateEntity)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun delete(id: String)
}
