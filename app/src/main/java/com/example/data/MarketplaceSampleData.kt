package com.example.data

import com.example.R
import com.example.model.*

object MarketplaceSampleData {

    fun getCreditPacks(): List<CreditPack> = listOf(
        CreditPack(
            id = "pack_starter",
            credits = 250,
            priceUsd = 4.99,
            bonusCredits = 0,
            isBestValue = false
        ),
        CreditPack(
            id = "pack_creator",
            credits = 1000,
            priceUsd = 14.99,
            bonusCredits = 150,
            isBestValue = true
        ),
        CreditPack(
            id = "pack_studio",
            credits = 3500,
            priceUsd = 39.99,
            bonusCredits = 600,
            isBestValue = false
        ),
        CreditPack(
            id = "pack_enterprise",
            credits = 10000,
            priceUsd = 99.99,
            bonusCredits = 2500,
            isBestValue = false
        )
    )

    fun getInitialMarketplaceAssets(): List<MarketplaceAsset> = listOf(
        MarketplaceAsset(
            id = "asset_ghibli_lora",
            title = "Studio Ghibli 90s Watercolor LoRA v3",
            creatorName = "HayaoDreamer",
            creatorHandle = "@hayaodream",
            category = AssetCategory.STYLE_LORA,
            priceCredits = 80,
            rating = 4.9f,
            reviewsCount = 428,
            downloadsCount = 18400,
            thumbnailResId = R.drawable.img_thumb_surreal_cat,
            description = "Trained on 1,500 hand-drawn animation frames. Produces lush nostalgic watercolor skies, soft natural sunlight, and charming whimsical textures.",
            promptTrigger = "in ghibli_watercolor style, nostalgic golden illumination, hand-painted anime sky",
            sampleVideoTitle = "Captain Whiskers: Flight Over the Floating Sakura Isles",
            isInstalled = true
        ),
        MarketplaceAsset(
            id = "asset_cyber_character",
            title = "Kira: Consistent Cyberpunk Girl Character Rig",
            creatorName = "NeoAnime Labs",
            creatorHandle = "@neoanime",
            category = AssetCategory.CHARACTER_PACK,
            priceCredits = 120,
            rating = 4.8f,
            reviewsCount = 312,
            downloadsCount = 12300,
            thumbnailResId = R.drawable.img_thumb_cyber_anime,
            description = "Complete character consistency seed checkpoint across multiple camera angles, lighting conditions, and dynamic battle outfits.",
            promptTrigger = "character kira_cyber_scout, twin teal ponytails, neon cyber visor",
            sampleVideoTitle = "CYBERPULSE: Shinjuku Neon Rooftop Chase",
            isInstalled = false
        ),
        MarketplaceAsset(
            id = "asset_clay_plasticine",
            title = "Tactile Fingerprint Clay Texture Pack",
            creatorName = "Clay & Wonder",
            creatorHandle = "@claywonder",
            category = AssetCategory.STYLE_LORA,
            priceCredits = 60,
            rating = 5.0f,
            reviewsCount = 189,
            downloadsCount = 8900,
            thumbnailResId = R.drawable.img_thumb_clay_baking,
            description = "Recreates Aardman physical stop-motion plasticine feel with micro thumb indentations, clay squish physics, and studio stage lighting.",
            promptTrigger = "claymation stop-motion style, visible plasticine thumbprints, 12fps timing",
            sampleVideoTitle = "Chef Squeaks & The Great Wobbling Pancake Stack",
            isInstalled = true
        ),
        MarketplaceAsset(
            id = "asset_pixar_dragon_rig",
            title = "Ember: Expressive 3D Pixar Baby Dragon Rig",
            creatorName = "Whimsical Studios",
            creatorHandle = "@whimsical_toons",
            category = AssetCategory.CHARACTER_PACK,
            priceCredits = 150,
            rating = 4.9f,
            reviewsCount = 520,
            downloadsCount = 24100,
            thumbnailResId = R.drawable.img_thumb_pixar_dragon,
            description = "Industry grade Pixar-style rigging prompt embeddings. Guarantees character face consistency, eye dilation, and cute fire sneeze physics.",
            promptTrigger = "character ember_baby_dragon, emerald scales, big purple curious eyes",
            sampleVideoTitle = "Ignis: The Baby Dragon Who Loves Toasted Marshmallows",
            isInstalled = false
        )
    )

    fun getInitialApiKeys(): List<ApiKeyInfo> = listOf(
        ApiKeyInfo(
            keyId = "key_1",
            name = "Production Mobile App",
            maskedKey = "toon_live_98a7************4b12",
            fullKey = "toon_live_98a7c29e18f237190044b12",
            createdDate = "Oct 12, 2026",
            monthlyRequests = 14250,
            isActive = true
        ),
        ApiKeyInfo(
            keyId = "key_2",
            name = "Staging & CI Pipeline",
            maskedKey = "toon_test_41b0************89fc",
            fullKey = "toon_test_41b06f890287114389fc",
            createdDate = "Oct 18, 2026",
            monthlyRequests = 890,
            isActive = true
        )
    )

    fun getSampleLicenses(): List<CommercialLicense> = listOf(
        CommercialLicense(
            certificateId = "CERT-TN-2026-88129",
            videoTitle = "CYBERPULSE: Shinjuku Neon Rooftop Chase",
            videoId = "v1",
            licenseeName = "Global Media Corp Ltd.",
            issuedDate = "Oct 20, 2026",
            hashProof = "0x88f4e912a781bcf034988019aa"
        )
    )
}
