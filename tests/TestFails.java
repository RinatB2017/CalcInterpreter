import org.junit.*;

import exceptions.MyException;
import static org.junit.Assert.*;
import options.OptId;
import lexer.Tag;


public class TestFails extends EnvForTests{

	@Before
	public void setUp() throws Exception {
		super.setUp();
		o.set(OptId.GREEDY_FUNC, true);
	}

	@After
	public void tearDown() {
		assertTrue(MyException.getErrors() != 0);
	}

	
	@Ignore
	@Test(expected = MyException.class)
	public void checkDivByZeroSin() throws Exception {
		b.setArgs(new String[] { "1.0/sin(-pi)" });
		p.program();
	}
	
	@Test(expected = MyException.class)
	public void checkDivZero() throws Exception {
		b.setArgs(new String[] { "-1/0" });
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkFactorial() throws Exception {
		b.setArgs(new String[] { "-3!" }); // Факториал отрицательного
		p.program();
	}

	@Test(expected = MyException.class, timeout = 2000)
	public void checkFactorialCos() throws Exception {
		b.setArgs(new String[] { "(cos pi)!" }); // Greedy! // Факториал
													// отрицательного
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkExtraRP() throws Exception {
		b.setArgs(new String[] { "sin(-pi/2.0))" });
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkExtraRF() throws Exception {
		b.setArgs(new String[] { "sin(-pi/2.0)}" });
		p.program();
	}

	@Test(expected = MyException.class)
	public void checkExtraRFIf() throws Exception {
		b.setArgs(new String[] { "if(true){sin(-pi/4.0);}}" });
		p.program();

		if (p.getCurrTok().tag == Tag.RF)
			p.error("Неправильный выход из expr_list, возможно лишняя RF }");
	}
	
	@Test(expected = MyException.class)
	public void checkEarlyExit() throws Exception {
		b.setArgs(new String[] { "aaa=2^3^4; bb=( 2 ^ 3 ) ^ 4; if(a-b){}else{print 2; exit;} print 3" });
		p.program();
	}
	
	@Test(expected = MyException.class)
	public void checkIntDivZero() throws Exception {
		b.setArgs(new String[] { "1/0" });
		p.program();
	}
	
	@Test(expected = MyException.class)
	public void checkIntDivZeroD() throws Exception {
		b.setArgs(new String[] { "1/0.0" });
		p.program();
	}
	
	@Test(expected = MyException.class)
	public void checkDoubleDivZeroI() throws Exception {
		b.setArgs(new String[] { "1.8/0" });
		p.program();
	}
	
	@Test(expected = MyException.class)
	public void checkDoubleDivZero() throws Exception {
		b.setArgs(new String[] { "1.8/0.0" });
		p.program();
	}
}
