package actors

import java.util.Properties

import actors.KafkaProducerActor.{PrintProps, Send, SendToTopic}
import akka.actor.{Actor, Props}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import play.Logger

/**
  * The Kafka Producer Actor
  * Created by abhiso on 5/27/17.
  */
object KafkaProducerActor {

  def props = Props[KafkaProducerActor]

  object PrintProps

  case class Send(key: String, msg: Any)

  case class SendToTopic(topic: String, key: String, msg: Any)
}

class KafkaProducerActor extends Actor {
  val kafkaConfig = ConfigFactory.load("kafka.conf").getConfig("kafka")

  lazy val kafkaProducer = getKafkaProducer

  lazy val mapper = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule)

  override def receive: Receive = {
    case PrintProps =>
      Logger.info(s"${kafkaConfig}")

    case Send(key, msg) =>
      Logger.info(s"Sending from actor ${self.toString()}")
      val record = new ProducerRecord[String, String](kafkaConfig.getString("topic"), key, mapper.writeValueAsString(msg))
      kafkaProducer.send(record)

    case SendToTopic(topic, key, msg) =>
      val json = mapper.writeValueAsString(msg)
      Logger.info(s"Sending from actor ${sender.toString()} and msg $msg json $json")
      val record = new ProducerRecord[String, String](topic, key, mapper.writeValueAsString(msg))
      kafkaProducer.send(record)
  }

  /**
    * get the kafka producer object
    * @return kafka producer
    */
  def getKafkaProducer: KafkaProducer[String, String] = {
    val props = new Properties
    props.put("bootstrap.servers", kafkaConfig.getString("bootstrap.servers"))
    props.put("acks", kafkaConfig.getString("acks"))
    props.put("retries", kafkaConfig.getString("retries"))
    props.put("batch.size", kafkaConfig.getString("batch.size"))
    props.put("linger.ms", kafkaConfig.getString("linger.ms"))
    props.put("buffer.memory", kafkaConfig.getString("buffer.memory"))
    props.put("key.serializer", kafkaConfig.getString("key.serializer"))
    props.put("value.serializer", kafkaConfig.getString("value.serializer"))

    new KafkaProducer[String, String](props)
  }
}
