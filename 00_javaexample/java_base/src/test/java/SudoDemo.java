import java.util.*;
public class SudoDemo{
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        int[][] sudoku=new int[9][9];

        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                sudoku[i][j]=sc.nextInt();
            }
        }

        fillVancancy(sudoku,0);

        for(int i=0;i<9;i++){
            for(int j=0;j<8;j++){
                System.out.print(sudoku[i][j]+" ");
            }
            System.out.println(sudoku[i][8]);
        }

    }
    private static boolean fillVancancy(int[][] sudoku,int r) {

        if(r>8){
            return true;
        }

        boolean findVancancy=false;
        int row=r;
        int col=0;
        for(int i=r;i<9;i++){
            for(int j=0;j<9;j++){
                if(sudoku[i][j]==0){
                    row=i;
                    col=j;
                    findVancancy=true;
                    break;
                }
            }
        }
        if(findVancancy){
            int[] used=new int[10];

            for(int i=0;i<9;i++){
                used[sudoku[row][i]]=1;
                used[sudoku[i][col]]=1;
            }

            int x=row/3;
            int y=col/3;
            for(int i=x*3;i<(x+1)*3;i++){
                for(int j=y*3;j<(y+1)*3;j++){
                    used[sudoku[i][j]]=1;
                }
            }

            for(int i=1;i<10;i++){
                if(used[i]!=1){
                    sudoku[row][col]=i;
                    used[i]=1;
                    if(fillVancancy(sudoku,r)){
                        return true;
                    }
                    sudoku[row][col]=0;
                    used[i]=0;
                }
            }
            return false;
        }else{
            return true;
        }
    }

}