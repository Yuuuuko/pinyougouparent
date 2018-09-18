import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test1 {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String s = encoder.encode("123456");
        System.out.println(s);
    }
}
