package com.example

import com.example.data.ToonifyRepository
import com.example.model.SubscriptionPlan
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {
    private lateinit var repository: ToonifyRepository

    @Before
    fun setUp() {
        repository = ToonifyRepository()
    }

    @Test
    fun testInitialCreditBalanceAndDeduction() {
        val initialCredits = repository.userCredits.value
        assertTrue(initialCredits > 0)

        val success = repository.deductCredits(25)
        assertTrue(success)
        assertEquals(initialCredits - 25, repository.userCredits.value)

        // Cannot deduct more than available
        val fail = repository.deductCredits(999999)
        assertFalse(fail)
    }

    @Test
    fun testPlanUpgradeAwardsCredits() {
        val beforeCredits = repository.userCredits.value
        repository.upgradePlan(SubscriptionPlan.STUDIO_MASTER)

        assertEquals(SubscriptionPlan.STUDIO_MASTER, repository.currentPlan.value)
        assertEquals(beforeCredits + SubscriptionPlan.STUDIO_MASTER.monthlyCredits, repository.userCredits.value)
    }

    @Test
    fun testMarketplacePurchase() {
        val asset = repository.marketplaceAssets.value.first()
        val success = repository.purchaseOrInstallAsset(asset.id)
        assertTrue(success)

        val updated = repository.marketplaceAssets.value.first { it.id == asset.id }
        assertTrue(updated.isInstalled)
    }

    @Test
    fun testApiKeyCreation() {
        val key = repository.createApiKey("Test Webhook")
        assertNotNull(key)
        assertTrue(key.fullKey.startsWith("toon_live_"))
        assertTrue(repository.apiKeys.value.any { it.keyId == key.keyId })
    }

    @Test
    fun testVideoLikeAndComment() {
        val firstVideo = repository.videos.value.first()
        val initialLikes = firstVideo.likesCount

        repository.toggleLike(firstVideo.id)
        val likedVideo = repository.videos.value.first { it.id == firstVideo.id }
        assertTrue(likedVideo.isLiked)
        assertEquals(initialLikes + 1, likedVideo.likesCount)

        repository.addComment(firstVideo.id, "Incredible animation!")
        val commentedVideo = repository.videos.value.first { it.id == firstVideo.id }
        assertEquals(firstVideo.commentsCount + 1, commentedVideo.commentsCount)
        assertEquals("Incredible animation!", commentedVideo.comments.first().text)
    }

    @Test
    fun testWeeklyLeaderboardHasUserCreator() {
        val creators = repository.weeklyCreators.value
        val userCreator = creators.find { it.creatorHandle == "@toonmaster" }
        assertNotNull(userCreator)
        assertEquals(4, userCreator?.rank)
        assertTrue(userCreator?.starsCount?.contains("124.8K") == true)
        assertEquals(1420, userCreator?.starsGainedThisWeek)
    }

    @Test
    fun testUserUploadedVideosInRepository() {
        val userVideos = repository.videos.value.filter { it.isUserUploaded || it.creatorHandle == "@toonmaster" }
        assertTrue(userVideos.isNotEmpty())
        assertTrue(userVideos.any { it.title.contains("Mecha Ronin") })
    }
}
