package handler

import CURRENT_RUNNER_NAME_FILE
import config.TwitchBotConfig
import json
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import logger
import java.io.File
import kotlin.math.absoluteValue

class RunNamesRedeemHandler(private val runNamesFile: File) {
    private var runners = listOf<RunNameUser>()
        private set(value) {
            field = value
            runNamesFile.writeText(json.encodeToString(field))
        }

    private var chatNamesToColor = mutableMapOf<String, String>()

    private val defaultColorHex = "FFFFFF"

    init {
        runners = if (!runNamesFile.exists()) {
            runNamesFile.createNewFile()
            logger.info("Run names file created.")
            mutableListOf()
        } else {
            try {
                json.decodeFromString<List<RunNameUser>>(runNamesFile.readText()).toMutableList().also { currentRemindersData ->
                    logger.info("Existing run names file found! Values: ${currentRemindersData.joinToString(" | ")}")
                }
            } catch (e: Exception) {
                logger.warn("Error while reading run names file. Did something alter its content? Manually check the content below, fix it, put it in and restart the app!", e)
                try {
                    logger.warn("\n" + runNamesFile.readText())
                } catch (e: Exception) {
                    logger.error("Something went wrong with reading the file content yet again. Aborting...")
                    throw ExceptionInInitializerError()
                }
                logger.info("Initializing empty list!")
                mutableListOf()
            }
        }
    }

    fun popNextRunner(): RunNameUser {
        return if(runners.isNotEmpty()) {
            runners.first().also {
                runners = runners.drop(1).toMutableList()
                logger.info("Popped new runner: $it")
            }
        } else {
            RunNameUser("", "")
        }.also {
            logger.info("New run names list: ${runners.joinToString("|")}")
        }
    }

    fun addRunner(name: String) {
        runners = (runners + RunNameUser(name, defaultColorHex)).also {
            logger.info("Added run name $name to the list!")
            logger.info("New run names list: ${it.joinToString("|")}")
        }
    }

    fun getNextRunnersList(amount: Int) =
        try {
            runners.map { it.name }.subList(0, amount.absoluteValue)
        } catch (e: IndexOutOfBoundsException) {
            runners.map { it.name }.subList(0, runners.size)
        }

    fun getMessageForPositionInQueue(name: String): String {
        val index = runners.map { it.name.lowercase() }.indexOf(name.lowercase())
        return if(index != -1) {
            "Stupid question, stop being impatient ${TwitchBotConfig.runnersListIndexEmote} $name's position in queue is ${index + 1} of ${runners.size} ${TwitchBotConfig.explanationEmote}"
        } else {
            "$name is not in queue. They should redeem it, if they want to change this ${TwitchBotConfig.confirmEmote}"
        }
    }

    fun saveNameWithColor(name: String, color: String) {
        // immediate color changes will not be updated
        if(name !in chatNamesToColor) {
            chatNamesToColor += name to color

            runners = runners.map { if (it.name == name && it.chatColor == defaultColorHex) it.copy(chatColor = color) else it }

            val currentRunnerFile = File(CURRENT_RUNNER_NAME_FILE)
            if(!currentRunnerFile.exists()) {
                currentRunnerFile.createNewFile()
                return
            }

            val currentRunner = json.decodeFromString<RunNameUser>(currentRunnerFile.readText())
            if(currentRunner.name == name && currentRunner.chatColor == defaultColorHex) {
                currentRunnerFile.writeText(json.encodeToString(currentRunner.copy(chatColor = color)))
            }
        }
    }
}

@Serializable
data class RunNameUser (
    val name: String,
    val chatColor: String
)