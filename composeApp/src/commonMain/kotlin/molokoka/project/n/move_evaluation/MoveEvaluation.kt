package molokoka.project.n.move_evaluation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// `+`, `-` and `=` name a side by chess convention (Chess Informant; PGN NAGs 14-19):
// the sign is always White-relative, never relative to whoever just moved.
@Serializable(with = MoveEvaluationSerializer::class)
enum class MoveEvaluation(private val symbol: String) {
    WHITE_BETTER("+"),
    BLACK_BETTER("-"),
    EQUAL("=");

    override fun toString(): String = symbol

    companion object {

        fun fromSymbol(symbol: String): MoveEvaluation {
            val evaluation = entries.firstOrNull { it.symbol == symbol }
            requireNotNull(evaluation) { "Symbol must be an evaluation sign, was '$symbol'" }

            return evaluation
        }
    }
}

object MoveEvaluationSerializer : KSerializer<MoveEvaluation> {

    override val descriptor = PrimitiveSerialDescriptor("MoveEvaluation", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: MoveEvaluation) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): MoveEvaluation =
        MoveEvaluation.fromSymbol(decoder.decodeString())
}
