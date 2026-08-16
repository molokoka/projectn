package molokoka.project.n.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MoveSerializer::class)
data class Move(val from: Coordinates, val to: Coordinates) {

    override fun toString(): String =
        "$from$to"

    companion object {

        fun parse(lan: String): Move {
            require(lan.length == 4) { "Move must be two squares, like 'b2b4', was '$lan'" }

            return Move(Coordinates.parse(lan.take(2)), Coordinates.parse(lan.drop(2)))
        }
    }
}

object MoveSerializer : KSerializer<Move> {

    override val descriptor = PrimitiveSerialDescriptor("Move", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Move) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Move = Move.parse(decoder.decodeString())
}
