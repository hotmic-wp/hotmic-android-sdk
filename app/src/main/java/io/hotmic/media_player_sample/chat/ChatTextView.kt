package io.hotmic.media_player_sample.chat

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.toColorInt
import io.hotmic.media_player_sample.ui.toPx
import org.nibor.autolink.LinkExtractor
import org.nibor.autolink.LinkType
import java.io.Serializable
import java.util.regex.Pattern

internal class ChatTextView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val linkExtractor by lazy {
        LinkExtractor.builder()
            .linkTypes(
                setOf(
                    LinkType.WWW,
                    LinkType.URL,
                    LinkType.EMAIL
                )
            )
            .build()
    }

    fun setChat(
        name: String,
        message: String,
        isVerified: Boolean = false,
        isHost: Boolean = false
    ): List<ChatLink> {
        val prefix: String

        val nameFormatStartIndex = 0
        val nameFormatEndIndex: Int

        prefix = "${name.trim()}: "
        nameFormatEndIndex = prefix.length

        val fullText = "$prefix$message"

        val linkData = linkExtractor.extractLinks(fullText)
        val ssb = SpannableStringBuilder(fullText)

        val nameSpan = ForegroundColorSpan("#FF7106".toColorInt())
        ssb.setSpan(
            nameSpan, nameFormatStartIndex, nameFormatEndIndex,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        val userReferenceSpan = ForegroundColorSpan("#303234".toColorInt())
        parseUserReferences(message, offset = prefix.length).forEach { ix ->
            ssb.setSpan(
                userReferenceSpan, ix.first, ix.second,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        var links: List<ChatLink>? = null

        if (isVerified) {
            links = linkData.map { ls ->
                val linkSpan =
                    ForegroundColorSpan("#303234".toColorInt())
                ssb.setSpan(
                    linkSpan, ls.beginIndex, ls.endIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                ChatLink(fullText.substring(ls.beginIndex, ls.endIndex), ls.type)
            }
        }

        text = ssb
        return links.orEmpty()
    }

    private fun parseUserReferences(message: String?, offset: Int = 0): List<Pair<Int, Int>> {
        message ?: return emptyList()

        val pattern = Pattern.compile("@[\\w_\\.]+")

        val m = pattern.matcher(message)
        val result = ArrayList<Pair<Int, Int>>()
        while (m.find()) {
            result.add(Pair(offset + m.start(), offset + m.end()))
        }

        return result
    }

    companion object {
        private val VERIFIED_ICON_WIDTH = 18.toPx()
        private val VERIFIED_ICON_HEIGHT = 18.toPx()

        private val HOST_ICON_WIDTH = 42.toPx()
        private val HOST_ICON_HEIGHT = 18.toPx()
    }

}


data class ChatLink(val string: String, val type: LinkType) : Serializable

data class ChatArgument(val links: List<ChatLink>) : Serializable