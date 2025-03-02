package com.swag.vyom.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
//@Preview(showSystemUi = true)
fun SigninScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard()
        Instructions()
        InteractionPart(navController)
    }

}


@Composable
fun RoundedCornerCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightSkyBlue),
        modifier = Modifier
            .width(400.dp)
            .height(400.dp)
            .offset(0.dp, (-140).dp)
            .graphicsLayer(rotationZ = 45f)
            .clip(shape = RoundedCornerShape(80.dp)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(150.dp)
                .offset(175.dp, 180.dp)
                .graphicsLayer(rotationZ = -45f),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Instructions() {
    Column(modifier = Modifier.offset(0.dp, (-60).dp)) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Image(
                painter = painterResource(id = R.drawable.mobile_msg),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(30.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Image(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(150.dp)
                    .height(30.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Image(
                painter = painterResource(id = R.drawable.bank),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.padding(30.dp, 0.dp),
            text = "The application will send SMS to verify your mobile number registered with Union Bank of India",
            textAlign = TextAlign.Center,
            color = SkyBlue,
            fontSize = 13.sp
        )
    }
}

@Composable
fun InteractionPart(navController: NavHostController) {

    var aadharNo by remember { mutableStateOf("") }
    var mobileNo by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp).offset(0.dp, (-60).dp)
    ) {
        CustomEditText(
            value = aadharNo,
            onValueChange = { aadharNo = it },
            label = "Aadhar Number"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .weight(6f)
                    .align(Alignment.CenterVertically),
                thickness = 1.dp,
            )

            Spacer(modifier = Modifier
                .height(8.dp)
                .weight(1f))

            Text(text = "OR")

            Spacer(modifier = Modifier
                .height(8.dp)
                .weight(1f))

            Divider(
                color = Color.Black,
                modifier = Modifier
                    .weight(6f)
                    .align(Alignment.CenterVertically),
                thickness = 1.dp,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomEditText(
            value = mobileNo,
            onValueChange = { mobileNo = it },
            label = "Mobile Number"
        )

        Text(
            text = "*Enter mobile number which is connected to your bank account",
            textAlign = TextAlign.Center,
            fontSize = 11.sp
        )

        Button(
            onClick = {
                navController.navigate("face_auth") {
                    popUpTo("splash_screen") { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().padding(30.dp, 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(text = "Next", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Card (
            colors = CardDefaults.cardColors(containerColor = LightSkyBlue),
            modifier = Modifier
                .width(400.dp)
                .clip(shape = RoundedCornerShape(20.dp)),
            elevation = CardDefaults.cardElevation(8.dp)
        ){

            Row(modifier = Modifier.padding(0.dp, 10.dp)){
                Image(
                    painter = painterResource(id = R.drawable.piggybank),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(35.dp).align(Alignment.CenterVertically).weight(1f)
                )

                Column (modifier = Modifier.weight(3f)){
                    Text(text = "New Customer?", modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Open a savings account with us.\nIn just few easy steps", modifier = Modifier, fontSize = 13.sp)
                }

                Text(text = "Apply", modifier = Modifier.align(Alignment.CenterVertically).padding(0.dp, 0.dp, 20.dp, 0.dp), color = AppRed, fontWeight = FontWeight.Bold)
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        Row{
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomEditText(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
            .background(Color.White, shape = RoundedCornerShape(10.dp)),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}