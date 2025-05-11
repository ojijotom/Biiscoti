package com.aphfiwiwi.biiscoti.ui.screens.item

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// 1. Define the Item Entity
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String
)

// 2. Define DAO (Data Access Object)
@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<Item>>
}

// 3. Define the Database
@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getDatabase(context: android.content.Context): ItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. ViewModel for managing data
class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ItemDatabase.getDatabase(application).itemDao()
    val items: Flow<List<Item>> = dao.getAll()

    suspend fun addItem(item: Item) {
        dao.insert(item)
    }
}

// ViewModel Factory
class ItemViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// 5. Define the Screen
@Composable
fun ItemScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(context.applicationContext as Application)
    )
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(emptyList<Item>()) }

    // Observe the data from the database
    LaunchedEffect(Unit) {
        viewModel.items.collectLatest {
            items = it
        }
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background) // Black background
    ) {
        Text(
            "Add Item",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.primary // Orange text for the header
            )
        )

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
                    val newItem = Item(
                        name = name,
                        price = price.toDouble(),
                        description = description
                    )
                    coroutineScope.launch {
                        viewModel.addItem(newItem)
                        name = ""
                        price = ""
                        description = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary // Orange button color
            )
        ) {
            Text("Add Item", color = MaterialTheme.colorScheme.onPrimary) // White text on button
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Available Items",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.primary // Orange text for the header
            )
        )

        LazyColumn {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface // Surface color for card
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Name: ${item.name}", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface))
                        Text("Price: ${item.price}", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface))
                        Text("Description: ${item.description}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface))
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemScreenPreview() {
    MaterialTheme {
        ItemScreen(rememberNavController())
    }
}
