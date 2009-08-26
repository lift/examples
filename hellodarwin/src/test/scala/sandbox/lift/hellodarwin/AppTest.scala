package sandbox.lift.hellodarwin;

import _root_.junit.framework._;
import Assert._;

object AppTest {
    def suite: Test = {
        val suite = new TestSuite(classOf[AppTest]);
        suite
    }

    def main(args : Array[String]) {
        _root_.junit.textui.TestRunner.run(suite);
    }
}

/**
 * Unit test for simple App.
 */
class AppTest extends TestCase("app") {

    /**
     * Rigourous Tests :-)
     */
    def testOK() = assertTrue(true);
    //def testKO() = assertTrue(false);


}
