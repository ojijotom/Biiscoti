package com.aphfiwiwi.biiscoti.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.aphfiwiwi.biiscoti.R
import com.aphfiwiwi.biiscoti.navigation.*
import androidx.compose.material3.OutlinedTextFieldDefaults


import com.aphfiwiwi.biiscoti.ui.theme.newOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedIndex by remember { mutableStateOf(0) }
    val searchQuery = remember { mutableStateOf("") }
    val mContext = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        placeholder = { Text("Search businesses") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(25.dp)),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = newOrange,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )

                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(ROUT_GROCERY) }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = newOrange)
            )
        },

        bottomBar = {
            NavigationBar(containerColor = newOrange) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                        navController.navigate(ROUT_HOME)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Place, contentDescription = "Places") },
                    label = { Text("Places") },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate(ROUT_RESTAURANT)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate(ROUT_PROFILE)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Bakery") },
                    label = { Text("Bakery") },
                    selected = selectedIndex == 3,
                    onClick = {
                        selectedIndex = 3
                        navController.navigate(ROUT_BARKERY)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Thrift") },
                    label = { Text("Thrift") },
                    selected = selectedIndex == 4,
                    onClick = {
                        selectedIndex = 4
                        navController.navigate(ROUT_THRIFT)
                    }
                )
            }
        },

        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val categories = listOf("Restaurant", "Cafe", "Thrift", "Jewelry")
                    items(categories) { category ->
                        Card(
                            modifier = Modifier
                                .width(140.dp)
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Popular Right Now:",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    color = Color.Magenta,
                    fontFamily = FontFamily.Cursive,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                ProductItem(
                    name = "Fats",
                    oldPrice = "ksh.500",
                    newPrice = "ksh.450",
                    imageRes = R.drawable.fruits,
                    context = mContext
                )

                ProductItem(
                    name = "Vegetables",
                    oldPrice = "ksh.300",
                    newPrice = "ksh.250",
                    imageRes = R.drawable.fruits,
                    context = mContext
                )

                ProductItem(
                    name = "Fruits",
                    oldPrice = "ksh.1000",
                    newPrice = "ksh.500",
                    imageRes = R.drawable.fruits,
                    context = mContext
                )
            }
        }
    )
}

@Composable
fun ProductItem(name: String, oldPrice: String, newPrice: String, imageRes: Int, context: android.content.Context) {
    Row(modifier = Modifier.padding(start = 20.dp, bottom = 24.dp)) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier
                .width(200.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(oldPrice, fontSize = 15.sp, textDecoration = TextDecoration.LineThrough)
            Text("Price $newPrice", fontSize = 15.sp)
            Row {
                repeat(3) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = newOrange)
                }
                repeat(2) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = "tel:0720245837".toUri()
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(newOrange),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Contact Us")
            }
        }
    }
}
