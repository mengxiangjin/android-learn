package com.jin.movie.room

import android.content.Context
import androidx.room.*
import com.jin.movie.bean.VideoTask

@Dao
interface VideoTaskDao {
    // 【关键修改】返回值改为 LiveData<List<...>>
    // 这样只要数据库有任何增删改，UI 会自动收到通知，无需手动刷新
    @Query("SELECT * FROM video_tasks ORDER BY rowid DESC")
    fun getAllLive(): androidx.lifecycle.LiveData<List<VideoTask>>

    //原本的同步方法保留，给 Manager 内部逻辑用
    @Query("SELECT * FROM video_tasks ORDER BY rowid DESC")
    fun getAll(): List<VideoTask>

    // ... 其他方法不变
    @Query("SELECT * FROM video_tasks WHERE url = :url")
    fun get(url: String): VideoTask?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: VideoTask)

    @Update
    fun update(task: VideoTask)

    @Delete
    fun delete(task: VideoTask)
}

@Database(entities = [VideoTask::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): VideoTaskDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "movie_db")
                    .build().also { INSTANCE = it }
            }
        }
    }
}