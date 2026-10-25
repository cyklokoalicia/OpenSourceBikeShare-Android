package com.bikeshare.app.ui.rental

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.bikeshare.app.data.api.dto.RentedBikeDto
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RentedBikeCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `shows the previous code next to the current one`() {
        composeRule.setContent {
            RentedBikeCard(
                bike = RentedBikeDto(
                    bikeNum = 5,
                    currentCode = "5678",
                    rentedSeconds = 320,
                    oldCode = "1234",
                ),
                onReturn = {},
            )
        }

        composeRule.onNodeWithText("Lock code: 5678").assertExists()
        composeRule.onNodeWithText("Previous code: 1234").assertExists()
    }

    @Test
    fun `shows only the current code on a first rental`() {
        composeRule.setContent {
            RentedBikeCard(
                bike = RentedBikeDto(
                    bikeNum = 7,
                    currentCode = "4321",
                    rentedSeconds = 95,
                    oldCode = null,
                ),
                onReturn = {},
            )
        }

        composeRule.onNodeWithText("Lock code: 4321").assertExists()
        composeRule.onNodeWithText("Previous code:", substring = true).assertDoesNotExist()
    }
}
