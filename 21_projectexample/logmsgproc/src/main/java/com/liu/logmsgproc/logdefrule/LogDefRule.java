package com.liu.logmsgproc.logdefrule;

import com.liu.logmsgproc.bean.LogDomainBean;

import java.util.ArrayList;
import java.util.List;

/**
 * translog interface.
 * @Auther: liudongfei
 * @Date: 2019/4/4 10:04
 * @Description:
 */
public interface LogDefRule {
    /** translog contains the logdomainbean list. */
    public static List<LogDomainBean> DOMAIN_BEANS = new ArrayList<LogDomainBean>();
}
