import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.helpers.step.StepContext

DslFactory dsl = this

// Jobs
['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].eachWithIndex { String projectName, Integer index ->
	defaultCompatibilitySteps(dsl, projectName, index)
}
consulBuild(dsl, 'spring-cloud-consul')


// Functions
String everyDayAt(int offset, int startingHour = 5) {
	return "0 0 ${startingHour + offset} 1/1 * ? *"
}

void defaultCompatibilitySteps(DslFactory dsl, String projectName, Integer offset = 0) {
	dsl.job("${projectName}-compatibility-check") {
		triggers {
			cron everyDayAt(offset)
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
	}
}

void consulBuild(DslFactory dsl, String projectName, Integer offset = 0) {
	dsl.job("${projectName}-compatibility-check") {
		triggers {
			cron everyDayAt(offset)
		}
		scm {
			git {
				remote {
					url "https://github.com/spring-cloud/$projectName"
				}
				createTag(false)
			}
		}
		steps {
			shell('''
					echo "Clearing consul data"
					rm -rf /tmp/consul
					rm -rf /tmp/consul-config
					''')
			shell('''
					echo "Install consul"
					./src/main/bash/travis_install_consul.sh

					echo "Run consul"
					./src/test/bash/travis_run_consul.sh
				''')
			steps defaultSteps()
			shell('''
					echo "Kill consul"
					kill -9 $(ps aux | grep '[c]onsul' | awk '{print $2}') && echo "Killed consul" || echo "Can't find consul in running processes"
					''')
		}
	}
}

Closure defaultSteps() {
	def springCloudBuildUrl = 'https://github.com/spring-cloud/spring-cloud-build'
	def springBootVersion = '1.4.0.BUILD-SNAPSHOT'
	def gistUrl = 'https://gist.githubusercontent.com/marcingrzejszczak/e63d4985f2a12d51af3310be51b2caa2/raw/c741bfea548b2b99fc242d743b1270301eab5167/replace_parent_version_in_pom.groovy'
	def groovyLocation = '/opt/groovy/2.4.5/bin/groovy'
	return buildStep {
		shell("""
					echo "Cloning spring-cloud-build"
					git clone $springCloudBuildUrl
					echo "Downloading and running script to change parent version"
					wget $gistUrl --no-check-certificate
					$groovyLocation replace_parent_version_in_pom.groovy -p "spring-cloud-build/pom.xml" -v "$springBootVersion"
					""")
		shell('''
					echo "Installing built version with different parent"
					./spring-cloud-build/mvnw clean install
					''')
		shell("""
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree
					""")
		shell('''
					echo -e "Running the tests"
					./mvnw clean verify -fae
					''')
	}
}

private Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
	return buildSteps
}