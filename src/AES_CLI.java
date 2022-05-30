import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;


public class AES_CLI {

	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		//get file path
		Scanner s = new Scanner(System.in);
		System.out.println("Enter file path for AES input");
		String f = s.next();
		File file = new File(f);
		Scanner sc = new Scanner(file);;
		String key = sc.next();
		String message = sc.next();
		String message2 = sc.next();
		String hexKey = convertToHex(key);
		String hexMessage = convertToHex(message);
		String hexMessage2 = convertToHex(message2);
		//get number of times to run AES
		System.out.println("enter positive digit (number of times to run AES Encryption)");
		int num = s.nextInt();
		
		AES aes = new AES();
		for(int i=0;i<num;i++) {
			hexMessage = aes.run(hexKey, hexMessage);	
			hexMessage2 = aes.run(hexKey, hexMessage2);	
			System.out.println("RUN " + i + " for message 1 : " + hexMessage);
			System.out.println("RUN " + i + " for message 2: " + hexMessage2);
		}
		System.out.println("\n\n\nFinal encoded message 1 in hex: " + hexMessage);
		String bin1 = new BigInteger(hexMessage, 16).toString(2);
		String bin2 = new BigInteger(hexMessage2, 16).toString(2);
		
		bin1 = normalizeBinary(bin1);
		bin2 = normalizeBinary(bin2);
		System.out.println("Final encoded message 1 in binary: " + bin1);
		System.out.println("\n\n\nFinal encoded message 2 in hex: " + hexMessage2);
		System.out.println("Final encoded message 2 in binary: " + bin2);
	}
	
	private static String normalizeBinary(String bin) {
		while(bin.length()<128) {
			bin = "0" + bin;
		}
		return bin;
		
	}

	private static String convertToHex(String str) {
		String res = "";

		for (int i = 0; i < str.length(); i += 8) {
			String hex = binaryToHex((str.substring(i, 8+i)));
			res += hex;
		}
		return res;
	}
	
	private static String binaryToHex(String st) {
		int decimal = Integer.parseInt(st,2);
		String hexStr = Integer.toString(decimal,16);
		if(hexStr.length() == 1) {
			hexStr = "0" + hexStr;
		}
		return hexStr;
	}
}