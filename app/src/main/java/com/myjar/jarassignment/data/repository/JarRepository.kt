package com.myjar.jarassignment.data.repository

import android.util.Log
import com.myjar.jarassignment.data.api.ApiService
import com.myjar.jarassignment.data.model.ComputerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface JarRepository {
    suspend fun fetchResults(): Flow<List<ComputerItem>>
}

class JarRepositoryImpl(
    private val apiService: ApiService
) : JarRepository {
    override suspend fun fetchResults(): Flow<List<ComputerItem>> = flow {
        try {
            val result = apiService.fetchResults()
            emit(result)
        }catch (e:Exception){
            Log.e("JarRepoImpl",e.message,e)
            emit(emptyList())
        }
    }
}