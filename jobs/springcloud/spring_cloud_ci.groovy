package springcloud

import io.springframework.cloud.ci.BenchmarksBuildMaker
import io.springframework.cloud.ci.DocsAppBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new BenchmarksBuildMaker(dsl).buildSleuth()
new DocsAppBuildMaker(dsl).buildDocs()