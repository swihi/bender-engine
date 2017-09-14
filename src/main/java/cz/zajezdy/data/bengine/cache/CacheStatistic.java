package cz.zajezdy.data.bengine.cache;

public class CacheStatistic {

	private String key = null;
	private int engineCount = 0;
	private long accessCount = 0;

	public CacheStatistic(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getEngineCount() {
		return engineCount;
	}

	public void setEngineCount(int engineCount) {
		this.engineCount = engineCount;
	}

	public long getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(long accessCount) {
		this.accessCount = accessCount;
	}

	public void incrementEngineCount() {
		this.engineCount++;
	}

	public void incrementAccessCount() {
		this.accessCount++;
	}

	@Override
	public String toString() {
		return "CacheStatistic [key=" + key + ", engineCount=" + engineCount + ", accessCount=" + accessCount + "]";
	}

}
