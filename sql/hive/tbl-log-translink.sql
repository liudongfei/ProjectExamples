-- 交易链属性信息表
create table if not exists automation_offrep.log_translink(
    translinked_id string,
    logmsg_transtype string,
    logmsg_transorg string,
    arrive_time string
)
row format delimited
    fields terminated by '\t'
stored as textfile;