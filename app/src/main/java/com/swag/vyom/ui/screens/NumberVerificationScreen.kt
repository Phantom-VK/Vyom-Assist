package com.swag.vyom.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swag.vyom.R
import com.swag.vyom.SharedPreferencesHelper
import com.swag.vyom.ui.components.CustomDialog
import com.swag.vyom.ui.components.CustomEditText
import com.swag.vyom.ui.components.CustomLoadingScreen
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue
import com.swag.vyom.viewmodels.AuthViewModel
import com.swag.vyom.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun NumberVerificationScreen(
    navController: NavHostController,
    authVM: AuthViewModel,
    userVM: UserViewModel,
    preferencesHelper: SharedPreferencesHelper
) {
    // State to control the loading screen
    var isLoading by remember { mutableStateOf(false) }

    // Get screen dimensions to make UI responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val context = LocalContext.current
    val activity = context as? Activity
    BackHandler {
        activity?.finish() // Close the app
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = screenWidth.times(0.04f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedCornerCard(screenWidth, screenHeight)
        Instructions(screenWidth)
        InteractionPart(navController, authVM, userVM, preferencesHelper) { isLoading = it }
    }
    if (isLoading) {
        CustomLoadingScreen()
    }
}


@Composable
fun RoundedCornerCard(screenWidth: Dp, screenHeight: Dp) {
    // Calculate dimensions based on screen size
    val cardWidth = screenWidth.times(0.9f)
    val cardHeight = screenHeight.times(0.4f)
    val logoSize = cardWidth.times(0.35f)

    Card(
        colors = CardDefaults.cardColors(containerColor = LightSkyBlue),
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .offset(y = (-screenHeight.times(0.15f)))
            .graphicsLayer(rotationZ = 45f)
            .clip(shape = RoundedCornerShape(screenWidth.times(0.15f))),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(logoSize)
                .offset(
                    x = cardWidth.times(0.45f),
                    y = cardHeight.times(0.45f)
                )
                .graphicsLayer(rotationZ = -45f),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Instructions(screenWidth: Dp) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-60).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mobile_msg),
                contentDescription = "Mobile Message Icon",
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(screenWidth.times(0.05f)))

            Image(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Arrow",
                modifier = Modifier
                    .width(screenWidth.times(0.35f))
                    .height(30.dp)
            )

            Spacer(modifier = Modifier.width(screenWidth.times(0.05f)))

            Image(
                painter = painterResource(id = R.drawable.bank),
                contentDescription = "Bank Icon",
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.padding(horizontal = screenWidth.times(0.08f)),
            text = "The application will send SMS to verify your mobile number registered with Union Bank of India",
            textAlign = TextAlign.Center,
            color = SkyBlue,
            fontSize = 13.sp
        )
    }
}

@Composable
fun InteractionPart(
    navController: NavHostController,
    authVM: AuthViewModel,
    userVM: UserViewModel,
    preferencesHelper: SharedPreferencesHelper,
    onLoadingStateChange: (Boolean) -> Unit // Callback to update loading state
) {
    var aadharNo by remember { mutableStateOf("") }
    var mobileNo by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authVM.customerStatus) {
        authVM.customerStatus.collect { response ->
            response?.let {
                if (it.success) {
                    userVM.savePrimaryUserDetails(
                        aadhaar = (if (aadharNo.isNotEmpty()) aadharNo else it.data?.aadhaar).toString(),
                        mobile = (if (mobileNo.isNotEmpty()) mobileNo else it.data?.mobile_number.toString()),
                        id = it.data?.id
                    )
                    if (it.data?.registered == true) {
                        // Start loading
                        onLoadingStateChange(true)

                        authVM.getUserDetails(
                            mobileNumber = preferencesHelper.getmobile(),
                            aadhaar = preferencesHelper.getaadhaar()
                        ) { response ->
                            if (response.success) {
                                userVM.saveUserDetails(response.data)
                                Log.d("NumberVerificationScreen", "Fetched data successfully: ${response.data}")
                            } else {
                                Log.e("NumberVerificationScreen", "Failed to fetch user details: ${response.msg}")
                            }
                            // Stop loading after fetching data
                            onLoadingStateChange(false)
                        }

                        navController.navigate("login_screen")
                    } else {
                        navController.navigate("register_screen")
                    }
                } else {
                    onLoadingStateChange(false)
                    showDialog = true

                }
            }
        }
    }

    if (showDialog) {
        CustomDialog(
            title = "Customer Not Found",
            message = "No bank account found for given credentials.",
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
            .offset(y = (-40).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomEditText(
            value = aadharNo,
            onValueChange = { aadharNo = it },
            label = "Aadhar Number"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(6f),
                thickness = 1.dp,
                color = Color.Black
            )

            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 14.sp
            )

            HorizontalDivider(
                modifier = Modifier.weight(6f),
                thickness = 1.dp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomEditText(
            value = mobileNo,
            onValueChange = { mobileNo = it },
            label = "Mobile Number"
        )

        Text(
            text = "*Enter mobile number which is connected to your bank account",
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    // Start loading
                    onLoadingStateChange(true)
                    authVM.checkCustomer(mobileNo, aadharNo)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppRed),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = "Next",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        NewCustomerCard()

        Spacer(modifier = Modifier.height(24.dp))

        CustomerCareInfo()
    }
}


@Composable
fun NewCustomerCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightSkyBlue),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.piggybank),
                contentDescription = "Piggy Bank",
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "New Customer?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Open a savings account with us.\nIn just few easy steps",
                    fontSize = 13.sp
                )
            }

            Text(
                text = "Apply",
                color = AppRed,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun CustomerCareInfo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.attherate),
            contentDescription = "At symbol",
            modifier = Modifier
                .size(30.dp)
                .padding(end = 8.dp)
        )

        Column {
            Text(
                text = "Need Help? Write to us at",
                fontSize = 12.sp
            )
            Text(
                text = "customercare@unionbankofindia.bank",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewSignInScreen(){
//    NumberVerificationScreen(rememberNavController())
//}