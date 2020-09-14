package scala.utils

import java.util.concurrent.TimeUnit

import cats.effect.IO
import fs2.Stream
import fs2.concurrent.Queue
import io.chrisdavenport.log4cats.Logger
import utils.Components._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

class Simulation(mainGate: Queue[IO, Operation],
                 validation: Queue[IO, Operation],
                 scanner: Queue[IO, Operation],
                 regulationExport: Queue[IO, Operation],
                 exportTerminal: Queue[IO, Operation],
                 boarding: Queue[IO, Operation])(implicit logger: Logger[IO]) {
  implicit val timer = IO.timer(ExecutionContext.global)

  /** Creating a stream of elements : Vehicles */
  val streamVehicle = Stream.eval(createVehiclewithIO)

  /** Scheduling emitting Vehicles each 10 seconds */
  val scheduledStream = Stream.fixedDelay(10.seconds) >> streamVehicle

  def enterMainGate: Stream[IO, Unit] = {
    scheduledStream
      .map { v =>
        val entryTime = timer.clock.realTime(TimeUnit.SECONDS)
        val serviceName = "Main Gate Operations"
        Operation(v.id, v.category, serviceName, entryTime, None)
      }
      .through(mainGate.enqueue)
      .evalMap(_ => logger.info("Vehicle  Entered Main Gate Operations"))
      .evalTap(_ => timer.sleep(Random.between(26, 90).seconds))
  }

  def leaveMainGate: Stream[IO, Operation] = {
    mainGate.dequeue
      .evalTap(n => logger.info(s"Vehicle ${n.id} Left ${n.serviceName}"))
  }

  def enterPrevalidation(s: Stream[IO, Operation]): Stream[IO, Unit] = {
    s.map { v =>
        val entryTime = timer.clock.realTime(TimeUnit.SECONDS)
        val serviceName = "Prevalidation"
        Operation(v.id, v.category, serviceName, entryTime, None)
      }
      .through(validation.enqueue)
      .evalMap(_ => logger.info(s"Vehicle starting prevalidation procedures "))
      .evalTap(_ => timer.sleep(Random.between(3, 20).seconds))
  }

  def finishPrevalidation: Stream[IO, Operation] = {
    validation.dequeue
      .map { v =>
        val exitTime = timer.clock.realTime(TimeUnit.SECONDS)

        Operation(v.id, v.category, v.serviceName, v.entryTime, Some(exitTime))
      }
      .evalTap(
        v => logger.info(s"Vehicle ${v.id} finished prevalidation procedures ")
      )
  }

  def enterScanner(s: Stream[IO, Operation]): Stream[IO, Unit] = {
    s.map { v =>
        val entryTime = timer.clock.realTime(TimeUnit.SECONDS)
        Operation(v.id, v.category, v.serviceName, entryTime, None)
      }
      .through(scanner.enqueue)
      .evalMap(_ => logger.info(s"Vehicle Entered Scanner "))
      .evalTap(_ => timer.sleep(Random.between(20, 120).seconds))
  }

  def leaveScanner: Stream[IO, Operation] = {
    scanner.dequeue
      .map { v =>
        val exitTime = timer.clock.realTime(TimeUnit.SECONDS)
        Operation(v.id, v.category, v.serviceName, v.entryTime, Some(exitTime))
      }
      .evalTap(v => logger.info(s"Vehicle ${v.id} Left ${v.serviceName}"))
  }

  def startAdministrativeRegulation(
    s: Stream[IO, Operation]
  ): Stream[IO, Unit] = {
    s.map { v =>
        val entryTime = timer.clock.realTime(TimeUnit.SECONDS)
        val serviceName = "Regulation export"
        Operation(v.id, v.category, serviceName, entryTime, None)
      }
      .through(regulationExport.enqueue)
      .evalMap(_ => logger.info(s"Vehicle entered Regulation export"))
      .evalTap(_ => timer.sleep(Random.between(8, 45).seconds))
  }

  def finishAdministrativeRegulation: Stream[IO, Operation] = {
    regulationExport.dequeue
      .map { v =>
        val exitTime = timer.clock.realTime(TimeUnit.SECONDS)
        Operation(v.id, v.category, v.serviceName, v.entryTime, Some(exitTime))
      }
      .evalTap(v => logger.info(s"Vehicle ${v.id} Left ${v.serviceName}"))
  }

  def enterExportTerminal(s: Stream[IO, Operation]): Stream[IO, Unit] = {
    s.map { v =>
        val entryTime = timer.clock.realTime(TimeUnit.SECONDS)
        val serviceName = "Export Terminal"
        Operation(v.id, v.category, serviceName, entryTime, None)
      }
      .through(exportTerminal.enqueue)
      .evalMap(_ => logger.info(s"Vehicle Entered Export Terminal "))
      .evalTap(_ => timer.sleep(Random.between(8, 10).seconds))
  }

  def leaveExportTerminal: Stream[IO, Operation] = {
    exportTerminal.dequeue
      .map { v =>
        val exitTime = timer.clock.realTime(TimeUnit.SECONDS)
        val serviceName = "Export Terminal"
        Operation(v.id, v.category, serviceName, v.entryTime, Some(exitTime))
      }
      .evalTap(v => logger.info(s"Vehicle ${v.id} Left ${v.serviceName}"))
  }

  def startBoarding(s: Stream[IO, Operation]): Stream[IO, Unit] = {
    s.map { v =>
        val entryTime = timer.clock.realTime(TimeUnit.SECONDS)
        val serviceName = "Boarding"
        Operation(v.id, v.category, serviceName, entryTime, None)
      }
      .through(boarding.enqueue)
      .evalTap(_ => logger.info(s"Vehicle is boarding"))
      .evalTap(_ => timer.sleep(Random.between(100, 500).seconds))
  }

  def finishBoarding: Stream[IO, Unit] = {
    boarding.dequeue
      .map { v =>
        val exitTime = timer.clock.realTime(TimeUnit.SECONDS)
        Operation(v.id, v.category, v.serviceName, v.entryTime, Some(exitTime))
      }
      .evalMap(v => logger.info(s"Vehicle ${v.id} On Board"))
  }

}
