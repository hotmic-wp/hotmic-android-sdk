package io.hotmic.media_player_sample.chat

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.toColorInt
import io.hotmic.media_player_sample.R
import io.hotmic.media_player_sample.ui.toPx
import org.nibor.autolink.LinkExtractor
import org.nibor.autolink.LinkType

internal class TipTextView : AppCompatTextView {

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

    fun setTip(name: String, message: String, tipAmount: String): List<ChatLink> {
        //Playing around with the spaces will mean you will have to adjust the spans below
        val txt = "   $tipAmount  $name: $message"

        val linkData = linkExtractor.extractLinks(txt)
        val ssb = SpannableStringBuilder(txt)

        val links = linkData.map { ls ->
            val linkSpan = ForegroundColorSpan("#303234".toColorInt())
            ssb.setSpan(
                linkSpan, ls.beginIndex, ls.endIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            ChatLink(txt.substring(ls.beginIndex, ls.endIndex), ls.type)
        }

        val amountSpan = ForegroundColorSpan("#FF7106".toColorInt())
        ssb.setSpan(
            amountSpan, 3, tipAmount.length + 3,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        val restSpan = ForegroundColorSpan("#303234".toColorInt())
        ssb.setSpan(
            restSpan, 3 + tipAmount.length + 1, txt.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        val tipDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_tip_orange)
        tipDrawable?.setBounds(0, 0, TIP_ICON_DIM, TIP_ICON_DIM)

        if (tipDrawable == null) return links
        val tipSpan = ImageSpan(tipDrawable)
        ssb.setSpan(tipSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        text = ssb
        return links
    }

    companion object {
        private val TIP_ICON_DIM = 18.toPx()
    }

}