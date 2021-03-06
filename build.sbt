import sbtrelease._
import ReleasePlugin._
import ReleaseStateTransformations._
import scalariform.formatter.preferences._

sbtPlugin := true
name := "sbt-docker-compose"
organization := "com.jacum"
scalaVersion := "2.12.8"
sbtVersion := "1.0.0"

libraryDependencies += {
  val liftJsonVersion = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n < 12 => "2.5.4"
    case _ => "3.0.1"
  }
  "net.liftweb" %% "lift-json" % liftJsonVersion
}

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.15",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test")

//publishTo := {
//  val nexus = "https://oss.sonatype.org"
//  if (isSnapshot.value)
//    Some("snapshots" at s"$nexus/content/repositories/snapshots")
//  else
//    Some("releases" at s"$nexus/service/local/staging/deploy/maven2")
//}

publishMavenStyle := true
publishTo := Some("Artifactory Realm" at "https://repo.dhlparcel.nl/artifactory/dhlparcel-sbt-local")
credentials += Credentials(Path.userHome / ".sbt" / ".artifactory-credentials")
publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

// useGpg := true

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := {
  <url>http://github.com/jacum/sbt-docker-compose</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://opensource.org/licenses/BSD-3-Clause</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jacum/sbt-docker-compose.git</url>
      <connection>scm:git:git@github.com:jacum/sbt-docker-compose.git</connection>
    </scm>
    <developers>
      <developer>
        <id>kurt.kopchik@tapad.com</id>
        <name>Kurt Kopchik</name>
        <url>http://github.com/kurtkopchik</url>
      </developer>
      <developer>
        <id>tim@pragmasoft.nl</id>
        <name>Tim Evdokimov</name>
        <url>http://github.com/jacum</url>
      </developer>
    </developers>
  }


releaseNextVersion := { (version: String) => Version(version).map(_.bumpBugfix.asSnapshot.string).getOrElse(versionFormatError) }

releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  releaseStepCommandAndRemaining("^test"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("^publishSigned"),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)
