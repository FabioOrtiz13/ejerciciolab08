package com.tecsup.ejerciciolab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch
import com.tecsup.ejerciciolab08.ui.theme.Ejerciciolab08Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ejerciciolab08Theme {
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()

                val taskDao = db.taskDao()
                val viewModel = TaskViewModel(taskDao)

                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var newTaskDescription by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isAscending by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Barra de búsqueda
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.searchTasks(searchQuery) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        // Botón para ordenar las tareas
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = {
                    isAscending = !isAscending
                    viewModel.sortTasksByName(isAscending)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Ordenar tareas ${if (isAscending) "Ascendente" else "Descendente"}")
            }
        }

        // Mostrar tareas
        tasks.forEach { task ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = task.description)
                Button(onClick = { viewModel.toggleTaskCompletion(task) }) {
                    Text(if (task.isCompleted) "Completada" else "Pendiente")
                }
                IconButton(onClick = { viewModel.deleteTask(task) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar tarea")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (newTaskDescription.isNotEmpty()) {
                    viewModel.addTask(newTaskDescription)
                    newTaskDescription = ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Agregar tarea")
        }

        Button(
            onClick = { viewModel.deleteAllTasks() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Eliminar todas las tareas")
        }
    }
}
