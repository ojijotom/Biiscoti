package com.aphfiwiwi.biiscoti.ui.screens.hair

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.*
import kotlinx.coroutines.launch

// ==================== Room Setup ====================

@Entity(tableName = "hair_services")
data class HairService(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String
)

@Dao
interface HairServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: HairService)

    @Query("SELECT * FROM hair_services ORDER BY id DESC")
    suspend fun getAll(): List<HairService>
}

@Database(entities = [HairService::class], version = 1)
abstract class HairDatabase : RoomDatabase() {
    abstract fun hairServiceDao(): HairServiceDao

    companion object {
        @Volatile private var INSTANCE: HairDatabase? = null

        fun getDatabase(context: Context): HairDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HairDatabase::class.java,
                    "hair_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ==================== ViewModel ====================

class HairViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = HairDatabase.getDatabase(application).hairServiceDao()
    var services by mutableStateOf(emptyList<HairService>())
        private set

    init {
        viewModelScope.launch {
            loadServices()
        }
    }

    suspend fun addService(service: HairService) {
        dao.insert(service)
        loadServices()
    }

    suspend fun loadServices() {
        services = dao.getAll()
    }
}

// ==================== Composable UI ====================

@Composable
fun HairScreen(navController: NavHostController? = null) {
    val context = LocalContext.current
    val viewModel: HairViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HairViewModel(context.applicationContext as Application) as T
            }
        }
    )

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp)
    ) {
        Text(
            "Add Hair Service",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Service Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
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
                    val service = HairService(name = name, price = price.toDouble(), description = description)
                    coroutineScope.launch {
                        viewModel.addService(service)
                    }
                    name = ""
                    price = ""
                    description = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Hair Service", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Available Services",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("Ksh ${service.price}", color = Color(0xFFFFA500), fontSize = 14.sp)
                        Text(service.description, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun PreviewHairScreen() {
    HairScreen()
}
