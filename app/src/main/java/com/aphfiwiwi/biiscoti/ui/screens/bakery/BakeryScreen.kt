package com.aphfiwiwi.biiscoti.ui.screens.bakery

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.*
import kotlinx.coroutines.launch

// -------------------- STYLING & ROUTES --------------------

val newOrange = Color(0xFFFF9800)

const val ROUT_CONTACT = "contact"
const val ROUT_GROCERY = "grocery"
const val ROUT_HAIR = "hair"
const val ROUT_JEWELRY = "jewelry"
const val ROUT_SEARCH = "search"

// -------------------- ENTITY --------------------

@Entity(tableName = "bakery_items")
data class BakeryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double,
    val contact: String
)

// -------------------- DAO --------------------

@Dao
interface BakeryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: BakeryItem)

    @Query("SELECT * FROM bakery_items")
    suspend fun getAllItems(): List<BakeryItem>
}

// -------------------- DATABASE --------------------

@Database(entities = [BakeryItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bakeryDao(): BakeryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bakery_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

// -------------------- VIEWMODEL --------------------

class BakeryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).bakeryDao()

    private val _items = MutableLiveData<List<BakeryItem>>()
    val items: LiveData<List<BakeryItem>> = _items

    init {
        loadItems()
    }

    fun addItem(item: BakeryItem) {
        viewModelScope.launch {
            dao.insertItem(item)
            loadItems()
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            _items.value = dao.getAllItems()
        }
    }
}

// -------------------- COMPOSABLE SCREEN --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakeryScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: BakeryViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application))
    val items by viewModel.items.observeAsState(emptyList())

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(3) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bakery Manager") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = newOrange, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = newOrange) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Jewelry") },
                    label = { Text("Jewelry") },
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                        navController.navigate(ROUT_JEWELRY)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Face, contentDescription = "Hair") },
                    label = { Text("Hair") },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate(ROUT_HAIR)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Contact") },
                    label = { Text("Contact") },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate(ROUT_CONTACT)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Grocery") },
                    label = { Text("Grocery") },
                    selected = selectedIndex == 3,
                    onClick = {
                        selectedIndex = 3
                        navController.navigate(ROUT_GROCERY)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = selectedIndex == 4,
                    onClick = {
                        selectedIndex = 4
                        navController.navigate(ROUT_SEARCH)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact Number") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
            }

            Button(
                onClick = {
                    if (name.isEmpty() || description.isEmpty() || price.isEmpty() || contact.isEmpty()) {
                        errorMessage = "All fields are required."
                    } else {
                        price.toDoubleOrNull()?.let {
                            viewModel.addItem(BakeryItem(name = name, description = description, price = it, contact = contact))
                            name = ""
                            description = ""
                            price = ""
                            contact = ""
                            errorMessage = ""
                        } ?: run {
                            errorMessage = "Invalid price format"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = newOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text("Add Bakery Item")
            }

            Text("Bakery Items", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))

            items.forEach {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Name: ${it.name}", fontWeight = FontWeight.Bold)
                        Text("Description: ${it.description}")
                        Text("Price: ${it.price}")
                        Text("Contact: ${it.contact}")
                    }
                }
            }
        }
    }
}
