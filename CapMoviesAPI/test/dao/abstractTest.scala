package dao

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

trait abstractTest extends AnyWordSpec with should.Matchers{
  org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    .asInstanceOf[ch.qos.logback.classic.Logger]
    .setLevel(ch.qos.logback.classic.Level.ERROR)
}
