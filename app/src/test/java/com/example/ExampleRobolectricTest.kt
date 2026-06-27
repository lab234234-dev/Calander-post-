package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PosterCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun verifyAppNameInResources() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        // Verify our correct, branded app name
        assertEquals("Poster365", appName)
    }

    @Test
    fun verifyPosterCatalogLoadsAllTemplates() {
        // Assert catalog is populated with beautiful preset designs
        val templates = PosterCatalog.templates
        assertTrue(templates.isNotEmpty())
        assertTrue(templates.size >= 5)
    }

    @Test
    fun verifyTodaySpecialLaunchPosterExists() {
        val templates = PosterCatalog.templates
        val launchPoster = templates.find { it.id == "today_launch" }
        assertTrue(launchPoster != null)
        assertEquals("2026-06-27", launchPoster?.dateStr)
    }

    @Test
    fun verifyPosterSearchFlow() {
        val searchResults = PosterCatalog.searchPosters("Diwali")
        assertTrue(searchResults.isNotEmpty())
        assertEquals("diwali_festival", searchResults.first().id)
    }

    @Test
    fun verifyMultiLanguageSlogansSupport() {
        val launchPoster = PosterCatalog.templates.first { it.id == "today_launch" }
        val englishSlogan = launchPoster.slogans.find { it.language == "English" }?.text
        val gujaratiSlogan = launchPoster.slogans.find { it.language == "ગુજરાતી" }?.text
        val hindiSlogan = launchPoster.slogans.find { it.language == "हिन्दी" }?.text

        assertTrue(englishSlogan != null && englishSlogan.contains("Poster365"))
        assertTrue(gujaratiSlogan != null && gujaratiSlogan.contains("ડિઝાઇન"))
        assertTrue(hindiSlogan != null && hindiSlogan.contains("डिजाइन"))
    }
}
