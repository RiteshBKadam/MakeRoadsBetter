public class FindMax {

        public static void main(String[] args){
            int[] arr={111,2,3,4,5555,6};
            int length=arr.length;
            int last=length-1;
            int max=arr[last];
            int ans=CheckMax(arr,last,max);
            System.out.println(ans);
        }
        static int CheckMax(int[] arr, int last, int max) {
            if (last == 0)
                return max;
            if (max < arr[last - 1]){
                max = arr[last-1];
            }
            return CheckMax(arr,last-1,max);

        }

}
