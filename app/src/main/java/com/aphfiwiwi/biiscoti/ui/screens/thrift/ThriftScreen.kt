package com.aphfiwiwi.biiscoti.ui.screens.thrift

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


// 1. Define the Thrift Data Class
@Entity(tableName = "thrift")
data class Thrift(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val description: String
)

// 2. Define the DAO (Data Access Object)
@Dao
interface ThriftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(thrift: Thrift)

    @Query("SELECT * FROM thrift")
    fun getAll(): Flow<List<Thrift>>
}

// 3. Define the Database
@Database(entities = [Thrift::class], version = 1)
abstract class ThriftDatabase : RoomDatabase() {
    abstract fun thriftDao(): ThriftDao

    companion object {
        @Volatile
        private var INSTANCE: ThriftDatabase? = null

        fun getDatabase(context: android.content.Context): ThriftDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ThriftDatabase::class.java,
                    "thrift_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. Define the ViewModel
class ThriftViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ThriftDatabase.getDatabase(application).thriftDao()
    val thrifts: Flow<List<Thrift>> = dao.getAll()

    suspend fun addThrift(thrift: Thrift) {
        dao.insert(thrift)
    }
}

// ViewModel Factory
class ThriftViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThriftViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThriftViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// 5. Thrift Screen UI
@Composable
fun ThriftScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: ThriftViewModel = viewModel(
        factory = ThriftViewModelFactory(context.applicationContext as Application)
    )
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Collect thrifts list as state
    val thrifts by viewModel.thrifts.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
    ) {
        Text("Add Thrift", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && amount.toDoubleOrNull() != null && description.isNotBlank()) {
                    val newThrift = Thrift(
                        name = name,
                        amount = amount.toDouble(),
                        description = description
                    )
                    coroutineScope.launch {
                        viewModel.addThrift(newThrift)
                        name = ""
                        amount = ""
                        description = ""
                    }
                } else {
                    // You could show an error message or toast here if needed
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Thrift")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Available Thrifts", style = MaterialTheme.typography.headlineSmall)

        // Use LazyColumn to display thrifts
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(thrifts) { thrift ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Name: ${thrift.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Amount: ${thrift.amount}", style = MaterialTheme.typography.bodyMedium)
                        Text("Description: ${thrift.description}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// 6. Preview for the Thrift Screen
@Composable
@Preview(showBackground = true)
fun ThriftScreenPreview() {
    ThriftScreen(rememberNavController())
}
