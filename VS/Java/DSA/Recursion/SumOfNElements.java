public class SumOfNElements {
    public static void main(String[] args){
        int n =5;
        int ans=sum(5);
        System.out.println(ans);
    }
    static int sum(int a){
        if(a==1)
            return 1;
        return a+sum(a-1);
    }
}
