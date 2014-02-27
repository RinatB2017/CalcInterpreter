import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestFunctions.class,
	TestBasicArith.class,
	TestFails.class,
	TestIf.class,
	TestCasting.class,
	TestCastingAndFunctions.class
})
public class JUnit4TestSuite {

}
