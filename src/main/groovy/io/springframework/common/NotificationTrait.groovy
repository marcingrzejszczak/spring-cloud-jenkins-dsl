package io.springframework.common

/**
 * @author Marcin Grzejszczak
 */
trait NotificationTrait {

	void appendSlackNotification(Node rootNode) {
		Node propertiesNode = rootNode / 'properties'
		def slack = propertiesNode / 'jenkins.plugins.slack.SlackNotifier_-SlackJobProperty'
		(slack / 'startNotification').setValue(true)
		(slack / 'notifySuccess').setValue(true)
		(slack / 'notifyAborted').setValue(true)
		(slack / 'notifyNotBuilt').setValue(true)
		(slack / 'notifyUnstable').setValue(true)
		(slack / 'notifyFailure').setValue(true)
		(slack / 'notifyBackToNormal').setValue(true)
		(slack / 'notifyRepeatedFailure').setValue(true)
		(slack / 'includeTestSummary').setValue(true)
		(slack / 'showCommitList').setValue(true)
		def publishers = (rootNode / 'publishers')
		publishers / 'jenkins.plugins.slack.SlackNotifier'
	}

}
