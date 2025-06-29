public class ChecckSortedOrNot {
    public static void main(String[] args){
        int[] arr={1,2,3,4,5,6,11,15};
        int length=arr.length;
        int last=length-1;
        boolean ans=CheckArray(arr,last);
        System.out.println(ans);
    }
    static boolean CheckArray(int[] arr, int last){
        if(last==0)
            return true;
        if(arr[last]<arr[last-1])
            return false;
        return CheckArray(arr,last-1);

    }
}
