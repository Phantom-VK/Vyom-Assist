package com.swag.vyom.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.dataclasses.Product
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.utils.getRecommendedProducts

@Composable
fun ForYouSection() {
    val context = LocalContext.current
    val prefrences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val recommendedProducts = remember { getRecommendedProducts(prefrences) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "For You",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppRed
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {

            recommendedProducts.forEach { product ->
                item{
                    ProductCard(product = product)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppRed
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.description,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (product.interestRate != null) {
                Text(
                    text = "Interest Rate: ${product.interestRate}%",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (product.benefits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Benefits:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppRed
                )
                product.benefits.forEach { benefit ->
                    Text(
                        text = "• $benefit",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}