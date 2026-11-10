import com.sun.source.tree.NewArrayTree;

import java.math.BigDecimal;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Money money = new Money(new BigDecimal(100),"a");
        Money B = money.add(new Money(new BigDecimal(100),"a"));
        System.out.println(B.getAmount());
    }
}