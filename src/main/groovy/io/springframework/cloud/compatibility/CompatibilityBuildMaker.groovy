package io.springframework.cloud.compatibility

import io.springframework.common.NotificationTrait
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CompatibilityBuildMaker extends CompatibilityTasks implements NotificationTrait {
	private final DslFactory dsl

	CompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String projectName, String cronExpr) {
		dsl.job("${projectName}-compatibility-check") {
			triggers {
				cron cronExpr
			}
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/$projectName"
					}
					createTag(false)
				}
			}
			steps defaultSteps()
			configure {
				appendSlackNotification(it as Node)
			}
		}
	}

}
