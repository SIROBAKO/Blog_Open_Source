import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);

		// 첫줄 M, N 만큼 크기의 행렬 지정
		String text = scan.nextLine();
		String[] temp = text.split(" ");
		int[][] MN = new int[Integer.parseInt(temp[0])][Integer.parseInt(temp[1])];

		// 처음 M x N 만큼의 데이터는 단순 입력
		for (int i = 0; i < Integer.parseInt(temp[0]); i++) {
			for (int j = 0; j < Integer.parseInt(temp[1]); j++) {
				MN[i][j] = scan.nextInt();
			}
		}

		// 다음 M x N 만큼의 데이터는 누적 합
		for (int i = 0; i < Integer.parseInt(temp[0]); i++) {
			for (int j = 0; j < Integer.parseInt(temp[1]); j++) {

				// 출력 형식 조정
				if (j == Integer.parseInt(temp[1]) - 1) {
					System.out.println(scan.nextInt() + MN[i][j]);
				} else {
					System.out.print((scan.nextInt() + MN[i][j]) + " ");

				}
			}
		}
	}

}