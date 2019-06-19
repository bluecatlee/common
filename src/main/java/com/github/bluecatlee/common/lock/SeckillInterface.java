package com.github.bluecatlee.common.lock;

public interface SeckillInterface {
    @CacheLock(lockedPrefix="TEST_PREFIX")
    public void secKill(String arg1,@LockedObject Long arg2);
}
