package com.github.feather.di

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.feather.di.ClassInfoHelper.getClass
import com.github.feather.di.annotations.ConfigurationProperty
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

import java.io.FileInputStream
import scala.jdk.CollectionConverters.*

class ConfigLoader:
  private val logger = LoggerFactory.getLogger(getClass)

  private var mapper: ObjectMapper                        = _
  private var serverConfig: java.util.Map[String, AnyRef] = _
  private var actualConfig: java.util.Map[String, AnyRef] = _

  def build(startingConfigPath: String): ConfigLoader =
    this.mapper = new ObjectMapper
    this.mapper.registerModule(DefaultScalaModule)
    val path                                           = System.getProperty("user.dir") + startingConfigPath
    val bootstrapConfig: java.util.Map[String, AnyRef] = new Yaml().load(new FileInputStream(path))
    this.serverConfig = getForFullKey(bootstrapConfig, "application.server")
    this.actualConfig = getForFullKey(bootstrapConfig, "application.config")
    this

  private def get(config: java.util.Map[String, AnyRef], key: String): java.util.Map[String, AnyRef] =
    config.get(key).asInstanceOf[java.util.Map[String, AnyRef]]

  private def getForFullKey(config: java.util.Map[String, AnyRef], fullKey: String): java.util.Map[String, AnyRef] =
    val keys      = fullKey.split("\\.")
    var configRef = config
    for key <- keys do
      configRef = get(configRef, key)
      if null == configRef then
        logger.debug(s"config is null for ${key} in ${fullKey}")
        throw new RuntimeException(s"config is null for ${key} in ${fullKey}")
    configRef

  def get[T](classType: Class[T]): T =
    val configurationProperty = classType.getAnnotation(classOf[ConfigurationProperty])
    mapper.convertValue(getForFullKey(actualConfig, configurationProperty.name()), classType)

  // TODO add another method for fetching proper value from environment_value -> config value -> default value with this order
  // TODO pattern will be like ${ENVIRONMENT_VARIABLE}|value.from.config|default_value
  def get[T](key: String, classType: Class[T]): T =
    classType.cast(actualConfig.get(key))

end ConfigLoader

object ConfigLoader:
  private val configLoader: ConfigLoader = new ConfigLoader().build("/src/main/resources/bootstrap.yaml")
  def instance: ConfigLoader           = configLoader
end ConfigLoader
