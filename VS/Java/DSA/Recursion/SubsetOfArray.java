import java.util.Arrays;

public class SubsetOfArray {
    public static void main(String[] args) {
        int[] arr = {1, 2};
        int length = arr.length;
        int index = 0;
        String newArray="";
        allSubsets(arr, newArray, index);
    }

    private static void allSubsets(int[] arr, String newArray, int index) {
        if(index==arr.length){
            System.out.println(newArray);
            return;
        }

        allSubsets(arr,newArray+arr[index]+" ",index+1);

        allSubsets(arr,newArray,index+1);




    }

}
