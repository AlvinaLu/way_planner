package cz.cvut.fel.lushnalv.models

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "comment")
data class Comment(
    @PrimaryKey @ColumnInfo(name = "commentId")
    val commentId: Long = 0,
    val message: String = "",
    var date: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "author")
    val author: Long,
    @ColumnInfo(name = "dayPointId")
    var dayPointId: Long,
)
