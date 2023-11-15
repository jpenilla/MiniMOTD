plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(libs.indraCommon)
  implementation(libs.indraLicenser)
  implementation(libs.shadow)
  implementation(libs.mod.publish.plugin)
  implementation(libs.hangarPublishPlugin)

  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
