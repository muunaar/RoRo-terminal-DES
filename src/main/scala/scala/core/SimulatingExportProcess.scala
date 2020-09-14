package scala.core

import cats.effect.ExitCase.Canceled
import cats.effect.{ExitCode, IO, IOApp}

import fs2.concurrent.Queue

import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import utils.Components.Operation
import utils.Simulation

object SimulatingExportProcess extends IOApp {

  def subcall(implicit logger: Logger[IO]): IO[Unit] = {

    val program = for {
      _ <- logger.info("Starting Simulation")
      mainGate <- Queue.bounded[IO, Operation](10)
      validation <- Queue.bounded[IO, Operation](10)
      scanner <- Queue.bounded[IO, Operation](10)
      regulation <- Queue.bounded[IO, Operation](10)
      exportTerminal <- Queue.bounded[IO, Operation](10)
      boarding <- Queue.bounded[IO, Operation](10)

      b = new Simulation(
        mainGate,
        validation,
        scanner,
        regulation,
        exportTerminal,
        boarding
      )(logger)
      _ <- b.enterMainGate.compile.drain.start
      _ <- b
        .enterPrevalidation(b.leaveMainGate)
        .compile
        .drain
        .start
      _ <- b
        .enterScanner(b.finishPrevalidation)
        .compile
        .drain
        .start
      _ <- b
        .startAdministrativeRegulation(b.leaveScanner)
        .compile
        .drain
        .start
      _ <- b
        .enterExportTerminal(b.finishAdministrativeRegulation)
        .compile
        .drain
        .start

      _ <- b
        .startBoarding(b.leaveExportTerminal)
        .compile
        .drain
        .start
      _ <- b.finishBoarding.compile.drain

    } yield ()
    program.as(ExitCode)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      logger <- Slf4jLogger.create[IO]
      x <- subcall(logger)
        .guaranteeCase {
          case Canceled =>
            logger
              .error("Simulation interrupted, releasing and exiting")
          case _ => logger.info("Normal Exit")
        }
        .as(ExitCode.Success)

    } yield x
  }

}
