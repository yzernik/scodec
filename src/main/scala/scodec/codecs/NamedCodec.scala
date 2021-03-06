package scodec
package codecs

import scodec.bits.BitVector

/** Codec that prefixes error messages with the specified name. */
private[codecs] final class NamedCodec[A](name: String, target: Codec[A]) extends Codec[A] {

  override def encode(a: A) =
    target.encode(a).leftMap { e => s"$name: $e" }

  override def decode(buffer: BitVector) =
    target.decode(buffer).leftMap { e => s"$name: $e" }

  override def toString = s"$name($target)"
}

