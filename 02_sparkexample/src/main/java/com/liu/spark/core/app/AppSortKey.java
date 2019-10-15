package com.liu.spark.core.app;

import scala.Serializable;
import scala.math.Ordered;

/**
 * 用于二次排序的key.
 */
public class AppSortKey implements Ordered<AppSortKey>, Serializable {
    private long uptraffic;
    private long downtraffic;
    private long timestamp;

    public AppSortKey() {
    }

    public AppSortKey(long uptraffic, long downtraffic, long timestamp) {
        this.uptraffic = uptraffic;
        this.downtraffic = downtraffic;
        this.timestamp = timestamp;
    }

    @Override
    public int compare(AppSortKey that) {
        return 0;
    }

    @Override
    public boolean $less(AppSortKey that) {
        if (this.uptraffic < that.uptraffic) {
            return true;
        } else if (this.uptraffic == that.uptraffic && this.downtraffic < that.downtraffic) {
            return true;
        } else if (this.uptraffic == that.uptraffic && this.downtraffic == that.downtraffic
                && this.timestamp < that.timestamp) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater(AppSortKey that) {
        if (this.uptraffic > that.uptraffic) {
            return true;
        } else if (this.uptraffic == that.uptraffic && this.downtraffic > that.downtraffic) {
            return true;
        } else if (this.uptraffic == that.uptraffic && this.downtraffic == that.downtraffic
                && this.timestamp > that.timestamp) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $less$eq(AppSortKey that) {
        if ($less(that)) {
            return true;
        } else if (this.uptraffic == that.uptraffic && this.downtraffic == that.downtraffic
                && this.timestamp == that.timestamp) {
            return true;
        }

        return false;
    }

    @Override
    public boolean $greater$eq(AppSortKey that) {
        if ($greater(that)) {
            return true;
        } else if (this.uptraffic == that.uptraffic && this.downtraffic == that.downtraffic
                && this.timestamp == that.timestamp) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(AppSortKey that) {
        return compare(that);
    }

    @Override
    public String toString() {
        return "AppSortKey{"
                + "uptraffic=" + uptraffic
                + ", downtraffic=" + downtraffic
                + ", timestamp=" + timestamp
                + '}';
    }
}
