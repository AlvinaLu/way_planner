package cz.cvut.fel.lushnalv

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ComposeHelpers {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
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

    }
    @Test
    fun openDayPointTest() {

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Berlin")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Berlin").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("Start point")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Start point").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("day_point_screen_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Start point").assertIsDisplayed()

    }

    @Test
    fun addExpenseTest() {

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Berlin")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Berlin").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("Start point")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Start point").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("day_point_screen_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("Add expense").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Add new expense and participants with whom you want to share it")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.apply {
            onNodeWithTag( "add_expense_dialog_title_tag").performTextInput("Tickets")
            onNodeWithTag("add_expense_dialog_amount_tag").performTextInput("10000")
            onNodeWithTag("add_expense_dialog_button_tag").performClick()
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Tickets")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Tickets").assertIsDisplayed()

    }


    @Test
    fun addNoteTest() {

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Berlin")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Berlin").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("Start point")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Start point").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("day_point_screen_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("Notes").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("add note")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.apply {
            onNodeWithTag( "add_new_note_tag").performTextInput("Hello")
            onNodeWithTag("add_new_note_button_tag").performClick()
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Hello")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()

    }


    @Test
    fun addFriendTest() {

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Berlin")
                .fetchSemanticsNodes().size == 1
        }
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
        composeTestRule.onNodeWithText("Team").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("trip_info_screen_add_friend_button_tag")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("trip_info_screen_add_friend_button_tag").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithTag("trip_info_add_friend_dialog")
                .fetchSemanticsNodes().size == 1
        }

        composeTestRule.apply {
            onNodeWithTag( "login_email_tag").performTextInput("test1@gmail.com")
            onNodeWithTag("trip_info_add_friend_dialog_button_tag").performClick()
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("test1")
                .fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithText("test1").assertIsDisplayed()

    }

    @Test
    fun deleteTripTest() {
        composeTestRule.setContent {
            AppTheme {
                WayPlannerApp()
            }
        }
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText("Berlin")
                .fetchSemanticsNodes().size == 1
        }
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
                .onAllNodesWithText( "Do you really want delete this trip? ")
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

    @Test
    fun logOutTest() {
        composeTestRule.setContent {
            AppTheme {
                WayPlannerApp()
            }
        }
        composeTestRule.onNodeWithTag("menu").performClick()
        composeTestRule.onNodeWithTag("log_out_tag").performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithTag("login_email_tag")
                .fetchSemanticsNodes().size == 1
        }
    }

}

