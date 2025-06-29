public class Subsets {
    public static void main(String[] args) {
        String str = "abc";
        printSubsets(str, "", 0);
    }

    static void printSubsets(String str, String current, int index) {
        if (index == str.length()) {
            System.out.println(current);
            return;
        }

        printSubsets(str, current + str.charAt(index), index + 1);

        printSubsets(str, current, index + 1);
    }
}
