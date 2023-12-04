#include <iostream>
using namespace std;

class Simplex {
private:
	int* z;
	int zSize;
	int rows;
	int cols;
	double** matrix;
	double** nonBasicMatrix;
	int* checkZ;
	int* b;
	int optimalResult;
public:
	Simplex(int rows, int cols) {
		this->rows = rows;
		this->cols = cols;
	}
	void inputObjectiveFunction() {
		//zSize = cols;
		z = new int[cols];
		for (int i = 0; i < cols; i++) {
			cin >> z[i];
		}
	}
	void inputConstraints() {
		initializeMatrix();
		for (int i = 1; i < rows; i++) {
			for(int j=0;j<cols;j++)
				cin >> matrix[i][j];
		}
	}
	void initializeB() {
		b = new int[rows];
		for (int i = 0; i < rows; i++) {
			b[i] = matrix[i][cols - 1];
		}
	}
	void initializeMatrix() {
		matrix = new double* [rows];
		for (int i = 0; i < rows; i++) {
			matrix[i] = new double[cols];
			matrix[0][i] = z[i];
		}


	}
	void initializeNonBasicMatrix() {
		nonBasicMatrix = new double* [rows - 1];
		for (int i = 0; i < rows; i++) {
			nonBasicMatrix[i] = new double[rows - 1];
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i == j) {
					nonBasicMatrix[i][j] = 1;
				}
				else {
					nonBasicMatrix[i][j] = 0;
				}
			}
		}
	}
	void convertObjectFunctionIntoNegative() {
		for (int i = 0; i < cols; i++) {
			if (z[i] > 0) {
				z[i] = z[i] * (-1);
			}
		}
	}
	int chooseColumn() {
		//int z[3] = { -2, -4, -5 };
		int col = 0;
		int min = matrix[0][0];
		for (int i = 0; i < cols; i++) {
			if (matrix[0][i] < min) {
				min = matrix[0][i];
				col = i;
			}
		}
		return col;
	}
	int chooseRow() {
		int row = 1;
		int col = chooseColumn();
		double min = abs(matrix[1][cols-1] / matrix[1][col]);
		for (int i = 1; i < rows; i++) {
			if (abs (matrix[i][cols - 1] / matrix[i][col]) < min) {
				min = matrix[i][cols - 1];
				row = i;
			}
		}
		return row;
	}
	bool isOptimal() {
		for (int i = 0; i < cols; i++) {
			if (matrix[0][i] < 0) {
				return false;
			}
		}
		return true;
	}


	void displayMatrix() {
		cout << endl;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				cout << matrix[i][j] << " ";
			cout << endl;
		}
		cout << endl;
	}
	void displayNonBasixMatrix() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				cout << nonBasicMatrix[i][j] << " ";
			cout << endl;
		}
		cout << endl;
	}

	void displayObjectiveFunction() {
		for (int i = 0; i < cols; i++) {
			cout << z[i] << " ";
		}
		cout << endl;
	}
	void displayB() {
		for (int i = 0; i < cols; i++) {
			cout << b[i] << " ";
		}
		cout << endl;
	}
	void makeOptimal() {
		int iteration = 1;
		int row = chooseRow();
		int col = chooseColumn();
		while (!isOptimal()) {
			cout << "\tIteration " << iteration << "\n";
			make1(row, col);
			makeUpperAndLowerEnteriesZero(row, col);
			displayWholeMatrix();
			row = chooseRow();
			col = chooseColumn();
			iteration++;
		}
		optimalResult = matrix[0][cols - 1];
		cout << "Optimal Value = " << optimalResult << endl;

	}
	void make1(int row, int col) {
		int divisionVar = matrix[row][col];
		//int nonDivVar= nonBasicMatrix[row][i];
		for (int i = 0; i < cols; i++) {
				matrix[row][i] = matrix[row][i] / divisionVar; 
				nonBasicMatrix[row][i] = nonBasicMatrix[row][i] / divisionVar;
		}
	}
	void makeUpperAndLowerEnteriesZero(int row, int col) {
		int temp;
		int div = matrix[row][col];
		int postivitePrev=-1;
		int negativePrev = -1;
		for (int i = 0; i < rows; i++) {
			temp = matrix[i][col];
		//	cout << "temp = " <<temp << "  ";
			if (matrix[i][col] != 0) {
				for (int j = 0; j < cols; j++) {
					if (!isBasic(i, j, row, col)) {
						//if (matrix[i][col] > 0 || postivitePrev==0) {
						//	cout << "Matrix row j = " << matrix[row][j] << endl;
							matrix[i][j] = matrix[i][j] - (temp * matrix[row][j]);
							nonBasicMatrix[i][j] = nonBasicMatrix[i][j] - (temp * matrix[row][j]);
							postivitePrev = matrix[i][j];
						//}
						/*else if (matrix[i][col] < 0 || negativePrev ==0) {
							matrix[i][j] = matrix[i][j] + (temp * matrix[row][j]);
							nonBasicMatrix[i][j] = nonBasicMatrix[i][j] + (temp * matrix[row][j]);
							negativePrev = matrix[i][j];
						}*/
					}
				}
			}
		}
	}
	void displayWholeMatrix() { 
		cout << endl;
		for (int i = 0; i < cols+rows-2; i++) {
			cout << "x" << i + 1 << "\t";
		}
		cout << "b";
		cout << endl;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols - 1; j++) {
				cout << matrix[i][j] << "\t";
			}
			for (int j = 0; j < cols - 1; j++) {
				cout << nonBasicMatrix[i][j] << "\t";
			}
			cout << matrix[i][cols - 1];
			cout << endl;
		}

	}
	bool isBasic(int i, int j, int row, int col) {
		if (i == row) {
			return true;
		}
		return false;
	}
};

int main() {
	int noOfconstraints;
	int noOfXs;
	cout << "Enter number of constraints: ";
	cin >> noOfconstraints;
	cout << "Enter number of variables: ";
	cin >> noOfXs;
	Simplex obj(noOfconstraints + 1, noOfXs + 1);
	cout << "Please Enter your Objective Function: ";
	obj.inputObjectiveFunction();
	obj.convertObjectFunctionIntoNegative();
	cout << "Please input your constraints: ";
	obj.inputConstraints();
	obj.initializeNonBasicMatrix();
	obj.initializeB();
	cout << "Your Matrix is: ";
	obj.displayWholeMatrix();
	obj.makeOptimal();
	return 0;
}
