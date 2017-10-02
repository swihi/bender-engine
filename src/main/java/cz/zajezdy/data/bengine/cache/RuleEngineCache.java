package cz.zajezdy.data.bengine.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.configuration.Configuration;

/**
 * <pre>
 * 
 * {
 * 	&#64;code
 * 	// use the cache as follows:
 * 	RuleEngine re = RuleEngineCache.createInstance(ruleEngineProviderInstance).getLockedRuleEngine("myconfig.json");
 * 	// do something with the rule engine ...
 * 	// unlock the object when you are done
 * 	RuleEngineCache.get().unlock(re);
 * }
 * 
 * </pre>
 * 
 * @author Florian Beese, David Kovar
 *
 */
public class RuleEngineCache {

	@SuppressWarnings("rawtypes")
	private Map<String, Configuration> configurationCache = new HashMap<String, Configuration>();

	private Map<String, List<RuleEngine>> engineCache = new HashMap<String, List<RuleEngine>>();
	private Set<RuleEngine> lockedEngines = new HashSet<RuleEngine>();
	private Map<String, CacheStatistic> cacheStatistics = new HashMap<String, CacheStatistic>();
	private RuleEngineProvider engineProvider = null;

	private RuleEngineCache() {

	}

	/**
	 * Create a new instance of RuleEngineCache for a specific RuleEngineProvider.
	 * Provided RuleEngineProvider will be used, to retrieve the configuration files,
	 * deserialize the configuration and create the engine if needed.
	 * You may use BasicRuleEngineProvider for easier usage.
	 *
	 * @param engineProvider The rule engine provider, which the cache uses
	 *            to create new rule engines.
	 */
	public static RuleEngineCache createInstance(RuleEngineProvider engineProvider) {
		final RuleEngineCache instance = new RuleEngineCache();
		instance.engineProvider = engineProvider;
		return instance;
	}

	/**
	 * Provides a RuleEngine object and locks the instance in the cache.
	 * The cache will not return this instance to any requester as long
	 * as it has not been unlocked (see {@link #unlock(RuleEngine)}).
	 * 
	 * @see #unlock(RuleEngine)
	 * 
	 * @param key The key in the cache - normally the configuration file name.
	 * @return a RuleEngine instance, that was built using the registered
	 *         RuleEngineProvider or was retrieved from the cache
	 */
	public RuleEngine getLockedRuleEngine(String key) throws IOException {
		RuleEngine re = null;
		while (true) {
			re = lookupEngineCache(key);
			if (re == null) {
				@SuppressWarnings("rawtypes")
				Configuration configuration = lookupConfigCache(key);
				if (configuration == null) {
					String jsonConfig = engineProvider.getConfigurationContent(key);
					configuration = engineProvider.deserialize(jsonConfig);
					cacheConfiguration(key, configuration);
				}
				re = engineProvider.getEngine(configuration);
				cacheEngine(key, re);
			}
			if (tryLockEngine(re)) {
				break;
			}
		}
		CacheStatistic cacheStatistic = cacheStatistics.get(key);
		cacheStatistic.incrementAccessCount();
		return re;
	}

	/**
	 * Unlocks the ruleEngine object. The cache will then use this object again,
	 * to deliver it when calling getLockedRuleEngine().
	 * 
	 * @see cz.zajezdy.data.bengine.cache.RuleEngineCache#getLockedRuleEngine(String key)
	 * 
	 * @param ruleEngine The ruleEngine object to be unlocked.
	 */
	public void unlock(RuleEngine ruleEngine) {
		lockedEngines.remove(ruleEngine);
	}

	/**
	 * Invalidates the cache for this key.
	 * After this call, the cache for this key will be empty and built up again
	 * with its next request.
	 * 
	 * @param key The key in the cache - normally the configuration file name.
	 */
	public synchronized void invalidate(String key) {
		if (configurationCache.containsKey(key)) {
			configurationCache.remove(key);
			List<RuleEngine> engines = engineCache.get(key);
			lockedEngines.removeAll(engines);
			engineCache.remove(key);
			cacheStatistics.remove(key);
		}
	}

	/**
	 * Invalidate all cache items in this cache
	 * After this call, the cache is empty
	 */
	public void invalidateAll() {
		Set<String> keys = new HashSet<>(configurationCache.keySet());
		keys.forEach(this::invalidate);
	}

	public List<CacheStatistic> getStatistics() {
		List<CacheStatistic> list = new ArrayList<CacheStatistic>();
		list.addAll(cacheStatistics.values());
		return list;
	}

	public ImmutableMap<String, CacheStatistic> getStatisticsMap() {
		return ImmutableMap.copyOf(cacheStatistics);
	}

	private synchronized boolean tryLockEngine(RuleEngine ruleEngine) {
		if (lockedEngines.contains(ruleEngine)) {
			return false;
		}
		lockedEngines.add(ruleEngine);
		return true;
	}

	private synchronized void cacheEngine(String key, RuleEngine ruleEngine) {
		if (!engineCache.containsKey(key)) {
			engineCache.put(key, new ArrayList<RuleEngine>());
		}
		List<RuleEngine> engines = engineCache.get(key);
		engines.add(ruleEngine);
		CacheStatistic cacheStatistic = cacheStatistics.get(key);
		cacheStatistic.incrementEngineCount();
	}

	private synchronized RuleEngine lookupEngineCache(String key) {
		// TODO: try to improve multithreading here...
		// some way to improve synchronized?
		if (engineCache.containsKey(key)) {
			List<RuleEngine> engines = engineCache.get(key);
			for (RuleEngine re : engines) {
				if (!lockedEngines.contains(re)) {
					return re;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Configuration lookupConfigCache(String key) {
		if (configurationCache.containsKey(key)) {
			return configurationCache.get(key);
		}
		return null;
	}

	private synchronized void cacheConfiguration(String key, @SuppressWarnings("rawtypes") Configuration configuration) {
		cacheStatistics.put(key, new CacheStatistic(key));
		configurationCache.put(key, configuration);
	}
}
