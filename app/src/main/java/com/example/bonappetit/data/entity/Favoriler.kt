package com.example.bonappetit.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "favoriler")
data class Favoriler(@PrimaryKey(autoGenerate = true)
                     @ColumnInfo(name = "yemek_id") @NotNull var yemek_id: Int,
                     @ColumnInfo(name = "yemek_adi") @NotNull var yemek_adi: String
): Serializable