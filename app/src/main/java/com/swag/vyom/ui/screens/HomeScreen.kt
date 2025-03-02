package com.swag.vyom.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.R
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TopBar()
        AccountInfo()
        QuickTask()
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomePreview() {
    HomeScreen(rememberNavController())
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = "Profile",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(70.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = LightSkyBlue),
            modifier = Modifier
                .height(45.dp)
                .clip(shape = RoundedCornerShape(22.5.dp)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = "Notifications",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AccountInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Good Afternoon",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Gajanan Palepwad",
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Account cards
        Row(modifier = Modifier.fillMaxWidth()) {
            AccountCard(
                title = "Deposits",
                amount = "₹ xxxxx",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            AccountCard(
                title = "Borrowings",
                amount = "₹ 0.00",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Savings card
        AccountCard(
            title = "Savings",
            accountNumber = "*****123",
            amount = "₹ xxxxxx",
            amountColor = SkyBlue,
            modifier = Modifier
                .widthIn(max = 200.dp)
                .align(Alignment.Start)
        )
    }
}

@Composable
fun AccountCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier,
    accountNumber: String? = null,
    amountColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            if (accountNumber != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = accountNumber,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = amountColor
            )
        }
    }
}

@Composable
fun QuickTask() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Tasks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Customise",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AppRed
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick task cards
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickTaskCard(
                iconResId = R.drawable.ic_chatbot,
                title = "Chatbot",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )

            QuickTaskCard(
                iconResId = R.drawable.ic_support,
                title = "Support",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(start = 8.dp)
            )

            // Empty space for the third column
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun QuickTaskCard(
    iconResId: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = LightSkyBlue)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier
                    .size(50.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = title,
                color = SkyBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}