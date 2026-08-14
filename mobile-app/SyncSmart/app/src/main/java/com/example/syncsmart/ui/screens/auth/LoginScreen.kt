package com.example.syncsmart.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.R
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.components.LogoMark
import com.example.syncsmart.ui.components.drawLock
import com.example.syncsmart.ui.theme.Accent
import com.example.syncsmart.ui.theme.ButtonBlue
import com.example.syncsmart.ui.theme.CardBg
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.SurfaceInset
import com.example.syncsmart.ui.theme.SyncSmartTheme

private val PanelBg = CardBg
private val FieldBorder = IconCyan.copy(alpha = 0.35f)
private val SocialBtnBg = SurfaceInset

/**
 * Login screen: house-photo banner with the Sync Smart logo/title, then a
 * rounded panel with email/password fields, remember-me + forgot-password,
 * a gradient Login button, social sign-in row and a sign-up link.
 */
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onAppleClick: () -> Unit = {},
    onMicrosoftClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = IconCyan,
        focusedBorderColor = IconCyan,
        unfocusedBorderColor = FieldBorder,
        focusedPlaceholderColor = MutedText,
        unfocusedPlaceholderColor = MutedText,
        focusedLeadingIconColor = IconCyan,
        unfocusedLeadingIconColor = IconCyan,
        focusedTrailingIconColor = IconCyan,
        unfocusedTrailingIconColor = IconCyan
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .verticalScroll(rememberScrollState())
            // Keeps the logo/title clear of the status bar on edge-to-edge devices.
            .statusBarsPadding()
    ) {
        // Banner: house photo on the right, logo + title on the left.
        Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
            Image(
                painter = painterResource(R.drawable.img_2),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.62f)
            )
            Column(modifier = Modifier.align(Alignment.TopStart).padding(24.dp)) {
                LogoMark(modifier = Modifier.size(92.dp))
                Spacer(modifier = Modifier.height(14.dp))
                Row {
                    Text(
                        text = "Sync ",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
                        color = Color.White
                    )
                    Text(
                        text = "Smart",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
                        color = IconCyan
                    )
                }
                Text(
                    text = "Smart Home Monitoring &\nControl System",
                    style = TextStyle(fontSize = 14.sp),
                    color = MutedText,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        // Form panel.
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(PanelBg)
                .border(1.dp, IconCyan.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Welcome back!",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Login to continue to your account",
                style = TextStyle(fontSize = 14.sp),
                color = MutedText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Text(
                text = "Email",
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                color = Color.White,
                modifier = Modifier.padding(top = 28.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("Enter your email") },
                leadingIcon = { Canvas(modifier = Modifier.size(18.dp)) { drawMail(IconCyan) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors
            )

            Text(
                text = "Password",
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                color = Color.White,
                modifier = Modifier.padding(top = 20.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("Enter your password") },
                leadingIcon = { Canvas(modifier = Modifier.size(18.dp)) { drawLock(IconCyan) } },
                trailingIcon = {
                    Canvas(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { passwordVisible = !passwordVisible }
                    ) {
                        if (passwordVisible) drawEyeOpen(IconCyan) else drawEyeClosed(IconCyan)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = IconCyan,
                            checkmarkColor = Color.White,
                            uncheckedColor = FieldBorder
                        )
                    )
                    Text(text = "Remember me", style = TextStyle(fontSize = 14.sp), color = Color.White)
                }
                Text(
                    text = "Forgot password?",
                    style = TextStyle(fontSize = 14.sp),
                    color = IconCyan,
                    modifier = Modifier.clickable { onForgotPasswordClick() }
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(ButtonBlue, Accent)))
                    .clickable(onClick = onLoginClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Login",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = Color.White
                )
                Box(modifier = Modifier.fillMaxWidth().padding(end = 20.dp), contentAlignment = Alignment.CenterEnd) {
                    Canvas(modifier = Modifier.size(20.dp)) { drawArrow(Color.White) }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(FieldBorder))
                Text(
                    text = "OR",
                    style = TextStyle(fontSize = 13.sp),
                    color = MutedText,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(FieldBorder))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
            ) {
                SocialButton(onClick = onGoogleClick) { drawGoogleLogo() }
                SocialButton(onClick = onAppleClick) { drawAppleLogo() }
                SocialButton(onClick = onMicrosoftClick) { drawMicrosoftLogo() }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have an account? ", style = TextStyle(fontSize = 14.sp), color = MutedText)
            Text(
                text = "Sign up",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = IconCyan,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }
    }
}

@Composable
private fun SocialButton(onClick: () -> Unit, draw: DrawScope.() -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(SocialBtnBg)
            .border(1.dp, FieldBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) { draw() }
    }
}

private fun DrawScope.drawMail(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(0f, h * 0.15f),
        size = Size(w, h * 0.7f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = Stroke(width = w * 0.09f)
    )
    val flap = Path().apply {
        moveTo(w * 0.06f, h * 0.22f)
        lineTo(w * 0.5f, h * 0.55f)
        lineTo(w * 0.94f, h * 0.22f)
    }
    drawPath(flap, color = color, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawEyeOpen(color: Color) {
    val w = size.width
    val h = size.height
    val eye = Path().apply {
        moveTo(w * 0.02f, h * 0.5f)
        quadraticBezierTo(w * 0.5f, h * 0.05f, w * 0.98f, h * 0.5f)
        quadraticBezierTo(w * 0.5f, h * 0.95f, w * 0.02f, h * 0.5f)
        close()
    }
    drawPath(eye, color = color, style = Stroke(width = w * 0.07f, join = StrokeJoin.Round))
    drawCircle(color = color, radius = w * 0.14f, center = Offset(w * 0.5f, h * 0.5f))
}

private fun DrawScope.drawEyeClosed(color: Color) {
    val w = size.width
    val h = size.height
    drawLine(
        color = color,
        start = Offset(w * 0.06f, h * 0.5f),
        end = Offset(w * 0.94f, h * 0.5f),
        strokeWidth = w * 0.08f,
        cap = StrokeCap.Round
    )
    val brow = Path().apply {
        moveTo(w * 0.18f, h * 0.32f)
        quadraticBezierTo(w * 0.5f, h * 0.02f, w * 0.82f, h * 0.32f)
    }
    drawPath(brow, color = color, style = Stroke(width = w * 0.06f, cap = StrokeCap.Round))
}

private fun DrawScope.drawArrow(color: Color) {
    val w = size.width
    val h = size.height
    drawLine(color, Offset(0f, h / 2), Offset(w * 0.72f, h / 2), strokeWidth = w * 0.11f, cap = StrokeCap.Round)
    val head = Path().apply {
        moveTo(w * 0.42f, h * 0.15f)
        lineTo(w * 0.9f, h * 0.5f)
        lineTo(w * 0.42f, h * 0.85f)
    }
    drawPath(head, color = color, style = Stroke(width = w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** Simplified 4-color "G" mark — a ring split into Google's brand colors plus a crossbar. */
private fun DrawScope.drawGoogleLogo() {
    val w = size.width
    val h = size.height
    val stroke = w * 0.16f
    val topLeft = Offset(w * 0.08f, h * 0.08f)
    val ringSize = Size(w * 0.84f, h * 0.84f)
    drawArc(Color(0xFF4285F4), startAngle = -45f, sweepAngle = 140f, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Butt), topLeft = topLeft, size = ringSize)
    drawArc(Color(0xFF34A853), startAngle = 95f, sweepAngle = 80f, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Butt), topLeft = topLeft, size = ringSize)
    drawArc(Color(0xFFFBBC05), startAngle = 175f, sweepAngle = 80f, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Butt), topLeft = topLeft, size = ringSize)
    drawArc(Color(0xFFEA4335), startAngle = 255f, sweepAngle = 80f, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Butt), topLeft = topLeft, size = ringSize)
    drawRect(Color(0xFF4285F4), topLeft = Offset(w * 0.52f, h * 0.42f), size = Size(w * 0.40f, h * 0.16f))
}

/** Simplified apple silhouette: rounded body with a bite taken out, plus a small leaf. */
private fun DrawScope.drawAppleLogo() {
    val w = size.width
    val h = size.height
    drawCircle(color = Color.White, radius = w * 0.30f, center = Offset(w * 0.35f, h * 0.45f))
    drawCircle(color = Color.White, radius = w * 0.32f, center = Offset(w * 0.62f, h * 0.50f))
    // Bite: cut a notch out of the top-right using the social button's own
    // (opaque) background color, so the cutout reads as transparent rather
    // than showing a mismatched patch.
    drawCircle(color = SocialBtnBg, radius = w * 0.16f, center = Offset(w * 0.78f, h * 0.30f))
    val leaf = Path().apply {
        moveTo(w * 0.55f, h * 0.16f)
        quadraticBezierTo(w * 0.72f, h * 0.05f, w * 0.80f, h * 0.14f)
        quadraticBezierTo(w * 0.65f, h * 0.20f, w * 0.55f, h * 0.16f)
        close()
    }
    drawPath(leaf, color = Color.White, style = Stroke(width = w * 0.04f))
}

/** Four-color Microsoft window mark. */
private fun DrawScope.drawMicrosoftLogo() {
    val w = size.width
    val h = size.height
    val gap = w * 0.06f
    val cell = (w - gap) / 2f
    drawRect(Color(0xFFF25022), topLeft = Offset(0f, 0f), size = Size(cell, cell))
    drawRect(Color(0xFF7FBA00), topLeft = Offset(cell + gap, 0f), size = Size(cell, cell))
    drawRect(Color(0xFF00A4EF), topLeft = Offset(0f, cell + gap), size = Size(cell, cell))
    drawRect(Color(0xFFFFB900), topLeft = Offset(cell + gap, cell + gap), size = Size(cell, cell))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun LoginScreenPreview() {
    SyncSmartTheme {
        LoginScreen()
    }
}
