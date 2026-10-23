package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.ProductionSampleData
import com.example.data.ToonifyRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface AppScreen {
    data object Home : AppScreen
    data class Watch(val videoId: String) : AppScreen
    data object Studio : AppScreen
    data object Shorts : AppScreen
    data object Marketplace : AppScreen
    data object PricingAndPlans : AppScreen
    data object EnterpriseApi : AppScreen
    data object Profile : AppScreen
    data object Search : AppScreen
    data object Canvas : AppScreen
    data object RenderQueue : AppScreen
    data object ProductionSettings : AppScreen
    data object Leaderboard : AppScreen
    data class QrCode(val targetUrl: String? = null, val initialTab: Int = 0) : AppScreen
}

data class StudioUiState(
    val ideaInput: String = "",
    val selectedStyle: ArtStyle = ArtStyle.PIXAR_3D,
    val selectedCamera: CameraMotion = CameraMotion.ORBIT_360,
    val durationSeconds: Int = 10,
    val isDirectorGenerating: Boolean = false,
    val directorPlan: AiCreatorPrompt? = null,
    val isRendering: Boolean = false,
    val renderProgress: Float = 0f,
    val renderPhaseLabel: String = "",
    val renderedVideo: AiVideo? = null,
    val isPublishedSuccessfully: Boolean = false,
    val errorMessage: String? = null
)

class ToonifyViewModel(
    private val repository: ToonifyRepository = ToonifyRepository()
) : ViewModel() {

    private val _screenStack = MutableStateFlow<List<AppScreen>>(listOf(AppScreen.Home))
    val currentScreen: StateFlow<AppScreen> = _screenStack.map { it.lastOrNull() ?: AppScreen.Home }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppScreen.Home)

    val videos: StateFlow<List<AiVideo>> = repository.videos
    val userCredits: StateFlow<Int> = repository.userCredits
    val currentPlan: StateFlow<SubscriptionPlan> = repository.currentPlan
    val marketplaceAssets: StateFlow<List<MarketplaceAsset>> = repository.marketplaceAssets
    val apiKeys: StateFlow<List<ApiKeyInfo>> = repository.apiKeys
    val licenses: StateFlow<List<CommercialLicense>> = repository.licenses

    // Production Engine Flows
    val renderQueue: StateFlow<List<RenderJob>> = repository.renderQueue
    val temporalSettings: StateFlow<TemporalConsistencyCaps> = repository.temporalSettings
    val audioSyncConfig: StateFlow<AudioSyncConfig> = repository.audioSyncConfig
    val cloudStorageConfig: StateFlow<CloudStorageConfig> = repository.cloudStorageConfig
    val timelineClips: StateFlow<List<TimelineClip>> = repository.timelineClips
    val weeklyCreators: StateFlow<List<WeeklyCreatorLeaderboardEntry>> = repository.weeklyCreators

    private val _selectedCategory = MutableStateFlow(VideoCategory.ALL)
    val selectedCategory: StateFlow<VideoCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered video list based on category and search
    val filteredVideos: StateFlow<List<AiVideo>> = combine(videos, _selectedCategory, _searchQuery) { allVideos, category, query ->
        allVideos.filter { video ->
            val matchesCategory = if (category == VideoCategory.ALL) true else video.category == category
            val matchesQuery = if (query.isBlank()) true else {
                video.title.contains(query, ignoreCase = true) ||
                        video.creatorName.contains(query, ignoreCase = true) ||
                        video.aiPrompt.contains(query, ignoreCase = true) ||
                        video.tags.any { it.contains(query, ignoreCase = true) }
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Creator Studio Stats & Videos
    val creatorUploadedVideos: StateFlow<List<AiVideo>> = videos.map { list ->
        list.filter { it.isUserUploaded || it.creatorHandle == "@toonmaster" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creatorTotalStars: String = "124.8K"
    val creatorWeeklyStarsDelta: String = "+1,420"
    val creatorWeeklyRank: Int = 4
    val creatorRankDelta: String = "+2"
    val creatorWeeklyPoints: Int = 24600
    val creatorNextRankPoints: Int = 27000
    val creatorChannelHandle: String = "@toonmaster"
    val creatorChannelName: String = "You (ToonMaster)"

    // Shared App URLs for QR Code scanning
    val appSharedUrl: String = "https://ais-pre-qfaq5pxe2im2tdo43bdb46-116238511945.europe-west2.run.app"
    val appDevUrl: String = "https://ais-dev-qfaq5pxe2im2tdo43bdb46-116238511945.europe-west2.run.app"

    fun handleScannedQrContent(rawContent: String): String {
        val clean = rawContent.trim()
        when {
            clean.contains("/watch/") || clean.contains("v=") -> {
                val videoId = clean.substringAfterLast("/").substringAfter("v=")
                val found = videos.value.find { it.id == videoId }
                if (found != null) {
                    navigateTo(AppScreen.Watch(found.id))
                    return "Opened video: ${found.title}"
                } else {
                    val first = videos.value.firstOrNull()
                    if (first != null) {
                        navigateTo(AppScreen.Watch(first.id))
                        return "Video scanned: playing '${first.title}'"
                    }
                }
            }
            clean.contains("@") -> {
                navigateTo(AppScreen.Profile)
                return "Navigated to Creator Channel: $clean"
            }
            clean.contains("toonify") || clean.contains("europe-west2.run.app") -> {
                navigateTo(AppScreen.Home)
                return "Toonify App link successfully scanned and opened!"
            }
        }
        return "QR Code Scanned: $clean"
    }

    // Studio state
    private val _studioState = MutableStateFlow(StudioUiState())
    val studioState: StateFlow<StudioUiState> = _studioState.asStateFlow()

    private var renderJob: Job? = null

    fun calculateRequiredCredits(durationSeconds: Int): Int {
        return when (durationSeconds) {
            5 -> 15
            10 -> 25
            15 -> 35
            30 -> 60
            else -> 25
        }
    }

    fun navigateTo(screen: AppScreen) {
        val current = _screenStack.value
        if (current.lastOrNull() == screen) return
        _screenStack.value = current + screen
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            return true
        }
        return false
    }

    fun selectCategory(category: VideoCategory) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLike(videoId: String) = repository.toggleLike(videoId)
    fun toggleDislike(videoId: String) = repository.toggleDislike(videoId)
    fun toggleSave(videoId: String) = repository.toggleSave(videoId)
    fun toggleSubscribe(creatorHandle: String) = repository.toggleSubscribe(creatorHandle)
    fun toggleStar(creatorHandle: String) = repository.toggleStarCreator(creatorHandle)
    fun addComment(videoId: String, text: String) = repository.addComment(videoId, text)
    fun sendPrize(videoId: String, prize: CreatorPrize, message: String): Boolean = repository.sendPrizeToCreator(videoId, prize, message)

    // Monetization & Subscription Actions
    fun purchaseCreditPack(pack: CreditPack) {
        repository.addCredits(pack.credits + pack.bonusCredits)
    }

    fun upgradeSubscription(plan: SubscriptionPlan) {
        repository.upgradePlan(plan)
    }

    fun purchaseOrInstallAsset(assetId: String): Boolean {
        return repository.purchaseOrInstallAsset(assetId)
    }

    fun createApiKey(name: String): ApiKeyInfo {
        return repository.createApiKey(name)
    }

    fun revokeApiKey(keyId: String) {
        repository.revokeApiKey(keyId)
    }

    fun issueLicense(videoTitle: String, videoId: String, licenseeName: String): CommercialLicense {
        return repository.issueCommercialLicense(videoTitle, videoId, licenseeName)
    }

    // Studio actions
    fun setStudioIdea(idea: String) {
        _studioState.update { it.copy(ideaInput = idea, errorMessage = null) }
    }

    fun setStudioStyle(style: ArtStyle) {
        _studioState.update { it.copy(selectedStyle = style) }
    }

    fun setStudioCamera(camera: CameraMotion) {
        _studioState.update { it.copy(selectedCamera = camera) }
    }

    fun setStudioDuration(seconds: Int) {
        _studioState.update { it.copy(durationSeconds = seconds) }
    }

    fun generateAiDirectorPlan() {
        val state = _studioState.value
        viewModelScope.launch {
            _studioState.update { it.copy(isDirectorGenerating = true, errorMessage = null) }
            val plan = repository.generateAiDirectorPlan(
                rawIdea = state.ideaInput,
                style = state.selectedStyle,
                camera = state.selectedCamera,
                durationSeconds = state.durationSeconds
            )
            _studioState.update {
                it.copy(
                    isDirectorGenerating = false,
                    directorPlan = plan
                )
            }
        }
    }

    fun startVideoRendering() {
        val cost = calculateRequiredCredits(_studioState.value.durationSeconds)
        if (repository.userCredits.value < cost) {
            _studioState.update {
                it.copy(
                    errorMessage = "Insufficient credits! Need $cost credits, but you have ${repository.userCredits.value}. Top up below or upgrade plan."
                )
            }
            return
        }

        // Deduct credits for the generation!
        repository.deductCredits(cost)

        renderJob?.cancel()
        renderJob = viewModelScope.launch {
            _studioState.update {
                it.copy(
                    isRendering = true,
                    renderProgress = 0.05f,
                    renderPhaseLabel = "Tokenizing prompt & LoRA style weights (-$cost credits)...",
                    errorMessage = null
                )
            }

            delay(700)
            _studioState.update {
                it.copy(
                    renderProgress = 0.25f,
                    renderPhaseLabel = "Synthesizing 60fps latent keyframes..."
                )
            }

            delay(900)
            _studioState.update {
                it.copy(
                    renderProgress = 0.55f,
                    renderPhaseLabel = "Applying temporal consistency pass..."
                )
            }

            delay(800)
            _studioState.update {
                it.copy(
                    renderProgress = 0.80f,
                    renderPhaseLabel = "Neural 4K upscaling & color grading..."
                )
            }

            delay(700)
            _studioState.update {
                it.copy(
                    renderProgress = 0.95f,
                    renderPhaseLabel = "Generating spatial audio & foley sound..."
                )
            }

            delay(500)
            val plan = _studioState.value.directorPlan
            val chosenRes = when (_studioState.value.selectedStyle) {
                ArtStyle.PIXAR_3D -> R.drawable.img_thumb_pixar_dragon
                ArtStyle.CLAYMATION -> R.drawable.img_thumb_clay_baking
                ArtStyle.STUDIO_GHIBLI -> R.drawable.img_thumb_surreal_cat
                ArtStyle.CYBERPUNK_ANIME -> R.drawable.img_thumb_cyber_anime
                ArtStyle.RUBBER_HOSE -> R.drawable.img_thumb_clay_baking
                ArtStyle.HYPER_CARTOON -> R.drawable.img_thumb_cyber_anime
            }

            val newVideo = AiVideo(
                id = "created_${UUID.randomUUID().toString().take(8)}",
                title = plan?.suggestedTitle ?: "My AI Video Creation",
                description = plan?.suggestedDescription ?: "Created with Toonify AI Video Studio.",
                creatorName = "You (ToonMaster)",
                creatorHandle = "@toonmaster",
                creatorSubscribers = "1",
                isVerifiedCreator = true,
                thumbnailResId = chosenRes,
                category = when (_studioState.value.selectedStyle) {
                    ArtStyle.PIXAR_3D -> VideoCategory.PIXAR_3D
                    ArtStyle.CLAYMATION -> VideoCategory.CLAYMATION
                    ArtStyle.STUDIO_GHIBLI -> VideoCategory.SURREAL
                    ArtStyle.CYBERPUNK_ANIME -> VideoCategory.CYBERPUNK
                    ArtStyle.RUBBER_HOSE -> VideoCategory.ANIME
                    ArtStyle.HYPER_CARTOON -> VideoCategory.ANIME
                },
                durationSeconds = _studioState.value.durationSeconds,
                durationFormatted = "0:${_studioState.value.durationSeconds.toString().padStart(2, '0')}",
                viewsCount = 1,
                viewsFormatted = "1",
                likesCount = 1,
                likesFormatted = "1",
                commentsCount = 0,
                uploadedAgo = "Just now",
                isLiked = true,
                isUserUploaded = true,
                aiPrompt = plan?.fullPrompt ?: _studioState.value.ideaInput,
                aiNegativePrompt = plan?.negativePrompt ?: "low quality, blur",
                aiModelUsed = plan?.recommendedModel ?: "Toonify Neural Gen-3",
                cameraMotion = _studioState.value.selectedCamera.label,
                seed = (1000000000L..9999999999L).random(),
                tags = plan?.suggestedTags ?: listOf("ToonifyAI", "Animation")
            )

            _studioState.update {
                it.copy(
                    isRendering = false,
                    renderProgress = 1.0f,
                    renderPhaseLabel = "Video generation complete!",
                    renderedVideo = newVideo
                )
            }
        }
    }

    fun publishRenderedVideo() {
        val video = _studioState.value.renderedVideo ?: return
        repository.publishVideo(video)
        _studioState.update {
            it.copy(
                isPublishedSuccessfully = true
            )
        }
        navigateTo(AppScreen.Watch(video.id))
    }

    fun loadPromptIntoStudio(video: AiVideo) {
        _studioState.update {
            it.copy(
                ideaInput = video.aiPrompt,
                renderedVideo = null,
                isPublishedSuccessfully = false,
                errorMessage = null
            )
        }
        navigateTo(AppScreen.Studio)
    }

    fun resetStudio() {
        _studioState.value = StudioUiState()
    }

    // Production Engine Pipeline Actions
    fun enqueueStudioJobToQueue(): Boolean {
        val cost = calculateRequiredCredits(_studioState.value.durationSeconds)
        if (repository.userCredits.value < cost) {
            _studioState.update {
                it.copy(errorMessage = "Insufficient credits to queue render! Need $cost credits.")
            }
            return false
        }
        repository.deductCredits(cost)
        val plan = _studioState.value.directorPlan
        val newJob = RenderJob(
            jobId = "job_${UUID.randomUUID().toString().take(6)}",
            projectTitle = plan?.suggestedTitle ?: (if (_studioState.value.ideaInput.isNotBlank()) _studioState.value.ideaInput.take(24) + "..." else "AI Scene Creation"),
            prompt = plan?.fullPrompt ?: _studioState.value.ideaInput,
            style = _studioState.value.selectedStyle,
            durationSeconds = _studioState.value.durationSeconds,
            status = RenderJobStatus.QUEUED,
            progress = 0.0f,
            currentStepLabel = "Queued in high-speed GPU cluster...",
            queuedTimestamp = "Just now",
            estimatedSecondsRemaining = 35,
            costCredits = cost,
            previewThumbnailResId = when (_studioState.value.selectedStyle) {
                ArtStyle.PIXAR_3D -> R.drawable.img_thumb_pixar_dragon
                ArtStyle.CLAYMATION -> R.drawable.img_thumb_clay_baking
                ArtStyle.STUDIO_GHIBLI -> R.drawable.img_thumb_surreal_cat
                ArtStyle.CYBERPUNK_ANIME -> R.drawable.img_thumb_cyber_anime
                ArtStyle.RUBBER_HOSE -> R.drawable.img_thumb_clay_baking
                ArtStyle.HYPER_CARTOON -> R.drawable.img_thumb_cyber_anime
            },
            isPriorityQueue = currentPlan.value != SubscriptionPlan.FREE
        )
        repository.enqueueJob(newJob)
        return true
    }

    fun cancelQueueJob(jobId: String) = repository.cancelJob(jobId)
    fun boostQueueJob(jobId: String) = repository.boostJobPriority(jobId)
    fun updateTemporalSettings(settings: TemporalConsistencyCaps) = repository.updateTemporalSettings(settings)
    fun updateAudioSync(config: AudioSyncConfig) = repository.updateAudioSync(config)
    fun updateCloudStorage(config: CloudStorageConfig) = repository.updateCloudStorage(config)
    fun addTimelineClip(clip: TimelineClip) = repository.addTimelineClip(clip)
    fun removeTimelineClip(clipId: String) = repository.removeTimelineClip(clipId)
    fun checkGuardrails(prompt: String): ContentGuardrailResult = ProductionSampleData.evaluateContentGuardrails(prompt)
    fun getC2paProvenance(videoId: String, title: String): C2paProvenanceRecord = ProductionSampleData.getSampleC2paRecord(videoId, title)

    fun createAndPostCustomVideo(
        title: String,
        description: String,
        category: VideoCategory,
        style: ArtStyle,
        tags: List<String>,
        durationSeconds: Int,
        customPromptOrSource: String,
        isCustomUpload: Boolean = false
    ): AiVideo {
        val chosenRes = when (style) {
            ArtStyle.PIXAR_3D -> R.drawable.img_thumb_pixar_dragon
            ArtStyle.CLAYMATION -> R.drawable.img_thumb_clay_baking
            ArtStyle.STUDIO_GHIBLI -> R.drawable.img_thumb_surreal_cat
            ArtStyle.CYBERPUNK_ANIME -> R.drawable.img_thumb_cyber_anime
            ArtStyle.RUBBER_HOSE -> R.drawable.img_thumb_clay_baking
            ArtStyle.HYPER_CARTOON -> R.drawable.img_thumb_cyber_anime
        }

        val cleanTitle = if (title.isBlank()) "My Custom Creation" else title.trim()
        val cleanDesc = if (description.isBlank()) "Created and posted directly in Toonify Studio." else description.trim()

        val newVideo = AiVideo(
            id = "custom_${UUID.randomUUID().toString().take(8)}",
            title = cleanTitle,
            description = cleanDesc,
            creatorName = "You (ToonMaster)",
            creatorHandle = "@toonmaster",
            creatorSubscribers = "1.2K",
            creatorStars = "1.2K ⭐",
            isStarred = true,
            isVerifiedCreator = true,
            thumbnailResId = chosenRes,
            category = category,
            durationSeconds = durationSeconds,
            durationFormatted = "0:${durationSeconds.toString().padStart(2, '0')}",
            viewsCount = 1,
            viewsFormatted = "1",
            likesCount = 1,
            likesFormatted = "1",
            commentsCount = 0,
            uploadedAgo = "Just now",
            isLiked = true,
            isUserUploaded = true,
            aiPrompt = if (customPromptOrSource.isBlank()) "Custom User Studio Creation & Community Post" else customPromptOrSource,
            aiNegativePrompt = "None (Direct Studio Post)",
            aiModelUsed = if (isCustomUpload) "Custom Device Upload / Direct Import" else "Toonify Universal Creator",
            cameraMotion = "Custom / Freeform",
            seed = (1000000000L..9999999999L).random(),
            tags = if (tags.isEmpty()) listOf("StudioUpload", "CreatorPost", category.displayName) else tags,
            weeklyPoints = 250,
            weeklyRank = 4
        )

        repository.publishVideo(newVideo)
        navigateTo(AppScreen.Watch(newVideo.id))
        return newVideo
    }
}

