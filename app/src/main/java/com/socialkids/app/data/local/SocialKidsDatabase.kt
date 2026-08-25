package com.socialkids.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.socialkids.app.data.local.dao.AnimoDao
import com.socialkids.app.data.local.dao.ColeccionDao
import com.socialkids.app.data.local.dao.IntentoDao
import com.socialkids.app.data.local.dao.PerfilDao
import com.socialkids.app.data.local.dao.ProgresoDao
import com.socialkids.app.data.local.dao.VisitaDao
import com.socialkids.app.data.local.entity.AnimoEntity
import com.socialkids.app.data.local.entity.CartaEntity
import com.socialkids.app.data.local.entity.InsigniaEntity
import com.socialkids.app.data.local.entity.IntentoEntity
import com.socialkids.app.data.local.entity.PerfilEntity
import com.socialkids.app.data.local.entity.ProgresoMisionEntity
import com.socialkids.app.data.local.entity.VisitaEntity

@Database(
    entities = [
        PerfilEntity::class,
        ProgresoMisionEntity::class,
        IntentoEntity::class,
        CartaEntity::class,
        InsigniaEntity::class,
        AnimoEntity::class,
        VisitaEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SocialKidsDatabase : RoomDatabase() {

    abstract fun perfilDao(): PerfilDao
    abstract fun progresoDao(): ProgresoDao
    abstract fun intentoDao(): IntentoDao
    abstract fun coleccionDao(): ColeccionDao
    abstract fun animoDao(): AnimoDao
    abstract fun visitaDao(): VisitaDao

    companion object {
        private const val NOMBRE = "socialkids.db"

        @Volatile
        private var instancia: SocialKidsDatabase? = null

        fun obtener(context: Context): SocialKidsDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    SocialKidsDatabase::class.java,
                    NOMBRE
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
    }
}
