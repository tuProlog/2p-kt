# 2P in Kotlin

Some quick links:
* [GitLab Repository](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin) (the one used by developers)
* [GitHub Repository](https://github.com/tuProlog/2p-kt) (where JVM releases are hosted)
* [NPM Repository](https://www.npmjs.com/org/tuprolog) (where JS releases are hosted)
* [Maven Repository](https://bintray.com/pika-lab/tuprolog) (where all releases are hosted)

## Intro

![The 2P logo](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/raw/master/logo.png)

[tuProlog](https://www.cs.nmsu.edu/ALP/2013/10/tuprolog-making-prolog-ubiquitous/) (2P henceforth) is multi-paradigm 
logic programming framework written in Java.

2P-Kt is a Kotlin-based and multi-platform reboot of 2P.
It aims at becoming an open ecosystem for Symbolic Artificial Intelligence (AI).
For this reason, 2P-Kt consists of a number of incrementally inter-dependent modules aimed at supporting symbolic 
manipulation and reasoning in an extensible and flexible way.

Currently, 2P-Kt focuses on supporting knowledge representation and automatic reasoning through logic programming, 
by featuring:

* a module for logic terms representation, namely `core`,

* a module for logic unification representation, namely `unify`,

* a module for in-memory indexing and storing logic theories, namely `theory`,

* a module providing ISO Prolog resolution of logic queries, namely `solve`, coming with two implementations 
(i.e. `solve-classic` and `solve-streams`),
    
* a number of modules (i.e., the many `dsl-*` modules) supporting a Prolog-like, Domain Specific Language (DSL) 
aimed at bridging the logic programming with the Kotlin object-oriented \& functional environment,

* two parsing modules: one aimed at parsing terms, namely `parser-core`, and the other aimed at parsing theories, 
namely `parser-theory`.

A complete overview about modules and their dependencies is provided by the following diagram: 

![2P-Kt project map](https://www.plantuml.com/plantuml/svg/TP31Rjim38RlV0eYT-q1XY0ePfy6332GPATTD8k9jSgY8SenWdNlFbjQNSLhBpRuVp_vYtoIg4CSUmUH1uoCFpb6xj7OG6sqx46UhHzqq3rAfmrFrb_nefqG5EZ2pb30tu3uHRVFry2ZDnKx3lkz7YooT_V30TdP6xtd2SnnvdToZgVt3BOVt2Sq5BLrit7gR2Ju0_0lUDToe1s-3bhbqTlBVRUMiICEHMt4gTof1UlgGK-j6PmVG1wIoMabmkuspodNVMIgTLh4jgdnM6sWn42wbmoFAnnq40flRsogBVfrwtUulK-oVlt-xJ-pVEJTHfPRDYU0vGMuVr669wymtGO_ew61l94VdZraRRNaVaY_GaxRGlekebekKLP7WBf25UorY-hbW4ikrET2IJb96eUbhYkeJmNFYREs6ivBiLUluT3C0OukR_ERKnBAzcPjRwhSeGxffFFMev0aAMKc_GXFvCtOkxy0)
    
The modular, unopinionated architecture of 2P-Kt is deliberately aimed at supporting and encouraging extensions towards 
other sorts of symbolic AI systems than Prolog---such as ASP, tabled-Prolog, Problog, etc.

Furthermore, 2P-Kt is developed as in _pure_, __multi-platform__ Kotlin project. 
This brings two immediate advantages:
1. it virtually supports several platforms, there including JVM, JS, Android, and Native
2. it consists of a very minimal and lightweight library, only leveraging on the Kotlin _common_ library, as it cannot 
commit to any particular platform standard library

<!-- 
## Overview

![Project Map](http://www.plantuml.com/plantuml/svg/dLTBSnCv4BxFhr3bagGsJW57bsC83xQsO2cAP7CWoK6UQTjQDDAWHpOkDlzzzHWt6eqof9P36Edx-j7gRgdzIXkgZ6rvPPZXG5vqX0doGHhEH5LrjFv6Dq1ggO3yg2f2Y8CDg3MjWLo2QjqkwV_zHfZ-NKahcZbvBIK7Age4XE-MSdqapHRKFCWU8o-XRQdUlf4D73dq3s5I3TeeMnkhAEUxTzFQ4X5McsbwQc8J_B-KHzgkvkJ2hhHXnXecnN7A0ZD5I9YBInBDBKbDjN6AZzsQf2PhBeMBDj86Y94NpdsnyM3y2k13-2ka0NBWF26KCwf1e-ytmkyoJTPI4H1qnPqaSP7V22_epLodZj_U2BqJXjw7p9i3GdKWVP86l2bSa1c7jPdvPPNNmXifjA723EhXZd8BSt8kTj7nNEYAgW5Vq1OmrEI4PDjnCF0fXGTbTowasBNIwYof8uiNeOIoedrlHMsaEZUoPJLg88DSh2Edu1rLpEn6jKhLHhl8bQAxHSX_Xdrasio9_rk4B-3xdBW_5aGDFIChG2OQk0zs-zefH_p9jg1OFGnPcAkOcjTiunHiG2Le0pLhAV_9U1itgYmbLC6btBTKRoOsRT4J3y16KJug4VG3l2D5sI-Go-NUbULyJiToMJib_HHWSdQsp_UD2EdrEy_yzqRxp-jRtoHUEJO9BiXCiStMpFnwT64gQxPPyRazm9eRbYLb7949Lm-DKt5u8xotFpVNb6iuz7v6itj7SOMUXetSrNTjPtRErIcxrxSZgpyUeiaWZYPnrTyWOLsqU07cyNIHCtBmu-40NHgB-twQbdUfAyjdebDndnuIAXg2bV42Jm4F9cCyyuSIJE2Pn0KUYL-ExrJRSPYEv_IWU1KHvrKvplzpmdZ5_-sxWaanhU7aIJx5zsxnxwoH_uXPVpEF0NnP2f1_Fu8k926slv0UGR4xzpo4mbLJHF02YuBHuKnb2V2fBGBfWPO8FbSLNYwu7Jkui9-qLVZqYEtwVVHY3A4Pnq7hcLY6nTACJWFkv_fmVEJK-WbbiVW2jQ5Yms6vt5ykMP5vAjPyuVd5AflF3ggCjGNQpFjJV9bXAzdiadSogKDiyN0T4hdUEp7G83kV9yg34KH0D46s5CeNcpPliHsc0iYWqK3XvAyz_oI4Tfj5bVdobYce3RlxXKFF9q_K4OJb_2cEFS6bznG7iLAMo5ciyrEvFdBcuRYaBM-Oc3Z4-pNY8IMFWdX6A1_uI0WUFp9N_acxnOSJSXNdk9fJhGS_3XPNL9qGdFG669ajlNnLEQtxZh62hTtpcrdvo-qo6CxCjNJmlPaFL5O9eTtO8_Cy7ke9CI_mvqygy38LeARUbaL6Oqfqj4ps_qMz3sbePWjIZMExn6tKhIm7tG_9FXFB16lwP241ivUmuiM3b9ZmmI3TSQkFeaVpteNZ_KTEYyhcVTtyf6N9e4patEB98JVjiOIPZVkvHlnrW64D-9dMCJkXKwr1cVF70XmpXygHDo9-VRBAhxzMgS0eMfi9bHcph7BCE-TyMcb6BklD1iVHev9hD7x9G1EgWB2sjOQkECmH1rflIGjcAnjlsohXtprAaD2RTJvPolck9sd4gRD_1tpVRolBk26h7K42DCmudPv0f83mFuAzPQO94tVo6pJ4Tb9KwPkNDhMM7D-MPDRqkweVbpw-ZKlY1uzbZwKwEghqMCTd9RdSVFQO6i_d9Uv7k2zGeoLluTJVvgTBic_x1ZeNaYOkl7ZjdjhEmRLKC00ZlqS3Sm378pWjAqOmHZd0NhaFs_B_0000)
-->

## Users

2P-Kt modules are currently available through an _ad-hoc_ [Maven repository](https://bintray.com/pika-lab/tuprolog) for JVM and Kotlin users. 
NPM modules will be deployed soon, making 2P easily available for JS users as well.

### Gradle

To import the 2P-Kt module named `2P_MODULE` (version `2P_VERSION`) into your Gradle-based project, you must setup your Maven repositories first:
```kotlin
// assumes Gradle's Kotlin DSL
repositories {
    mavenCentral()
    maven("https://dl.bintray.com/pika-lab/tuprolog/")
}
``` 
and then declare the desired dependency:
 ```kotlin
// assumes Gradle's Kotlin DSL
dependencies {
    implementation("it.unibo.tuprolog", "2P_MODULE", "2P_VERSION")
}
 ``` 
Notice that dependencies of `2P_MODULE` should be automatically imported. 

### Maven

To import the 2P-Kt module named `2P_MODULE` (version `2P_VERSION`) into your Maven-based project, you must setup your Maven repositories first:
```xml
<repositories>
    <repository>
        <id>bintray-2p-repo</id>
        <url>https://dl.bintray.com/pika-lab/tuprolog/</url>
    </repository>
</repositories>
``` 
and then declare the desired dependency:
 ```xml
<dependency>
    <groupId>it.unibo.tuprolog</groupId>
    <artifactId>2P_MODULE</artifactId>
    <version>2P_VERSION</version>
</dependency>
 ``` 
Notice that dependencies of `2P_MODULE` should be automatically imported. 

## Developers

Working with the 2P-Kt codebase requires a number of tools to be installed and properly configured on your system:
- JDK 12+ (please ensure the `JAVA_HOME` environment variable is properly) configured
- Kotlin 1.3.70+
- Gradle 6.2+ (please ensure the `GRADLE_HOME` environment variable is properly configured)
- Git 2.20+

### Develop 2P-Kt with IntelliJ Idea

To participate in the development of 2P-Kt, we suggest the [IntelliJ Idea](https://www.jetbrains.com/idea/download/) IDE. 
The free, _Community_ version will be fine. 

#### Recommended configuration
You will need the __Kotlin__ plugin for IntelliJ Idea. 
This is usually installed upon Idea's very first setup wizard.
However, one may easily late-install such plugin through the IDE's Plugins settings dialog.
To open such dialog, use <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>A</kbd>, then search for "Plugins"

#### Importing the project

1. Clone this repository in a folder of your preference using `git clone` appropriately

0. Open IntellJ Idea. 
If a project opens automatically, select "Close project". 
You should be on the welcome screen of IntelliJ idea, with an aspect similar to this image: 
![IntelliJ Welcome Screen](https://www.jetbrains.com/help/img/idea/2018.2/ideaWelcomeScreen.png)

0. Select "Import Project"

0. Navigate your file system and find the folder where you cloned the repository. 
**Do not select it**. 
Open the folder, and you should find a lowercase `2p-in-kotlin` folder. 
That is the correct project folder, created by `git` in case you cloned without specifying a different folder name. 
Once the correct folder has been selected, click <kbd>Ok</kbd>

0. Select "Import Project from external model"

0. Make sure "Gradle" is selected as external model tool

0. Click <kbd>Finish</kbd>

0. If prompted to override any `.idea` file, try to answer <kbd>No</kbd>. It's possible that IntelliJ refuses to proceed, in which case click <kbd>Finish</kbd> again, then select <kbd>Yes</kbd>

0. A dialog stating that "IntelliJ IDEA found a Gradle build script" may appear, in such case answer <kbd>Import Gradle Project</kbd>

0. Wait for the IDE to import the project from Gradle. The process may take several minutes, due to the amount of dependencies. Should the synchronization fail, make sure that the IDE's Gradle is configured correctly:

0. In 'Settings -> Build, Execution, Deployment -> Build Tools > Gradle', for the option 'Use Gradle from' select 'gradle-wrapper.properties file'. Enabling auto-import is also recommended

### Developing the project
Contributions to this project are welcome. Just some rules:

* We use [git flow](https://github.com/nvie/gitflow), so if you write new features, please do so in a separate `feature/` branch

* We recommend forking the project, developing your stuff, then contributing back via pull request directly from the Web interface

* Commit often. Do not throw pull requests with a single giant commit adding or changing the whole world. Split it in multiple commits and request a merge to the mainline often

* Stay in sync with the `develop` branch: pull often from `develop` (if the build passes), so that you don't diverge too much from the main development line

* Do not introduce low quality or untested code. Merge requests will be reviewed before merge.


#### Building the project
While developing, you can rely on IntelliJ to build the project, it will generally do a very good job.
If you want to generate the artifacts, you can rely on Gradle. Just point a terminal on the project's root and issue

```bash
./gradlew build
```

This will trigger the creation of the artifacts the executions of the tests, the generation of the documentation and of the project reports.

#### Versioning

The 2P project leverages on [Semantic Versioning](https://semver.org/) (SemVer, henceforth).

In particular, SemVer is enforced by the current Gradle configuration, which features [DanySK](https://github.com/DanySK)'s [Git sensitive SemVer Gradle Plugin](https://github.com/DanySK/git-sensitive-semantic-versioning-gradle-plugin).
This implies it is strictly forbidden in this project to create tags whose label is not a valid SemVar string.

Notice that the 2P project is still in its initial development stage---as proven by the major number equal to `0` in its version string.
According to SemVer, this implies anything _may_ change at any time, as the public API _should not_ be considered stable.

#### Issue tracking

If you meet some problem in using or developing 2P, you are encouraged to signal it through the project ["Issues" section](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/issues) on GitLab.