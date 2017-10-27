JIRA TestKit
============

The JIRA TestKit was created to make it easier for plugin developers to set
up integration tests for JIRA. 

Using the TestKit
-----------------

The TestKit has two components: the `jira-testkit-plugin` and the
`jira-testkit-client`. The plugin component is a JIRA plugin that should be
installed when running integration tests, and the client component contains the
classes that you will interact with in your test code.

### Install the plugin

Using the [Atlassian Plugin SDK] [amps], simply add a `pluginArtifact` to the
`maven-jira-plugin` configuration. This ensures that the plugin gets installed
during integration testing.

        <plugin>
            <groupId>com.atlassian.maven.plugins</groupId>
            <artifactId>maven-jira-plugin</artifactId>
            <configuration>
                <pluginArtifacts>
                    <pluginArtifact>
                        <groupId>com.atlassian.jira.tests</groupId>
                        <artifactId>jira-testkit-plugin</artifactId>
                        <version>${testkit.version}</version>
                    </pluginArtifact>
                </pluginArtifacts>
            </configuration>
        <plugin>

Also add `jira-testkit-client` to your project dependencies so that the
`*Control` classes are made available to your test code.

        <dependency>
            <groupId>com.atlassian.jira.tests</groupId>
            <artifactId>jira-testkit-client</artifactId>
            <version>${testkit.version}</version>
            <scope>test</scope>
        </dependency>

Remember to use `scope=test` for this dependency.

### Call TestKit classes in your tests

The main entry point is the `Backdoor` class. Here is an example of an
integration test that disabled WebSudo and enabled Subtasks during its setup.

        public class MyPluginTest extends FuncTestCase {
            @Override
            protected void setUpTest() {
                super.setUpTest();
                Backdoor testKit = new Backdoor(new TestKitLocalEnvironmentData());
                testKit.restoreBlankInstance(TimeBombLicence.LICENCE_FOR_TESTING);
                testKit.usersAndGroups().addUser("test-user");
                testKit.websudo().disable();
                testKit.subtask().enable();
            }

            // ...
        }

There's a lot more things that the TestKit can do. Have a look at the code!

Compatibility across JIRA versions
----------------------------------

The TestKit version numbering scheme mirrors the JIRA version that it is
compatible with. For example, TestKit 5.0 is compatible with JIRA 5.0. But
because the TestKit builds on the [official JIRA Java API] [jiraapi], TestKit
5.0 is also compatible with any 5.x version of JIRA, as per the
[Java API Policy for JIRA] [promise]. This means you can use TestKit 5.0 in
your integration tests that run against JIRA 5.0, JIRA 5.1, and JIRA 5.2, with
no additional modifications.


  [amps]: https://developer.atlassian.com/display/DOCS/Atlassian+Plugin+SDK+Documentation
  [jiraapi]: https://developer.atlassian.com/static/javadoc/jira/5.0/reference/packages.html
  [promise]: https://developer.atlassian.com/display/JIRADEV/Java+API+Policy+for+JIRA

## Branches
- master - JIRA 7.6+
- testkit_for_jira_7_2 - JIRA 7.2 - 7.5


# Development

This project requires JDK 8 in order to build without error. Maven builds will appear to succeed on JDK 7, but the logs will contain a stacktrace about "unsupported major.minor version 52.0".

## Continuous Integration ##
* There is a Bamboo [CI plan](https://jira-bamboo.internal.atlassian.com/browse/JT-TC) on JBAC (only accessible within Atlassian). Create any feature branches from this plan.

## Releasing ##
* Releasing is via another [Bamboo plan](https://jira-bamboo.internal.atlassian.com/browse/JT-TR) on JBAC. This plan has branches for each supported JIRA version. Do not create feature branches of this plan.