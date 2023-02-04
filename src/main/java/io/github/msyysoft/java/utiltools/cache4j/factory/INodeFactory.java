package io.github.msyysoft.java.utiltools.cache4j.factory;

import io.github.msyysoft.java.utiltools.cache4j.node.NodeAbstract;

/**
 * 链表节点创建工厂接口
 * @author 杨元
 *
 */
public interface INodeFactory<K, V> {
    
    public NodeAbstract<K, V> createNode(K key, V value);
    
}
