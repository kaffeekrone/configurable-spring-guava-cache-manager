package de.kaffeekrone.spring;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ConfigurableGuavaCacheManager implements CacheManager {

	private static final Logger												LOGGER				= LoggerFactory.getLogger(ConfigurableGuavaCacheManager.class);

	private final ConcurrentMap<String, org.springframework.cache.Cache>	cacheMap			= new ConcurrentHashMap<>();

	private final CacheBuilder<Object, Object>								defaultCacheBuilder	= CacheBuilder.newBuilder();
	private final GuavaCacheConfigProdivder guavaCacheConfigProdivder;

	public ConfigurableGuavaCacheManager(final GuavaCacheConfigProdivder guavaCacheConfigProdivder) {
		this.guavaCacheConfigProdivder = guavaCacheConfigProdivder;
	}

	private Cache<Object, Object> createNativeGuavaCache(final String cacheName) {
		LOGGER.debug("Cache with name [{}] is about to get created", cacheName);
		final Optional<String> optSpecForCache = this.guavaCacheConfigProdivder.getSpecForCache(cacheName);
		if (optSpecForCache.isPresent()) {
			try {
				return createCacheWithConfig(cacheName, optSpecForCache.get());
			} catch (Exception e) {
				LOGGER.error("Unable to create custom cache [{}]! Will create default one", cacheName, e);
				return createDefaultNativeGuavaCache(cacheName);
			}
		} else {
			return createDefaultNativeGuavaCache(cacheName);
		}
	}

	// we have to be extreeeemly save here
	private Cache<Object, Object> createCacheWithConfig(final String cacheName, final String cacheSpecString) throws Exception {
		if (Strings.isNullOrEmpty(cacheSpecString)) {
			LOGGER.warn("Cache Spec is for [{}] not preset but settings should be overridden! Will create default one", cacheName);
			return createDefaultNativeGuavaCache(cacheName);
		} else {
			return CacheBuilder.from(cacheSpecString).build();
		}
	}

	@Override
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(this.cacheMap.keySet());
	}

	/**
	 * Create a native Guava Cache instance for the specified cache name.
	 *
	 * @param name the name of the cache
	 * @return the native Guava Cache instance
	 */
	private com.google.common.cache.Cache<Object, Object> createDefaultNativeGuavaCache(final String name) {
		LOGGER.debug("Cache with name [{}] is about to get created the default way", name);
		return this.defaultCacheBuilder.build();
	}

	@Override
	public org.springframework.cache.Cache getCache(final String name) {
		org.springframework.cache.Cache cache = this.cacheMap.get(name);
		if (cache == null) {
			synchronized (this.cacheMap) {
				cache = this.cacheMap.get(name);
				if (cache == null) {
					cache = new GuavaCache(name, createNativeGuavaCache(name));
					this.cacheMap.put(name, cache);
				}
			}
		}
		return cache;
	}

}
