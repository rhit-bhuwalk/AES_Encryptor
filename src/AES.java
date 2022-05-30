import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//C:\Users\bhuwalk\Desktop\AESINPUT.txt
public class AES {

	String [][] key_matrix;
	String [][] message_matrix;
	String [][] w_matrix;
	SBox sbox;
	int[] r = { 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36 };
	
	
	public AES() {
		key_matrix = new String[4][4];
		message_matrix = new String[4][4];
		w_matrix = new String[4][44];
		sbox = SBox.getInstance();
	}
	
	
	public String run(String keyInHex, String messageInHex) {
				
		int iterate=0;
		
		//populate initial key_matrix, message_matrix, and w_matrix
		for(int col=0;col<4;col++) {
			for(int row = 0; row<4; row++) 
			{
				char c1 = keyInHex.charAt(iterate);
				char cm1 = messageInHex.charAt(iterate);
				iterate++;
				char c2 = keyInHex.charAt(iterate);
				char cm2 = messageInHex.charAt(iterate);
				iterate++;
				String str = c1 + ""+ c2 + "";
				String str_m = cm1 + ""+ cm2 + "";
				this.key_matrix[row][col] = str;	   
				this.message_matrix[row][col] = str_m;	//(M)
				this.w_matrix[row][col] = str;   //W(i)
			}
		}
		
		expandWmatrix();	
		String [][] k0 = getKey(0);
		String [][] M1 = ark(message_matrix, k0);
		
		for(int i=1;i<10;i++) {
			String [][] SB_M1 = subBytes(M1);
			String [][] SR = shiftRows(SB_M1);
			String [][] MC = mixedColumns(SR);
			String [][] keyy = getKey(i);
			M1 = ark(MC, keyy);
		}
		String [][] SB_last = subBytes(M1);
		String [][] SR_last = shiftRows(SB_last);
		String [][] k10 = getKey(10);
		M1 = ark(SR_last, k10);
		M1 = normalize(M1);
		return hexEncodeMatrix(M1);	
	}
	
	//ensures that all characters in hex matrix are hex encoded (eg converts 9 to 09)
	private String [][] normalize(String [][] m) {
		for(int row = 0;row<4;row++) {
			for(int col=0;col<4;col++) {
				if(m[row][col].length() == 1) {
					m[row][col] = "0" + m[row][col];
				}
			}
		}
		return m;
	}
	

	// converts our matrix into hex
	private String hexEncodeMatrix(String [][] m1) {
		String hex = "";
		for(int col=0;col<4;col++) {
			for(int row=0;row<4;row++) {
				hex = hex + (m1[row][col]);
			}
		}
		return hex;
	}
	
	//get K(key)
	private String [][] getKey(int keyNumber) {
		String [][] key = new String[4][4];
		for(int row=0;row<4;row++) {
			int count = 0;
			for(int col=4*keyNumber;col<4*keyNumber+4;col++) {
				key[row][count] =  w_matrix[row][col];
				count++;
				}
		}
		return key;
	}
	
	
	public String [][] subBytes(String [][] m) {
		for(int row = 0;row<4;row++) {
			for(int col=0;col<4;col++) {
				int s_val = Integer.parseInt(m[row][col],16);   //convert value of matrix into int  
				s_val = sbox.box[s_val];
				m[row][col] = Integer.toHexString(s_val);
				}
	}
		return m;
	}
	
	public String [][] mixedColumns(String [][] m) {
		int [][] fix = {{2,3,1,1},{1,2,3,1},{1,1,2,3},{3,1,1,2}};
		String [][] newMatrix = new String[4][4];
		
		for(int row = 0;row<4;row++) {
			for(int col = 0;col<4;col++) {
				int val = multiply(fix[row], getColumn(m, col));
				newMatrix[row][col] = Integer.toHexString(val);
			}
		}
		return newMatrix;
	}
	
	
	public int multiply(int[] fix, String[] m) {  
		int[] values = new int[4];
		for(int i = 0;i<4;i++) {
				int val = Integer.parseInt(m[i],16);				
				
				if(fix[i]  == 3) {
					int temp = val * 2;
					if(temp>255) {
						temp = temp - 256;
						temp = temp ^ 27;
					}
					temp = temp ^ val;
					val = temp;
				}
				else {
				val = val * fix[i];
				if(val>255) {
					val = val - 256;
					val = val ^ 27;
				}
				}
				values[i] = val;
		}
		
		int count = ((values[0] ^ values[1]) ^ values[2]) ^ values[3];
		//System.out.println(count);
		return count;
	}
	
	
	public String [][] shiftRows(String [][] m) {
		
		for(int row = 0;row<4;row++) {
			String a = m[row][0];
			String b = m[row][1];
			String c = m[row][2];
			String d = m[row][3];
			if(row == 0) 
				continue;
			if(row == 1) {
				m[row][0] = b;
				m[row][1] = c;
				m[row][2] = d;
				m[row][3] = a;
				}
			if(row == 2) {
				m[row][0] = c;
				m[row][1] = d;
				m[row][2] = a;
				m[row][3] = b;
			}
			
			if(row == 3) {
				m[row][0] = d;
				m[row][1] = a;
				m[row][2] = b;
				m[row][3] = c;
			}
			}
		return m;
	}
	
	
	
	
	public String [][] ark(String [][] m, String [][] k) {
		String [][] arked = new String[4][4];
		for(int row = 0;row<4;row++) {
			for(int col=0;col<4;col++) {
				int m_val = Integer.parseInt(m[row][col],16);  
				int k_val = Integer.parseInt(k[row][col],16); 
				int xord = m_val ^ k_val;
				arked[row][col] = Integer.toHexString(xord);  
			}
		}
		return arked;
	}
	
	//prints matrix for debugging purposes
	private void prettyPrintMatrix(String [][] arr) {
		for (int row = 0; row < arr.length; row++)//Cycles through rows
		{
		  for (int col = 0; col < arr[row].length; col++)//Cycles through columns
		  {
			
		    System.out.print(arr[row][col] + "    "); //change the %5d to however much space you want
		  }
		  System.out.println(); //Makes a new row
		}
	}
	
	//gets column of double dimensional array
	public String[] getColumn(String[][] array, int index){
	    String[] column = new String[4]; // Here I assume a rectangular 2D array! 
	    for(int i=0; i<column.length; i++){
	       column[i] = array[i][index];
	    }
	    return column;
	}
	

	
//	expand w_matrix to 44 columns to get round keys
	private void expandWmatrix() {
		for(int col=4;col<44;col++) {
			if(col%4 == 0) {
				String[] col_w1 =  getColumn(w_matrix, col-1);   //31 32 33 34 -->  34 31 32 33
				String a = "0x" + col_w1[0];
				String b = "0x" + col_w1[1];
				String c = "0x" + col_w1[2];
				String d = "0x" + col_w1[3];
				
				int b_byte = sbox.box[Integer.decode(b)];   // this represents T(w-1)
				b_byte = b_byte ^ r[col/4];
				int c_byte = sbox.box[Integer.decode(c)];
				int d_byte = sbox.box[Integer.decode(d)];
				int a_byte = sbox.box[Integer.decode(a)];
				
				int [] tw = {b_byte, c_byte, d_byte, a_byte};
				
				for(int row = 0;row<4;row++) {
					String hex1 = w_matrix[row][col-4];
					int dec1=Integer.parseInt(hex1,16);  
					int dec2 = tw[row];
					int xord = dec1 ^ dec2;
					String hex = Integer.toHexString(xord);  
					w_matrix[row][col] = hex;
				}		
			}
			else {
				for(int row = 0; row<4; row++) {
					
					String hex1 = w_matrix[row][col-4];
					String hex2 = w_matrix[row][col-1];
					int dec1=Integer.parseInt(hex1,16);  
					int dec2=Integer.parseInt(hex2,16);
					int xord = dec1 ^ dec2;
					String hex = Integer.toHexString(xord);  
					w_matrix[row][col] = hex;
				}
			}
		}
	}

}
