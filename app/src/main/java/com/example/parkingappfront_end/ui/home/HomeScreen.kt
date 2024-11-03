package com.example.parkingappfront_end.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

var testImgs : List<String> = listOf("https://mockuptree.com/wp-content/uploads/edd/2019/10/free-Book-mockup-150x150.jpg",
    "https://images.thegreatestbooks.org/m8kb7ah2lhy960dbp473zna11wb4",
    "https://images.thegreatestbooks.org/e6ucr7aqiwuvqs1of4x4hlvql2x3",
    "https://images.thegreatestbooks.org/2msw2obu4l2xo14lgbkupce9f1y3", "https://images.thegreatestbooks.org/2msw2obu4l2xo14lgbkupce9f1y3",
    "https://images.thegreatestbooks.org/02k8grlgw3la54zif8ubgy9fiyrx")

fun Modifier.productCardModifier(height: Dp, width: Dp, navController: NavController, bookId: Long) = composed {
    this
        .padding(8.dp)
        .width(width)
        .height(height)
        .clickable { navController.navigate("/books_details/${bookId}") }
}
@Composable
fun HomeScreen(navController: NavController) {
    //val products by bookViewModel.allProducts.collectAsState()

    LaunchedEffect(Unit) {
        //bookViewModel.clearCache()
    }

}

//@Composable
//fun ProductCard(navController: NavController, book: Book, height: Dp, width: Dp) { // Book is a data class, nav controller is used to navigate to the details page





