import java.util.*;

public class BFSDemo {
    static int[][] move = {{0,1},{0,-1},{1,0},{-1,0}};
    static void print(Node node, Node[] q) {
        if(node.pre == -1) {
            System.out.println("(" + node.x + ", " + node.y + ")");
            return;
        } else {
            print(q[node.pre], q);
            System.out.println("(" + node.x + ", " + node.y + ")");
        }
    }

    static boolean check(int x,int y, int[][] map, int[][] vis, int n, int m) {
        return x >= 0 && x < n && y >= 0 && y < m && vis[x][y] != 1 && map[x][y] != 1;
    }


    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        String[] numStrArr = cin.nextLine().split(" ");
        int N = Integer.parseInt(numStrArr[0]);
        int M = Integer.parseInt(numStrArr[1]);
        int[][] map = new int[N][M];
        int[][] vis = new int[N][M];
        for(int i = 0;i < N;i++){
            String str = cin.nextLine();
            String[] strArr = str.split(" ");
            for(int j = 0;j < M;j++) {
                map[i][j] = Integer.parseInt(strArr[j]);
            }
        }
//        for(int i = 0;i < N;i++){
//            for(int j = 0;j < M;j++) {
//                map[i][j] = cin.nextInt();
//            }
//        }
        Node[] q = new Node[20];
        int head = 0;
        int tail = 0;
        q[tail] = new Node(0,0,-1);
        vis[0][0] = 1;
        tail++;
        while(head < tail) {
            boolean flag = false;//找没找到终点
            for(int i = 0;i < 4;i++) {
                int nx = q[head].x + move[i][0];
                int ny = q[head].y + move[i][1];
                if(check(nx,ny,map,vis, N, M)) {
                    vis[nx][ny] = 1;
                    q[tail] = new Node(nx,ny,head);
                    tail++;
                }
                if(nx == (N - 1) && ny == (M - 1)) {
                    flag = true;
                    break;
                }
            }
            if(flag) {
                print(q[tail - 1], q);
                break;
            }
            head++;
        }
    }
}
class Node {
    int x,y,pre;//来到此点的出发点
    Node() {}
    Node(int x,int y,int pre) {
        this.x = x;
        this.y = y;
        this.pre = pre;
    }
}