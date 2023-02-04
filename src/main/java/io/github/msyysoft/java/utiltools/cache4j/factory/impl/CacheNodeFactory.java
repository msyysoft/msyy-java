package io.github.msyysoft.java.utiltools.cache4j.factory.impl;

import io.github.msyysoft.java.utiltools.cache4j.factory.INodeFactory;
import io.github.msyysoft.java.utiltools.cache4j.node.NodeAbstract;
import io.github.msyysoft.java.utiltools.cache4j.node.impl.CacheNode;

/**
 * 链表缓存节点创建工厂
 * @author 杨元
 *
 */
public class CacheNodeFactory<K, V> implements INodeFactory<K, V>{

    @Override
    public NodeAbstract<K, V> createNode(K key, V value) {
        
        CacheNode<K, V> node = new CacheNode<K, V>();
        node.setKey(key);
        node.setValue(value);
        
        return node;
    }

}
