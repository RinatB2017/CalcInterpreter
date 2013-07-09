import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestParserGreedyMost.class, TestParserNonGreedy.class/*, TestParserFails.class*/})
public class AllTests {

}
