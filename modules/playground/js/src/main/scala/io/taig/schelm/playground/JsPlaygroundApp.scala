package io.taig.schelm.playground

import cats.effect.{ExitCode, IO, IOApp}
import io.taig.schelm.interpreter.JsHtmlSchelm
import org.scalajs.dom.document

object JsPlaygroundApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    JsHtmlSchelm
      .default[IO, Event]
      .flatMap { schelm =>
        val root = document.getElementById("main")
        PlaygroundApp.start(schelm, root)
      }
      .as(ExitCode.Success)
}
