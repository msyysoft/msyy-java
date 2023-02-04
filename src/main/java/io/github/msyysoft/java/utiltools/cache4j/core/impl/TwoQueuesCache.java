package io.github.msyysoft.java.utiltools.cache4j.core.impl;

import io.github.msyysoft.java.utiltools.cache4j.core.ICache;
import io.github.msyysoft.java.utiltools.cache4j.factory.INodeFactory;
import io.github.msyysoft.java.utiltools.cache4j.node.NodeAbstract;
import io.github.msyysoft.java.utiltools.cache4j.node.impl.CacheNode;

/**
 * TwoQueues缓存淘汰算法实现
 * TwoQueues = FIFO + LRU
 * FIFO主要存放初次存入的数据，LRU中存放至少使用过两次的热点数据，此算法命中率高，适应性强，复杂度低。
 * @author 杨元
 *
 * @param <K>
 * @param <V>
 */
public class TwoQueuesCache<K, V> implements ICache<K, V> {
    
    /**
     * fifo缓存模型
     */
    private FifoCache<K, V>  fifoCache= null;
    
    /**
     * lru缓存模型
     */
    private LruCache<K, V> lruCache = null;
    
    /**
     * fifo缓存配置
     */
    private CacheConfig fifoConfig = null;
    
    /**
     * lru缓存配置
     */
    private CacheConfig lruConfig = null;
    
    /**
     * 链表节点工厂
     */
    private INodeFactory<K, V> nodeFactory = null;
    
    /**
     * 热点数据阈值
     */
    private int hotLimit = 2;
    
    /**
     * 构造方法
     * @param config  配置对象
     * @param nodeFactory  链表节点对象创建工厂
     */
    public TwoQueuesCache(CacheConfig config, INodeFactory<K, V> nodeFactory){
        //分配元素上限
        this.lruConfig = CacheConfig.custom().setMaxElement(config.getMaxElement()/2);
        if(config.getMaxElement()%2 == 0){
            this.fifoConfig = CacheConfig.custom().setMaxElement(config.getMaxElement()/2);
        }else{
            this.fifoConfig = CacheConfig.custom().setMaxElement(config.getMaxElement()/2+1);
        }
        this.nodeFactory = nodeFactory;
        
        this.lruCache = new LruCache<K, V>(lruConfig, nodeFactory);
        this.fifoCache = new FifoCache<K, V>(this.fifoConfig, nodeFactory);
    }
    
    @Override
    public void put(K key, V value) {
        
        //新元素直接放入先进先出缓存
        this.fifoCache.put(key, value);
        
    }

    @Override
    public V get(K key) {
        //一级缓存取数据
        NodeAbstract<K, V> node = this.fifoCache.getNode(key);
        
        if(node != null){
            //如果取到了，计数+1
            int usedCount = ((CacheNode<K, V>) node).usedCountIncrement();
            //判断是否满足热点数据条件
            if(usedCount >= this.hotLimit){
                this.lruCache.put(key, node.getValue());
                this.fifoCache.remove(key);
            }
            
            return node.getValue();
        }
        
        node = this.lruCache.getNode(key);
        
        if(node != null){
            return node.getValue();
        }
        
        return null;
    }

    @Override
    public int size() {
        return this.fifoCache.size() + this.lruCache.size();
    }

    @Override
    public double hitRatio() {
        double _hitCount = this.fifoCache.getHitCount().doubleValue() + this.lruCache.getHitCount().doubleValue();
        double _missCount = this.fifoCache.getMissCount().doubleValue() + this.lruCache.getMissCount().doubleValue();
        double dividend = _hitCount + _missCount;
        
        if(dividend == 0){
            return 0.0d;
        }
        
        return _hitCount / dividend;
    }

    @Override
    public void clear() {
        this.lruCache = new LruCache<K, V>(lruConfig, nodeFactory);
        this.fifoCache = new FifoCache<K, V>(this.fifoConfig, nodeFactory);
    }

    @Override
    public NodeAbstract<K, V> getNode(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(K key) {
        
        this.fifoCache.remove(key);
        this.lruCache.remove(key);
        
    }
    
    
    
}
