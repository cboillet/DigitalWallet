package utc.digitalwallet.Beans;

public enum Rights {
    lifecycle(0x01), //delete and create the pot
    editUser(0x02),
    editMoneyRequest(0x04),
    debitor(0x08),
    creditor(0x16);

    private int numVal;

    Rights(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
    public void setNumVal(int numVal) {
        this.numVal = numVal;
    }
}

