package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artspace.data.DataSource
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ArtSpaceTheme {

                NavHost(navController = navController, startDestination = Screen.Home.route + "/{id}") {

                    composable(
                        Screen.Home.route + "/{id}", arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ) {
                        HomePage(navController = navController)
                    }

                    composable(
                        Screen.Artist.route + "/{id}",
                        arguments = listOf(navArgument("id"){
                            type = NavType.IntType })
                    ) {
                        ArtistPage(navController = navController)
                    }
                }

            }
        }
    }
}

@Composable
fun ArtistPage (navController: NavController){
    Text(text = "Artist Page")
}

@Composable
fun HomePage(navController: NavController){
    var current by remember {
        mutableIntStateOf(
            navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
        )
    }

    val art = DataSource.arts[current]
    Text(text="Home Page - ${stringResource(id=art.artistId)}")
}

