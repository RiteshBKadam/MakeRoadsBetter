public class FactorialByRecursion {
    public static void main(String[] Args){
        int n=5;
        int facto=1;
        int ans= factorial(5);
        System.out.println(ans);
    }
    static int factorial(int a){
        if(a==0 || a==1)
            return 1;
        return a*factorial(a-1);
    }
}

