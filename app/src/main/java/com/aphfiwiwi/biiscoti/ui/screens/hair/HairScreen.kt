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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.*
import kotlinx.coroutines.launch

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
        @Volatile
        private var INSTANCE: HairDatabase? = null

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

class HairViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HairDatabase.getDatabase(application)
    private val dao = db.hairServiceDao()

    var services by mutableStateOf(listOf<HairService>())
        private set

    suspend fun addService(service: HairService) {
        dao.insert(service)
        loadServices()
    }

    suspend fun loadServices() {
        services = dao.getAll()
    }
}

@Composable
fun HairScreen(navController: NavHostController) {
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

    LaunchedEffect(Unit) {
        viewModel.loadServices()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF1A1A1A)) // Black background
    ) {
        Text(
            text = "Add Hair Service",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Service Name", color = Color(0xFFFFA500)) }, // Orange color for label
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price", color = Color(0xFFFFA500)) }, // Orange color for label
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description", color = Color(0xFFFFA500)) }, // Orange color for label
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                if (name.isNotBlank() && price.toDoubleOrNull() != null && description.isNotBlank()) {
                    val newService = HairService(
                        name = name,
                        price = price.toDouble(),
                        description = description
                    )
                    name = ""
                    price = ""
                    description = ""
                    coroutineScope.launch {
                        viewModel.addService(newService)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFA500)), // Orange button background
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("Add Hair Service", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Available Services",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )

        LazyColumn {
            items(viewModel.services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(Color(0xFF333333)) // Dark card background using Modifier
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(service.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Ksh ${service.price}", color = Color(0xFFFFA500)) // Orange price text
                        Text(service.description, color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HairScreenPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(value = "Braiding", onValueChange = {}, label = { Text("Service Name", color = Color(0xFFFFA500)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "1000", onValueChange = {}, label = { Text("Price", color = Color(0xFFFFA500)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "Single lines, 3 hours", onValueChange = {}, label = { Text("Description", color = Color(0xFFFFA500)) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth().background(Color(0xFFFFA500))) {
                Text("Add Hair Service", color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Available Services", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)

            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).background(Color(0xFF333333))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Sample Service", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Ksh 1500", color = Color(0xFFFFA500))
                    Text("Sample description of hair service", color = Color.White)
                }
            }
        }
    }
}
