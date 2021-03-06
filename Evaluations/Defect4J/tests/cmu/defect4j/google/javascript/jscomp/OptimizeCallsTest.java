package cmu.defect4j.google.javascript.jscomp;

import gov.nasa.jpf.util.test.TestJPF;
import org.junit.Test;
import junit.framework.TestCase;
public class OptimizeCallsTest extends TestJPF {

    private final String[] config = {"+nhandler.delegateUnhandledNative", "+classpath+=${jpf-core}/lib/junit-4.11.jar,lib/compiler.jar"};

    public static void main(String[] testMethods){
        runTestsOfThisClass(testMethods);
    }
	@Test(timeout=120000)
	public void testRemovingReturnCallToFunctionWithUnusedParams() throws Exception {
		if (verifyNoPropertyViolation(config)) {
			TestCase testcase = new com.google.javascript.jscomp.OptimizeCallsTest() {
				public void runTest() throws Exception {
					testRemovingReturnCallToFunctionWithUnusedParams();
				}
			};
			testcase.run();
		}
	}

	@Test(timeout=120000)
	public void testNestingFunctionCallWithUnsedParams() throws Exception {
		if (verifyNoPropertyViolation(config)) {
			TestCase testcase = new com.google.javascript.jscomp.OptimizeCallsTest() {
				public void runTest() throws Exception {
					testNestingFunctionCallWithUnsedParams();
				}
			};
			testcase.run();
		}
	}

	@Test(timeout=120000)
	public void testUnusedAssignOnFunctionWithUnusedParams() throws Exception {
		if (verifyNoPropertyViolation(config)) {
			TestCase testcase = new com.google.javascript.jscomp.OptimizeCallsTest() {
				public void runTest() throws Exception {
					testUnusedAssignOnFunctionWithUnusedParams();
				}
			};
			testcase.run();
		}
	}

}