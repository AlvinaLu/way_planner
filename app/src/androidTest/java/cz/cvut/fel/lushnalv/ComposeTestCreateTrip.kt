package cz.cvut.fel.lushnalv


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
/**
 * Create trip test
 * Before run the tests, there are must be the following permissions on your phone:
 * Camera
 * Files and media
 * Location
 * Display pop-up windows while running the background
 * Autostart
 */
class ComposeTestCreateTrip {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun loginTest() {
        composeTestRule.setContent {
            AppTheme {
                WayPlannerApp()
            }
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("login_screen_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.apply {
            onNodeWithTag("login_email_tag").performTextInput("test@gmail.com")
            onNodeWithTag("login_password_tag").performTextInput("z1234567G")
            onNodeWithTag("login_button_tag").assertIsDisplayed().performClick()
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("main_screen")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("menu").assertIsDisplayed()
    }

    @Test
    fun createTripTest() {
        var date = LocalDate.now().dayOfMonth
        if(date > 28){
            date = 1
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("create_trip_button_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.apply {
            onNodeWithTag("create_trip_button_tag").performClick()
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("create_trip_screen")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.apply {
            onNodeWithTag("create_trip_title_tag").performTextInput("Berlin")
            onNodeWithTag("create_trip_date_tag").performClick()
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("create_trip_calendar_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.apply {
            onNodeWithTag(date.toString()).performClick()
            onNodeWithTag((date+1).toString()).performClick()
            onNodeWithTag("create_trip_ok_button_tag").performClick()
        }
        composeTestRule.apply {
            onNodeWithTag("create_trip_create_tag").performClick()
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Berlin")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Berlin").assertIsDisplayed()

        composeTestRule.onNodeWithText("Berlin").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("Start point")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("trip_screen_button_info_tag").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("trip_info_screen_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("trip_info_screen_delete_trip_button_tag").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag( "trip_info_screen_confirm_delete_trip_note_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("trip_info_screen_confirm_delete_trip_button_tag").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("main_screen")
                .fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithText("Berlin").assertDoesNotExist()

    }

    @After
    fun logOutTest() {
     
        composeTestRule.onNodeWithTag("menu").performClick()
        composeTestRule.onNodeWithTag("log_out_tag").performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithTag("login_email_tag")
                .fetchSemanticsNodes().size == 1
        }
    }

}

