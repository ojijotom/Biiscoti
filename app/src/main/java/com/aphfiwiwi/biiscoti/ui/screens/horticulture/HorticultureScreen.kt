package com.aphfiwiwi.biiscoti.ui.screens.horticulture

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Entity(tableName = "horticulture_services")
data class HorticultureService(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String
)

@Dao
interface HorticultureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: HorticultureService)

    @Query("SELECT * FROM horticulture_services")
    fun getAll(): Flow<List<HorticultureService>>
}

@Database(entities = [HorticultureService::class], version = 1)
abstract class HorticultureDatabase : RoomDatabase() {
    abstract fun horticultureDao(): HorticultureDao

    companion object {
        @Volatile
        private var INSTANCE: HorticultureDatabase? = null

        fun getDatabase(context: android.content.Context): HorticultureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HorticultureDatabase::class.java,
                    "horticulture_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class HorticultureViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = HorticultureDatabase.getDatabase(application).horticultureDao()
    val services: Flow<List<HorticultureService>> = dao.getAll()

    suspend fun addService(service: HorticultureService) {
        dao.insert(service)
    }
}

class HorticultureViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HorticultureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HorticultureViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun HorticultureScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: HorticultureViewModel = viewModel(
        factory = HorticultureViewModelFactory(context.applicationContext as Application)
    )
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var services by remember { mutableStateOf(emptyList<HorticultureService>()) }

    LaunchedEffect(Unit) {
        viewModel.services.collectLatest {
            services = it
        }
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
    ) {
        Text("Add Horticulture Service", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && price.toDoubleOrNull() != null && description.isNotBlank()) {
                    val newService = HorticultureService(
                        name = name,
                        price = price.toDouble(),
                        description = description
                    )
                    coroutineScope.launch {
                        viewModel.addService(newService)
                        name = ""
                        price = ""
                        description = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Use the primary color of the app
        ) {
            Text("Add Service", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Available Services", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(services) { service ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surface) // Optional: customize card background
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Name: ${service.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Price: ${service.price}", style = MaterialTheme.typography.bodyMedium)
                        Text("Description: ${service.description}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HorticultureScreenPreview() {
    HorticultureScreen(rememberNavController())
}
