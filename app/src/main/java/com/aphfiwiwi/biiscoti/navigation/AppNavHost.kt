package com.aphfiwiwi.biiscoti.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aphfiwiwi.biiscoti.data.UserDatabase
import com.aphfiwiwi.biiscoti.repository.UserRepository
import com.aphfiwiwi.biiscoti.ui.screens.bakery.BakeryScreen
import com.aphfiwiwi.biiscoti.ui.screens.contact.ContactScreen
import com.aphfiwiwi.biiscoti.ui.screens.grocery.GroceryScreen
import com.aphfiwiwi.biiscoti.ui.screens.hair.HairScreen

import com.aphfiwiwi.biiscoti.ui.screens.home.HomeScreen
import com.aphfiwiwi.biiscoti.ui.screens.horticulture.HorticultureScreen
import com.aphfiwiwi.biiscoti.ui.screens.item.ItemScreen
import com.aphfiwiwi.biiscoti.ui.screens.jewelry.JewelryScreen
import com.aphfiwiwi.biiscoti.ui.screens.profile.ProfileScreen
import com.aphfiwiwi.biiscoti.ui.screens.restaurants.RestaurantScreen
import com.aphfiwiwi.biiscoti.ui.screens.search.SearchScreen
import com.aphfiwiwi.biiscoti.ui.screens.splash.SplashScreen
import com.aphfiwiwi.biiscoti.ui.screens.thrift.ThriftScreen
import com.aphfiwiwi.biiscoti.viewmodel.AuthViewModel
import com.example.harakamall.ui.screens.auth.LoginScreen
import com.example.harakamall.ui.screens.auth.RegisterScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH,
) {
    val context = LocalContext.current

    // Initialize Room Database and Repository for Authentication
    val appDatabase = UserDatabase.getDatabase(context)
    val authRepository = UserRepository(appDatabase.userDao())
    val authViewModel: AuthViewModel = AuthViewModel(authRepository)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUT_HOME) {
            HomeScreen(navController)
        }
        composable(ROUT_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUT_ITEM) {
            ItemScreen(navController)
        }
        composable(ROUT_THRIFT) {
            ThriftScreen(navController)
        }
        composable(ROUT_BARKERY) {
            BakeryScreen(navController)
        }
        composable(ROUT_CONTACT) {
            ContactScreen(navController)
        }
        composable(ROUT_HAIR) {
            HairScreen(navController)
        }
        composable(ROUT_GROCERY) {
            GroceryScreen(navController)
        }
        composable(ROUT_HORTICULTURE) {
            HorticultureScreen(navController)
        }
        composable(ROUT_JEWELRY) {
            JewelryScreen(navController)
        }
        composable(ROUT_PROFILE) {
            ProfileScreen(navController)
        }
        composable(ROUT_RESTAURANT) {
            RestaurantScreen(navController)
        }
        composable(ROUT_SEARCH) {
            SearchScreen(navController)
        }

        // Authentication screens
        composable(ROUT_REGISTER) {
            RegisterScreen(authViewModel, navController) {
                navController.navigate(ROUT_LOGIN) {
                    popUpTo(ROUT_REGISTER) { inclusive = true }
                }
            }
        }

        composable(ROUT_LOGIN) {
            LoginScreen(authViewModel, navController) {
                navController.navigate(ROUT_HOME) {
                    popUpTo(ROUT_LOGIN) { inclusive = true }
                }
            }
        }
    }
}
