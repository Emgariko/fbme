<?xml version="1.0" encoding="UTF-8"?>
<solution name="org.fbme.debugger" uuid="15002a50-4ccf-4200-98db-35093fe08692" moduleVersion="0" pluginKind="PLUGIN_OTHER" compileInMPS="true">
  <models>
    <modelRoot contentPath="${module}" type="default">
      <sourceRoot location="models" />
    </modelRoot>
    <modelRoot contentPath="${module}" type="java_classes">
      <sourceRoot location="lib/animation-core-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/animation-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/debugger.jar" />
      <sourceRoot location="lib/desktop-jvm-1.0.0-beta5.jar" />
      <sourceRoot location="lib/foundation-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/foundation-layout-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/kotlinx-coroutines-core-jvm-1.5.2.jar" />
      <sourceRoot location="lib/kotlinx-coroutines-swing-1.5.2.jar" />
      <sourceRoot location="lib/material-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/material-icons-core-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/material-ripple-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/runtime-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/runtime-saveable-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/skiko-jvm-0.5.9.jar" />
      <sourceRoot location="lib/skiko-jvm-runtime-macos-arm64-0.5.9.jar" />
      <sourceRoot location="lib/ui-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/ui-geometry-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/ui-graphics-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/ui-text-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/ui-tooling-preview-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/ui-unit-desktop-1.0.0-beta5.jar" />
      <sourceRoot location="lib/ui-util-desktop-1.0.0-beta5.jar" />
    </modelRoot>
  </models>
  <facets>
    <facet type="java">
      <classes generated="true" path="${module}/classes_gen" />
    </facet>
  </facets>
  <stubModelEntries>
    <stubModelEntry path="${module}/lib/animation-core-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/animation-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/desktop-jvm-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/foundation-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/foundation-layout-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/kotlinx-coroutines-core-jvm-1.5.2.jar" />
    <stubModelEntry path="${module}/lib/kotlinx-coroutines-swing-1.5.2.jar" />
    <stubModelEntry path="${module}/lib/material-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/material-icons-core-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/material-ripple-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/runtime-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/runtime-saveable-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/skiko-jvm-0.5.9.jar" />
    <stubModelEntry path="${module}/lib/skiko-jvm-runtime-macos-arm64-0.5.9.jar" />
    <stubModelEntry path="${module}/lib/ui-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/ui-geometry-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/ui-graphics-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/ui-text-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/ui-tooling-preview-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/ui-unit-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/ui-util-desktop-1.0.0-beta5.jar" />
    <stubModelEntry path="${module}/lib/debugger.jar" />
  </stubModelEntries>
  <sourcePath />
  <dependencies>
    <dependency reexport="false">6354ebe7-c22a-4a0f-ac54-50b52ab9b065(JDK)</dependency>
    <dependency reexport="false">742f6602-5a2f-4313-aa6e-ae1cd4ffdc61(MPS.Platform)</dependency>
    <dependency reexport="false">498d89d2-c2e9-11e2-ad49-6cf049e62fe5(MPS.IDEA)</dependency>
    <dependency reexport="false">292e01a6-3380-40ca-8417-b86844de2d63(org.fbme.ide.platform)</dependency>
    <dependency reexport="false">8865b7a8-5271-43d3-884c-6fd1d9cfdd34(MPS.OpenAPI)</dependency>
    <dependency reexport="false">6594f340-4d73-4027-b7d3-c6ca2e70a53b(org.fbme.ide.iec61499.lang)</dependency>
    <dependency reexport="false">6ed54515-acc8-4d1e-a16c-9fd6cfe951ea(MPS.Core)</dependency>
    <dependency reexport="false">1db6de07-b355-4c0f-9979-75b4ac1e8215(org.fbme.lib)</dependency>
    <dependency reexport="false">5aff85f5-c1e8-49b6-a1f1-66d79702cceb(org.fbme.ide.iec61499.adapter)</dependency>
  </dependencies>
  <languageVersions>
    <language slang="l:f3061a53-9226-4cc5-a443-f952ceaf5816:jetbrains.mps.baseLanguage" version="11" />
    <language slang="l:443f4c36-fcf5-4eb6-9500-8d06ed259e3e:jetbrains.mps.baseLanguage.classifiers" version="0" />
    <language slang="l:fd392034-7849-419d-9071-12563d152375:jetbrains.mps.baseLanguage.closures" version="0" />
    <language slang="l:83888646-71ce-4f1c-9c53-c54016f6ad4f:jetbrains.mps.baseLanguage.collections" version="1" />
    <language slang="l:f2801650-65d5-424e-bb1b-463a8781b786:jetbrains.mps.baseLanguage.javadoc" version="2" />
    <language slang="l:ceab5195-25ea-4f22-9b92-103b95ca8c0c:jetbrains.mps.lang.core" version="2" />
    <language slang="l:446c26eb-2b7b-4bf0-9b35-f83fa582753e:jetbrains.mps.lang.modelapi" version="0" />
    <language slang="l:28f9e497-3b42-4291-aeba-0a1039153ab1:jetbrains.mps.lang.plugin" version="5" />
    <language slang="l:ef7bf5ac-d06c-4342-b11d-e42104eb9343:jetbrains.mps.lang.plugin.standalone" version="0" />
    <language slang="l:7866978e-a0f0-4cc7-81bc-4d213d9375e1:jetbrains.mps.lang.smodel" version="18" />
    <language slang="l:9ded098b-ad6a-4657-bfd9-48636cfe8bc3:jetbrains.mps.lang.traceable" version="0" />
  </languageVersions>
  <dependencyVersions>
    <module reference="3f233e7f-b8a6-46d2-a57f-795d56775243(Annotations)" version="0" />
    <module reference="6354ebe7-c22a-4a0f-ac54-50b52ab9b065(JDK)" version="0" />
    <module reference="6ed54515-acc8-4d1e-a16c-9fd6cfe951ea(MPS.Core)" version="0" />
    <module reference="1ed103c3-3aa6-49b7-9c21-6765ee11f224(MPS.Editor)" version="0" />
    <module reference="498d89d2-c2e9-11e2-ad49-6cf049e62fe5(MPS.IDEA)" version="0" />
    <module reference="8865b7a8-5271-43d3-884c-6fd1d9cfdd34(MPS.OpenAPI)" version="0" />
    <module reference="742f6602-5a2f-4313-aa6e-ae1cd4ffdc61(MPS.Platform)" version="0" />
    <module reference="ceab5195-25ea-4f22-9b92-103b95ca8c0c(jetbrains.mps.lang.core)" version="0" />
    <module reference="15002a50-4ccf-4200-98db-35093fe08692(org.fbme.debugger)" version="0" />
    <module reference="5aff85f5-c1e8-49b6-a1f1-66d79702cceb(org.fbme.ide.iec61499.adapter)" version="0" />
    <module reference="6594f340-4d73-4027-b7d3-c6ca2e70a53b(org.fbme.ide.iec61499.lang)" version="0" />
    <module reference="292e01a6-3380-40ca-8417-b86844de2d63(org.fbme.ide.platform)" version="0" />
    <module reference="2046780a-246e-4cb0-90fe-56a2b7d92c54(org.fbme.ide.st.lang)" version="0" />
    <module reference="1db6de07-b355-4c0f-9979-75b4ac1e8215(org.fbme.lib)" version="0" />
  </dependencyVersions>
</solution>

