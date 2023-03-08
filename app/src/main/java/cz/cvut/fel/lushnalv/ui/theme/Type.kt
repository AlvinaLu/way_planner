package cz.cvut.fel.lushnalv.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cz.cvut.fel.lushnalv.R

private val Montserrat = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

private val Karla = FontFamily(
    Font(R.font.karla_regular, FontWeight.Normal),
    Font(R.font.karla_bold, FontWeight.Bold)
)

private val Roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_thin, FontWeight.Thin)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Roboto,
        fontSize = 57.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 64.sp,
        letterSpacing = 0.25.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Roboto,
        fontSize = 45.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 52.sp,
        letterSpacing = 0.00.sp
    ),
   displaySmall = TextStyle(
       fontFamily = Roboto,
       fontSize = 36.sp,
       fontWeight = FontWeight.Normal,
       lineHeight = 44.sp,
       letterSpacing = 0.00.sp
   ),
    headlineLarge = TextStyle(
        fontFamily = Roboto,
        fontSize = 34.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 40.sp,
        letterSpacing = 0.00.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Roboto,
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 36.sp,
        letterSpacing = 0.00.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Roboto,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 32.sp,
        letterSpacing = 0.00.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Roboto,
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
        letterSpacing = 0.00.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Roboto,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.00.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Roboto,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.00.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Roboto,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Roboto,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Roboto,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.04.sp
    ),
)