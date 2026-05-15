package com.preshan.grocerylist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.preshan.grocerylist.data.local.dao.AppSettingDao
import com.preshan.grocerylist.data.local.dao.CategoryDao
import com.preshan.grocerylist.data.local.dao.ItemDao
import com.preshan.grocerylist.data.local.dao.ShoppingSessionDao
import com.preshan.grocerylist.data.local.dao.ShoppingSessionItemDao
import com.preshan.grocerylist.data.local.dao.StoreTypeDao
import com.preshan.grocerylist.data.local.entity.AppSettingEntity
import com.preshan.grocerylist.data.local.entity.CategoryEntity
import com.preshan.grocerylist.data.local.entity.ItemEntity
import com.preshan.grocerylist.data.local.entity.ShoppingSessionEntity
import com.preshan.grocerylist.data.local.entity.ShoppingSessionItemEntity
import com.preshan.grocerylist.data.local.entity.StoreTypeEntity

@Database(
    entities = [
        CategoryEntity::class,
        StoreTypeEntity::class,
        ItemEntity::class,
        ShoppingSessionEntity::class,
        ShoppingSessionItemEntity::class,
        AppSettingEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun storeTypeDao(): StoreTypeDao
    abstract fun itemDao(): ItemDao
    abstract fun shoppingSessionDao(): ShoppingSessionDao
    abstract fun shoppingSessionItemDao(): ShoppingSessionItemDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        const val DB_NAME = "grocery_list.db"

        // Explicit migration structure for future versions.
        val ALL_MIGRATIONS: Array<Migration> = arrayOf()
    }
}
