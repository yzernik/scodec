package scodec
package codecs

import scalaz.\/
import org.scalacheck.Gen
import scodec.bits.BitVector

class LongCodecTest extends CodecSuite {
  def check(low: Long, high: Long)(f: (Long) => Unit) {
    forAll(Gen.choose(low, high)) { n =>
      whenever(n >= low) { f(n) }
    }
  }

  "the int64 codec" should { "roundtrip" in { forAll { (n: Long) => roundtrip(int64, n) } } }
  "the int64L codec" should { "roundtrip" in { forAll { (n: Long) => roundtrip(int64L, n) } } }
  "the uint32 codec" should { "roundtrip" in { check(0, 1L << (32 - 1)) { (n: Long) => roundtrip(uint32, n) } } }
  "the uint32L codec" should { "roundtrip" in { check(0L, (1L << 32) - 1) { (n: Long) => roundtrip(uint32L, n) } } }

  "the ulong(n) codec" should { "roundtrip" in { ulong(13).encode(1) shouldBe \/.right(BitVector.low(13).set(12)) } }
  "the ulongL(n) codec" should { "roundtrip" in { ulongL(13).encode(1) shouldBe \/.right(BitVector.low(13).set(7)) } }

  "the long codecs" should {
    "support endianess correctly" in {
      forAll { (n: Long) =>
        val bigEndian = int64.encode(n).toOption.get.toByteVector
        val littleEndian = int64L.encode(n).toOption.get.toByteVector
        littleEndian shouldBe bigEndian.reverse
      }
    }

    "return an error when value to encode is out of legal range" in {
      uint32.encode(-1) shouldBe \/.left("-1 is less than minimum value 0 for 32-bit unsigned integer")
    }

    "return an error when decoding with too few bits" in {
      uint32.decode(BitVector.low(8)) shouldBe \/.left("cannot acquire 32 bits from a vector that contains 8 bits")
    }
  }
}
