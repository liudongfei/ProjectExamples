import sys
import time
#字符串截取函数
for line in sys.stdin:
    line = line.strip()
    remote_addr, time_local, request, http_referer = line.split('\t')
    weekday = request[0:3]
    print '\t'.join([remote_addr, time_local, weekday, http_referer])
