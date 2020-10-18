//package com.ft.jenkins.deployment
//
//import com.ft.jenkins.BasePipelineUnitTest
//import com.lesfurets.jenkins.unit.MethodCall
//import org.junit.Ignore
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//import static com.ft.jenkins.Hash.md5
//import static org.junit.jupiter.api.Assertions.assertEquals
//import static org.junit.jupiter.api.Assertions.assertFalse
//
//class DeploymentUtilPipelineUnitTest extends BasePipelineUnitTest {
//
//  private Object script
//
//  @Override
//  @BeforeEach
//  void setUp() throws Exception {
//    super.setUp()
//
//    helper.registerAllowedMethod('readYaml', [String.class], null)
//    script = loadScript("com/ft/jenkins/Deployments.groovy")
//  }
//
//  @Test
//  void updateChartDeps_notExistentFile() {
//    helper.registerAllowedMethod("fileExists", [String.class], { return false })
//
//    script.updateChartDeps("somelocation")
//
//    //  print call stack for debugging purposes
//    printCallStack()
//
//    /*  check that the method 'getChartDepsRepos' doesn't get called. */
//    assertFalse(helper.callStack.findAll { call ->
//      call.methodName == 'getChartDepsRepos'
//    }.any())
//  }
//
//  @Test
//  void updateChartDeps_SingleDep() {
//    //  given
//    helper.registerAllowedMethod("fileExists", [String.class], { return true })
//    String testRepo = "https://s3-eu-west-1.amazonaws.com/coreos-charts/stable/"
//    Map<String, Object> parsedDeps = [
//        dependencies: [
//            [
//                name      : "test",
//                version   : "1.0.0",
//                repository: testRepo
//            ]
//        ]
//    ]
//
//    helper.registerAllowedMethod("readYaml", [Map.class], { return parsedDeps })
//
//    //  when:
//    String chartLocation = "somelocation"
//    script.updateChartDeps(chartLocation)
//    printCallStack() //  print call stack for debugging purposes
//    //  then:
//    /*  find the sh calls*/
//    List<MethodCall> shCalls = helper.callStack.findAll { call ->
//      call.methodName == 'sh'
//    }
//
//    assertEquals(2, shCalls.size(), "There should be 2 sh calls for update")
//    assertEquals("helm repo add ${md5(testRepo)} ${testRepo}", shCalls[0].args[0])
//    assertEquals("helm dep update ${chartLocation}", shCalls[1].args[0])
//  }
//
//  @Test
//  void updateChartDeps_MultipleDepsWithSameRepo() {
//    //  given:
//    helper.registerAllowedMethod("fileExists", [String.class], { return true })
//    String testRepo = "https://s3-eu-west-1.amazonaws.com/coreos-charts/stable/"
//    Map<String, Object> parsedDeps = [
//        dependencies: [
//            [
//                name      : "test1",
//                version   : "1.0.0",
//                repository: testRepo
//            ],
//            [
//                name      : "test2",
//                version   : "1.0.2",
//                repository: testRepo
//            ],
//        ]
//    ]
//    helper.registerAllowedMethod("readYaml", [Map.class], { return parsedDeps })
//
//    //  when:
//    String chartLocation = "somelocation"
//    script.updateChartDeps(chartLocation)
//    printCallStack() //  print call stack for debugging purposes
//
//    //  then:
//
//    /*  find the sh calls*/
//    List<MethodCall> shCalls = helper.callStack.findAll { call ->
//      call.methodName == 'sh'
//    }
//    assertEquals(2, shCalls.size(), "There should be 2 sh calls for update")
//    assertEquals("helm repo add ${md5(testRepo)} ${testRepo}", shCalls[0].args[0])
//    assertEquals("helm dep update ${chartLocation}", shCalls[1].args[0])
//  }
//
//  @Test
//  void updateChartDeps_DepWithNoRepo() {
//    //  given:
//    helper.registerAllowedMethod("fileExists", [String.class], { return true })
//    Map<String, Object> parsedDeps = [
//        dependencies: [
//            [
//                name   : "test1",
//                version: "1.0.0",
//            ]
//        ]
//    ]
//    helper.registerAllowedMethod("readYaml", [Map.class], { return parsedDeps })
//
//    //  when:
//    String chartLocation = "somelocation"
//    script.updateChartDeps(chartLocation)
//    printCallStack() //  print call stack for debugging purposes
//
//    //  then:
//
//    /*  find the sh calls*/
//    List<MethodCall> shCalls = helper.callStack.findAll { call ->
//      call.methodName == 'sh'
//    }
//    assertEquals(1, shCalls.size(), "There should be 1 sh calls for update")
//    assertEquals("helm dep update ${chartLocation}", shCalls[0].args[0])
//  }
//
//  @Test
//  void updateChartDeps_MultipleDepsWithDifferentRepos() {
//    //  given:
//    helper.registerAllowedMethod("fileExists", [String.class], { return true })
//    String testRepo1 = "https://s3-eu-west-1.amazonaws.com/coreos-charts/stable/"
//    String testRepo2 = "http://upp-helm-repo.s3-website-eu-west-1.amazonaws.com"
//    Map<String, Object> parsedDeps = [
//        dependencies: [
//            [
//                name      : "test1",
//                version   : "1.0.0",
//                repository: testRepo1
//            ],
//            [
//                name      : "test2",
//                version   : "1.0.2",
//                repository: testRepo2
//            ],
//        ]
//    ]
//    helper.registerAllowedMethod("readYaml", [Map.class], { return parsedDeps })
//
//    //  when:
//    String chartLocation = "somelocation"
//    script.updateChartDeps(chartLocation)
//
//    //  then:
//
//    /*  find the sh calls*/
//    List<MethodCall> shCalls = helper.callStack.findAll { call ->
//      call.methodName == 'sh'
//    }
//    assertEquals(3, shCalls.size(), "There should be 3 sh calls for update")
//
//    //  the calls to repo add may not be executed in the order of the repos, so we need to find the calls
//    assertMethodWasCalled(shCalls, "sh", "helm repo add ${md5(testRepo1)} ${testRepo1}")
//    assertMethodWasCalled(shCalls, "sh", "helm repo add ${md5(testRepo2)} ${testRepo2}")
//    assertEquals("helm dep update ${chartLocation}", shCalls[2].args[0])
//  }
//}
