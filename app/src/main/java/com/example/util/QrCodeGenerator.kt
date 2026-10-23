package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ToonifyCyan
import com.example.ui.theme.ToonifyPink
import com.example.ui.theme.ToonifyViolet
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object QrCodeGenerator {

    /**
     * Generates a high-quality Bitmap representing a QR Code for the given [content].
     */
    suspend fun generateQrBitmap(
        content: String,
        sizePx: Int = 600,
        foregroundColor: Int = AndroidColor.BLACK,
        backgroundColor: Int = AndroidColor.WHITE
    ): Bitmap = withContext(Dispatchers.Default) {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
        )

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
            }
        }

        Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}

/**
 * Reusable Composable that renders a scannable QR code card with quick actions.
 */
@Composable
fun QrCodeCard(
    content: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onOpenInBrowser: ((String) -> Unit)? = null,
    onShare: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var qrBitmap by remember(content) { mutableStateOf<Bitmap?>(null) }
    var copiedToClipboard by remember { mutableStateOf(false) }

    LaunchedEffect(content) {
        qrBitmap = QrCodeGenerator.generateQrBitmap(content)
    }

    Surface(
        color = Color(0xFF1E212D),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ToonifyViolet.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("qr_code_card")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF9E9E9E),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // High contrast white background for optimal optical camera scanning
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(14.dp)
                    .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "Scannable QR Code for $title",
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("qr_code_image")
                    )
                } else {
                    CircularProgressIndicator(
                        color = ToonifyPink,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // URL badge
            Surface(
                color = Color(0xFF13151D),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3040)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = content,
                    color = ToonifyCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Copy, Share, Open
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(content))
                        copiedToClipboard = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (copiedToClipboard) Color(0xFF00E676) else Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("copy_qr_link_btn")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (copiedToClipboard) "Copied!" else "Copy Link",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (onShare != null) {
                    Button(
                        onClick = { onShare(content) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ToonifyPink),
                        modifier = Modifier.weight(1f).testTag("share_qr_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (onOpenInBrowser != null) {
                    IconButton(
                        onClick = { onOpenInBrowser(content) },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF2B2F40)),
                        modifier = Modifier.size(40.dp).testTag("browser_qr_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInBrowser,
                            contentDescription = "Open in browser",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
