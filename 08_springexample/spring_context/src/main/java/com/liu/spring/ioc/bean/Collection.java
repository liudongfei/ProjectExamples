package com.liu.spring.ioc.bean;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Spring如何注入值到集合类型(List, Set, Map, and Properties).
 * @Auther: liudongfei
 * @Date: 2019/9/18 21:57
 * @Description:
 */
public class Collection {
    private List<Object> lists;
    private Set<Object> sets;
    private Map<Object, Object> maps;
    private Properties pros;

    public List<Object> getLists() {
        return lists;
    }

    public void setLists(List<Object> lists) {
        this.lists = lists;
    }

    public Set<Object> getSets() {
        return sets;
    }

    public void setSets(Set<Object> sets) {
        this.sets = sets;
    }

    public Map<Object, Object> getMaps() {
        return maps;
    }

    public void setMaps(Map<Object, Object> maps) {
        this.maps = maps;
    }

    public Properties getPros() {
        return pros;
    }

    public void setPros(Properties pros) {
        this.pros = pros;
    }

    @Override
    public String toString() {
        return "Collection{"
                + "lists=" + lists
                + ", sets=" + sets
                + ", maps=" + maps
                + ", pros=" + pros
                + '}';
    }
}
