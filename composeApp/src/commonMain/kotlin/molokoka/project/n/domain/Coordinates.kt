package molokoka.project.n.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CoordinatesSerializer::class)
data class Coordinates(
    val file: Char,
    val rank: Int
) {
    init {
        require(file in FILE_RANGE) { "File must be in $FILE_RANGE, was '$file'" }
        require(rank in RANK_RANGE) { "Rank must be in $RANK_RANGE, was $rank" }
    }

    override fun toString(): String = "$file$rank"

    companion object {

        fun parse(notation: String): Coordinates {
            require(notation.length == 2) { "Square must be a file and a rank, like 'a1', was '$notation'" }

            return Coordinates(notation[0], notation[1] - '0')
        }
    }
}

object CoordinatesSerializer : KSerializer<Coordinates> {

    override val descriptor = PrimitiveSerialDescriptor("Coordinates", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Coordinates) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Coordinates =
        Coordinates.parse(decoder.decodeString())
}

val Coordinates.isLightSquare: Boolean get() = (file.code + rank) % 2 != 0
