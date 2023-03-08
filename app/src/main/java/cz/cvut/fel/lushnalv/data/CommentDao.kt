package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.cvut.fel.lushnalv.models.Comment
@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(vararg comment: Comment)

    @Query("SELECT * FROM comment")
    fun getAllComments(): LiveData<List<Comment>>

    @Query("SELECT * FROM comment WHERE dayPointId=:id")
    fun getAllCommentsByDayPointId(id: Long): LiveData<List<Comment>>

    @Transaction
    @Query("SELECT * FROM comment WHERE commentId=:id LIMIT 1")
    fun findCommentsById(id: Long): LiveData<Comment>

    @Transaction
    @Query("SELECT * FROM comment WHERE commentId=:id LIMIT 1")
    fun findCommentById(id: Long): Comment?

    @Transaction
    @Query("DELETE FROM comment WHERE commentId=:id")
    fun delete(id: Long)

    @Query("DELETE FROM comment")
    fun deleteAll()
}
