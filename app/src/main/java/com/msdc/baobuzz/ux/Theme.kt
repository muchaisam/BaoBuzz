package com.msdc.baobuzz.ux

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.msdc.baobuzz.R

@Composable
fun FootballAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
//        colors = lightColors(), // Or darkColors() for a dark theme
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}

// Declare the font families

val Inter = FontFamily(
    Font(R.font.inter),
)


val Typography = Typography(
    body1 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp
    ),
    body2 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    h5 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
)