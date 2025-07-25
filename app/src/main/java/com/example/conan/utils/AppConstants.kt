package com.example.conan.utils

/**
 * Unified constants for PurityPath app
 * Consolidates all app constants in one place following best practices
 */
object AppConstants {

    // Detection sensitivity levels
    enum class DetectionSensitivity {
        LOW,    // Only explicit sites and obvious terms
        MEDIUM, // Includes suggestive content
        HIGH    // Includes all potential triggers
    }

    // Blocking strength options
    enum class BlockingStrength {
        LOW,    // Just minimize/hide app
        HIGH    // Completely terminate app
    }

    // Comprehensive trigger keywords organized by category
    object TriggerKeywords {
        private val EXPLICIT_SITES = listOf(
            "pornhub", "xvideos", "xhamster", "redtube", "youporn", "xnxx", "spankbang",
            "tube8", "beeg", "tnaflix", "motherless", "chaturbate", "cam4", "onlyfans",
            "brazzer", "fapello", "fux", "hclips", "slutload", "extremetube", "mofos",
            "drtuber", "nuvid", "porndoe", "pornerbros", "3movs", "bangbros", "realitykings",
            "hqporner", "yespornplease", "spankwire", "bigtitsglamour", "metart", "watchmygf",
            "rule34", "leakgirls", "rule34video", "thothub", "nudostar", "kink", "vrporn",
            "sexvid", "wetplace", "eroprofile", "erome", "mrdeepfakes", "deepnude"
        )


        private val EXPLICIT_TERMS = listOf(
            "porn", "xxx", "sex", "nude", "naked", "erotic", "nsfw", "adult",
            "milf", "fetish", "cum", "orgasm", "masturbate", "masturbation", "horny",
            "blowjob", "bj", "anal", "threesome", "lesbian", "gay", "incest", "stepmom",
            "stepsister", "deepthroat", "bdsm", "hardcore", "softcore", "boobs", "tits",
            "pussy", "cock", "dick", "penis", "vagina", "clit", "moan", "strip", "nipple",
            "slut", "whore", "hentai", "camgirl", "camsex", "fap", "squirt", "pegging",
            "dildo", "sex tape", "leaked", "onlyfans", "nsfw art", "nudes"
        )



        private val ALL_KEYWORDS = EXPLICIT_SITES + EXPLICIT_TERMS

        fun getKeywordsForSensitivity(sensitivity: DetectionSensitivity): List<String> {
            return when (sensitivity) {
                DetectionSensitivity.LOW -> EXPLICIT_SITES + EXPLICIT_TERMS.take(10)
                DetectionSensitivity.MEDIUM -> EXPLICIT_SITES + EXPLICIT_TERMS
                DetectionSensitivity.HIGH -> ALL_KEYWORDS
            }
        }
    }

    // Monitored app packages
    object MonitoredApps {
        val BROWSERS = setOf(
            // Mainstream browsers
            "com.android.chrome",
            "com.chrome.beta",
            "com.chrome.dev",
            "com.chrome.canary",
            "org.mozilla.firefox",
            "org.mozilla.firefox_beta",
            "com.opera.browser",
            "com.opera.mini.native",
            "com.microsoft.emmx",
            "com.brave.browser",
            "com.UCMobile.intl",
            "com.qbrowser",
            "com.duckduckgo.mobile.android",
            "com.vivaldi.browser",
            "com.kiwibrowser.browser",
            "com.yandex.browser",
            "com.sec.android.app.sbrowser",
            "com.google.android.googlequicksearchbox",
            "com.google.android.apps.searchlite",
            "com.google.android.apps.chrome",
            "com.google.android.youtube",
            "com.google.android.youtube.tv",
            "com.google.android.youtube.kids",
            "com.facebook.katana",
            "com.instagram.android",
            "com.twitter.android",
            "com.reddit.frontpage",
            "com.snapchat.android",
            "com.tiktok.android",
            "com.pinterest",
            "com.estrongs.android.pop",
            "com.xender",
            "com.ionitech.airscreen",
            "org.torproject.torbrowser",
            "org.lineageos.jelly"
        )

        val SOCIAL_MEDIA = setOf(
            // Meta
            "com.instagram.android",
            "com.facebook.katana",
            "com.facebook.lite",
            "com.facebook.orca",
            "com.twitter.android",
            "com.tumblr",
            "com.reddit.frontpage",
            "org.telegram.messenger",
            "com.discord",
            "com.snapchat.android",
            "com.tiktok.android",
            "com.pinterest",
            "com.quora.android",
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill",
            "com.tencent.mm",
            "com.kakao.talk",
            "com.barinsta",
            "me.luckydog.plus",
            "io.github.nevalackin.reel",
            "org.telegram.plus",
            "com.tinder",
            "com.badoo.mobile",
            "com.okcupid.app",
            "com.happn.app"
        )


        val ALL_MONITORED = BROWSERS + SOCIAL_MEDIA

        fun shouldMonitorApp(packageName: String): Boolean {
            return ALL_MONITORED.contains(packageName) && !isSystemApp(packageName)
        }

        private fun isSystemApp(packageName: String): Boolean {
            val systemPackages = setOf(
                "com.android.systemui", "com.android.inputmethod",
                "com.android.settings", "com.example.puritypath"
            )
            return systemPackages.contains(packageName)
        }
    }

    // Service configuration
    object ServiceConfig {
        const val MIN_CONTENT_LENGTH = 5
        const val MAX_TEXT_EXTRACTION_DEPTH = 15 // Increased to 15 for deeper traversal
        const val MAX_TEXT_LENGTH = 5000 // Max characters to extract
        const val DETECTION_DISABLE_DURATION_MS = 3 * 60 * 1000L // 10 minutes
    }

    // Intent actions
    object Actions {
        const val CLOSE_APP = "com.example.puritypath.CLOSE_APP"
        const val DISABLE_DETECTION = "com.example.puritypath.DISABLE_DETECTION"
    }

    // Educational resources
    val EDUCATIONAL_LINKS = mapOf(
        "Understanding Addiction" to "https://fightthenewdrug.org/",
        "Islamic Guidance" to "https://islamqa.info/",
        "Recovery Support" to "https://www.nofap.com/",
        "Mental Health" to "https://www.mentalhealth.gov/"
    )
}