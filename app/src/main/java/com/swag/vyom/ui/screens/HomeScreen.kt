package com.swag.vyom.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swag.vyom.R
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue

@Composable
fun HomeScreen(navController: NavHostController) {

    Column {
        TopBar()
        AccountInfo()
        QuickTask()
    }

}

@Preview(showSystemUi = true)
@Composable
fun HomePreview() {

    Column {
        TopBar()
        AccountInfo()
        QuickTask()
    }

}


@Composable
fun TopBar() {
    Row(modifier = Modifier.padding(20.dp)) {
        Image(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = "Profile",
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.weight(1.5F))

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Profile",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(70.dp)
        )

        Spacer(modifier = Modifier.weight(0.5F))

        Card(
            colors = CardDefaults.cardColors(containerColor = LightSkyBlue),
            modifier = Modifier
                .height(45.dp)
                .weight(1.5f)
                .clip(shape = RoundedCornerShape(80.dp))
                .align(Alignment.CenterVertically),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(10.dp, 7.dp).fillMaxHeight()
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = "Bell",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(10.dp, 10.dp).fillMaxHeight()
                )
            }


        }

    }
}

@Composable
fun AccountInfo() {

    Column {
        Text(
            modifier = Modifier.padding(25.dp, 0.dp),
            text = "Good Afternoon",
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier.padding(25.dp, 0.dp),
            text = "Gajanan Palepwad",
            fontSize = 23.sp
        )

        Row(modifier = Modifier.padding(25.dp, 10.dp)) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 0.dp, 10.dp, 25.dp)
                    .shadow(5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Deposits",
                    fontSize = 13.sp
                )

                Text(
                    modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 10.dp),
                    text = "₹ xxxxx",
                    fontSize = 18.sp
                )
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp, 0.dp, 0.dp, 25.dp)
                    .shadow(5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Borrowings",
                    fontSize = 13.sp
                )

                Text(
                    modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 10.dp),
                    text = "₹ 0.00",
                    fontSize = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier
                .width(200.dp)
                .padding(25.dp, 0.dp, 0.dp, 25.dp)
                .shadow(5.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                modifier = Modifier.padding(10.dp, 10.dp, 0.dp, 0.dp),
                text = "Savings",
                fontSize = 13.sp
            )

            Text(
                modifier = Modifier.padding(10.dp, 0.dp),
                text = "*****123",
                fontSize = 13.sp
            )

            Text(
                modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 10.dp),
                text = "₹ xxxxxx",
                fontSize = 18.sp,
                color = SkyBlue
            )
        }
    }
}


@Composable
fun QuickTask() {

    Column {

        Row {
            Text(
                modifier = Modifier.padding(25.dp, 0.dp),
                text = "Quick Tasks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.padding(25.dp, 0.dp),
                text = "Customise",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AppRed
            )
        }

        Row (Modifier.fillMaxWidth().padding(25.dp, 10.dp, 25.dp, 0.dp)){
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = LightSkyBlue)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chatbot),
                        contentDescription = "chatbot",
                        modifier = Modifier
                            .padding(20.dp)
                            .size(50.dp).align(Alignment.CenterHorizontally)
                    )

                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "Chatbot",
                        color = SkyBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp).weight(1f), colors = CardDefaults.cardColors(containerColor = LightSkyBlue)) {
                Column (modifier = Modifier.fillMaxWidth()){
                    Image(
                        painter = painterResource(id = R.drawable.ic_support),
                        contentDescription = "chatbot",
                        modifier = Modifier
                            .padding(20.dp)
                            .size(50.dp).align(Alignment.CenterHorizontally)
                    )

                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "Support",
                        color = SkyBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp).weight(1f))

        }


    }

}