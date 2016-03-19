package de.kaffeekrone.spring;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.testng.Assert.assertNotNull;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.cache.Cache;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

public class ConfigurableGuavaCacheManagerTest {

	@Test
	public void testGetCacheWithoutSpecificConfig() {
		final String someCacheName = "someCache";
		final GuavaCacheConfigProdivderTestImpl cacheConfigProdivder = Mockito.mock(GuavaCacheConfigProdivderTestImpl.class);

		final ConfigurableGuavaCacheManager configurableGuavaCacheManager = new ConfigurableGuavaCacheManager(cacheConfigProdivder);
		doCallRealMethod().when(cacheConfigProdivder).getSpecForCache(someCacheName);
		final Cache someCache = configurableGuavaCacheManager.getCache(someCacheName);

		assertSomething(someCacheName, cacheConfigProdivder, configurableGuavaCacheManager, someCache);
	}

	@Test
	public void testGetCacheWithEmptyString() {
		final String someCacheName = "someCacheWithEmptyStringConfig";
		final GuavaCacheConfigProdivderTestImpl cacheConfigProdivder = Mockito.mock(GuavaCacheConfigProdivderTestImpl.class);

		final ConfigurableGuavaCacheManager configurableGuavaCacheManager = new ConfigurableGuavaCacheManager(cacheConfigProdivder);
		doAnswer(new Answer<Optional<String>>() {
			@Override
			public Optional<String> answer(final InvocationOnMock invocation) throws Throwable {
				return Optional.of("");
			}
		}).when(cacheConfigProdivder).getSpecForCache(someCacheName);
		final Cache someCache = configurableGuavaCacheManager.getCache(someCacheName);

		assertSomething(someCacheName, cacheConfigProdivder, configurableGuavaCacheManager, someCache);
	}

	private void assertSomething(final String someCacheName, final GuavaCacheConfigProdivderTestImpl cacheConfigProdivder,
			final ConfigurableGuavaCacheManager configurableGuavaCacheManager, final Cache someCache) {
		assertNotNull(someCache);
		configurableGuavaCacheManager.getCache(someCacheName);
		verify(cacheConfigProdivder, times(1)).getSpecForCache(anyString());

		assertThat(configurableGuavaCacheManager.getCacheNames(), hasItems(someCacheName));
	}

	@Test
	public void testGetCacheWithNullString() {
		final String someCacheName = "someCacheWithNullStringConfig";
		final GuavaCacheConfigProdivderTestImpl cacheConfigProdivder = Mockito.mock(GuavaCacheConfigProdivderTestImpl.class);

		final ConfigurableGuavaCacheManager configurableGuavaCacheManager = new ConfigurableGuavaCacheManager(cacheConfigProdivder);
		doAnswer(new Answer<Optional<String>>() {
			@Override
			public Optional<String> answer(final InvocationOnMock invocation) throws Throwable {
				return Optional.fromNullable(null);
			}
		}).when(cacheConfigProdivder).getSpecForCache(someCacheName);
		final Cache someCache = configurableGuavaCacheManager.getCache(someCacheName);

		assertSomething(someCacheName, cacheConfigProdivder, configurableGuavaCacheManager, someCache);
	}

	@Test
	public void testGetCacheWithConfig() {
		final String someCacheName = "someCacheWithConfig";
		final GuavaCacheConfigProdivderTestImpl cacheConfigProdivder = Mockito.mock(GuavaCacheConfigProdivderTestImpl.class);

		final ConfigurableGuavaCacheManager configurableGuavaCacheManager = new ConfigurableGuavaCacheManager(cacheConfigProdivder);
		doAnswer(new Answer<Optional<String>>() {
			@Override
			public Optional<String> answer(final InvocationOnMock invocation) throws Throwable {
				return Optional.of("expireAfterAccess=15m");
			}
		}).when(cacheConfigProdivder).getSpecForCache(someCacheName);
		final Cache someCache = configurableGuavaCacheManager.getCache(someCacheName);

		assertSomething(someCacheName, cacheConfigProdivder, configurableGuavaCacheManager, someCache);
	}

	@Test
	public void testGetCacheWithConfigWithErros() {
		final String someCacheName = "someCacheWithConfig";
		final GuavaCacheConfigProdivderTestImpl cacheConfigProdivder = Mockito.mock(GuavaCacheConfigProdivderTestImpl.class);

		final ConfigurableGuavaCacheManager configurableGuavaCacheManager = new ConfigurableGuavaCacheManager(cacheConfigProdivder);
		doAnswer(new Answer<Optional<String>>() {
			@Override
			public Optional<String> answer(final InvocationOnMock invocation) throws Throwable {
				return Optional.of("broken");
			}
		}).when(cacheConfigProdivder).getSpecForCache(someCacheName);
		final Cache someCache = configurableGuavaCacheManager.getCache(someCacheName);

		assertSomething(someCacheName, cacheConfigProdivder, configurableGuavaCacheManager, someCache);
	}
}