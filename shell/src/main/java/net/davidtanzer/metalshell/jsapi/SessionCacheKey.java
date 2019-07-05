package net.davidtanzer.metalshell.jsapi;

class SessionCacheKey implements CacheKey {
	private static int nextValue = 0;
	private final String key;

	SessionCacheKey() {
		this(String.valueOf(nextValue));
		nextValue++;
	}

	private SessionCacheKey(String key) {
		this.key = key;
	}

	public static SessionCacheKey of(String key) {
		return new SessionCacheKey(key);
	}

	@Override
	public String asString() {
		return key;
	}

	@Override
	public boolean equals(Object other) {
		if(other != null && other.getClass() == SessionCacheKey.class) {
			return ((SessionCacheKey)other).key.equals(key);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}
}
