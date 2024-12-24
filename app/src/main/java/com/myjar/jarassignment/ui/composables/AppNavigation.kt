package com.myjar.jarassignment.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.ui.vm.JarViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: JarViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "item_list"
    ) {
        composable("item_list") {

            var navigate by remember { mutableStateOf<String>("") }

            LaunchedEffect(navigate) {
                if (navigate.isNotEmpty()) {
                    val currRoute = navController.currentDestination?.route.orEmpty()
                    if (!currRoute.contains("item_detail")) {
                        navController.navigate("item_detail/${navigate}")
                        navigate = ""
                    }
                }
            }

            val searchQuery by viewModel.searchText.collectAsStateWithLifecycle()
            val computerItems by viewModel.listStringData.collectAsStateWithLifecycle()

            ItemListScreen(
                computerItems = computerItems,
                searchQuery = searchQuery,
                onNavigateToDetail = { selectedItem -> navigate = selectedItem },
                onSearch = viewModel::onSearchTextChange,
            )
        }

        composable(route = "item_detail/{itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemDetailScreen(itemId = itemId)
        }
    }
}


@Composable
fun ItemListScreen(
    computerItems: List<ComputerItem>,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        CustomSearchView(
            modifier = Modifier.fillMaxWidth(),
            search = searchQuery,
            onValueChange = onSearch
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(computerItems) { item ->
                ItemCard(
                    item = item,
                    onClick = { onNavigateToDetail(item.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

    }
}

@Composable
fun ItemCard(item: ComputerItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (item.data != null) {
            Text(text = item.data.getFormattedData())
        }
    }
}

@Composable
fun ItemDetailScreen(itemId: String?) {
    // Fetch the item details based on the itemId
    // Here, you can fetch it from the ViewModel or repository
    Text(
        text = "Item Details for ID: $itemId",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchView(
    search: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {

    Box(
        modifier = modifier
            .padding(20.dp)
            .clip(CircleShape)
            .background(Color.LightGray)

    ) {
        TextField(value = search,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                focusedPlaceholderColor = Color(0XFF888D91),
                unfocusedLabelColor = Color(0XFF888D91),
                focusedLeadingIconColor = Color(0XFF888D91),
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black
            ),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
           placeholder = { Text(text = "Search") }
        )
    }

}