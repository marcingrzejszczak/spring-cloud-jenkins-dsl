import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// Jobs
['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].eachWithIndex { String projectName, Integer index ->
	new CompatibilityBuildMaker(dsl).build(projectName, everyDayAt(index))
}
new ConsulCompatibilityBuildMaker(dsl).build('spring-cloud-consul')

// Functions
String everyDayAt(int offset, int startingHour = 5) {
	return "0 0 ${startingHour + offset} 1/1 * ? *"
}

