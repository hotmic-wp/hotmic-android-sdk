package io.hotmic.media_player_sample.chat

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import io.hotmic.media_player_sample.R
import io.hotmic.player.models.Tip
import polyadapter.PolyAdapter
import polyadapter.equalityItemCallback
import java.text.NumberFormat
import java.util.Currency

internal class SampleTipDelegate(
    private val actionHandler: ChatDelegatesListener
): PolyAdapter.BindingDelegate<Tip, SampleTipViewHolder> {

    override val layoutId = SampleTipViewHolder.LAYOUT
    override val dataType = Tip::class.java

    override val itemCallback = equalityItemCallback<Tip> { this.id }

    override fun createViewHolder(itemView: View) = SampleTipViewHolder(itemView)

    override fun bindView(holder: SampleTipViewHolder, item: Tip) {
        val numberFormat = NumberFormat.getCurrencyInstance().apply {
            currency = getCurrency(item.priceLocaleId)
        }

        val amount = numberFormat.format(item.price)

        val name: String
        val message: String
        val userName = item.userName
        if (userName.isNullOrBlank()) {
            name = "Anonymous"
            message = "Someone made an anonymous tip!"
        } else {
            name = userName
            message = item.message ?: ""
        }

        val links = holder.setTip(name, message, amount)

        holder.setClick { _ ->
            actionHandler.onDelegateTipClicked(item, links)
        }
    }

    /**
     * Returns the currency object associated with the locale in the tip object. The locale is
     * expected to be in en_US@currency=USD format.  If the locale is empty or if the currency is
     * not decipherable then it will be defaulted to USD
     */
    private fun getCurrency(locale: String?): Currency {
        locale ?: return Currency.getInstance("USD")

        val split = locale.split("=")

        return try {
            if (split.size >= 2) {
                Currency.getInstance(split[1])
            } else {
                Currency.getInstance("USD")
            }
        } catch (e: Exception) {
            Currency.getInstance("USD")
        }
    }
}

internal class SampleTipViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tipView = view.findViewById<TipTextView>(R.id.tip_text)

    fun setTip(name: String, message: String, amount: String): List<ChatLink> {
        return tipView.setTip(name, message, amount)
    }

    fun setClick(listener: (View) -> Unit) {
        itemView.setOnClickListener(listener)
    }

    companion object {
        @LayoutRes
        val LAYOUT = R.layout.sample_tip_delegate
    }
}