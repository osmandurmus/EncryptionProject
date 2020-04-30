public class DiagonalOrder {

    // A utility function to find min of two integers
    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    // A utility function to find min of three integers
    public static int min(int a, int b, int c) {
        return min(min(a, b), c);
    }

    // A utility function to find max of two integers
    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    // The main function that prints and return given matrix in diagonal order
    public static int[] diagonalOrder(int matrix[][]) {
        int ROW=matrix.length;
        int COL=matrix[0].length;

        int dizi[]=new int[ROW*COL];
        int diziIndex=0;

        // There will be ROW+COL-1 lines in the output
        for (int line = 1; line <= (ROW + COL - 1); line++) {

            // Get column index of the first element in this
            // line of output.The index is 0 for first ROW
            // lines and line - ROW for remaining lines
            int start_col = max(0, line - ROW);

            // Get count of elements in this line. The count
            // of elements is equal to minimum of line number,
            // COL-start_col and ROW
            int count = min(line, (COL - start_col), ROW);

            // Print elements of this line
            for (int j = 0; j < count; j++) {
                dizi[diziIndex++] = matrix[min(ROW, line) - j - 1]
                        [start_col + j];
         //       System.out.print(matrix[min(ROW, line) - j - 1][start_col + j] + " ");
            }

            // Print elements of next diagonal on next line
         //   System.out.println();
        }
        return dizi; // Diagonal şekilde sıralanmış tek boyutlu dize
    }
}
