import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws NumberFormatException, IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        // 수 두개 입력
        String nums = br.readLine();
        
        // 입력된 값을 공백으로 분리하여 배열에 저장
        String[] tmp = num.split(" ");
        
        // 문자열로 저장된 숫자를 정수형으로 변환
        int num1 = Integer.parseInt(tmp[0]);
        int num2 = Integer.parseInt(tmp[1]);

        // 최대공약수(GCD)를 찾음
        // 큰 숫자부터 시작하여 num1과 num2를 모두 나누어 떨어지게 하는 첫 번째 수를 찾음
        for (int i = num1; i > 0; i--) {
            if (num1 % i == 0 && num2 % i == 0) {
                sb.append(i + "\n");
                break;
            }
        }

        // i가 증가하는 while 루프
        for (int i = 1;; i++) {

            // num1 x i 가 num2의 배수일 경우
            // 최소공배수(LCM)
            if ((num1 * i) % num2 == 0) {
                sb.append(num1 * i);
                break;
            }

        }

        // 결과 출력
        System.out.println(sb);

    }

}