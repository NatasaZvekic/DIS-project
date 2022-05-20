package core.rates;

public class Rate {

    private int bookId;
    private int rateId;
    private int rate;

    public Rate() {
    }

    public Rate(int bookId, int rateId, int rate) {
        this.bookId = bookId;
        this.rateId = rateId;
        this.rate = rate;
    }

    public int getRateId() {
        return rateId;
    }

    public void setRateId(int rateId) {
        this.rateId = rateId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

}
