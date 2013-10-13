import org.junit.*;
import static org.junit.Assert.*;


public class TestFunctions extends EnvForTests{
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		//b.setArgs(new String[] { "state" });
		//p.program(); // TODO затирает lastResult. исправить
	}

	
	@Test
	// Проверяет отсутствие значений null в таблице
	public void testPrint() throws Exception {
		b.setArgs(new String[] { "print" }); 
		p.program();
	}
	
	@Test
	public void testPrint2As3() throws Exception {
		// http://automated-testing.info/forum/kak-poluchit-imya-metoda-vo-vremya-vypolneniya-testa#comment-961
		System.out.println(new Object() {
		}.getClass().getEnclosingMethod().getName());
		// не смотря на '\n', лексер считает это как одну строку
		b.setArgs(new String[] { "print print2=as3=-321.694;\nas3=-as3" }); 
		p.program();
		assertEquals(321.694, i.lastResult.getDouble(), 0.001); // работает
	}

	@Test
	public void testPrint_() throws Exception {
		b.setArgs(new String[] { "print print_ = as3=321.694", "", "" });
		p.program();
		assertEquals(321.694, i.lastResult.getDouble(), 0.001); // работает
	}

	@Test
	public void testPrint_1() throws Exception {
		b.setArgs(new String[] { "print -1.0+0.0" });
		p.program();
		assertEquals(-1.0, i.lastResult.getDouble(), 0.01); // работает
	}

	@Test
	public void testPrintZero() throws Exception {
		b.setArgs(new String[] { "print 0.0-0.0" });
		p.program();
		assertEquals(0.0, i.lastResult.getDouble(), 0.01); // работает
	}
	
	@Test
	public void testaab() throws Exception {
		b.setArgs(new String[] { "a = 1; b = a+2; print b;" });
		p.program();
		assertEquals(3, i.lastResult.getInt()); // работает
	}
		
	@Test
	public void testDelAll() throws Exception {
		b.setArgs(new String[] { "del *;" });
		p.program();
		assertEquals(0, i.table.size());
	}
	
	@Test
	public void testDelAllAddOneDelOne() throws Exception {
		b.setArgs(new String[] { "del *; var uut=9; var t; del uut;" });
		p.program();
		assertEquals(2, i.table.size());
	}
		
	@Test
	public void testFuncArgs0() throws Exception {
		b.setArgs(new String[] { "sin(pi)" });
		p.program();
		//assertEquals(1, i.table.size());
	}
	
	@Ignore
	@Test
	public void testFuncArgs2() throws Exception {
		b.setArgs(new String[] { "/*del *; */sin(1, 2.3, e)" });
		p.program();
		//assertEquals(2, i.table.size());
	}
	
	@Ignore
	@Test
	public void testFuncVoidArgs() throws Exception {
		b.setArgs(new String[] { "/*del *;*/ log()" });
		p.program();
		//assertEquals(1, i.table.size());
	}
	
	@Test
	public void testFuncSinPiDiv2MinusCosPi() throws Exception {
		b.setArgs(new String[] { "sin(pi/2.0)-cos(pi)" });
		p.program();
		assertEquals(2.0, i.lastResult.getDouble(), 0.01);
	}
}
