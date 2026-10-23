package com.example.model

enum class SubscriptionPlan(
    val title: String,
    val monthlyPriceUsd: Int,
    val annualPriceMonthlyUsd: Int,
    val monthlyCredits: Int,
    val maxResolution: String,
    val badge: String,
    val features: List<String>
) {
    FREE(
        title = "Free Starter",
        monthlyPriceUsd = 0,
        annualPriceMonthlyUsd = 0,
        monthlyCredits = 50,
        maxResolution = "720p 30fps",
        badge = "Basic",
        features = listOf(
            "50 AI Video credits / month",
            "720p HD generation",
            "Standard community queue",
            "Personal non-commercial license",
            "Toonify watermark"
        )
    ),
    CREATOR_PRO(
        title = "Creator Pro",
        monthlyPriceUsd = 19,
        annualPriceMonthlyUsd = 15,
        monthlyCredits = 1000,
        maxResolution = "1080p 60fps",
        badge = "Most Popular",
        features = listOf(
            "1,000 AI credits / month (~40 videos)",
            "1080p 60fps temporal smoothing",
            "Zero watermarks",
            "Priority GPU cluster queue",
            "Commercial rights & monetization",
            "Access to community Asset Marketplace"
        )
    ),
    STUDIO_MASTER(
        title = "Studio Master",
        monthlyPriceUsd = 49,
        annualPriceMonthlyUsd = 39,
        monthlyCredits = 3500,
        maxResolution = "4K 60fps Cinema",
        badge = "For Studios",
        features = listOf(
            "3,500 AI credits / month (~150 videos)",
            "4K Ultra-HD 60fps Cinema Master",
            "Multi-shot AI Director Storyboard",
            "Train 5 Custom Character LoRAs/mo",
            "Instant dedicated GPU queue",
            "Sell custom styles on Marketplace (85% payout)"
        )
    ),
    ENTERPRISE(
        title = "Enterprise & API",
        monthlyPriceUsd = 199,
        annualPriceMonthlyUsd = 169,
        monthlyCredits = 15000,
        maxResolution = "8K Master / Raw Latent",
        badge = "API & Teams",
        features = listOf(
            "15,000+ pooled team credits",
            "Full REST API & SDK access",
            "Dedicated H100 GPU compute slice",
            "Custom enterprise SLA (99.9%)",
            "Full copyright clearance indemnity",
            "SSO & multi-seat role controls"
        )
    )
}

data class CreditPack(
    val id: String,
    val credits: Int,
    val priceUsd: Double,
    val bonusCredits: Int = 0,
    val isBestValue: Boolean = false
)

enum class AssetCategory(val displayName: String) {
    ALL("All Assets"),
    STYLE_LORA("Art Style LoRAs"),
    CHARACTER_PACK("Consistent Characters"),
    CAMERA_PRESETS("Camera Rigs"),
    AUDIO_PACKS("AI Foley & Voice")
}

data class MarketplaceAsset(
    val id: String,
    val title: String,
    val creatorName: String,
    val creatorHandle: String,
    val category: AssetCategory,
    val priceCredits: Int,
    val rating: Float,
    val reviewsCount: Int,
    val downloadsCount: Int,
    val thumbnailResId: Int,
    val description: String,
    val promptTrigger: String,
    val sampleVideoTitle: String,
    val isInstalled: Boolean = false
)

data class ApiKeyInfo(
    val keyId: String,
    val maskedKey: String,
    val fullKey: String,
    val name: String,
    val createdDate: String,
    val monthlyRequests: Int,
    val isActive: Boolean = true
)

data class CommercialLicense(
    val certificateId: String,
    val videoTitle: String,
    val videoId: String,
    val licenseeName: String,
    val issuedDate: String,
    val licenseType: String = "Worldwide Royalty-Free Commercial Rights",
    val hashProof: String
)
