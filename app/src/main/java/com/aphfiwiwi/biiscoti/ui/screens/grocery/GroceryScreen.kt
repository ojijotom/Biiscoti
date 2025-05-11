package com.aphfiwiwi.biiscoti.ui.screens.grocery

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.room.*
import kotlinx.coroutines.launch


// ----------- Colors --------------
val PrimaryGreen = Color(0xFF4CAF50)
val BackgroundDark = Color(0xFF121212)
val TextWhite = Color(0xFFFFFFFF)
val CardDark = Color(0xFF1E1E1E)
val WhiteText = TextWhite
val OrangePrimary = Color(0xFFFF9800) // Assuming this is your Orange color

// ----------- Room Data Models --------------
@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val quantity: Int
)

@Dao
interface GroceryDao {
    @Query("SELECT * FROM grocery_items")
    suspend fun getAll(): List<GroceryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GroceryItem)
}

@Database(entities = [GroceryItem::class], version = 1)
abstract class GroceryDatabase : RoomDatabase() {
    abstract fun groceryDao(): GroceryDao

    companion object {
        @Volatile private var INSTANCE: GroceryDatabase? = null

        fun getDatabase(context: Context): GroceryDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GroceryDatabase::class.java,
                    "grocery_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

// ----------- Composable Screen ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { GroceryDatabase.getDatabase(context) }
    val dao = remember { db.groceryDao() }

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var itemList by remember { mutableStateOf<List<GroceryItem>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Load items from the database
    fun loadItems() {
        scope.launch {
            itemList = dao.getAll()
        }
    }

    LaunchedEffect(Unit) {
        loadItems()
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Grocery Products", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            // Item Name TextField
            GroceryInputField(
                label = "Item Name",
                value = name,
                onValueChange = { name = it }
            )

            // Price TextField
            GroceryInputField(
                label = "Price",
                value = price,
                onValueChange = { price = it }
            )

            // Quantity TextField
            GroceryInputField(
                label = "Quantity",
                value = quantity,
                onValueChange = { quantity = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Add Grocery Button
            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()) {
                        scope.launch {
                            dao.insert(
                                GroceryItem(
                                    name = name,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    quantity = quantity.toIntOrNull() ?: 1
                                )
                            )
                            name = ""
                            price = ""
                            quantity = ""
                            loadItems()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Grocery Item", color = TextWhite)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Grocery List", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextWhite)

            // LazyColumn for displaying grocery items
            LazyColumn {
                items(itemList) { item ->
                    GroceryItemCard(item)
                }
            }
        }
    }
}

// Helper Composable for Grocery Input Fields
@Composable
fun GroceryInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextWhite) },
        modifier = Modifier.fillMaxWidth()
    )
}

// Helper Composable for displaying a single grocery item card
@Composable
fun GroceryItemCard(item: GroceryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold, color = TextWhite)
            Text("Ksh ${item.price} - Qty: ${item.quantity}", color = TextWhite)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GroceryScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(BackgroundDark)
                .padding(16.dp)
        ) {
            GroceryInputField(
                label = "Item Name",
                value = "Tomatoes",
                onValueChange = {}
            )
            GroceryInputField(
                label = "Price",
                value = "50",
                onValueChange = {}
            )
            GroceryInputField(
                label = "Quantity",
                value = "10",
                onValueChange = {}
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Grocery Item", color = TextWhite)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Grocery List", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextWhite)

            GroceryItemCard(
                GroceryItem(name = "Sample Item", price = 100.0, quantity = 5)
            )
        }
    }
}
