package scala.utils

import cats.effect.IO
import com.softwaremill.id.pretty.{PrettyIdGenerator, StringIdGenerator}
import scala.util.Random

object Components {

  /** A sequence of possible values for vehicle types */
  /** Randomly getting a vehicle category from the defined vehicle category */
  def randomVehicleCategory: String = {
    val vehicleCategory: Seq[String] = Seq("Car", "Truck")
    val random = new Random()
    vehicleCategory(random.nextInt(vehicleCategory.length))
  }

  /** Generates unique identifiers to identify vehicles in the simulation */
  def generateIdVehicule: String = {
    val generator: StringIdGenerator = PrettyIdGenerator.singleNode
    generator.nextId()
  }

  /** Creating a Vehicle  */
  def createVehiclewithIO: IO[Vehicle] =
    IO {
      Vehicle(generateIdVehicule, randomVehicleCategory)
    }

  /** Defining the object vehicle */
  case class Vehicle(id: String, category: String)

  /** Defining the object Operation comprising the full information about the transaction */
  case class Operation(id: String,
                       category: String,
                       serviceName: String,
                       entryTime: IO[Long],
                       exitTime: Option[IO[Long]])
}
