package com.aphfiwiwi.biiscoti.ui.screens.contact

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch


// Define the Contact Entity
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val message: String
)

// Define the DAO (Data Access Object)
@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Flow<List<Contact>>
}

// Define the Database
@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null

        fun getDatabase(context: android.content.Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Define the ViewModel
class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ContactDatabase.getDatabase(application).contactDao()
    val contacts: Flow<List<Contact>> = dao.getAllContacts()

    suspend fun addContact(contact: Contact) {
        dao.insert(contact)
    }
}

// ViewModel Factory
class ContactViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Define the Contact Screen UI
@Composable
fun ContactScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: ContactViewModel = viewModel(factory = ContactViewModelFactory(context.applicationContext as Application))
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val contacts by viewModel.contacts.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Contact Us", style = MaterialTheme.typography.headlineSmall, color = Color.White)

        Spacer(modifier = Modifier.height(8.dp))

        // Name Input Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Input Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message Input Field
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && message.isNotBlank()) {
                    val newContact = Contact(name = name, email = email, message = message)
                    coroutineScope.launch {
                        viewModel.addContact(newContact)
                        name = ""
                        email = ""
                        message = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Text("Send Message", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Contact History", style = MaterialTheme.typography.headlineSmall, color = Color.White)

        // Displaying Contacts in LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(contacts) { contact ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Name: ${contact.name}", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text("Email: ${contact.email}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Text("Message: ${contact.message}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ContactScreenPreview() {
    ContactScreen(rememberNavController())
}
