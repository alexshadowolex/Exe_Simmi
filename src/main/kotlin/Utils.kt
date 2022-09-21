import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant
import java.io.OutputStream

class MultiOutputStream(private vararg val streams: OutputStream) : OutputStream() {
    override fun close() = streams.forEach(OutputStream::close)
    override fun flush() = streams.forEach(OutputStream::flush)

    override fun write(b: Int) = streams.forEach {
        it.write(b)
    }

    override fun write(b: ByteArray) = streams.forEach {
        it.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) = streams.forEach {
        it.write(b, off, len)
    }
}

data class DiscordMessageContent (
    val message: Message,
    val title: String,
    val user: String,
    val channelId: Snowflake
) {
    sealed interface Message {
        data class FromText(val text: String) : Message
        data class FromLink(val link: String) : Message
    }
}

@kotlinx.serialization.Serializable
data class Reminder (
    val message: String,
    val timestampDue: Instant
)

fun String.pluralForm(number: Int) = when {
    number == 1 -> this
    endsWith("y") -> "${substringBeforeLast("y")}ies"
    equals("is") -> "are"
    else -> "${this}s"
}