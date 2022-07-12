Feature: Verifies that unhandled exceptions in scheduled tasks are reported to Bugsnag regardless of Scheduler configuration

Scenario: By default a single-threaded ThreadPoolTaskScheduler is used
    When I run spring boot "ScheduledTaskScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "Unhandled exception from ScheduledTaskService"

Scenario: A TaskScheduler supplied as a bean is used
    Given I set environment variable "custom_task_scheduler_bean" to "true"
    And I run spring boot "ScheduledTaskScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "Unhandled exception from ScheduledTaskService"

Scenario: A ScheduledExecutorService supplied as a bean is used
    Given I set environment variable "scheduled_executor_service_bean" to "true"
    And I run spring boot "ScheduledTaskScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "Unhandled exception from ScheduledTaskService"

Scenario: For multiple ScheduledExecutorService beans, the one named "taskScheduler" is used
    Given I set environment variable "scheduled_executor_service_bean" to "true"
    And I set environment variable "other_scheduled_executor_service_bean" to "true"
    And I run spring boot "ScheduledTaskScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "Unhandled exception from ScheduledTaskService"

Scenario: For multiple TaskScheduler beans, the one named "taskScheduler" is used
    Given I set environment variable "custom_task_scheduler_bean" to "true"
    Given I set environment variable "second_task_scheduler_bean" to "true"
    And I run spring boot "ScheduledTaskScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "Unhandled exception from ScheduledTaskService"

Scenario: Scheduled tasks execute on a ScheduledExecutorService rather than a single-threaded Executor, when available
    Given I set environment variable "scheduled_executor_service_bean" to "true"
    And I run spring boot "ScheduledTaskExecutorScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.executor.multiThreaded" is true
