package com.example.data

import com.example.R
import com.example.model.*

object ProductionSampleData {

    fun getInitialRenderJobs(): List<RenderJob> = listOf(
        RenderJob(
            jobId = "job_8819",
            projectTitle = "Neo-Tokyo Cyber Run (Episode 2)",
            prompt = "Cyber girl leaping across holographic neon billboards in rain, 60fps optical flow",
            style = ArtStyle.CYBERPUNK_ANIME,
            durationSeconds = 15,
            status = RenderJobStatus.PROCESSING,
            progress = 0.68f,
            currentStepLabel = "Temporal smoothing & optical flow pass...",
            queuedTimestamp = "2 mins ago",
            estimatedSecondsRemaining = 18,
            costCredits = 35,
            previewThumbnailResId = R.drawable.img_thumb_cyber_anime,
            isPriorityQueue = true
        ),
        RenderJob(
            jobId = "job_8820",
            projectTitle = "Little Ignis: Campfire Sparks",
            prompt = "Baby dragon roasting marshmallows, warm volumetric smoke, 3D Pixar render",
            style = ArtStyle.PIXAR_3D,
            durationSeconds = 10,
            status = RenderJobStatus.QUEUED,
            progress = 0.0f,
            currentStepLabel = "Awaiting GPU allocation in priority cluster...",
            queuedTimestamp = "1 min ago",
            estimatedSecondsRemaining = 45,
            costCredits = 25,
            previewThumbnailResId = R.drawable.img_thumb_pixar_dragon,
            isPriorityQueue = false
        ),
        RenderJob(
            jobId = "job_8818",
            projectTitle = "Chef Squeaks: Pancake Flip 4K",
            prompt = "Aardman claymation mouse flipping golden pancakes with funny expression",
            style = ArtStyle.CLAYMATION,
            durationSeconds = 10,
            status = RenderJobStatus.COMPLETED,
            progress = 1.0f,
            currentStepLabel = "Completed & synced to Cloudflare R2",
            queuedTimestamp = "10 mins ago",
            estimatedSecondsRemaining = 0,
            costCredits = 25,
            previewThumbnailResId = R.drawable.img_thumb_clay_baking,
            isPriorityQueue = true
        )
    )

    fun getInitialTimelineClips(): List<TimelineClip> = listOf(
        // Track 1: Video scenes
        TimelineClip("clip_v1", TrackType.VIDEO_KEYFRAMES, "Scene 1: Establishing Shot", 0f, 4f, "#7C4DFF", "aerial camera reveal"),
        TimelineClip("clip_v2", TrackType.VIDEO_KEYFRAMES, "Scene 2: Core Action", 4f, 7f, "#9C27B0", "dynamic 360 orbit"),
        TimelineClip("clip_v3", TrackType.VIDEO_KEYFRAMES, "Scene 3: Hero Resolution", 11f, 4f, "#E040FB", "hero close up pose"),

        // Track 2: Audio & Sound FX
        TimelineClip("clip_a1", TrackType.AUDIO_TRACK, "Synthwave Beats 128 BPM", 0f, 15f, "#00E5FF", "beat sync drop"),

        // Track 3: Voiceover & Lip-sync
        TimelineClip("clip_vo1", TrackType.VOICEOVER_LIPSYNC, "Dialogue: \"I see the target.\"", 2f, 3.5f, "#FFB300", "phoneme viseme lock"),
        TimelineClip("clip_vo2", TrackType.VOICEOVER_LIPSYNC, "Dialogue: \"Initiating jump!\"", 8f, 3.0f, "#FFB300", "phoneme viseme lock"),

        // Track 4: VFX & Particle Overlays
        TimelineClip("clip_fx1", TrackType.VFX_OVERLAY, "Rain Streaks & Bokeh", 0f, 15f, "#FF4081", "screen blend mode")
    )

    fun getSampleC2paRecord(videoId: String, title: String): C2paProvenanceRecord = C2paProvenanceRecord(
        manifestId = "urn:c2pa:toonify:asset:${videoId.take(8)}",
        assetHashSha256 = "c2pa_sha256_9b81f018e3902187b9231f89aa0914c",
        generatorEngine = "Toonify Neural Gen-3 (TensorRT Engine)",
        timestampUtc = "2026-10-21T18:40:00Z",
        isSignedByRootAuthority = true,
        watermarkType = "C2PA Steganographic Provenance Marker",
        assertions = listOf(
            C2paAssertion("Action", "c2pa.created"),
            C2paAssertion("Content Type", "AI-Generated Animated Video"),
            C2paAssertion("Base Model", "Toonify Diffusion Latent-v3.8"),
            C2paAssertion("Training Lineage", "Clean Licensed Training Set / Copyright Verified"),
            C2paAssertion("Software Agent", "Toonify Studio Mobile (Build 36.1)"),
            C2paAssertion("Digital Signature", "Signed by Toonify Trust Anchor Root CA")
        )
    )

    fun evaluateContentGuardrails(prompt: String): ContentGuardrailResult {
        val lower = prompt.lowercase()
        val bannedKeywords = listOf("nsfw", "nude", "hate", "violence", "blood", "kill", "weapon", "terror")
        val trademarkKeywords = listOf("mickey mouse", "batman", "superman", "pikachu", "spiderman", "iron man")

        val hasBanned = bannedKeywords.any { lower.contains(it) }
        val matchedTrademarks = trademarkKeywords.filter { lower.contains(it) }

        return if (hasBanned) {
            ContentGuardrailResult(
                isApproved = false,
                safetyScore = 0.1f,
                ipInfringementRisk = "Prohibited Content",
                warningMessage = "Content safety violation: Prompt contains prohibited terms violating Toonify Safety Guidelines."
            )
        } else if (matchedTrademarks.isNotEmpty()) {
            ContentGuardrailResult(
                isApproved = true,
                safetyScore = 0.75f,
                ipInfringementRisk = "Trademark Fair Use Warning",
                detectedTopics = matchedTrademarks,
                warningMessage = "Notice: Prompt references protected character trademark (${matchedTrademarks.joinToString()}). Commercial licensing requires parody/fair-use compliance."
            )
        } else {
            ContentGuardrailResult(
                isApproved = true,
                safetyScore = 0.98f,
                ipInfringementRisk = "Low / Clean Commercial IP",
                detectedTopics = listOf("Original AI Character", "Cinematic Animation")
            )
        }
    }
}
