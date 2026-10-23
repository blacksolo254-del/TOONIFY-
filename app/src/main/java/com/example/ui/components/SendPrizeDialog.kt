package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiVideo
import com.example.model.CreatorPrize
import com.example.model.PrizeCatalog
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ToonifyViewModel

@Composable
fun SendPrizeDialog(
    video: AiVideo,
    viewModel: ToonifyViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val userCredits by viewModel.userCredits.collectAsState()
    var selectedPrize by remember { mutableStateOf<CreatorPrize>(PrizeCatalog.allPrizes.first()) }
    var patronMessage by remember { mutableStateOf("Keep creating amazing AI toons!") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.CardGiftcard,
                    contentDescription = null,
                    tint = ToonifyPink,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Send Creator Prize",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "To ${video.creatorName} (${video.creatorStars})",
                        color = ToonifyCyan,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Select a virtual prize to boost their weekly ranking points and gift credits:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Grid of 5 Prizes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PrizeCatalog.allPrizes.forEach { prize ->
                        val isSelected = prize.id == selectedPrize.id
                        Surface(
                            color = if (isSelected) ToonifyPink.copy(alpha = 0.25f) else DarkSurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 0.5.dp,
                                if (isSelected) ToonifyPink else DarkOutline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clickable { selectedPrize = prize }
                                .testTag("prize_item_${prize.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(prize.emoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${prize.creditCost} cr",
                                    color = if (isSelected) ToonifyPink else ToonifyAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prize detail description
                Surface(
                    color = DarkSurfaceCard,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedPrize.emoji} ${selectedPrize.name}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = ToonifyGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "+${selectedPrize.rankBoostPoints} Weekly Rank Pts",
                                    color = ToonifyGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = selectedPrize.description,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom cheer message
                Text(
                    text = "Cheer Message (Highlighted in comments):",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = patronMessage,
                    onValueChange = { patronMessage = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ToonifyPink,
                        unfocusedBorderColor = DarkOutline
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp).testTag("patron_cheer_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Cost vs balance summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cost: ${selectedPrize.creditCost} credits",
                        color = ToonifyAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your Balance: $userCredits cr",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = viewModel.sendPrize(video.id, selectedPrize, patronMessage)
                    if (success) {
                        Toast.makeText(context, "Sent ${selectedPrize.name} ${selectedPrize.emoji} to ${video.creatorName}!", Toast.LENGTH_LONG).show()
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Insufficient credits! Top up to send this prize.", Toast.LENGTH_SHORT).show()
                        onDismiss()
                        viewModel.navigateTo(AppScreen.PricingAndPlans)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_send_prize_btn")
            ) {
                Text(
                    text = "Send Prize (${selectedPrize.creditCost} cr)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurfaceElevated
    )
}
