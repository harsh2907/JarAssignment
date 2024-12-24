package com.myjar.jarassignment.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JarViewModel : ViewModel() {

    private val _searchText:MutableStateFlow<String> = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _searchText
            .combine(_listStringData) { text, computerItems ->
                if (text.isBlank()) {
                    computerItems
                }else{
                    computerItems.filter { item ->
                        item.name.uppercase().contains(text.trim().uppercase())
                    }
                }
            }.stateIn(//basically convert the Flow returned from combine operator to StateFlow
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),//it will allow the StateFlow survive 5 seconds before it been canceled
                initialValue = _listStringData.value
            )



    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.fetchResults().collectLatest { result ->
                    Log.d("Result", result.toString())
                    _listStringData.update { result }
                }
            } catch (e: Exception) {
                Log.e("JarVM Error", e.message, e)
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

}