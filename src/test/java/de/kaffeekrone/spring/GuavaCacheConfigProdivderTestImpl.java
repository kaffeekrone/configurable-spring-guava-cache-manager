package de.kaffeekrone.spring;

import com.google.common.base.Optional;

public class GuavaCacheConfigProdivderTestImpl implements GuavaCacheConfigProdivder {
	@Override
	public Optional<String> getSpecForCache(final String cacheName) {
		return Optional.absent();
	}
}
