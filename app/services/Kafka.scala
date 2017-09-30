package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.Configuration

import scala.util.{Failure, Success, Try}

/**
  * Created by abhiso on 5/6/17.
  */
trait Kafka {
  def source(topic: String): Try[Source[ConsumerRecord[String, String], _]]
}

@Singleton
class KafkaImpl @Inject() (configuration: Configuration) extends Kafka {

  /**
    * fetch the kafka url from the configurations
    *
    * @param f function which takes string and gives K
    * @tparam K type K
    * @return Try monad for K
    */
  def maybeKafkaUrl[K](f: String => K): Try[K] = {
    configuration.getString("kafka.url").fold[Try[K]] {
      Failure(new Error("kafka.url was not set"))
    } { kafkaUrl =>
      Success(f(kafkaUrl))
    }
  }

  /**
    * get the consumer settings for the kafka source
    * @return consumer settings
    */
  def consumerSettings: Try[ConsumerSettings[String, String]] = {
    maybeKafkaUrl { kafkaUrl =>
      val deserializer = new StringDeserializer()
      val config = configuration.getConfig("akka.kafka.consumer").getOrElse(Configuration.empty)
      ConsumerSettings(config.underlying, deserializer, deserializer)
        .withBootstrapServers(kafkaUrl)
        .withGroupId(UUID.randomUUID().toString)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    }
  }

  /**
    * get the kafka source
    * @param topic topic to listen on
    * @return the stream of kafka messages
    */
  override def source(topic: String): Try[Source[ConsumerRecord[String, String], _]] = {
    val subscriptions = Subscriptions.topics(topic)
    consumerSettings.map(Consumer.plainSource(_, subscriptions))
  }
}
