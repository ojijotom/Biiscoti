package com.aphfiwiwi.biiscoti.ui.screens.bakery

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.aphfiwiwi.biiscoti.navigation.ROUT_CONTACT
import com.aphfiwiwi.biiscoti.navigation.ROUT_GROCERY
import com.aphfiwiwi.biiscoti.navigation.ROUT_HAIR
import com.aphfiwiwi.biiscoti.navigation.ROUT_HORTICULTURE
import com.aphfiwiwi.biiscoti.navigation.ROUT_JEWELRY
import com.aphfiwiwi.biiscoti.navigation.ROUT_SEARCH

val newOrange = Color(0xFFFF9800)

const val ROUT_HOME = "home"
const val ROUT_RESTAURANT = "restaurant"
const val ROUT_PROFILE = "profile"
const val ROUT_BARKERY = "bakery"
const val ROUT_THRIFT = "thrift"

// ---------------- BAKERY SCREEN ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakeryScreen(navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableStateOf(3) } // Bakery tab is selected by default

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
                    icon = { Icon(Icons.Default.Star, contentDescription = "Home") },
                    label = { Text("Jewelry") },
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                        navController.navigate(ROUT_JEWELRY)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Face, contentDescription = "Places") },
                    label = { Text("Hair") },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate(ROUT_HAIR)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("contact") },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate(ROUT_CONTACT)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Bakery") },
                    label = { Text("Grocery") },
                    selected = selectedIndex == 3,
                    onClick = {
                        selectedIndex = 3
                        navController.navigate(ROUT_GROCERY)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Thrift") },
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
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    // Validate fields before adding the item (no database insertion anymore)
                    if (name.isEmpty() || description.isEmpty() || price.isEmpty() || contact.isEmpty()) {
                        errorMessage = "All fields must be filled"
                    } else {
                        val priceDouble = price.toDoubleOrNull()
                        if (priceDouble == null) {
                            errorMessage = "Price must be a valid number"
                        } else {
                            // Item creation logic, but no database storage here
                            name = ""
                            description = ""
                            price = ""
                            contact = ""
                            errorMessage = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = newOrange)
            ) {
                Text("Add Bakery Item")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // No longer displaying bakery items from database
        }
    }
}
