package com.example.model

data class CreatorPrize(
    val id: String,
    val name: String,
    val emoji: String,
    val creditCost: Int,
    val description: String,
    val rankBoostPoints: Int,
    val badgeColorHex: String
)

data class SentPrizeRecord(
    val id: String,
    val prize: CreatorPrize,
    val senderName: String,
    val creatorHandle: String,
    val videoId: String,
    val message: String,
    val timestamp: String
)

object PrizeCatalog {
    val allPrizes: List<CreatorPrize> = listOf(
        CreatorPrize(
            id = "prize_star_spark",
            name = "Star Spark",
            emoji = "⭐",
            creditCost = 15,
            description = "Shine bright on this prompt! Boosts creator's weekly star score.",
            rankBoostPoints = 50,
            badgeColorHex = "#FFB300"
        ),
        CreatorPrize(
            id = "prize_rocket",
            name = "GPU Rocket",
            emoji = "🚀",
            creditCost = 50,
            description = "Fuel their render cluster with extra GPU compute power!",
            rankBoostPoints = 180,
            badgeColorHex = "#00E5FF"
        ),
        CreatorPrize(
            id = "prize_palette",
            name = "Golden Palette",
            emoji = "🎨",
            creditCost = 100,
            description = "For mind-blowing art direction and color grading.",
            rankBoostPoints = 400,
            badgeColorHex = "#B388FF"
        ),
        CreatorPrize(
            id = "prize_crown",
            name = "Toon Crown",
            emoji = "👑",
            creditCost = 250,
            description = "Crown this AI animation as Masterpiece of the Day!",
            rankBoostPoints = 1200,
            badgeColorHex = "#FF4081"
        ),
        CreatorPrize(
            id = "prize_diamond_trophy",
            name = "Diamond Trophy",
            emoji = "💎",
            creditCost = 500,
            description = "Ultimate patron award. Grants massive weekly ranking surge!",
            rankBoostPoints = 3000,
            badgeColorHex = "#00E676"
        )
    )
}
