import java.util.*;
public class Main{
    public static void main(String[] args) {
        Scanner scaner = new Scanner(System.in);
        Map<String, List<String>> map = new HashMap<>();
        while (scaner.hasNext()) {
            String str = scaner.nextLine();
            String[] strArr = str.split("\\.");
            if(map.containsKey(strArr[0])){
                map.get(strArr[0]).add(strArr[1]);
            } else {
                List<String> list = new LinkedList<>();
                list.add(strArr[1]);
                map.put(strArr[0], list);
            }
        }
        int count = 0;
        for (String str : map.keySet()) {
            List<String> strList = map.get(str);
            String first = strList.get(0);
            for(String s : strList) {
                if(s.equals(first)) {
                    count++;
                }
            }
        }
        System.out.println(count);
        scaner.close();
    }
}