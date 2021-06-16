import java.util.LinkedList;
import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        while(true) {

            Scanner sc = new Scanner(System.in);
            System.out.print("pyj > ");
            String text = sc.nextLine();

            LinkedList<Token> res = null;
            try {
                res = PyJ.run(text);
            } catch(Exception e){
                System.out.println(e.toString());
            }
            if(res != null) {
                System.out.println(res.toString());
            }
        }
    }
}
