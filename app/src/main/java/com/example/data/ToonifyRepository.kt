package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ToonifyRepository {
    private val _videos = MutableStateFlow<List<AiVideo>>(SampleData.getInitialVideos())
    val videos: StateFlow<List<AiVideo>> = _videos.asStateFlow()

    private val _subscribedChannels = MutableStateFlow<Set<String>>(setOf("@neoanime"))
    val subscribedChannels: StateFlow<Set<String>> = _subscribedChannels.asStateFlow()

    private val _savedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val savedVideoIds: StateFlow<Set<String>> = _savedVideoIds.asStateFlow()

    // Credit-Based Consumption Model State
    private val _userCredits = MutableStateFlow(450)
    val userCredits: StateFlow<Int> = _userCredits.asStateFlow()

    // Tiered SaaS Subscriptions State
    private val _currentPlan = MutableStateFlow(SubscriptionPlan.CREATOR_PRO)
    val currentPlan: StateFlow<SubscriptionPlan> = _currentPlan.asStateFlow()

    // Marketplace Assets State
    private val _marketplaceAssets = MutableStateFlow(MarketplaceSampleData.getInitialMarketplaceAssets())
    val marketplaceAssets: StateFlow<List<MarketplaceAsset>> = _marketplaceAssets.asStateFlow()

    // Enterprise & API Licensing State
    private val _apiKeys = MutableStateFlow(MarketplaceSampleData.getInitialApiKeys())
    val apiKeys: StateFlow<List<ApiKeyInfo>> = _apiKeys.asStateFlow()

    private val _licenses = MutableStateFlow(MarketplaceSampleData.getSampleLicenses())
    val licenses: StateFlow<List<CommercialLicense>> = _licenses.asStateFlow()

    // Production Engine & Pipeline State
    private val _renderQueue = MutableStateFlow(ProductionSampleData.getInitialRenderJobs())
    val renderQueue: StateFlow<List<RenderJob>> = _renderQueue.asStateFlow()

    private val _temporalSettings = MutableStateFlow(TemporalConsistencyCaps())
    val temporalSettings: StateFlow<TemporalConsistencyCaps> = _temporalSettings.asStateFlow()

    private val _audioSyncConfig = MutableStateFlow(AudioSyncConfig())
    val audioSyncConfig: StateFlow<AudioSyncConfig> = _audioSyncConfig.asStateFlow()

    private val _cloudStorageConfig = MutableStateFlow(CloudStorageConfig())
    val cloudStorageConfig: StateFlow<CloudStorageConfig> = _cloudStorageConfig.asStateFlow()

    private val _timelineClips = MutableStateFlow(ProductionSampleData.getInitialTimelineClips())
    val timelineClips: StateFlow<List<TimelineClip>> = _timelineClips.asStateFlow()

    private val _weeklyCreators = MutableStateFlow(SampleData.getWeeklyTopCreators())
    val weeklyCreators: StateFlow<List<WeeklyCreatorLeaderboardEntry>> = _weeklyCreators.asStateFlow()

    fun enqueueJob(job: RenderJob) {
        _renderQueue.value = listOf(job) + _renderQueue.value
    }

    fun cancelJob(jobId: String) {
        _renderQueue.value = _renderQueue.value.filterNot { it.jobId == jobId }
    }

    fun boostJobPriority(jobId: String) {
        _renderQueue.value = _renderQueue.value.map {
            if (it.jobId == jobId) it.copy(isPriorityQueue = true) else it
        }
    }

    fun updateTemporalSettings(settings: TemporalConsistencyCaps) {
        _temporalSettings.value = settings
    }

    fun updateAudioSync(config: AudioSyncConfig) {
        _audioSyncConfig.value = config
    }

    fun updateCloudStorage(config: CloudStorageConfig) {
        _cloudStorageConfig.value = config
    }

    fun addTimelineClip(clip: TimelineClip) {
        _timelineClips.value = _timelineClips.value + clip
    }

    fun removeTimelineClip(clipId: String) {
        _timelineClips.value = _timelineClips.value.filterNot { it.clipId == clipId }
    }

    fun addCredits(amount: Int) {
        _userCredits.value += amount
    }

    fun deductCredits(amount: Int): Boolean {
        if (_userCredits.value >= amount) {
            _userCredits.value -= amount
            return true
        }
        return false
    }

    fun upgradePlan(plan: SubscriptionPlan) {
        _currentPlan.value = plan
        // Instantly credit the monthly allotment
        _userCredits.value += plan.monthlyCredits
    }

    fun purchaseOrInstallAsset(assetId: String): Boolean {
        val asset = _marketplaceAssets.value.find { it.id == assetId } ?: return false
        if (asset.isInstalled) return true

        if (_userCredits.value >= asset.priceCredits) {
            _userCredits.value -= asset.priceCredits
            _marketplaceAssets.value = _marketplaceAssets.value.map {
                if (it.id == assetId) it.copy(isInstalled = true) else it
            }
            return true
        }
        return false
    }

    fun createApiKey(name: String): ApiKeyInfo {
        val randomSuffix = UUID.randomUUID().toString().replace("-", "").take(16)
        val fullKey = "toon_live_$randomSuffix"
        val masked = "toon_live_${randomSuffix.take(4)}************${randomSuffix.takeLast(4)}"
        val newKey = ApiKeyInfo(
            keyId = "key_${UUID.randomUUID().toString().take(6)}",
            name = if (name.isBlank()) "Default App Key" else name.trim(),
            maskedKey = masked,
            fullKey = fullKey,
            createdDate = "Just now",
            monthlyRequests = 0,
            isActive = true
        )
        _apiKeys.value = listOf(newKey) + _apiKeys.value
        return newKey
    }

    fun revokeApiKey(keyId: String) {
        _apiKeys.value = _apiKeys.value.filterNot { it.keyId == keyId }
    }

    fun issueCommercialLicense(videoTitle: String, videoId: String, licenseeName: String): CommercialLicense {
        val cert = CommercialLicense(
            certificateId = "CERT-TN-2026-${(10000..99999).random()}",
            videoTitle = videoTitle,
            videoId = videoId,
            licenseeName = if (licenseeName.isBlank()) "Licensee Organization" else licenseeName.trim(),
            issuedDate = "Oct 21, 2026",
            hashProof = "0x" + UUID.randomUUID().toString().replace("-", "").take(26)
        )
        _licenses.value = listOf(cert) + _licenses.value
        return cert
    }

    fun toggleLike(videoId: String) {
        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                val newIsLiked = !video.isLiked
                val newLikesCount = if (newIsLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
                video.copy(
                    isLiked = newIsLiked,
                    isDisliked = false,
                    likesCount = newLikesCount,
                    likesFormatted = formatNumber(newLikesCount)
                )
            } else video
        }
    }

    fun toggleDislike(videoId: String) {
        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                val newIsDisliked = !video.isDisliked
                val newLikesCount = if (video.isLiked) (video.likesCount - 1).coerceAtLeast(0) else video.likesCount
                video.copy(
                    isLiked = false,
                    isDisliked = newIsDisliked,
                    likesCount = newLikesCount,
                    likesFormatted = formatNumber(newLikesCount)
                )
            } else video
        }
    }

    fun toggleSave(videoId: String) {
        val current = _savedVideoIds.value.toMutableSet()
        if (current.contains(videoId)) {
            current.remove(videoId)
        } else {
            current.add(videoId)
        }
        _savedVideoIds.value = current

        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                video.copy(isSaved = current.contains(videoId))
            } else video
        }
    }

    fun toggleStarCreator(channelHandle: String) {
        val current = _subscribedChannels.value.toMutableSet()
        val isNowStarred = if (current.contains(channelHandle)) {
            current.remove(channelHandle)
            false
        } else {
            current.add(channelHandle)
            true
        }
        _subscribedChannels.value = current

        _videos.value = _videos.value.map { video ->
            if (video.creatorHandle == channelHandle) {
                video.copy(
                    isSubscribed = isNowStarred,
                    isStarred = isNowStarred
                )
            } else video
        }
    }

    fun toggleSubscribe(channelHandle: String) = toggleStarCreator(channelHandle)

    fun addComment(videoId: String, commentText: String) {
        if (commentText.isBlank()) return
        val newComment = VideoComment(
            id = UUID.randomUUID().toString(),
            authorName = "You (ToonMaster)",
            authorBadge = "Creator Pro",
            text = commentText.trim(),
            timeAgo = "Just now",
            likesCount = 0,
            isLiked = false,
            isHeartedByCreator = false
        )

        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                val updatedComments = listOf(newComment) + video.comments
                video.copy(
                    comments = updatedComments,
                    commentsCount = video.commentsCount + 1
                )
            } else video
        }
    }

    fun publishVideo(video: AiVideo) {
        _videos.value = listOf(video) + _videos.value
    }

    private val _sentPrizes = MutableStateFlow<List<SentPrizeRecord>>(emptyList())
    val sentPrizes: StateFlow<List<SentPrizeRecord>> = _sentPrizes.asStateFlow()

    fun sendPrizeToCreator(videoId: String, prize: CreatorPrize, message: String): Boolean {
        if (_userCredits.value < prize.creditCost) return false
        _userCredits.value -= prize.creditCost

        val cleanMsg = if (message.isBlank()) "Sent ${prize.name} ${prize.emoji} to cheer your AI toon!" else message.trim()

        val prizeComment = VideoComment(
            id = UUID.randomUUID().toString(),
            authorName = "You (Patron)",
            authorBadge = "🎁 ${prize.name} ${prize.emoji}",
            text = cleanMsg,
            timeAgo = "Just now",
            likesCount = 5,
            isLiked = true,
            isHeartedByCreator = true
        )

        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                video.copy(
                    weeklyPoints = video.weeklyPoints + prize.rankBoostPoints,
                    comments = listOf(prizeComment) + video.comments,
                    commentsCount = video.commentsCount + 1
                )
            } else video
        }

        val targetVideo = _videos.value.find { it.id == videoId }
        val record = SentPrizeRecord(
            id = UUID.randomUUID().toString(),
            prize = prize,
            senderName = "You",
            creatorHandle = targetVideo?.creatorHandle ?: "@creator",
            videoId = videoId,
            message = cleanMsg,
            timestamp = "Just now"
        )
        _sentPrizes.value = listOf(record) + _sentPrizes.value
        return true
    }

    /**
     * AI Director Assistant:
     * Takes raw input idea, art style, and camera movement, and crafts
     * a cinematic prompt, shot breakdown, negative prompt, sound design, and title!
     */
    suspend fun generateAiDirectorPlan(
        rawIdea: String,
        style: ArtStyle,
        camera: CameraMotion,
        durationSeconds: Int
    ): AiCreatorPrompt {
        delay(1200)

        val cleanIdea = if (rawIdea.isBlank()) "A magical cartoon adventurer on an epic quest" else rawIdea.trim()

        val enhancedPrompt = buildString {
            append("Cinematic animated sequence of ")
            append(cleanIdea)
            append(", ")
            append(style.promptModifier)
            append(", ")
            append(camera.promptTag)
            append(", highly detailed volumetric lighting, dynamic fluid temporal coherence, rich vibrant color grading, pristine cinematic master quality.")
        }

        val negativePrompt = when (style) {
            ArtStyle.PIXAR_3D -> "flat 2D, low poly, bad lighting, creepy untextured surfaces, noisy grain, glitch, anatomical deformity, watermark, blur"
            ArtStyle.STUDIO_GHIBLI -> "photorealistic 3D CGI, plastic gloss, harsh synthetic lighting, modern cars, neon cyberpunk, low resolution, amateur sketch"
            ArtStyle.CLAYMATION -> "smooth vector, hyper digital CGI, glossy render, flat cartoon, high fps motion blur, washed out colors"
            ArtStyle.CYBERPUNK_ANIME -> "dull pastel colors, medieval fantasy, daylight pastoral, low contrast, grainy compression, oversaturated bloom artifacts"
            ArtStyle.RUBBER_HOSE -> "color CGI, modern 3D meshes, realistic human textures, high polygon count, digital gradient fills"
            ArtStyle.HYPER_CARTOON -> "monochrome, static posing, soft airbrush, lack of line weight, blurry edges, boring angle"
        }

        val scenes = generateStoryboard(cleanIdea, style, camera, durationSeconds)

        val suggestedTitle = when {
            cleanIdea.contains("dragon", ignoreCase = true) -> "The Tale of the Ember Drake"
            cleanIdea.contains("cat", ignoreCase = true) -> "Whiskers & The Sky Odyssey"
            cleanIdea.contains("robot", ignoreCase = true) -> "Neon Automaton: Awakening"
            cleanIdea.contains("cyber", ignoreCase = true) -> "Ghost in the Toon: Neo Velocity"
            cleanIdea.contains("pancake", ignoreCase = true) -> "The Breakfast Tower Incident"
            else -> cleanIdea.split(" ").take(5).joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }

        val soundDesign = when (style) {
            ArtStyle.PIXAR_3D -> "Orchestral woodwinds, whimsical marimba taps, soft atmospheric reverb, tactile foley"
            ArtStyle.STUDIO_GHIBLI -> "Nostalgic piano melody, gentle ambient breeze, rustling leaves, soft acoustic chime"
            ArtStyle.CLAYMATION -> "Quirky upright bass plucks, comedic slide whistle, squishy clay foley claps"
            ArtStyle.CYBERPUNK_ANIME -> "Analog synthwave pulse, heavy bass drop, holographic hum, neon arpeggiator"
            ArtStyle.RUBBER_HOSE -> "Honky-tonk piano, spring boing sound effect, vintage 1930s gramophone crackle"
            ArtStyle.HYPER_CARTOON -> "High energy comic drum breaks, kinetic whooshes, comic pop-hit accents"
        }

        val recommendedModel = when (style) {
            ArtStyle.PIXAR_3D -> "Toonify 3D-Engine v4 (Octane)"
            ArtStyle.STUDIO_GHIBLI -> "GhibliDiffusion Dream-2"
            ArtStyle.CLAYMATION -> "ClayMotion Latent-X"
            ArtStyle.CYBERPUNK_ANIME -> "Toonify Neural Gen-3 (60fps)"
            ArtStyle.RUBBER_HOSE -> "VintageToon 1930 Latent"
            ArtStyle.HYPER_CARTOON -> "SpiderComic Cel-Motion 4K"
        }

        return AiCreatorPrompt(
            idea = cleanIdea,
            style = style,
            camera = camera,
            durationSeconds = durationSeconds,
            fullPrompt = enhancedPrompt,
            negativePrompt = negativePrompt,
            suggestedTitle = suggestedTitle,
            suggestedDescription = "Created with Toonify AI Video Director. $cleanIdea in exquisite ${style.label}.",
            suggestedTags = listOf("ToonifyAI", style.name.replace("_", ""), "AIVideo", "Animation"),
            scenes = scenes,
            soundDesign = soundDesign,
            recommendedModel = recommendedModel
        )
    }

    private fun generateStoryboard(
        idea: String,
        style: ArtStyle,
        camera: CameraMotion,
        totalDuration: Int
    ): List<StoryboardScene> {
        val s1Time = totalDuration / 3
        val s2Time = totalDuration / 3
        val s3Time = totalDuration - s1Time - s2Time

        return listOf(
            StoryboardScene(
                sceneNumber = 1,
                title = "Establishing Scene & World Entry",
                shotType = "Wide Angle Establishing",
                actionDescription = "Atmospheric opening introducing the environment. The focal character or subject enters into view with ambient weather effects.",
                promptSegment = "$idea, establishing shot, slow reveal, serene introduction",
                durationSeconds = s1Time
            ),
            StoryboardScene(
                sceneNumber = 2,
                title = "Core Action & Kinetic Motion",
                shotType = camera.label,
                actionDescription = "Peak movement and primary kinetic interaction with dynamic camera motion matching ${camera.label}.",
                promptSegment = "intense kinetic motion, high emotional expression, dynamic particle FX",
                durationSeconds = s2Time
            ),
            StoryboardScene(
                sceneNumber = 3,
                title = "Hero Climax & Pose Resolution",
                shotType = "Medium Hero Close-up",
                actionDescription = "Resolving hero angle showcasing the character's charming expression with cinematic lighting fade.",
                promptSegment = "satisfying punchy resolution, glowing rim highlight, memorable final frame",
                durationSeconds = s3Time
            )
        )
    }

    private fun formatNumber(count: Long): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
            else -> count.toString()
        }
    }
}
