package com.example.model

enum class TemporalInterpolation(val label: String, val fps: Int) {
    STANDARD_12FPS("12 FPS (Clay/Classic)", 12),
    CINEMATIC_24FPS("24 FPS (Film Look)", 24),
    SMOOTH_30FPS("30 FPS (Standard Video)", 30),
    HYPER_60FPS("60 FPS (Ultra Coherent)", 60)
}

data class TemporalConsistencyCaps(
    val interpolation: TemporalInterpolation = TemporalInterpolation.HYPER_60FPS,
    val smoothingStrength: Float = 0.85f, // 0.0 - 1.0
    val opticalFlowCompensation: Boolean = true,
    val deflickerPass: Boolean = true,
    val characterConsistencyLock: Boolean = true,
    val keyframeAnchorInterval: Int = 8 // every N frames
)

data class AudioSyncConfig(
    val bpmTempo: Int = 128,
    val beatSnapEnabled: Boolean = true,
    val voiceActor: String = "Cyber Anime Female (Neural)",
    val lipSyncAlignment: Boolean = true,
    val autoDuckingMusic: Boolean = true,
    val audioWaveformPoints: List<Float> = listOf(0.2f, 0.5f, 0.8f, 0.95f, 0.6f, 0.3f, 0.7f, 1.0f, 0.85f, 0.4f, 0.6f, 0.75f, 0.9f, 0.4f, 0.2f)
)

enum class RenderJobStatus {
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED,
    PAUSED
}

data class RenderJob(
    val jobId: String,
    val projectTitle: String,
    val prompt: String,
    val style: ArtStyle,
    val durationSeconds: Int,
    val status: RenderJobStatus,
    val progress: Float, // 0.0 - 1.0
    val currentStepLabel: String,
    val queuedTimestamp: String,
    val estimatedSecondsRemaining: Int,
    val costCredits: Int,
    val previewThumbnailResId: Int,
    val isPriorityQueue: Boolean = false
)

enum class CloudStorageProvider(val displayName: String, val endpointDefault: String) {
    CLOUDFLARE_R2("Cloudflare R2", "https://<account_id>.r2.cloudflarestorage.com"),
    AMAZON_S3("Amazon AWS S3", "https://s3.us-east-1.amazonaws.com"),
    GOOGLE_CLOUD_STORAGE("Google Cloud Storage", "https://storage.googleapis.com")
}

data class CloudStorageConfig(
    val provider: CloudStorageProvider = CloudStorageProvider.CLOUDFLARE_R2,
    val bucketName: String = "toonify-production-assets",
    val region: String = "auto",
    val isConnected: Boolean = true,
    val usedStorageGb: Float = 14.8f,
    val maxStorageGb: Float = 100.0f
)

enum class TrackType {
    VIDEO_KEYFRAMES,
    AUDIO_TRACK,
    VOICEOVER_LIPSYNC,
    VFX_OVERLAY
}

data class TimelineClip(
    val clipId: String,
    val trackType: TrackType,
    val name: String,
    val startSeconds: Float,
    val durationSeconds: Float,
    val colorHex: String,
    val promptKeyword: String = ""
)

enum class ExportPlatform(val title: String, val aspectRatio: String, val resolution: String) {
    YOUTUBE_4K("YouTube 4K Cinema", "16:9", "3840 x 2160"),
    SHORTS_TIKTOK("Shorts / TikTok / Reels", "9:16", "1080 x 1920"),
    INSTAGRAM_POST("Instagram Square", "1:1", "1080 x 1080"),
    TWITTER_X("X / Twitter Web", "16:9", "1920 x 1080"),
    PRORES_STUDIO("Apple ProRes 422 HQ", "16:9", "Master Native")
}

data class C2paAssertion(
    val label: String,
    val value: String
)

data class C2paProvenanceRecord(
    val manifestId: String,
    val assetHashSha256: String,
    val generatorEngine: String,
    val claimGenerator: String = "Toonify C2PA Verifier v2.4",
    val timestampUtc: String,
    val isSignedByRootAuthority: Boolean = true,
    val watermarkType: String = "Invisible Steganographic C2PA Watermark",
    val assertions: List<C2paAssertion>
)

data class ContentGuardrailResult(
    val isApproved: Boolean,
    val safetyScore: Float, // 0.0 - 1.0 (higher = safer)
    val ipInfringementRisk: String = "Low",
    val detectedTopics: List<String> = emptyList(),
    val warningMessage: String? = null
)
