package com.aphfiwiwi.biiscoti.ui.screens.restaurants

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// 1. Define the Restaurant Entity
@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String
)

// 2. Define the DAO (Data Access Object)
@Dao
interface RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(restaurant: Restaurant)

    @Query("SELECT * FROM restaurants")
    fun getAll(): Flow<List<Restaurant>>
}

// 3. Define the Database
@Database(entities = [Restaurant::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao

    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null

        fun getDatabase(context: android.content.Context): RestaurantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantDatabase::class.java,
                    "restaurant_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. Define the ViewModel
class RestaurantViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RestaurantDatabase.getDatabase(application).restaurantDao()
    val restaurants: Flow<List<Restaurant>> = dao.getAll()

    suspend fun addRestaurant(restaurant: Restaurant) {
        dao.insert(restaurant)
    }
}

// ViewModel Factory
class RestaurantViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// 5. Restaurant Screen UI
@Composable
fun RestaurantScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: RestaurantViewModel = viewModel(
        factory = RestaurantViewModelFactory(context.applicationContext as Application)
    )
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Collect restaurants list as state
    val restaurants by viewModel.restaurants.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
    ) {
        Text("Add Restaurant", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        // Use TextField instead of OutlinedTextField
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && price.toDoubleOrNull() != null && description.isNotBlank()) {
                    val newRestaurant = Restaurant(
                        name = name,
                        price = price.toDouble(),
                        description = description
                    )
                    coroutineScope.launch {
                        viewModel.addRestaurant(newRestaurant)
                        name = ""
                        price = ""
                        description = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Restaurant")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Available Restaurants", style = MaterialTheme.typography.headlineSmall)

        // Use LazyColumn to display restaurants
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(restaurants) { restaurant ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Name: ${restaurant.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Price: ${restaurant.price}", style = MaterialTheme.typography.bodyMedium)
                        Text("Description: ${restaurant.description}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// 6. Preview for the Restaurant Screen
@Composable
@Preview(showBackground = true)
fun RestaurantScreenPreview() {
    RestaurantScreen(rememberNavController())
}
