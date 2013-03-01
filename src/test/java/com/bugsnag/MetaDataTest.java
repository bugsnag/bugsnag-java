package com.bugsnag;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;

public class MetaDataTest {
	@Test
	public void testNullIsNull() {
		assertThat(null, is(nullValue()));
	}
}