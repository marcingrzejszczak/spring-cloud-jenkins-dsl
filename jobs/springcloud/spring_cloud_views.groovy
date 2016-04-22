package springcloud

import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

defaultViews(dsl)

void defaultViews(DslFactory dsl) {
	dsl.nestedView('Spring Cloud') {
		views {
			listView('Compatibility Jobs') {
				jobs {
					regex('.*-compatibility-check')
				}
				columns {
					status()
					name()
					lastSuccess()
					lastFailure()
					lastBuildConsole()
					buildButton()
				}
			}
		}
	}

}