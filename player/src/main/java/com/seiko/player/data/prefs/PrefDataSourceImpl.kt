package com.seiko.player.data.prefs

import androidx.preference.PreferenceDataStore
import com.seiko.common.util.prefs.boolean
import com.seiko.common.util.prefs.int
import com.seiko.common.util.prefs.long
import com.seiko.common.util.prefs.string

class PrefDataSourceImpl(prefs: PreferenceDataStore): PrefDataSource {

    override var mIsUsingMediaCodec by prefs.boolean("PREF_IS_USING_MEDIA_CODEC", true)
    override var mIsUsingMediaCodecH265 by prefs.boolean("PREF_IS_USING_MEDIA_CODEC_H265", true)
    override var mIsUsingOpenSLES by prefs.boolean("PREF_IS_USING_OPEN_SLES", false)
    override var mIsUsingDetachedSurfaceTextureView by prefs.boolean("PREF_USING_DETACH_SURFACE_TEXTURE_VIEW", false)
    override var mIsUsingSurfaceRenders by prefs.boolean("PREF_IS_USING_SURFACE_RENDERS", false)
    override var mPixelFormat by prefs.string("PREF_PIXEL_FORMAT", "")

//    override var timeStretch by prefs.boolean("PREF_TIME_STRETCH", false)
//    override var subtitlesEncoding by prefs.string("PREF_SUBTITLES_ENCODING", "")
//    override var enableFrameSkip by prefs.boolean("PREF_ENABLE_FRAME_SKIP", false)
//    override var chromaFormat by prefs.string("PREF_CHROMA_FORMAT", CHROMA_RV16)
//    override var enableVerboseMode by prefs.boolean("PREF_ENABLE_VERBOSE_MODE", true)
//    override var deBlocking by prefs.int("PREF_DE_BLOCKING", -1)
//    override var networkCaching by prefs.long("PREF_NETWORK_CACHING", 60000L)
//    override var freeTypeRelFontSize by prefs.int("PREF_FREE_TYPE_REL_FONT_SIZE", 16)
//    override var freeTypeBold by prefs.boolean("PREF_FREE_TYPE_BOLD", false)
//    override var freeTypeColor by prefs.int("PREF_FREE_TYPE_COLOR", 16777215)
//    override var freeTypeBackground by prefs.boolean("PREF_FREE_TYPE_BACKGROUND", false)
//    override var openGL by prefs.int("PREF_OPEN_GL", -1)
//    override var castingPassThrough by prefs.boolean("PREF_CASTING_PASS_THROUGH", false)
//    override var castingQuality by prefs.int("PREF_CASTING_QUALITY", 2)
//    override var customVLCOptions: String by prefs.string("PREF_CUSTOM_VLC_OPTIONS", "")
}