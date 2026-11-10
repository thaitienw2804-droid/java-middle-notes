public final class Money{
    private final BigDecimal amount;
    private final String currency; 

    Money(BigDecimal amount, String currency){
        if(Objects.requireNonNull(amount)){
            if(Objects.requireNonNull(currency)){
                this.amount = amount;
                this.currency = currency;
                
            }
        }
    }
    public Money add(Money other){
        if(this.currency.equals(other.currency)){
            throw IllegalArgumentException("Khác loại tiền tệ!")
        }else{
            return Money(this.amount.add(other.amount),other.currency)
        }
    }

}