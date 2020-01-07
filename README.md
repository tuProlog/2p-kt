# 2P in Kotlin

![The 2P logo](./logo.svg)

tuProlog (2P henceforth) is a Kotlin multi-platform ecosystem for Symbolic Artificial Intelligence (AI).
It consists of a number of incrementally inter-dependent modules aimed at supporting symbolic manipulation and reasoning in an extensible and flexible way.

Currently, 2P focuses on supporting knowledge representation and automatic reasoning through logic programming, by featuring:

* a module for logic terms representation, namely `core`

* a module for logic unification representation, namely `unify`

* a module for in-memory indexing and storing logic theories, namely `theory`

* a module providing ISO Prolog resolution of logic queries, namely `solve`, coming with two implementations (i.e. `solve-classic` and `solve-streams`)
    
* a number of modules (i.e., the many `dsl-*` modules) supporting a Prolog-like, Domain Specific Language (DSL) aimed at bridging the logic programming with the Kotlin object-oriented \& functional environment
    
However, the modular, unopinionated architecture of 2P is deliberately aimed at supporting and encouraging extensions towards other sorts of symbolic AI systems than Prolog---such as ASP, tabled-Prolog, Problog, etc.

Furthermore, 2P is developed as in _pure_, __multi-platform__ Kotlin project. 
This brings two immediate advantages:
1. it virtually supports several platforms, there including JVM, JS, Android, and Native
2. it consists of a very minimal and lightweight library, only leveraging on the Kotlin _common_ library, as it cannot commit to any particular platform standard library

<!-- 
## Overview

![Project Map](http://www.plantuml.com/plantuml/svg/dLTBSnCv4BxFhr3bagGsJW57bsC83xQsO2cAP7CWoK6UQTjQDDAWHpOkDlzzzHWt6eqof9P36Edx-j7gRgdzIXkgZ6rvPPZXG5vqX0doGHhEH5LrjFv6Dq1ggO3yg2f2Y8CDg3MjWLo2QjqkwV_zHfZ-NKahcZbvBIK7Age4XE-MSdqapHRKFCWU8o-XRQdUlf4D73dq3s5I3TeeMnkhAEUxTzFQ4X5McsbwQc8J_B-KHzgkvkJ2hhHXnXecnN7A0ZD5I9YBInBDBKbDjN6AZzsQf2PhBeMBDj86Y94NpdsnyM3y2k13-2ka0NBWF26KCwf1e-ytmkyoJTPI4H1qnPqaSP7V22_epLodZj_U2BqJXjw7p9i3GdKWVP86l2bSa1c7jPdvPPNNmXifjA723EhXZd8BSt8kTj7nNEYAgW5Vq1OmrEI4PDjnCF0fXGTbTowasBNIwYof8uiNeOIoedrlHMsaEZUoPJLg88DSh2Edu1rLpEn6jKhLHhl8bQAxHSX_Xdrasio9_rk4B-3xdBW_5aGDFIChG2OQk0zs-zefH_p9jg1OFGnPcAkOcjTiunHiG2Le0pLhAV_9U1itgYmbLC6btBTKRoOsRT4J3y16KJug4VG3l2D5sI-Go-NUbULyJiToMJib_HHWSdQsp_UD2EdrEy_yzqRxp-jRtoHUEJO9BiXCiStMpFnwT64gQxPPyRazm9eRbYLb7949Lm-DKt5u8xotFpVNb6iuz7v6itj7SOMUXetSrNTjPtRErIcxrxSZgpyUeiaWZYPnrTyWOLsqU07cyNIHCtBmu-40NHgB-twQbdUfAyjdebDndnuIAXg2bV42Jm4F9cCyyuSIJE2Pn0KUYL-ExrJRSPYEv_IWU1KHvrKvplzpmdZ5_-sxWaanhU7aIJx5zsxnxwoH_uXPVpEF0NnP2f1_Fu8k926slv0UGR4xzpo4mbLJHF02YuBHuKnb2V2fBGBfWPO8FbSLNYwu7Jkui9-qLVZqYEtwVVHY3A4Pnq7hcLY6nTACJWFkv_fmVEJK-WbbiVW2jQ5Yms6vt5ykMP5vAjPyuVd5AflF3ggCjGNQpFjJV9bXAzdiadSogKDiyN0T4hdUEp7G83kV9yg34KH0D46s5CeNcpPliHsc0iYWqK3XvAyz_oI4Tfj5bVdobYce3RlxXKFF9q_K4OJb_2cEFS6bznG7iLAMo5ciyrEvFdBcuRYaBM-Oc3Z4-pNY8IMFWdX6A1_uI0WUFp9N_acxnOSJSXNdk9fJhGS_3XPNL9qGdFG669ajlNnLEQtxZh62hTtpcrdvo-qo6CxCjNJmlPaFL5O9eTtO8_Cy7ke9CI_mvqygy38LeARUbaL6Oqfqj4ps_qMz3sbePWjIZMExn6tKhIm7tG_9FXFB16lwP241ivUmuiM3b9ZmmI3TSQkFeaVpteNZ_KTEYyhcVTtyf6N9e4patEB98JVjiOIPZVkvHlnrW64D-9dMCJkXKwr1cVF70XmpXygHDo9-VRBAhxzMgS0eMfi9bHcph7BCE-TyMcb6BklD1iVHev9hD7x9G1EgWB2sjOQkECmH1rflIGjcAnjlsohXtprAaD2RTJvPolck9sd4gRD_1tpVRolBk26h7K42DCmudPv0f83mFuAzPQO94tVo6pJ4Tb9KwPkNDhMM7D-MPDRqkweVbpw-ZKlY1uzbZwKwEghqMCTd9RdSVFQO6i_d9Uv7k2zGeoLluTJVvgTBic_x1ZeNaYOkl7ZjdjhEmRLKC00ZlqS3Sm378pWjAqOmHZd0NhaFs_B_0000)
-->

## Users

2P modules are currently available through an _ad-hoc_ [Maven repository](https://bintray.com/pika-lab/tuprolog) for JVM and Kotlin users. 
NPM modules will be deployed soon, making 2P easily available for JS users as well.

### Gradle

To import the 2P module named `2P_MODULE` (version `2P_VERSION`) into your Gradle-based project, you must setup your Maven repositories first:
```kotlin
// assumes Gradle's Kotlin DSL
repositories {
    mavenCentral()
    maven("https://bintray.com/pika-lab/tuprolog")
}
``` 
and then declare the desired dependency:
 ```kotlin
// assumes Gradle's Kotlin DSL
dependencies {
    implementation("it.unibo.tuprolog", "2P_MODULE", "2P_VERSION")
}
 ``` 
Notice that dependencies of `2P_MODULE` should be automatically imported 
