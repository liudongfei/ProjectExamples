import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

public class TestDemo {
    public static void main(String[] args) throws UnsupportedEncodingException {
//        Scanner scanner = new Scanner(System.in);
//        String str = scanner.nextLine();
//        String[] strList = str.split(" ");
//        int size = strList.length;
//        System.out.println(strList[size - 1].length());
//
//
//        Scanner scanner1 = new Scanner(System.in);
//        String str1 = scanner1.nextLine();
//        String ch = scanner1.nextLine();
//        String strRep = str1.replaceAll(ch, "");
//        System.out.println(str1.length() - strRep.length());
//
//
//        Set set = new TreeSet<Integer>();
//        Scanner scanner2 = new Scanner(System.in);
//        while (scanner.hasNext()) {
//            int arrNum = scanner2.nextInt();
//            for(int i = 0; i < arrNum; i++) {
//                int tempNum = scanner2.nextInt();
//                set.add(tempNum);
//            }
//        }
//        Iterator iterator = set.iterator();
//        while(iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }

//        String res = new BigInteger("0xc460".substring(2), 16).toString(10);
//        System.out.println(res);
//        new HashMap<String, Integer>().keySet();
//
//        Scanner scanner = new Scanner(System.in);
//        Map<String, Integer> map = new TreeMap();
//        int number = scanner.nextInt();
//        for(int i = 0;i < number;i++) {
//            String kv = scanner.nextLine();
//            String[] kvArr = kv.split(" ");
//            Integer value = Integer.parseInt(kvArr[1]);
//            if (map.get(kvArr[0]) != null) {
//                Integer newVal = map.get(kvArr[0]) + value;
//                map.put(kvArr[0], newVal);
//            } else {
//                map.put(kvArr[0], value);
//            }
//        }
//        for (String key : map.keySet()) {
//            System.out.println(key + " " + map.get(key));
//        }
//        scanner.close();
//        String str = "W49;W92;A84;W33;A32;A52;D97;S14;S33;D79;A14;W25;D97;D21;D31;A66;S67;S4;A59;S62;W40;S26;S65;A58;S17;A67;D48;W23;D68;S99;S97;W63;W27;D12;D83;W8;S3;W6;A88;D23;W30;S91;D58;W74;D45;W3;D19;S72;D8;S79;S76;S49;W16;A29;W93;W99;W92;D82;A10;A4;D25;A90;D83;W45;W20;S68;D59;S48;A18;S0;W24;S48;W75;A39;W29;S28;W76;W78;D94;A57;A5;D51;S61;A39;W77;S70;A2;D8;S58;D51;S86;W30;A27;S62;D56;A51;D0;S58;W34;S39;A64;A68;A96;D37;S91;S16;A17;D35;A85;W88;S57;S61;A28;D12;A87;S39;A85;W22;D65;D72;A5;A78;A59;D75;D57;S66;A55;D84;D72;W87;S46;W64;D49;S46;W34;D60;S39;A30;W86;D20;W93;D25;W44;W86;A16;D4;A86;S86;S27;W7;W89;W93;S17;S39;W66;W72;D81;W93;A88;D46;S57;W45;S84;S57;D27;A11;D54;S8;W15;A50;A69;A4;D19;D69;A3;A28;D47;W18;A39;D47;W14;D77;W59;S84;A32;D56;S16;D99;A33;A51;A24;D65;W37;D98;A13;W6;D94;D28;A12;S18;W40;S23;W76;D6;S40;D26;W97;W7;W90;W75;S12;A89;S46;S36;D96;A49;A73;S53;D84;A87;D75;D48;W84;S14;W65;W79;W51;S9;S77;D51;S76;W16;W77;A90;S96;D78;S7;W71;D17;W10;W4;D96;S58;A88;S89;D41;W47;W13;S75;S85;W63;W87;S19;S80;W92;W95;W73;D43;D35;W60;S31;D72;A73;W90;S51;A55;S82;W81;S56;W83;W41;A77;S37;D63;A72;D44;W81;S26;S53;W32;A24;S90;S83;W34;D10;S53;D69;A87;W84;S32;D72;S63;D97;W27;D23;D25;D18;W86;D74;D42;A31;S1;D98;D76;S46;D67;W94;S6;S97;S40;W71;W12;D62;W57;A92;W59;W59;D15;A16;D21;S33;S45;S83;D89;W77;A93;S60;S6;W24;S93;D69;D79;S13;S8;W63;D99;S36;D35;W53;S5;S44;S19;D76;S14;A86;A86;W43;W65;D20;S19;W66;A54;A98;A17;D2;W98;W2;D77;A90;S8;S55;S67;W88;W19;S59;D9;S75;S56;W75;W54;A61;W47;S19;D67;S39;D55;S6;A89;A5;W58;W7;W88;W92;D85;S32;S32;A27;S42;D52;A55;S26;D27;W40;D76;A55;D38;W13;A71;D79;W59;A76;A33;A12;D1;D63;W63;W62;D7;W48;A84;D27;A80;D42;D27;D49;D4;D71;W90;W39;D24;W71;D16;S1;W88;W88;D25;D66;S39;S43;A99;A92;W19;W20;A90;A31;A6;A79;D4;D80;A77;D71;D36;S64;";
//        int x = 0;
//        int y = 0;
//        String[] strArr = str.split(";");
//        String regex = "[ADSW][0-9]+";
//        for(String s : strArr) {
//            if(s.length() > 0 || s.length() <= 3) {
//                if(s.matches(regex)) {
//                    String direct = s.substring(0,1);
//                    int distance = Integer.parseInt(s.substring(1));
//                    if(direct.equals("A")) {
//                        x -= distance;
//                    }
//                    if(direct.equals("D")) {
//                        x += distance;
//                    }
//                    if(direct.equals("W")) {
//                        y += distance;
//                    }
//                    if(direct.equals("S")) {
//                        y -= distance;
//                    }
//                }
//            }
//        }
//        char ch = 'a';
//        int N = 692;
//        int[] height = {692,160,459,759,192,1251,1070,77,365,428,291,1013,521,86,121,87,731,1249,238,861,605,780,103,501,1380,326,683,506,1067,949,625,148,1106,937,659,28,1059,826,98,938,1175,1270,161,790,939,1235,729,1232,1221,390,744,1245,1298,157,391,881,812,1202,714,1287,1126,140,783,717,144,889,795,713,12,151,137,458,374,38,256,1058,1052,642,557,240,294,473,1077,815,153,682,384,520,1016,821,556,418,890,1308,1042,704,1203,1362,168,822,1362,973,282,1256,1229,235,463,408,708,920,950,1193,285,805,408,445,1091,980,761,373,48,588,1086,933,476,874,1166,513,1165,532,129,135,1022,236,1266,1370,365,22,583,1261,756,908,1070,322,1148,1041,935,601,548,784,1270,1306,1166,31,203,1230,523,1336,590,596,673,199,456,827,544,48,270,249,336,209,926,464,402,1074,135,582,1014,750,758,509,380,925,598,241,706,768,729,467,741,1202,49,1206,297,827,1139,1100,372,312,1014,328,678,303,245,382,1228,7,339,215,1193,1214,1007,582,613,695,338,442,196,54,395,232,815,573,807,1199,519,1016,53,1267,835,1090,547,1052,1199,960,1192,841,1025,29,131,1291,1341,182,671,663,609,1177,905,86,907,55,693,1249,59,926,541,1245,1301,57,687,999,92,175,747,788,741,978,139,481,1362,741,419,1153,454,670,969,820,463,879,1075,783,304,1273,773,851,1088,1016,1267,803,589,122,357,5,106,396,98,1099,1152,176,960,1264,8,321,303,1201,832,151,877,977,178,1339,749,358,1192,111,1141,512,590,827,363,1232,685,933,975,1222,850,1348,1236,554,658,115,1337,1016,977,269,62,1086,291,29,99,683,824,1240,100,1,573,1357,149,364,790,536,40,1246,939,263,1161,462,1373,974,712,791,515,1126,370,1245,1052,856,657,1,1292,437,380,315,988,939,1068,618,362,407,1176,362,1206,27,1113,1065,1166,1193,942,637,443,1053,44,1366,200,680,450,636,1271,457,687,1327,1302,888,695,176,790,916,693,444,289,410,1291,1030,155,801,1129,478,1123,424,91,1057,1059,535,142,490,234,982,220,368,385,1203,854,63,50,454,440,1283,737,210,832,75,1045,1110,1326,1056,589,177,161,96,422,118,1352,1113,1295,1146,587,1330,1094,1356,575,724,90,196,766,481,657,79,219,548,392,157,120,678,672,1326,17,1065,704,583,52,589,1378,365,321,1305,463,668,1323,1331,1036,572,278,753,328,1046,1197,846,724,1170,1153,1154,980,614,335,1072,1221,810,129,521,227,723,1043,407,990,1030,239,814,1034,1287,546,759,1327,1140,1142,442,1135,656,1182,744,240,186,1024,274,1185,1226,744,1154,338,24,216,789,285,638,631,1125,945,1025,226,1026,138,1308,504,102,91,619,1186,891,259,982,336,1245,1280,657,243,417,1336,1281,222,95,1010,768,534,778,1033,876,1309,1289,519,1248,1199,39,169,248,207,416,1377,312,1261,96,1143,1289,709,211,1099,904,389,69,1126,784,662,279,94,1318,856,845,670,791,528,629,689,782,574,13,1152,393,1118,53,750,932,827,131,116,248,505,1059,300,91,1309,373,530,1216,310,652,75,1335,1221,273,285,404,511,417,95,443,1134,253,399,115,517,269,1307,1287,1337,875,536,985,692,252,552,1,1280,1043,1066,123,233,1351,858,216,307,1083,293,866,441,1287,633,615,1197,1365,1006,1259,1230,281,718,875,26,770,929,626,765,1132,1086,139,567,1171,766};
//        int min = 1000;
//        for (int j = 1; j < N - 1; j++) {
//            int maxInDcr = height[j];
//            int countInDcr = 0;
//            for (int i = j - 1; i >= 0; i--) {
//                if(height[i] >= maxInDcr) {
//                    countInDcr++;
//                } else {
//                    maxInDcr = height[i];
//                }
//            }
//
//            int minInIncr = height[j];
//            int countInIncr = 0;
//            for (int i  = j + 1; i < N ;i++) {
//                if (height[i] >= minInIncr) {
//                    countInIncr++;
//                } else {
//                    minInIncr = height[i];
//                }
//            }
//            if ((countInDcr + countInIncr) < min) {
//                min = countInDcr + countInIncr;
//            }
//        }
//
//        System.out.println(min);

//        System.out.println(x + "," + y);





        //System.out.println("100".matches("1+0+"));
        String str = "abcd123.4567.890.123abcd123.4567.890.123abcd123.4567.890.123";
        //Pattern pattern = Pattern.compile("([])")
        boolean flag = false;
        StringBuffer sb = new StringBuffer();
        List<String> list = new ArrayList<>();
        Map<Integer, String> res = new HashMap<>();
        for (char ch : str.toCharArray()){
            int asc = ch;
            if (asc >= 46 && asc <= 57) {
                if (!flag && asc >= 48 && asc <= 57) {
                    flag = true;
                    sb.append(ch);
                    continue;
                }
                if (flag && asc >= 48 && asc <= 57) {
                    sb.append(ch);
                    continue;
                }
                if (flag && asc == 46) {
                    sb.append(ch);
                    continue;
                }

                list.add(sb.toString());
                flag = false;
                sb = new StringBuffer();
            }
            if (flag) {
                list.add(sb.toString());
                flag = false;
                sb = new StringBuffer();
            }
        }
        if (flag) {
            list.add(sb.toString());
            flag = false;
            sb = new StringBuffer();
        }

        for (String s : list) {
            if (s.contains(".")) {
                if((s.length() - s.replace(".", "").length()) == 1) {
                    res.put(s.length(), s);
                } else {
                    String[] strArr = s.split("\\.");
                    for (int i = 0; i < strArr.length - 1; i++) {
                        res.put(strArr[i].length() + strArr[i+1].length() + 1, strArr[i] + "." + strArr[i+1]);
                    }
                }
            } else {
                res.put(s.length(), s);
            }
        }
        int maxLen = 0;
        for (int le : res.keySet()) {
            if (le > maxLen) {
                maxLen = le;
            }
        }
        System.out.println(res.get(maxLen));
    }
    private static String findPrimes(long num) {

        StringBuilder builder = new StringBuilder(128);
        long i = 2;
        while (i <= num) {
            // 每次的i一定是质数时才会满足
            // 因为如果是一个合数，那那它一定是由更小的质数相乘得来的，
            // 而在i前的质数已经全部被使用过了，不能再整除num了
            while (num % i == 0) {
                builder.append(i).append(' ');
                num /= i;
            }
            i++;
        }

        return builder.toString();
    }
}
//24点游戏算法：
//         输入四个数n1,n2,n3,n4，求解目标数T=24，以及一组计算操作符"+" "-" "*" "/" ，求所有由该组数字及操作符组成的多项式表达式集合，其值等于目标数T ，即T = 24。
//            算法思路如下：
//               1.在集合{n1,n2,n3，n4}中，首先取两个数字,如n1,n2,与操作符集合进行组合，分别得到一组表达式：n1*n2,n1+n2,n1-n2,n1/n2,n2-n1,n2/n1.(其中由于"-"和"/"操作符，左右互换会导致计算结果不同，所以在该组合中，包含"-"和"/"操作符的表达式各有两个，操作数先后顺序不同)；
//                2.对于新得到的每个表达式，都可以和原集合中剩下的元素，组合成新的集合组，同时，我将每次得到的表达式，都用"()"包住，以保证计算先后顺序：
//        {(n1*n2),n3,n4}，{(n1+n2),n3,n4}，{(n1-n2),n3,n4}，{(n1/n2),n3,n4}，{(n2-n1),n3,n4}，{(n2/n1),n3,n4}；
//               3.基于以上方法，对集合中所有元素进行两两组合，并与剩余元素形成新的集合。由此，我们得到了一组元素为k-1个的集合组
//              4.对新集合组中的每一个集合，重复以上1-3步，可得到一组包含k-2个元素的集合组...以此类推，最后会得到一组集合，其中每个集合都只包含一个元素，这个就是我们合成的最终表达式
//              5.对第四步得到的表达式集合进行求解，判断其是否等于目标数24，将符合条件的过滤出来，即得到所有满足条件的表达式。
//
//        计算字符串的距离：
//
//
//
//        迷宫问题：
//        import java.util.*;
//public class Main {
//    static int[][] move = {{0,1},{0,-1},{1,0},{-1,0}};
//    static void print(Node node, Node[] q) {
//        if(node.pre == -1) {
//            System.out.println("(" + node.x + ", " + node.y + ")");
//            return;
//        } else {
//            print(q[node.pre], q);
//            System.out.println("(" + node.x + ", " + node.y + ")");
//        }
//    }
//    static boolean check(int x,int y, int[][] map, int[][] vis, int n, int m) {
//        return x >= 0 && x < n && y >= 0 && y < m && vis[x][y] != 1 && map[x][y] != 1;
//    }
//    public static void main(String[] args) {
//        Scanner cin = new Scanner(System.in);
//        String[] numStrArr = cin.nextLine().split(" ");
//        int N = Integer.parseInt(numStrArr[0]);
//        int M = Integer.parseInt(numStrArr[1]);
//        int[][] map = new int[N][M];
//        int[][] vis = new int[N][M];
//        for(int i = 0;i < N;i++){
//            String str = cin.nextLine();
//            String[] strArr = str.split(" ");
//            for(int j = 0;j < M;j++) {
//                map[i][j] = Integer.parseInt(strArr[j]);
//            }
//        }
//        Node[] q = new Node[M * N];
//        int head = 0;
//        int tail = 0;
//        q[tail] = new Node(0,0,-1);
//        vis[0][0] = 1;
//        tail++;
//        while(head < tail) {
//            boolean flag = false;//找没找到终点
//            for(int i = 0;i < 4;i++) {
//                int nx = q[head].x + move[i][0];
//                int ny = q[head].y + move[i][1];
//                if(check(nx,ny,map,vis, N, M)) {
//                    vis[nx][ny] = 1;
//                    q[tail] = new Node(nx,ny,head);
//                    tail++;
//                }
//                if(nx == (N - 1) && ny == (M - 1)) {
//                    flag = true;
//                    break;
//                }
//            }
//            if(flag) {
//                print(q[tail - 1], q);
//                break;
//            }
//            head++;
//        }
//    }
//}
//class Node {
//    int x,y,pre;//来到此点的出发点
//    Node() {}
//    Node(int x,int y,int pre) {
//        this.x = x;
//        this.y = y;
//        this.pre = pre;
//    }
//}
//
//求最小公倍数：
//        import java.util.*;
//
//public class Main{
//    public static void main(String[] args){
//        Scanner sc = new Scanner(System.in);
//        int a = sc.nextInt();
//        int b = sc.nextInt();
//        int m = min(a, b);
//        int n = a * b / m;
//        System.out.print(n);
//    }
//    public static int min(int a, int b){
//        if(a < b){
//            int t = a;
//            a = b;
//            b = t;
//        }
//        while(b != 0){
//            if(a == b){
//                return a;
//            }else{
//                int k = a % b;
//                a = b;
//                b = k;
//            }
//        }
//        return a;
//    }
//}
//
//
//合唱队：
//
//
//        统计每个月的兔子总数：
//
//
//        将真分数分解为埃及分数：
//        1/2的来历是11/8+1=2，这个2做分母，得到一个埃及分数1/2。然后8/11-1/2=5/22，现在要把5/22分解。22/5+1=5，得到埃及分数1/5，5/22-1/5=3/110。110/3+1=37，得到埃及分数1/37，3/110-1/37=1/4070，结果刚好是一个埃及分数，故分解到此为止。
//
//        两个超长正整数相加：
//        1.首先将输入的两个字符串分别存入两个字符数组(a1和a2)中；
//
//        2.比较两个字符数组的长度，开辟一个新的数组(c1)长度为长度较大的字符数组长度+1(原因为若为实例此种情况，最终结果长度加1)；
//
//        3.新开辟两个char数组(b1和b2)长度分别与a1和a2相同，分别将a1与a2中数据反向存储(原因是a1和a2数组中数据为直接存储，不可直接相加，因按照数学知识加法是从个位开始，但若直接相加则长度较短的数组最高位与长度较长数组最高位同位，结果出现偏差。如：输入 123 12 ，正确结果为135，但若直接相加结果变为243);
//
//        4.将b1和b2数组中数据依次相加存储至c1数组中(相加分为两种情况：相同下标下均有元素相加后存储；相同下标下一个有一个没有，直接存入有元素即可)；
//
//        5.新创建数组c2，目的在于将c1数组反向存储。但一个大前提在于c1最后一个元素是否为0，若为0则c1长度减1后再将其反向存储于c2；若不为0，则直接将其反向存储于c2。
//
//        需要将c1反向存储的原因是相加后的结果也为反向的，但结果需正向输出(如输入123 12 被反向存储后为321 21 相加后得到的元素内容为：531 再次将其反转后才为正确结果135)。
//
//        判断c1最后一个元素是否为0的原因在于：c1数组开辟时比最长字符数组长度大1，若不考虑是否为0情况直接反转的话，输入123 12时得到的结果是0135，这明显是不符合数学定义的，故我们需要进行判断是否为0。
//
//        如输入 123 12 翻转后存储321 21 相加后 531 翻转后最终结果 135
//
//        如输入 999 1 翻转后存储999 1 相加后0001 翻转后最终结果 1000
//
//        6.最终将c2数组转换为String类型输出即可。
//
//
//
//        火车进站：
//
//        主要是这个题目没有说清楚，我举个例子：假设火车到站的顺序是1 2 3；
//        （1）这时候可以是1 到站后 马上就出站；然后2 进站，然后2出站；最后3进站，然后3出站；这时候出站顺序是 1 2 3；
//        （2）也可以是1 进站，2进站，这时候，必须2先出站，接着1出站。最后是3进站，然后3出站；这时候出站顺序是2 1 3；
//        （3）当然也可以是1 进站，2进站，3进站；然后必须是 3 先出站，2 次出站，1最后出站
//        解释一下为什么3 1 2不可以；
//        要是3先出站，必须是 1 进站 2进站 3进站；这时候3出站，但是这时候2在1的前面，必须是2先出站最后是1出站，因此3 1 2这个答案不行；
//
//
//        质数因子：
//        import java.util.ArrayList;
//        import java.util.LinkedList;
//        import java.util.List;
//        import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        while (scanner.hasNext()) {
//            long input = scanner.nextLong();
//            System.out.println(findPrimes(input));
//        }
//        scanner.close();
//    }
//
//
//    private static String findPrimes(long num) {
//        StringBuilder builder = new StringBuilder(128);
//        long i = 2;
//        while (i <= num) {
//            // 每次的i一定是质数时才会满足
//            // 因为如果是一个合数，那那它一定是由更小的质数相乘得来的，
//            // 而在i前的质数已经全部被使用过了，不能再整除num了
//            while (num % i == 0) {
//                builder.append(i).append(' ');
//                num /= i;
//            }
//            i++;
//        }
//
//        return builder.toString();
//    }
//}
//
//素数伴侣：
//        import java.util.ArrayList;
//        import java.util.Scanner;
//
//public class Main {
//
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        while(scanner.hasNext()){
//            int N = scanner.nextInt();
//            ArrayList<Integer> evens = new ArrayList<Integer>();//存放偶数
//            ArrayList<Integer> odds = new ArrayList<Integer>();//存放奇数
//            for (int i = 0; i < N; i++) {
//                int num=scanner.nextInt();
//                if(num%2==0){
//                    evens.add(num);
//                }
//                odds.add(num);
//            }
//            //最大的素数对也就是跟奇数的个数相等，因为素数一定是一个偶数加一个奇数
//            int result=0;
//            int matched[]=new int[evens.size()];
//            for(int i=0;i<odds.size();i++){
//                int used []=new int[evens.size()];
//                if(find(odds.get(i),used,matched,evens)){
//                    result++;
//                }
//            }
//            System.out.println(result);
//        }
//        scanner.close();
//
//    }
//
//    private static boolean find(Integer integer, int[] used, int[] matched,
//                                ArrayList<Integer> evens) {
//        for(int j=0;j<evens.size();j++){
//            if(isPrim(integer, evens.get(j)) && used[j]==0){
//                used[j]=1;
//                if(matched[j]==0 || find(matched[j], used, matched, evens)){
//                    matched[j]=integer;
//                    return true;
//                }
//            }
//
//        }
//        return false;
//    }
//
//    public static boolean isPrim(int num1, int num2) {
//        int sum = num1 + num2;
//        for (int i = 2; i < sum; i++) {
//            if (sum % i == 0) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//}
//
//sudoku：
//        import java.util.*;
//public class Main{
//    public static void main(String[] args){
//        Scanner sc=new Scanner(System.in);
//        int[][] sudoku=new int[9][9];
//
//        for(int i=0;i<9;i++){
//            for(int j=0;j<9;j++){
//                sudoku[i][j]=sc.nextInt();
//            }
//        }
//
//        fillVancancy(sudoku,0);
//
//        for(int i=0;i<9;i++){
//            for(int j=0;j<8;j++){
//                System.out.print(sudoku[i][j]+" ");
//            }
//            System.out.println(sudoku[i][8]);
//        }
//
//    }
//    private static boolean fillVancancy(int[][] sudoku,int r) {
//
//        if(r>8){
//            return true;
//        }
//
//        boolean findVancancy=false;
//        int row=r;
//        int col=0;
//        for(int i=r;i<9;i++){
//            for(int j=0;j<9;j++){
//                if(sudoku[i][j]==0){
//                    row=i;
//                    col=j;
//                    findVancancy=true;
//                    break;
//                }
//            }
//        }
//        if(findVancancy){
//            int[] used=new int[10];
//
//            for(int i=0;i<9;i++){
//                used[sudoku[row][i]]=1;
//                used[sudoku[i][col]]=1;
//            }
//
//            int x=row/3;
//            int y=col/3;
//            for(int i=x*3;i<(x+1)*3;i++){
//                for(int j=y*3;j<(y+1)*3;j++){
//                    used[sudoku[i][j]]=1;
//                }
//            }
//
//            for(int i=1;i<10;i++){
//                if(used[i]!=1){
//                    sudoku[row][col]=i;
//                    used[i]=1;
//                    if(fillVancancy(sudoku,r)){
//                        return true;
//                    }
//                    sudoku[row][col]=0;
//                    used[i]=0;
//                }
//            }
//            return false;
//        }else{
//            return true;
//        }
//
//
//    }
//
//}
//
