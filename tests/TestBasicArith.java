import org.junit.*;

import static org.junit.Assert.*;

public class TestBasicArith extends EnvForTests{
	@Test
	public void test2plus2mul2() throws Exception {
		b.setArgs(new String[] { "2+2*2;" });
		p.program();
		assertEquals(6, i.lastResult.getInt());
	}
	
	@Test
	public void test3FactorialFactorial() throws Exception {
		b.setArgs(new String[] { "3!!" });
		p.program();
		assertEquals(720, i.lastResult.getInt());
	}

	@Test
	public void test3FactorialAdd4Factorial() throws Exception {
		b.setArgs(new String[] { "3!+4!" });
		p.program();
		assertEquals(30, i.lastResult.getInt());
	}

	@Test
	public void test1minus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] { "1-3!+4!" });
		p.program();
		assertEquals(19, i.lastResult.getInt());
	}

	@Test
	public void test1plus3FactoriAladd4Factorial() throws Exception {
		b.setArgs(new String[] { "1+3!+4!" });
		p.program();
		assertEquals(31, i.lastResult.getInt());
	}

	@Test
	public void test3FactorialMul4Factorial() throws Exception {
		b.setArgs(new String[] { "3!*4!" });
		p.program();
		assertEquals(144, i.lastResult.getInt());
	}

	@Test
	public void test2pow3Factorial() throws Exception {
		b.setArgs(new String[] { "2^3!" }); // 2^(3!)
		p.program();
		assertEquals(64, i.lastResult.getInt());
	}

	@Test
	public void test2pow3FactorialPlus1() throws Exception {
		b.setArgs(new String[] { "2^3!+1" }); // 2^(3!)+1
		p.program();
		assertEquals(65, i.lastResult.getInt());
	}

	@Test
	public void testRightAssociatePower() throws Exception {
		b.setArgs(new String[] { "2^3^4" });
		p.program();
		assertEquals((int)Math.pow(2, Math.pow(3, 4)), i.lastResult.getInt());
	}
	
	@Test
	public void testAns() throws Exception {
		b.setArgs(new String[] { "2; (5+3)+ans" });
		p.program();
		assertEquals(16, i.lastResult.getInt()); // работает
	}
	
	// TODO Убрать p=4 и использовать для проверки деления функции на ЧИСЛО
	@Test
	public void testTemplateForFutureVector() throws Exception {
		b.setArgs(new String[] { "p=4; (3*p^2 + 6*p - 4)/2" }); 
		p.program();
		assertEquals(34, i.lastResult.getInt()); // работает
	}
}