import caliban.codegen.CalibanKeys.calibanSetting
import caliban.tools.Codegen.GenType
import sbt.file

object Settings {
  lazy val calibanSettings = Seq(
    calibanSetting(file("schema.graphql"))(fs =>
      fs.packageName("com.github.nikodemin.cdms.api")
        .scalarMapping("ID" -> "Long", "OffsetDateTime" -> "java.time.OffsetDateTime")
        .genType(GenType.Schema)
    )
  )
  lazy val calibanSourcesRoot = file("conf/api/graphql/")
}
