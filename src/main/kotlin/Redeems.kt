import com.github.twitch4j.chat.TwitchChat
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import handler.RunNamesRedeemHandler
import redeems.runNameRedeem

data class Redeem(
    val id: String, // Will initially hold the name of the redeem
    val handler: suspend RedeemHandlerScope.() -> Unit
)

data class RedeemHandlerScope(
    val chat: TwitchChat,
    val redeemEvent: RewardRedeemedEvent,
    val runNamesRedeemHandler: RunNamesRedeemHandler
)

val redeems = listOf(
    runNameRedeem
)