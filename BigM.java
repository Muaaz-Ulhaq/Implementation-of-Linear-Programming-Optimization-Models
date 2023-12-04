
import java.io.*;
import java.util.*;

public class BigM {
	
    static float[][] matrix;
    static String[] rowVariables;       
    static String[] basicVariables;    
    static boolean isMinimization = false; 
    static List<Float> zj = new ArrayList<>();
    static List<Integer> cj = new ArrayList<>();
    static boolean canPrint = true;
    static List<Integer> cb = new ArrayList<>();
    static List<Float> xb = new ArrayList<>();
    static List<String> b = new ArrayList<>();
    static List<String> variables = new ArrayList<>();
    static List<String> artificialVariables = new ArrayList<>();
    static List<Float> variablesDiff = new ArrayList<>();
    static int m = 10000;
    
    public static void initializeVariables(int row, int col) {

        basicVariables = new String[col+1];
        basicVariables[0] = "c";
        for (int i=0; i<col; i++) {
            basicVariables[i+1] = "s" + (i+1);
        }
        rowVariables = new String[row+col+2];
        rowVariables[0] = "z";
        for (int i=0; i<row; i++) {
            rowVariables[i+1] = "x" + (i+1);
        }
        for (int i=0; i<col; i++) {
            rowVariables[row+i+1] = "s" + (i+1);
        }
        rowVariables[row+col+1] = "b";
    }
    
    public static boolean isNotOptimal() {
        calculateDifference();
        boolean state = false;
        for (int i=0; i<variablesDiff.size(); i++) {
            if (variablesDiff.get(i) < 0) {
                state = true;
                break;
            }
        }
        return state;
    }
    
    public static void makeEnteriesZeroAndOne(int row, int minRow) {

        float entry = matrix[minRow][row];
        xb.set(minRow, xb.get(minRow)/entry);
        for (int i=0; i<cj.size(); i++) {
            matrix[minRow][i] = matrix[minRow][i] / entry;
        }
        for (int i=0; i<matrix.length; i++) {
            if (i != minRow) {
                float temp = -matrix[i][row];
                for (int j=0; j<cj.size(); j++) {
                    matrix[i][j] = temp*matrix[minRow][j] + matrix[i][j];
                }
                xb.set(i, temp*xb.get(minRow) + xb.get(i));
            }
        }
    }
        
    public static void bigMOptimality() {
        System.out.println("Select the Type of the Problem:\n" +"Maximization\tPress 1\n" + "Minimization\tPress 2!");
        Scanner inputValues = new Scanner(System.in);
        int type = inputValues.nextInt();
        System.out.print("Enter number of constraints: ");
        int cols = inputValues.nextInt();
        System.out.print("Enter numberr of variables: ");
        int rows = inputValues.nextInt();
        int[] z = new int[rows];
        System.out.println("Please Enter your objective function:");
        for (int i=0; i<rows; i++) {
            if(type==1) {
            	z[i] = inputValues.nextInt() ;
            	
            }
            else {
            	z[i]=- inputValues.nextInt();
            	isMinimization=true;
            }
            String str = "x" + (i+1);
            variables.add(str);
        }

        for (int i=0; i<z.length; i++) {
             cj.add(z[i]);
        }
        int index = rows;
        List<List<Integer>> tempList = new ArrayList<>();
        int var = 1;
        int art = 1;
        for (int j=0; j<cols; j++) {
            System.out.println("Please enter LHS values of your constraints " + (j+1) + ": ");
            List<Integer> left = new ArrayList<>();
            for (int i=0; i<rows; i++) {
                int num = inputValues.nextInt();
                left.add(num);
            }

            System.out.println("Select Inequality: \n" + " <=\tPress 1\n" + " >=\tPress 2\n" + " =\tPress 3");
            int choice = inputValues.nextInt();
            System.out.print("Please enter b of your constraint: ");
            int bRight = inputValues.nextInt();
            xb.add((float) Math.abs(bRight));

            for (int i=0; i<index; i++) {
                left.add(0);
            }
            if(bRight < 0) { 
                for (int i=0; i<left.size(); i++) {
                    left.set(i, -left.get(i));
                }
                switch(choice) {
                case 1:
                	left.set(index++, -1); // to add a slack variable
                    cj.add(0);

                    left.set(index++, 1);
                    cj.add(-m);

                    String s = "s" + (var++);
                    variables.add(s);

                    String a = "a" + (art++);
                    variables.add(a);
                    break;
                case 2:
                	left.set(index++, 1);  // to add a surplus variable
                    cj.add(0);

                    String s1 = "s" + (var++);
                    variables.add(s1);
                    break;
                default:
                	 left.set(index++, 1);
                     cj.add(-m);
                     String a1 = "a" + (art++);
                     variables.add(a1);
                     break;

                }
                
            } else {
            	 switch(choice) {
                 case 1:
                	 left.set(index++, 1); // to add slack
                     cj.add(0);
                     String s = "s" + (var++);
                     variables.add(s);
                     break;
                 case 2:
                	 left.set(index++, -1); // to add surplus
                     cj.add(0);
                     left.set(index++, 1);
                     cj.add(-m);

                     String s1 = "s" + (var++);
                     variables.add(s1);

                     String a = "a" + (art++);
                     variables.add(a);
                     break;
                 default:
                	 left.set(index++, 1);
                     cj.add(-m);
                     String a1 = "a" + (art++);
                     variables.add(a1);
                     break;

                 }
               
            }
            tempList.add(left);
        }
       
        matrix = new float[cols][tempList.get(tempList.size()-1).size()];
        for (int i=0; i<tempList.size(); i++) {
            for (int j=0; j<tempList.get(i).size(); j++) {
                matrix[i][j] = tempList.get(i).get(j);
            }
        } 

        for (int i=0; i<matrix.length; i++) {
            for (int j=rows; j<cj.size(); j++) {
                if(matrix[i][j] == 1.0) {
                    b.add(variables.get(j));
                    cb.add(cj.get(j));
                }
            }
        }

        for (int i=0; i<cj.size(); i++) {
            if(cj.get(i) == -m) {
                artificialVariables.add(variables.get(i));
            }
        }
        makeOptimal();
        if(canPrint) {
            display();
        }
    }
    /*public void displayMatrix() {
    	System.out.println(Arrays.deepToString(matrix)); 
    }*/
    public static void display() {

        boolean notPrinted = true;
        for (int i=0; i<artificialVariables.size(); i++) {
            for (int j=0; j<b.size(); j++) {
                if(artificialVariables.get(i).equals(b.get(j)) && xb.get(j)>0) {
                    notPrinted = false;
                    break;
                }
            }
        }
        if (!notPrinted) {
            System.out.println("No Feasible Solution Exists");

        } else {

            System.out.println("Basic Feasible Solution");

            for (int i=0; i<variables.size(); i++) {
                boolean state = false;

                for (int j=0; j<b.size(); j++) {
                    if(variables.get(i).equals(b.get(j))) {
                        System.out.println(variables.get(i) + " = " + xb.get(j));
                        state = true;
                        break;
                    }

                }

                if(!state) {
                    System.out.println(variables.get(i) + " = " + 0);
                }
            }

            float optimalValue = 0;
            for (int i=0; i<xb.size(); i++) {
                optimalValue += cb.get(i)*xb.get(i);
            }

            if (!isMinimization) {
                System.out.println("Optimal Value= " + optimalValue);
            } else {
                System.out.println("Optimal Value= " + -optimalValue);
            }

        }
    }

    public static void makeOptimal() {

        int iteration = 1;
        while (isNotOptimal() && iteration < 4) {
            int col = getCol();
            float minRatio = Float.MAX_VALUE;
            int min_index = 0;
            boolean state = false;
            for (int j=0; j<matrix.length; j++) {

                if (matrix[j][col] > 0) {
                    state = true;
                    float rowRatio = xb.get(j) / matrix[j][col];

                    if(rowRatio < minRatio) {
                        minRatio = rowRatio;
                        min_index = j;
                    }
                }
            }
            if(!state) {
                System.out.println("Unbounded Solution");
                canPrint = false;
                break;

            } else {

                System.out.println("\tIteration " + iteration);
                System.out.println("Entering Basic Variable is: " + variables.get(col));
                System.out.println("Leaving Basic Variable is: " + b.get(min_index));
                b.set(min_index, variables.get(col));
                cb.set(min_index, cj.get(col));
                makeEnteriesZeroAndOne(col, min_index);
                iteration++;

            }
            zj = new ArrayList<>();
            variablesDiff = new ArrayList<>();
        }
    }

    public static int getCol() {
        int col = 0;
        float min = variablesDiff.get(0);
        for (int i=0; i<variablesDiff.size(); i++) {
            if (variablesDiff.get(i) < min) {
                col = i;
                min = variablesDiff.get(i);
            }
        }
        return col;
    }
    public static void calculateDifference() {

        for (int i=0; i<cj.size(); i++) {
            float val = 0;
            for (int j=0; j<matrix.length; j++) {
                val += cb.get(j)*matrix[j][i];
            }
            zj.add(val);
            variablesDiff.add(zj.get(i) - cj.get(i));
        }
    }

    public static void main(String[] args) {
        bigMOptimality();
    }
}
