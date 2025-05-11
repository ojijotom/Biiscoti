package com.aphfiwiwi.biiscoti.ui.screens.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aphfiwiwi.biiscoti.R
import com.aphfiwiwi.biiscoti.ui.theme.pastelyellow
import com.aphfiwiwi.biiscoti.navigation.ROUT_LOGIN
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(navController: NavController){

    //navigation
    val coroutine = rememberCoroutineScope()
    coroutine.launch{
        delay(5000)
        navController.navigate(ROUT_LOGIN)
    }
    //navigation end

    //content start
    Column(modifier = Modifier
        .fillMaxSize().background(pastelyellow),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Spacer(modifier = Modifier.height(90.dp))

        Text(
            text = "Welcome",
            fontSize = 100.sp,
            fontWeight = FontWeight.Thin,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Cursive
        )

        Spacer(modifier = Modifier.height(40.dp))

        Image(painter = painterResource(R.drawable.biiscotilogo),
            contentDescription = "home",
            modifier = Modifier.size(300.dp)
        )

    }


}
@Preview
@Composable
fun SplashScreenPreview(){
    SplashScreen(rememberNavController())


}