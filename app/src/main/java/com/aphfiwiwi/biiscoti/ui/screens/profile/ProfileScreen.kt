package com.aphfiwiwi.biiscoti.ui.screens.profile

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// 1. Define the UserProfile Entity
@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phoneNumber: String
)

// 2. Define the DAO (Data Access Object)
@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfile)

    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getProfile(): Flow<UserProfile?>
}

// 3. Define the Database
@Database(entities = [UserProfile::class], version = 1)
abstract class UserProfileDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: UserProfileDatabase? = null

        fun getDatabase(context: android.content.Context): UserProfileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserProfileDatabase::class.java,
                    "user_profile_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. Define the ViewModel
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = UserProfileDatabase.getDatabase(application).userProfileDao()
    val profile: Flow<UserProfile?> = dao.getProfile()

    suspend fun saveProfile(profile: UserProfile) {
        dao.insert(profile)
    }
}

// ViewModel Factory
class UserProfileViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserProfileViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// 5. Profile Screen UI
@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: UserProfileViewModel = viewModel(
        factory = UserProfileViewModelFactory(context.applicationContext as Application)
    )
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    // Observe existing profile data
    LaunchedEffect(Unit) {
        viewModel.profile.collectLatest { userProfile ->
            if (userProfile != null) {
                name = userProfile.name
                email = userProfile.email
                phoneNumber = userProfile.phoneNumber
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("User Profile", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Phone Number Field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Handle done action if needed */ }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Profile Button
        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank()) {
                    val newProfile = UserProfile(
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber
                    )
                    coroutineScope.launch {
                        viewModel.saveProfile(newProfile)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            // Updated to use bodyLarge typography style
            Text("Save Profile", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// 6. Preview for the Profile Screen
@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}
