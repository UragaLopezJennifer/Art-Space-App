package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artspace.data.DataSource
import com.example.artspace.ui.theme.ArtSpaceTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout


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
fun ArtistPage(navController: NavController) {
    val id = navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
    val art = DataSource.arts[id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.spacer_extra_large))
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val borderWidth = 4.dp
                        Image(
                            painter = painterResource(id = art.artistImageId),
                            contentDescription = "Artist image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(125.dp)
                                .border(
                                    BorderStroke(borderWidth, MaterialTheme.colorScheme.primary),
                                    CircleShape
                                )
                                .padding(borderWidth)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_small)))
                        Column {
                            Text(
                                text = stringResource(id = art.artistId),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = "(" + stringResource(id = art.artistInfoId) + ")",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_large)))
            }

            item {
                Text(
                    text = stringResource(id = art.artistBioId),
                    //style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_large)))
            }
            item {
                Button(
                    onClick = { navController.navigate(route = Screen.Home.route + "/$id") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(id = R.string.back))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController){
    var current by remember {
        mutableIntStateOf(
            navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
        )
    }

    val art = DataSource.arts[current]

    Scaffold( topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name))},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
    } ) { innerPadding ->
        /**
         *The children without weight (a) are measured first. After that, the remaining space in the column
         * is spread among the children with weights (b), proportional to their weight. If you have 2
         * children with weight 1f, each will take half the remaining space.
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // children with weight (b)

            ) {
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_extra_large)))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ArtWall(current, art.artworkImageId, art.descriptionId, navController)
                }
            }
            // (a) children without weight
            ArtDescriptor(art.titleId, art.artistId, art.yearId)
            DisplayController(current) {
                current = if (it !in 0 ..<DataSource.arts.size) 0 else it
            }
        }
    }
}


@Composable
fun ArtWall(artistId: Int, artImageId: Int, artDescriptionId: Int, navController: NavController) {
    // HOME PAGE section A
    // TODO: 1. Add image of artwork
    Image(
        painter = painterResource(id = artImageId),
        contentDescription = null,
        modifier = Modifier
           .shadow(12.dp)
           .clickable { navController.navigate(Screen.Artist.route + "/$artistId") }
    )
    // TODO: 2. Add a click listener to navigate to the artist page
    // Note: use the following code on your click event
    // navController.navigate(Screen.Artist.route + "/$artistId")

    // Safely REMOVE the following code and ADD your own code
    //Text(text = "Section A - Display Artwork image here as per the design")
}

@Composable
fun ArtDescriptor(artTitleId: Int, artistId: Int, artYearId: Int) {
    // HOME PAGE section B

    // TODO: 1. Add title of artwork
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = artTitleId),
            style = MaterialTheme.typography.titleLarge
        )

         // TODO: 2. Add artist name and year of artwork
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_small)))
        Text(
            text = stringResource(id = artistId) + " ("  + stringResource(id = artYearId) + ")",
            style = MaterialTheme.typography.titleSmall
        )
    }

    // Safely REMOVE the following code and ADD your own code
   // Text(text = "Section B - Display Artwork title, artist name and year as per the design")
}

@Composable
fun DisplayController(current: Int, updateCurrent: (Int) -> Unit) {
    // HOME PAGE section C
    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_extra_small)))
    // TODO: 1. Add a button to navigate to the previous artwork

    // TODO: 2. Add a button to navigate to the next artwork


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { updateCurrent(current - 1) },
            colors = ButtonDefaults.buttonColors (
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Previous")
        }
        Button(onClick = { updateCurrent(current + 1) }) {
            Text("Next")
        }
    }

    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_extra_small)))

    // NOTE:
    // The buttons should be disabled if there is no previous or next artwork to navigate to
    // You can use the following code to disable the button:
    // enabled = current != 0 // for the previous button
    // enabled = current != DataSource.arts.size - 1 // for the next button

    // You can use the following code to navigate to the previous or next artwork:
    // updateCurrent(current - 1) // for the previous button
    // updateCurrent(current + 1) // for the next button

    // Safely REMOVE the following code and ADD your own code
    //Text(text = "Section C - Display buttons to navigate to previous and next artwork")
}



