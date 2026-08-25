package com.socialkids.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.socialkids.app.data.local.entity.AnimoEntity
import com.socialkids.app.data.local.entity.CartaEntity
import com.socialkids.app.data.local.entity.InsigniaEntity
import com.socialkids.app.data.local.entity.IntentoEntity
import com.socialkids.app.data.local.entity.PerfilEntity
import com.socialkids.app.data.local.entity.ProgresoMisionEntity
import com.socialkids.app.data.local.entity.VisitaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilDao {
    @Query("SELECT * FROM perfil WHERE id = 1")
    fun observar(): Flow<PerfilEntity?>

    @Query("SELECT * FROM perfil WHERE id = 1")
    suspend fun obtener(): PerfilEntity?

    @Upsert
    suspend fun guardar(perfil: PerfilEntity)

    @Query("UPDATE perfil SET xp = xp + :delta WHERE id = 1")
    suspend fun sumarXp(delta: Int)

    @Query("UPDATE perfil SET alias = :alias, avatarId = :avatarId WHERE id = 1")
    suspend fun actualizarIdentidad(alias: String, avatarId: Int)

    @Query("UPDATE perfil SET onboardingHecho = 1 WHERE id = 1")
    suspend fun marcarOnboarding()

    @Query("DELETE FROM perfil")
    suspend fun borrar()
}

@Dao
interface ProgresoDao {
    @Query("SELECT * FROM progreso_mision")
    fun observarTodo(): Flow<List<ProgresoMisionEntity>>

    @Query("SELECT * FROM progreso_mision WHERE misionId = :misionId")
    suspend fun obtener(misionId: String): ProgresoMisionEntity?

    @Upsert
    suspend fun guardar(progreso: ProgresoMisionEntity)

    @Query("SELECT * FROM progreso_mision")
    suspend fun todos(): List<ProgresoMisionEntity>

    @Query("SELECT COUNT(*) FROM progreso_mision WHERE completada = 1")
    suspend fun completadas(): Int

    @Query("DELETE FROM progreso_mision")
    suspend fun borrar()
}

@Dao
interface IntentoDao {
    @Insert
    suspend fun insertar(intento: IntentoEntity): Long

    @Query("SELECT * FROM intento ORDER BY creadoEn DESC LIMIT :limite")
    fun observarUltimos(limite: Int): Flow<List<IntentoEntity>>

    @Query("SELECT COUNT(DISTINCT misionId) FROM intento WHERE mecanica = :mecanica AND hito = 1")
    fun contarHitos(mecanica: String): Flow<Int>

    @Query("SELECT COUNT(DISTINCT misionId) FROM intento WHERE mecanica = :mecanica AND hito = 1")
    suspend fun contarHitosAhora(mecanica: String): Int

    @Query("SELECT COUNT(*) FROM intento")
    suspend fun total(): Int

    @Query("SELECT AVG(puntaje) FROM intento")
    fun puntajeMedio(): Flow<Double?>

    @Query("SELECT * FROM intento WHERE misionId = :misionId ORDER BY creadoEn DESC")
    fun observarDeMision(misionId: String): Flow<List<IntentoEntity>>

    @Query("DELETE FROM intento")
    suspend fun borrar()
}

@Dao
interface ColeccionDao {
    @Query("SELECT * FROM carta")
    fun observarCartas(): Flow<List<CartaEntity>>

    @Query("SELECT cartaId FROM carta")
    suspend fun idsCartas(): List<String>

    @Query("SELECT insigniaId FROM insignia")
    suspend fun idsInsignias(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun desbloquearCarta(carta: CartaEntity): Long

    @Query("SELECT COUNT(*) FROM carta")
    suspend fun totalCartas(): Int

    @Query("SELECT * FROM insignia")
    fun observarInsignias(): Flow<List<InsigniaEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun conseguirInsignia(insignia: InsigniaEntity): Long

    @Query("DELETE FROM carta")
    suspend fun borrarCartas()

    @Query("DELETE FROM insignia")
    suspend fun borrarInsignias()
}

@Dao
interface AnimoDao {
    @Query("SELECT * FROM animo ORDER BY diaEpoch DESC, creadoEn DESC")
    fun observarTodo(): Flow<List<AnimoEntity>>

    @Insert
    suspend fun insertar(animo: AnimoEntity): Long

    @Query("DELETE FROM animo WHERE id = :id")
    suspend fun borrarUno(id: Long)

    @Query("SELECT COUNT(*) FROM animo")
    suspend fun total(): Int

    @Query("DELETE FROM animo")
    suspend fun borrar()
}

@Dao
interface VisitaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registrar(visita: VisitaEntity): Long

    @Query("SELECT diaEpoch FROM visita ORDER BY diaEpoch DESC")
    fun observarDias(): Flow<List<Long>>

    @Query("SELECT diaEpoch FROM visita")
    suspend fun dias(): List<Long>

    @Query("DELETE FROM visita")
    suspend fun borrar()
}
