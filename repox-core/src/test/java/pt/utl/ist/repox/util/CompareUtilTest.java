package pt.utl.ist.repox.util;

import com.ibm.icu.util.Calendar;
import org.junit.Assert;
import org.junit.Test;

public class CompareUtilTest {

	@Test
	public void testNullObjectsEqual() throws InterruptedException {
		Calendar instance = null;
		Calendar copy = null;
		Assert.assertTrue(CompareUtil.compareObjectsAndNull(instance, copy));
	}
	
	@Test
	public void testNullObjectsNotEqual() throws InterruptedException {
		Calendar instance = null;
		Calendar copy = Calendar.getInstance();
		Assert.assertTrue(!CompareUtil.compareObjectsAndNull(instance, copy));
	}
	
	@Test
	public void testObjectsEqual() throws InterruptedException {
		Calendar instance = Calendar.getInstance();
		Calendar copy = Calendar.getInstance();
		copy.setTimeInMillis(instance.getTimeInMillis());
		Assert.assertTrue(CompareUtil.compareObjectsAndNull(instance, copy));
	}

	@Test
	public void testObjectsNotEqual() throws InterruptedException {
		Calendar instance = Calendar.getInstance();
		Calendar copy = Calendar.getInstance();
		copy.set(Calendar.YEAR, 1980);
		Assert.assertTrue(!CompareUtil.compareObjectsAndNull(instance, copy));
	}
	
	@Test
	public void testNullArraysEqual() throws InterruptedException {
		String[] instance = null;
		String[] copy = null;
		Assert.assertTrue(CompareUtil.compareArraysAndNull(instance, copy));
	}
	
	@Test
	public void testNullArraysNotEqual() throws InterruptedException {
		String[] instance = null;
		String[] copy = new String[]{"asd"};
		Assert.assertTrue(!CompareUtil.compareArraysAndNull(instance, copy));
	}
	
	@Test
	public void testArraysEqual() throws InterruptedException {
		String[] instance = new String[]{"asd"};;
		String[] copy = new String[]{"asd"};
		Assert.assertTrue(CompareUtil.compareArraysAndNull(instance, copy));
	}
	
	@Test
	public void testArraysNotEqual() throws InterruptedException {
		String[] instance = new String[]{"asd", "asd2"};;
		String[] copy = new String[]{"asd"};
		Assert.assertTrue(!CompareUtil.compareArraysAndNull(instance, copy));
	}
}
