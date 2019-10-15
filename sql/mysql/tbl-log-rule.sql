create database automation_offrep;
use automation_offrep;
create table if not exists automation_offrep.tbl_log_rule(
    squence int,
    domain_id int,
    domain_name varchar(100),
    domain_datatype varchar(100),
    is_fixed_length tinyint,
    domain_length int,
    should_substr tinyint,
    domain_actual_length int,
    content_format varchar(100),
    header_format varchar(100),
    is_optional tinyint,
    expected_value varchar(100),
    expected_value_startwith tinyint,
    rule_interface varchar(100)
) engine=innodb default charset=utf8;


insert into tbl_log_rule(squence, domain_id, domain_name, domain_datatype,
                         is_fixed_length, domain_length, should_substr,
                         domain_actual_length, content_format, header_format,
                         is_optional, expected_value, expected_value_startwith,
                         rule_interface) values
(1, -1, '总字节数', '', 1,2, 0, 2, '', '',  0, '',0, 'POST_ISO8583'),
(2, -1, 'TPDU指定值', '', 1,5, 0, 5, 'bcd', '',  0, '', 0, 'POST_ISO8583'),
(3, -1, '报文头', '',  1,6, 0, 6, 'bcd', '',  0, '', 0, 'POST_ISO8583'),
(11, 0, '消息类型', '', 1,2, 0, 2, '', '',  0, '', 0, 'POST_ISO8583'),
(12, 1, '位图', '', 1,8, 0, 8, '', '',  0, '', 0, 'POST_ISO8583'),
(100, 2, '主账号', '', 0,1, 0, 1, 'bcd', '',  0, '', 0, 'POST_ISO8583'),
(101, 3, '交易处理码', '', 1,3, 0, 3, '', '', 0, '', 0, 'POST_ISO8583'),
(102, 4, '交易金额', '', 1,6, 0, 6, '', '', 0, '', 0, 'POST_ISO8583'),
(103, 11, '受卡方系统跟踪号', '', 1,3, 0, 3, '', '', 0, '', 0, 'POST_ISO8583'),
(104, 12, '受卡方所在地时间', '', 1,3, 0, 3, '', '', 0, '', 0, 'POST_ISO8583'),
(105, 13, '受卡方所在地日期', '', 1,2, 0, 2, '', '', 0, '', 0, 'POST_ISO8583'),
(106, 14, '卡有效期', '', 1,2, 0, 2, '', '', 0, '', 0, 'POST_ISO8583'),
(107, 15, '清算日期', '',1,2, 0, 2, '', '', 0, '', 0, 'POST_ISO8583'),
(108, 22, '服务点输入方式码', '', 1,2, 0, 2, '', '', 0, '', 0, 'POST_ISO8583'),
(109, 23, '卡序列号',  '', 1,2, 0, 2, '', '', 0, '', 0, 'POST_ISO8583'),
(110, 25, '服务点条件码', '',1,1, 0, 1, '', '', 0, '', 0, 'POST_ISO8583'),
(111, 26, '服务点PIN获取码', '',1,1, 0, 1, '', '', 0, '', 0, 'POST_ISO8583'),
(112, 32, '受理方标识码', '', 0,1, 0, 1, 'bcd', '',  0, '', 0, 'POST_ISO8583'),
(113, 35, '2磁道数据',  '', 0,1, 0, 1, 'bcd', '',  0, '', 0, 'POST_ISO8583'),
(114, 36, '3磁道数据', '', 0,2, 0, 2, 'bcd', '', 0, '', 0, 'POST_ISO8583'),
(115, 37, '检索参考号', '', 1,12, 0, 12, 'ascii', '', 0, '', 0, 'POST_ISO8583'),
(116, 38, '授权标识应答码', '', 1,6, 0, 6, 'ascii', '', 0, '', 0, 'POST_ISO8583'),
(117, 39, '应答码', '', 1,2, 0, 2, 'ascii', '', 0, '', 0, 'POST_ISO8583'),
(118, 41, '受卡机终端标识码', '',1,8, 0, 8, 'ascii', '', 0, '', 0, 'POST_ISO8583'),
(119, 42, '受卡方标识码', '', 1,15, 0, 15, 'ascii', '', 0, '', 0, 'POST_ISO8583'),
(120, 44, '附加响应数据', '', 0,1, 0, 1, '', '', 0, '', 0, 'POST_ISO8583'),
(122, 49, '交易货币代码', '', 1,3, 0, 3, 'ascii', '',  0, '', 0, 'POST_ISO8583'),
(123, 52, '个人标识码数据', '', 1,8, 0, 8, '', '',  0, '', 0, 'POST_ISO8583'),
(124, 53, '安全控制信息', '', 1,8, 0, 8, '', '',  0, '', 0, 'POST_ISO8583'),
(125, 54, '附加金额', '', 0,2, 0, 2, 'ascii', '',  0, '', 0, 'POST_ISO8583'),
(126, 55, 'IC卡数据域', '', 0,2, 0, 2, '', '',  0, '', 0, 'POST_ISO8583'),
(127, 59, '自定义域', '', 0,2, 0, 2, '', '',  0, '', 0, 'POST_ISO8583'),
(128, 60, '自定义域', '', 0,2, 0, 2, 'bcd', '',  0, '', 0, 'POST_ISO8583'),
(130, 62, '自定义域62', '', 0, 2, 0, 2, '', '',  0, '', 0, 'POST_ISO8583'),
(131, 63, '自定义域63',  '', 0, 2, 0, 2, '', '',  0, '', 0, 'POST_ISO8583'),
(132, 64, 'MAC', '', 1,8, 0, 8, 'ascii', '', 0, '', 0, 'POST_ISO8583');