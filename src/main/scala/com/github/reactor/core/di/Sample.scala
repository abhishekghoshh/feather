package com.github.reactor.core.di

import com.github.reactor.core.di.annotations.*

@Component
class Sample @Inject() (@Inject name: Dependent, @Inject name2: Dependent):

  def this() =
    this(null, null)
  

  @Inject
  private var anotherName: Dependent = _

  @Inject
  def set(@Inject name: Dependent): Unit =
    this.anotherName = name

  @PostConstruct
  private def build(): Unit =
    println(s"I will be in post construct in ${getClass}")
end Sample

@Component
class Dependent(@Inject dependent1: Dependent2):

  @Inject
  private var dependent2: Dependent2 = _

  private var dependent3: Dependent2 = _

  @Inject
  private def set(@Inject dependent: Dependent2): Unit =
    this.dependent3 = dependent

  @PostConstruct
  private def build(): Unit =
    println(s"I will be in post construct in ${getClass} with " +
      s"3 dependent2 injected reference => $dependent1 $dependent2 $dependent3")
end Dependent

@Component
class Dependent2(@Value("name") val name: String):
  @PostConstruct
  private def build(): Unit =
    println(s"I will be in post construct in ${getClass} with name ${name}")
end Dependent2

@ConfigurationProperty("data.user")
case class User(name: String, age: Int)

@Configuration
class SampleConfiguration:

  @Bean
  def customDependent1(@Inject d2: Dependent2): Dependent =
    new Dependent(d2)

  @Bean
  def customDependent2: Dependent2 =
    new Dependent2("abhishek")
end SampleConfiguration
