package ru.skarpushin.swingpm.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import ru.skarpushin.swingpm.collections.ListEx;
import ru.skarpushin.swingpm.collections.ListExImpl;

public class ListExIteratorTest {
	@Test
	public void testIterations_expectOkForCommonOperations() {
		List<String> list = new ArrayList<String>();
		ListEx<String> listEx = new ListExImpl<String>(list);

		listEx.add("10");
		listEx.add("20");
		listEx.add("30");

		assertEquals(3, listEx.size());

		listEx.add(2, "25");
		listEx.add(1, "15");

		assertEquals(5, listEx.size());

		listEx.addAll(Arrays.asList("40", "50"));
		listEx.addAll(6, Arrays.asList("41", "42"));

		assertEquals(9, listEx.size());

		String[] expected = new String[] { "10", "15", "20", "25", "30", "40", "41", "42", "50" };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], listEx.get(i));
		}

		Iterator<String> iter = listEx.iterator();
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], iter.next());
		}

		iter = listEx.iterator();
		iter.next();
		iter.next();
		iter.next();
		iter.next();
		iter.remove();

		assertEquals("30", listEx.get(3));
	}

	@Test(expected = IllegalStateException.class)
	public void testIterations_expectIseOnDoubleRemove() {
		List<String> list = new ArrayList<String>();
		ListEx<String> listEx = new ListExImpl<String>(list);
		listEx.add("10");
		listEx.add("20");
		listEx.add("30");

		Iterator<String> iter = listEx.iterator();
		iter.next();
		iter.remove();
		iter.remove();

		fail();
	}

	@Test(expected = IllegalStateException.class)
	public void testIterations_expectIseOnMissedNext() {
		List<String> list = new ArrayList<String>();
		ListEx<String> listEx = new ListExImpl<String>(list);
		listEx.add("10");
		listEx.add("20");
		listEx.add("30");

		Iterator<String> iter = listEx.iterator();
		iter.remove();

		fail();
	}

	@Test(expected = NoSuchElementException.class)
	public void testIterations_expectNsiOnEmptyList() {
		List<String> list = new ArrayList<String>();
		ListEx<String> listEx = new ListExImpl<String>(list);

		Iterator<String> iter = listEx.iterator();
		iter.next();

		fail();
	}
}
