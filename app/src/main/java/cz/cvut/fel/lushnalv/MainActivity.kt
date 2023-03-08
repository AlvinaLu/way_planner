package cz.cvut.fel.lushnalv


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    private lateinit var appPreferences: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            AppTheme {
                WayPlannerApp()
            }
        }


    }

    private fun goToAppSettings() {

    }

}
