package com.swag.vyom.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.swag.vyom.R
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.SkyBlue

@Composable
fun FaceAuth(navController: NavHostController){

    // Get screen dimensions to make UI responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard(
            screenWidth,
            screenHeight
        )
        Column(modifier = Modifier.offset(0.dp, -80.dp)) {
            AuthPart(navController)
        }
    }
}

@Composable
fun AuthPart(navController: NavHostController){
    Column {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).padding(20.dp).border(
            width = (1.5).dp,
            color = AppRed,
            shape = RoundedCornerShape(50.dp)
        )) {

        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.padding(30.dp, 0.dp).align(Alignment.CenterHorizontally),
            text = "Move Face As Per \n" +
                    "Instructions",
            textAlign = TextAlign.Center,
            color = SkyBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Button(
            onClick = {
                navController.navigate("home_screen") {
                    popUpTo("splash_screen") { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(30.dp, 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(text = "Authenticate", fontSize = 15.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))


        Row(modifier = Modifier.padding(20.dp, 0.dp)){
            Image(
                painter = painterResource(id = R.drawable.attherate),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically)
            )

            Column {
                Text(text = "Need Help? Write to us at", modifier = Modifier, fontSize = 12.sp)
                Text(text = "customercare@unionbankofindia.bank", modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFaceAuth(){
    FaceAuth(navController = rememberNavController())
}