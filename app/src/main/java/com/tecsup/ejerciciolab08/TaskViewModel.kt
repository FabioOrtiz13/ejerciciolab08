package com.tecsup.ejerciciolab08

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    fun addTask(description: String, priority: String = "Medium") {
        val newTask = Task(description = description, priority = priority)
        viewModelScope.launch {
            dao.insertTask(newTask)
            _tasks.value = dao.getAllTasks()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            _tasks.value = dao.getAllTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
            _tasks.value = dao.getAllTasks()
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
            _tasks.value = emptyList()
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            _tasks.value = dao.searchTasks(query)
        }
    }

    // Función para ordenar las tareas por nombre (descripción)
    fun sortTasksByName(ascending: Boolean = true) {
        _tasks.value = _tasks.value.sortedBy { it.description }
        if (!ascending) {
            _tasks.value = _tasks.value.reversed()
        }
    }
}

