package com.db.iss.dispatcher.future;

import com.db.iss.core.exception.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 异步返回结果
 */
public class DefaultFuture<T> implements IFuture<T> {

    private T result;

    private boolean isReturn = false;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public T get(long timeout) throws TimeoutException {
        long total = 0;
        synchronized (this) {
            long start = System.currentTimeMillis();
            while (!isReturn) {
                try {
                    this.wait(timeout);
                } catch (InterruptedException e) {
                    logger.warn("InterruptedException occurred");
                }
                long end = System.currentTimeMillis();

                //超时退出等待
                if((end-start) >= timeout){
                    throw new TimeoutException(String.format("wait %d ms limit %d ms",(end-start),timeout));
                }
            }
        }
        return result;
    }

    @Override
    public void set(T result) {
        synchronized (this) {
            this.result = result;
            this.isReturn = true;
            this.notifyAll();
        }
    }
}
