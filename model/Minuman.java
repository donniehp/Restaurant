package restaurant.model;

public class Minuman extends MenuItem {
    private String jenisMinuman;

    public Minuman(String nama, double harga, String jenisMinuman) {
        super(nama, harga, "Minuman");
        this.jenisMinuman = jenisMinuman;
    }

    public String getJenisMinuman() { return jenisMinuman; }
    public void setJenisMinuman(String jenisMinuman) { this.jenisMinuman = jenisMinuman; }

    @Override
    public String tampilMenu() {
        return String.format("%s - Rp%.2f", getNama(), getHarga());
    }
}