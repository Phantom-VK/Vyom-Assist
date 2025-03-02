package com.swag.vyom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swag.vyom.R
import com.swag.vyom.ui.components.CustomDropdown
import com.swag.vyom.ui.theme.AppRed
import com.swag.vyom.ui.theme.LightSkyBlue
import com.swag.vyom.ui.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketGenerationScreen(
    onBackClick: () -> Unit = {},
    onGenerateClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Generate Ticket",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                        color = SkyBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onGenerateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Generate",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    ) { paddingValues ->
        var queryDescription by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            QuerySelectionSection()

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Black
            )

            SupportModeSection()

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Black
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttachmentOptions(
                    icon = R.drawable.record,
                    text = "Record Video/Issue"
                )
                AttachmentOptions(
                    icon = R.drawable.attachment,
                    text = "Attach Image"
                )
            }

            TextField(
                value = queryDescription,
                onValueChange = { queryDescription = it },
                placeholder = {
                    Row {
                        Text("Brief Description", color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Edit Description",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                    .background(Color.White),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray,

                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun QuerySelectionSection() {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        text = "Query Selection",
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = AppRed
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CustomDropdown(
            placeHolder = "Category",
            options = listOf("Category 1", "Category 2", "Category 3")
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomDropdown(
            placeHolder = "Sub Category",
            options = listOf("Sub Category 1", "Sub Category 2", "Sub Category 3")
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomDropdown(
            placeHolder = "Urgency Level",
            options = listOf("High", "Mid", "Low")
        )
    }
}

@Composable
fun SupportModeSection() {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
        text = "Preferred Support Mode",
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = AppRed
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SupportModeOption(
                icon = R.drawable.fluent_video_person_call_16_regular,
                text = "Video Call"
            )
            SupportModeOption(
                icon = R.drawable.call_icon,
                text = "Audio Call"  // Fixed the text
            )
            SupportModeOption(
                icon = R.drawable.gridicons_chat,
                text = "Chat"  // Fixed the text
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdown(
            placeHolder = "Available Time Slot",
            options = listOf("Slot 1", "Slot 2", "Slot 3")
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomDropdown(
            placeHolder = "Language Preference",
            options = listOf("Hindi", "English", "Marathi")
        )
    }
}

@Composable
fun SupportModeOption(
    icon: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    var isSelected by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(100.dp)
            .border(
                2.dp,
                color = if (isSelected) SkyBlue else Color.Black,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = if (isSelected) LightSkyBlue else Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                isSelected = !isSelected
                onClick()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) SkyBlue else Color.Black
            )

            Text(
                text = text,
                fontSize = 15.sp,
                color = if (isSelected) SkyBlue else Color.Black
            )
        }
    }
}

@Composable
fun AttachmentOptions(
    icon: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .height(120.dp)
            .width(120.dp)
            .border(
                2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTicketScreen() {
    TicketGenerationScreen()
}