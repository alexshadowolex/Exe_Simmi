
import com.github.twitch4j.chat.TwitchChat
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.google.api.services.sheets.v4.Sheets
import commands.*
import dev.kord.core.Kord
import handler.RemindHandler
import handler.RunNamesRedeemHandler
import kotlin.time.Duration

data class Command(
    val names: List<String>,
    val description: String,
    val handler: suspend CommandHandlerScope.(arguments: List<String>) -> Unit
)

data class CommandHandlerScope(
    val discordClient: Kord,
    val chat: TwitchChat,
    val messageEvent: ChannelMessageEvent,
    val remindHandler: RemindHandler,
    val runNamesRedeemHandler: RunNamesRedeemHandler,
    var addedUserCooldown: Duration = Duration.ZERO,
    var addedCommandCooldown: Duration = Duration.ZERO
)

val commands = listOf(
    helpCommand,
    feedbackCommand,
    gameSuggestionCommand,
    sendClipCommand,
    remindCommand,
    runnerNameCommand,
    runnersListCommand
)