package io.github.msyysoft.java.utiltools.cache4j.node.impl;

import io.github.msyysoft.java.utiltools.cache4j.node.NodeAbstract;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 链表缓存节点实现
 * @author 杨元
 *
 */
public class CacheNode<K, V> extends NodeAbstract<K, V>{
    
    /**
     * 使用计数
     */
    private AtomicInteger usedCount;
    
    public CacheNode(){
        usedCount = new AtomicInteger();
    }
    
    /**
     * 使用计数递增
     * @return 递增后的值
     */
    public int usedCountIncrement() {
        return this.usedCount.incrementAndGet();
    }
    
}
