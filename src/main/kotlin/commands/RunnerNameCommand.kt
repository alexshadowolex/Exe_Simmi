package commands

import Command
import config.TwitchBotConfig
import updateCurrentRunnerName

val runnerNameCommand: Command = Command(
    names = listOf("rn", "runnername"),
    description = "Pops the next runner name out of the list. Usually, this command is only for few people enabled (e.g. only the streamer).",
    handler = {
        if(messageEvent.user.name != TwitchBotConfig.channel) {
            return@Command
        }
        val runnerName = runNamesRedeemHandler.popNextRunName()
        val message = "${TwitchBotConfig.remindEmote} Next Runner is: " +
                if(runnerName != "") {
                    runnerName
                } else {
                    "No one, we are missing runners!"
                }
        updateCurrentRunnerName(runnerName)
        chat.sendMessage(TwitchBotConfig.channel, message)
    }
)