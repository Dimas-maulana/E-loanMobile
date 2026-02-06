package com.example.eloanmust.feature.product.data.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.core.network.NetworkMonitor
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.product.data.datasource.PlafondLocalDataSource
import com.example.eloanmust.feature.product.data.dto.PlafondDto
import com.example.eloanmust.feature.product.data.mapper.toDto
import com.example.eloanmust.feature.product.data.mapper.toDtoList
import com.example.eloanmust.feature.product.data.mapper.toEntity
import com.example.eloanmust.feature.product.data.mapper.toEntityList
import com.example.eloanmust.feature.product.domain.repository.PlafondRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PlafondRepository with offline-first strategy.
 *
 * Offline-First Flow:
 * 1. Emit cached data from local database immediately
 * 2. Check network availability
 * 3. If online: fetch fresh data from API, update cache, emit fresh data
 * 4. If offline: keep using cached data
 */
@Singleton
class PlafondRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val localDataSource: PlafondLocalDataSource,
    private val networkMonitor: NetworkMonitor
) : PlafondRepository {

    override fun getPlafonds(): Flow<Resource<List<PlafondDto>>> = flow {
        Timber.d("PlafondRepo: Getting plafonds with offline-first strategy")
        
        // Step 1: Emit cached data first (if available)
        val cachedPlafonds = localDataSource.getActivePlafondsSync().toDtoList()
        if (cachedPlafonds.isNotEmpty()) {
            Timber.d("PlafondRepo: Emitting ${cachedPlafonds.size} cached plafonds")
            emit(Resource.Success(cachedPlafonds))
        } else {
            // No cache, show loading
            emit(Resource.Loading)
        }
        
        // Step 2: Check network and fetch fresh data if online
        if (networkMonitor.isCurrentlyConnected()) {
            Timber.d("PlafondRepo: Online, fetching fresh plafonds from API")
            
            val remoteResult = safeApiCall { apiService.getPlafonds() }
            
            when (remoteResult) {
                is Resource.Success -> {
                    val remotePlafonds = remoteResult.data
                    Timber.d("PlafondRepo: Fetched ${remotePlafonds.size} plafonds from API")
                    
                    // Step 3: Update local cache
                    localDataSource.clearAll()
                    localDataSource.insertPlafonds(remotePlafonds.toEntityList())
                    
                    // Step 4: Emit fresh data
                    emit(Resource.Success(remotePlafonds))
                }
                is Resource.Error -> {
                    Timber.e("PlafondRepo: API fetch failed: ${remoteResult.message}")
                    // If we have cached data, don't emit error
                    if (cachedPlafonds.isEmpty()) {
                        emit(Resource.Error(remoteResult.message, remoteResult.code, remoteResult.exception))
                    }
                }
                else -> {
                    // Loading/Idle - do nothing
                }
            }
        } else {
            Timber.d("PlafondRepo: Offline, using cached data only")
            // Already emitted cached data above
            if (cachedPlafonds.isEmpty()) {
                emit(Resource.Error("Tidak ada koneksi internet dan tidak ada data tersimpan"))
            }
        }
    }

    override suspend fun getCachedPlafonds(): List<PlafondDto> {
        return localDataSource.getActivePlafondsSync().toDtoList()
    }

    override suspend fun refreshPlafonds(): Resource<Unit> {
        Timber.d("PlafondRepo: Force refreshing plafonds")
        
        val result = safeApiCall { apiService.getPlafonds() }
        
        return when (result) {
            is Resource.Success -> {
                localDataSource.clearAll()
                localDataSource.insertPlafonds(result.data.toEntityList())
                Timber.d("PlafondRepo: Refreshed ${result.data.size} plafonds")
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Timber.e("PlafondRepo: Refresh failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }

    override suspend fun findPlafondForAmount(amount: Long): PlafondDto? {
        val entity = localDataSource.findPlafondForAmount(amount.toDouble())
        return entity?.let {
            Timber.d("PlafondRepo: Found cached plafond ${it.name} for amount $amount")
            it.toDto()
        }
    }

    override suspend fun detectPlafond(amount: Long): Resource<PlafondDto> {
        // First try local cache
        val cachedPlafond = findPlafondForAmount(amount)
        if (cachedPlafond != null) {
            Timber.d("PlafondRepo: Detected plafond from cache: ${cachedPlafond.name}")
            return Resource.Success(cachedPlafond)
        }
        
        // Fallback to API if online
        if (networkMonitor.isCurrentlyConnected()) {
            Timber.d("PlafondRepo: No cached plafond, fetching from API for amount $amount")
            val result = safeApiCall { apiService.detectPlafond(amount) }
            
            if (result is Resource.Success && result.data != null) {
                // Cache this plafond
                localDataSource.insertPlafond(result.data.toEntity())
            }
            
            return result
        }
        
        return Resource.Error("Tidak ada produk pinjaman yang cocok untuk jumlah ini")
    }
}
