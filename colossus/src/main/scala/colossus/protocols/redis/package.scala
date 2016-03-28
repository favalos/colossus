package colossus
package protocols

import akka.util.{ByteString, ByteStringBuilder}
import service._


package object redis {

  trait Redis extends CodecDSL {
    type Input = Command
    type Output = Reply
  }

  object Redis extends ClientFactories[Redis, RedisClient]{
    object defaults extends CodecDefaults[Redis] {

      implicit val serverDefaults = new CodecProvider[Redis] {
        def provideCodec() = new RedisServerCodec
        def errorResponse(request: Command, reason: Throwable) = ErrorReply(s"Error (${reason.getClass.getName}): ${reason.getMessage}")
      }

      implicit val clientDefaults = new ClientCodecProvider[Redis] {
        def clientCodec() = new RedisClientCodec
        val name = "redis"
      }
    }

  }

  object UnifiedBuilder {

    import UnifiedProtocol._

    def buildArg(data: ByteString, builder: ByteStringBuilder) {
      builder append ARG_LEN
      builder append ByteString(data.size.toString)
      builder append RN
      builder append data
      builder append RN
    }
  }
}

