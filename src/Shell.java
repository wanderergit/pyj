import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        while(true) {

            Scanner sc = new Scanner(System.in);
            System.out.print("pyj > ");
            String text = sc.nextLine();

            Node res = null;
            try {
                res = PyJ.run(text);
            } catch(Exception e){
                e.printStackTrace();
            }
            if(res != null) {
                System.out.println(res.toString());
            }
        }
    }
}
