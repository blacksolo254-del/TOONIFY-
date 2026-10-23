package com.example.model

enum class VideoCategory(val displayName: String, val iconEmoji: String) {
    ALL("All", "✨"),
    ANIME("Anime", "🌸"),
    PIXAR_3D("3D Pixar", "🧸"),
    CLAYMATION("Claymation", "🧱"),
    CYBERPUNK("Cyberpunk", "⚡"),
    SURREAL("Surreal Toons", "🎨"),
    SHORTS("AI Shorts", "📱")
}

enum class ArtStyle(val label: String, val description: String, val promptModifier: String) {
    PIXAR_3D(
        "3D Pixar Animation",
        "Expressive character animation, soft subsurface scattering, Disney/Pixar lighting",
        "in whimsical 3D Disney Pixar animation style, highly expressive eyes, soft subsurface scattering, volumetric studio rim lighting, 8k render Octane Engine"
    ),
    STUDIO_GHIBLI(
        "Ghibli Anime",
        "Hand-drawn aesthetic, lush painted backgrounds, nostalgic cinematic palette",
        "hand-drawn Makoto Shinkai and Studio Ghibli anime style, lush watercolor painted skies, nostalgic golden hour illumination, cinematic anime masterpiece"
    ),
    CLAYMATION(
        "Claymation Stop-Motion",
        "Tactile plasticine clay, thumbprint textures, 12fps quirky charm",
        "stop-motion claymation plasticine animation style in the spirit of Aardman, visible tactile fingerprints on clay, miniature physical set lighting, 12fps charm"
    ),
    CYBERPUNK_ANIME(
        "Cyberpunk Neon",
        "Futuristic neon glow, holographic chromatic aberration, rain reflections",
        "cyberpunk anime sci-fi aesthetic, neon magenta and cyan ambient lighting, rain-slicked reflective surfaces, holographic glitch particles, cinematic anamorphic lens flare"
    ),
    RUBBER_HOSE(
        "1930s Vintage Cartoon",
        "Vintage rubber-hose animation, sepia grain, bouncy character limbs",
        "classic 1930s rubber-hose cartoon animation style, bouncy bendy physics, vintage 35mm projector grain, charming expressive black and white with sepia tint"
    ),
    HYPER_CARTOON(
        "Hyper-Real Comic",
        "Dynamic action comic book, stylized halftone shading, dramatic angles",
        "Into the Spider-Verse inspired dynamic comic book animation style, halftone dots, chromatic 3D offset, kinetic speed lines, bold cel-shaded ink contours"
    )
}

enum class CameraMotion(val label: String, val promptTag: String) {
    ORBIT_360("360° Orbit", "dynamic 360-degree smooth circular camera orbit around subject"),
    DRONE_FLYTHROUGH("Drone Fly-Through", "seamless cinematic FPV drone sweeping forward through environment"),
    DOLLY_ZOOM("Dolly Zoom (Vertigo)", "dramatic cinematic Hitchcock dolly zoom z-axis shift"),
    SLOW_TILT_PAN("Slow Tilt & Pan", "gentle upward camera tilt revealing scale with horizontal pan"),
    STATIC_CLOSEUP("Intimate Close-up", "static centered macro lens with shallow depth of field and soft bokeh")
}

data class StoryboardScene(
    val sceneNumber: Int,
    val title: String,
    val shotType: String,
    val actionDescription: String,
    val promptSegment: String,
    val durationSeconds: Int
)

data class AiCreatorPrompt(
    val idea: String,
    val style: ArtStyle,
    val camera: CameraMotion,
    val durationSeconds: Int,
    val fullPrompt: String,
    val negativePrompt: String,
    val suggestedTitle: String,
    val suggestedDescription: String,
    val suggestedTags: List<String>,
    val scenes: List<StoryboardScene>,
    val soundDesign: String,
    val recommendedModel: String
)

data class VideoComment(
    val id: String,
    val authorName: String,
    val authorBadge: String = "",
    val text: String,
    val timeAgo: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isHeartedByCreator: Boolean = false
)

data class AiVideo(
    val id: String,
    val title: String,
    val description: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorSubscribers: String,
    val creatorStars: String = creatorSubscribers,
    val isVerifiedCreator: Boolean = true,
    val thumbnailResId: Int,
    val category: VideoCategory,
    val durationSeconds: Int,
    val durationFormatted: String,
    val viewsCount: Long,
    val viewsFormatted: String,
    val likesCount: Long,
    val likesFormatted: String,
    val commentsCount: Int,
    val uploadedAgo: String,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false,
    val isSubscribed: Boolean = false,
    val isStarred: Boolean = isSubscribed,
    val isUserUploaded: Boolean = false,
    val weeklyRank: Int? = null,
    val weeklyPoints: Int = 0,
    val weeklyPrizeBadge: String? = null,
    // AI Creation Metadata
    val aiPrompt: String,
    val aiNegativePrompt: String,
    val aiModelUsed: String,
    val cameraMotion: String,
    val seed: Long,
    val resolution: String = "4K 60fps",
    val guidanceScale: Float = 7.5f,
    val tags: List<String> = emptyList(),
    val comments: List<VideoComment> = emptyList()
)

data class WeeklyCreatorLeaderboardEntry(
    val rank: Int,
    val creatorName: String,
    val creatorHandle: String,
    val starsCount: String,
    val starsGainedThisWeek: Int,
    val isVerified: Boolean = true,
    val topVideoTitle: String,
    val prizeCreditsReward: Int
)
