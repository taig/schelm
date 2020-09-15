package io.taig.schelm.data

final case class Attribute(key: Attribute.Key, value: Attribute.Value) {
  def toTuple: (Attribute.Key, Attribute.Value) = (key, value)
}

object Attribute {
  final case class Key(value: String) extends AnyVal

  object Key {
    val Class: Key = Key("class")
  }

  final case class Value(value: String) extends AnyVal {
    def combine(value: Value, separator: String): Value = Value(this.value + separator + value.value)

    def ++(value: Value): Value = combine(value, separator = " ")
  }

  object Value {
    def fromInt(value: Int): Value = Value(String.valueOf(value))

    def fromLong(value: Long): Value = Value(String.valueOf(value))
  }
}
