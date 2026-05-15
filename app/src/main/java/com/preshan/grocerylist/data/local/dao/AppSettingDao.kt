package com.preshan.grocerylist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.preshan.grocerylist.data.local.entity.AppSettingEntity

@Dao
interface AppSettingDao {

    @Query("SELECT * FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getByKey(key: String): AppSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: AppSettingEntity)
}
