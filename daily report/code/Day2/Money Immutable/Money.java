import java.math.BigDecimal;
import java.util.*;

public final class Money{
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency){
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
    }



    public Money add(Money other){

        if(this.currency.equals(other.currency)){
            return new Money(this.amount.add(other.amount),other.currency);

        }else{
            throw new IllegalArgumentException("Khác loại tiền tệ!");

        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}