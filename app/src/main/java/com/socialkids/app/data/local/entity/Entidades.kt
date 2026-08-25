package com.socialkids.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Perfil unico y local del jugador. No guarda nombre real ni ningun dato personal. */
@Entity(tableName = "perfil")
data class PerfilEntity(
    @PrimaryKey val id: Int = 1,
    val alias: String,
    val avatarId: Int,
    val xp: Int = 0,
    val creadoEn: Long = 0L,
    val onboardingHecho: Boolean = false
)

/** Mejor resultado guardado de cada mision. */
@Entity(tableName = "progreso_mision")
data class ProgresoMisionEntity(
    @PrimaryKey val misionId: String,
    val zonaId: String,
    val mejoresEstrellas: Int = 0,
    val mejorPuntaje: Int = 0,
    val intentos: Int = 0,
    val completada: Boolean = false,
    val actualizadoEn: Long = 0L
)

/** Historial real de cada intento. De aqui salen las estadisticas y las insignias. */
@Entity(
    tableName = "intento",
    indices = [Index("misionId"), Index("diaEpoch")]
)
data class IntentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val misionId: String,
    val mecanica: String,
    val puntaje: Int,
    val estrellas: Int,
    /** Hito propio de la mecanica: puente firme, mensaje asertivo perfecto, acuerdo con calma alta... */
    val hito: Boolean,
    val diaEpoch: Long,
    val creadoEn: Long
)

/** Carta de la coleccion desbloqueada. */
@Entity(tableName = "carta")
data class CartaEntity(
    @PrimaryKey val cartaId: String,
    val desbloqueadaEn: Long
)

/** Insignia conseguida. */
@Entity(tableName = "insignia")
data class InsigniaEntity(
    @PrimaryKey val insigniaId: String,
    val conseguidaEn: Long
)

/** Anotacion del Diario de Animo. */
@Entity(
    tableName = "animo",
    indices = [Index("diaEpoch")]
)
data class AnimoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diaEpoch: Long,
    val emocion: String,
    val intensidad: Int,
    val nota: String,
    val creadoEn: Long
)

/** Dia en el que el jugador entro a la isla. Sirve para calcular la racha. */
@Entity(tableName = "visita")
data class VisitaEntity(
    @PrimaryKey val diaEpoch: Long
)
