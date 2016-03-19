package de.kaffeekrone.spring;

import com.google.common.base.Optional;

public interface GuavaCacheConfigProdivder {

	Optional<String> getSpecForCache(String cacheName);
}
