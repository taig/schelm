package com.ayendo.schelm

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.ayendo.schelm.css._
import com.ayendo.schelm.dsl.Dsl
import org.scalajs.dom

object Playground extends IOApp with Dsl[Event] {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      main <- IO(dom.document.getElementById("main"))
      globals = Stylesheet.of(normalize)
      render <- Css.enable[IO, State, Event](globals, App.widget)
      _ <- Schelm.start(main)(State(), render, App.events, App.commands)
    } yield ExitCode.Success
}
