package restaurant.model;

public class Diskon extends MenuItem {
    private double diskon;
    private boolean isPersentase;

    public Diskon(String nama, double diskon, boolean isPersentase) {
        super(nama, 0.0, "Diskon");
        this.diskon = diskon;
        this.isPersentase = isPersentase;
    }

    public double getDiskon() { return diskon; }
    public void setDiskon(double diskon) { this.diskon = diskon; }
    public boolean isPersentase() { return isPersentase; }
    public void setPersentase(boolean persentase) { isPersentase = persentase; }

    @Override
    public String tampilMenu() {
        return String.format("%s - Potongan %s%.2f",
                getNama(), isPersentase() ? "" : "Rp", diskon);
    }
}