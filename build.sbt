import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val CatsEffectVersion = "2.2.0"
val ColorVersion = "0.2.3-SNAPSHOT"
val Fs2Version = "2.4.4"
val JsoupVersion = "1.13.1"
val ScalaCollectionCompatVersion = "2.2.0"
val ScalajsDomVersion = "1.1.0"
val ShapelessVersion = "2.3.3"

noPublishSettings

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/core"))
  .settings(sonatypePublishSettings)
  .settings(
    libraryDependencies ++=
      "org.typelevel" %%% "cats-effect" % CatsEffectVersion ::
        "co.fs2" %%% "fs2-core" % Fs2Version ::
        Nil,
    name := "schelm-core"
  )
  .jvmSettings(
    libraryDependencies ++=
      "org.jsoup" % "jsoup" % JsoupVersion ::
        "org.scala-lang.modules" %% "scala-collection-compat" % ScalaCollectionCompatVersion ::
        Nil
  )
  .jsSettings(
    libraryDependencies ++=
      "org.scala-js" %%% "scalajs-dom" % ScalajsDomVersion ::
        Nil
  )

lazy val css = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/css"))
  .settings(sonatypePublishSettings)
  .settings(
    name := "schelm-css"
  )
  .dependsOn(core)

lazy val redux = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/redux"))
  .settings(sonatypePublishSettings)
  .settings(
    name := "schelm-redux"
  )
  .dependsOn(core)

lazy val dsl = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/dsl"))
  .settings(sonatypePublishSettings)
  .settings(
    libraryDependencies ++=
      "io.taig" %%% "color-core" % ColorVersion ::
        Nil,
    name := "schelm-dsl",
    Compile / sourceGenerators += Def.task {
      val html = (Compile / sourceManaged).value / "html.scala"
      IO.write(html, HtmlGenerator())
      List(html)
    }
  )
  .dependsOn(css, redux)

lazy val material = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/material"))
  .settings(sonatypePublishSettings)
  .settings(
    name := "schelm-material"
  )
  .dependsOn(dsl)

lazy val documentation = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("modules/documentation"))
  .settings(noPublishSettings)
  .settings(
    name := "schelm-documentation",
    packageSite := {
      val target = crossTarget.value / "site"
      val resources = (Compile / resourceDirectory).value
      val index = resources / "index.html" -> target / "index.html"
      val javascript = (Compile / fastOptJS).value.data -> target / "asset" / "javascript" / "main.js"
      IO.copy(List(index, javascript))
      target
    },
    scalaJSUseMainModuleInitializer := true
  )
  .dependsOn(material.js)
