package restaurant.model;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Pesanan {
    private ArrayList<MenuItem> itemDipesan;

    public Pesanan() {
        itemDipesan = new ArrayList<>();
    }

    public void tambahItem(MenuItem item) {
        tambahItem(item, 1);
    }

    public void tambahItem(MenuItem item, int qty) {
        if (item instanceof Diskon) {
            itemDipesan.add(item);
            return;
        }
        for (MenuItem existing : itemDipesan) {
            if (existing.getNama().equalsIgnoreCase(item.getNama()) && !(existing instanceof Diskon)) {
                existing.setQuantity(existing.getQuantity() + qty);
                return;
            }
        }
        item.setQuantity(qty);
        itemDipesan.add(item);
    }

    public void hapusItem(int index) {
        if (index >= 0 && index < itemDipesan.size()) {
            itemDipesan.remove(index);
        }
    }

    public ArrayList<MenuItem> getItemDipesan() {
        return itemDipesan;
    }

    public void reset() {
        itemDipesan.clear();
    }

    public double hitungTotal() {
        double subtotal = 0.0;
        Diskon diskonItem = null;

        for (MenuItem item : itemDipesan) {
            if (item instanceof Diskon) {
                if (diskonItem == null) {
                    diskonItem = (Diskon) item;
                }
            } else {
                subtotal += item.getHarga() * item.getQuantity();
            }
        }

        double total = subtotal;
        if (diskonItem != null) {
            double potongan = diskonItem.isPersentase() ? subtotal * diskonItem.getDiskon() / 100.0
                    : diskonItem.getDiskon();
            total = subtotal - potongan;
            if (total < 0)
                total = 0.0;
        }
        return total;
    }

    /** Hitung total akhir termasuk pajak 10% dan service charge Rp20.000 */
    public double hitungTotalBayar() {
        double afterDiskon = hitungTotal();
        double pajak = afterDiskon * 0.10;
        double serviceCharge = 20000;
        return afterDiskon + pajak + serviceCharge;
    }

    /** Akses publik ke formatRupiah untuk digunakan GUI */
    public String formatRupiahPublic(double amount) {
        return formatRupiah(amount);
    }

    public String generateStruk() {
        return generateStruk(0);
    }

    public String generateStruk(double pembayaranCash) {
        // Lebar struk: 60 karakter (cocok untuk Monospaced 13pt)
        final int W = 60;
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");

        // ========== Header ==========
        sb.append(center("RESTORAN", W)).append("\n");
        sb.append(center("NIKMAT LEZAT", W)).append("\n");
        sb.append(center("Alamat: Denpasar - Bali", W)).append("\n");
        sb.append(center("No. Telp & WA : 08123456789", W)).append("\n");
        sb.append(center("email : info@nikmatlezat.com", W)).append("\n");
        sb.append(center("https://nikmatlezat.com", W)).append("\n");
        sb.append(center(sdf.format(new Date()), W)).append("\n");
        sb.append("=".repeat(W)).append("\n");

        // Kolom: Item (25) | Qty (3) | Harga (11) | Total (11) = 48-1sp
        sb.append(String.format("%-32s %3s %11s %11s\n", "Item", "Qty", "Harga", "Total"));
        sb.append("-".repeat(W)).append("\n");

        double subtotal = 0.0;
        Diskon diskonItem = null;

        for (MenuItem item : itemDipesan) {
            if (item instanceof Diskon) {
                if (diskonItem == null)
                    diskonItem = (Diskon) item;
                continue;
            }
            double itemTotal = item.getHarga() * item.getQuantity();
            String hargaStr = "Rp " + formatRupiah(item.getHarga());
            String totalStr = "Rp " + formatRupiah(itemTotal);

            // Nama item bisa lebih dari 20 char, potong jika perlu
            String nama = item.getNama();
            if (nama.length() > 20) {
                sb.append(nama).append("\n");
                sb.append(String.format("%-32s %3d %11s %11s\n", "", item.getQuantity(), hargaStr, totalStr));
            } else {
                sb.append(String.format("%-32s %3d %11s %11s\n",
                        nama, item.getQuantity(), hargaStr, totalStr));
            }
            subtotal += itemTotal;
        }

        // ========== Subtotal & Diskon ==========
        sb.append("-".repeat(W)).append("\n");
        sb.append(String.format("%-38s %21s\n", "Subtotal", "Rp " + formatRupiah(subtotal)));

        double total = subtotal;
        if (diskonItem != null) {
            double potongan = diskonItem.isPersentase()
                    ? subtotal * diskonItem.getDiskon() / 100.0
                    : diskonItem.getDiskon();
            sb.append(String.format("%-38s %21s\n", "Diskon (" + diskonItem.getNama() + ") ",
                    "Rp " + formatRupiah(potongan)));
            total = subtotal - potongan;
            if (total < 0)
                total = 0;
            sb.append(String.format("%-38s %21s\n", "Subtotal setelah Diskon", "Rp " + formatRupiah(total)));
        }

        // ========== Pajak & Total Bayar ==========
        double pajak = total * 0.10;
        sb.append(String.format("%-38s %21s\n", "Pajak (10%)", "Rp " + formatRupiah(pajak)));
        // sb.append("=".repeat(W)).append("\n");
        double serviceCharge = 20000;
        sb.append(String.format("%-38s %21s\n", "Service Charge", "Rp " + formatRupiah(serviceCharge)));
        sb.append("=".repeat(W)).append("\n");
        double totalBayar = total + pajak + serviceCharge;
        sb.append(String.format("%-38s %21s\n", "TOTAL BAYAR", "Rp " + formatRupiah(totalBayar)));
        sb.append("=".repeat(W)).append("\n");

        // ========== Pembayaran Cash & Kembalian ==========
        if (pembayaranCash > 0) {
            double kembalian = pembayaranCash - totalBayar;
            sb.append(String.format("%-38s %21s\n", "Pembayaran (Cash)", "Rp " + formatRupiah(pembayaranCash)));
            if (kembalian >= 0) {
                sb.append(String.format("%-38s %21s\n", "Kembalian", "Rp " + formatRupiah(kembalian)));
            } else {
                sb.append(String.format("%-38s %21s\n", "UANG KURANG", "Rp " + formatRupiah(-kembalian)));
            }
            sb.append("-".repeat(W)).append("\n");
        }

        // ========== Footer ==========
        sb.append("\n");
        sb.append(center("Terima kasih atas kunjungan Anda", W)).append("\n");
        sb.append("-".repeat(W)).append("\n");
        if (subtotal >= 50000) {
            sb.append(center("!!! S E L A M A T !!!", W)).append("\n");
            sb.append(center("Anda Mendapatkan", W)).append("\n");
            sb.append(center("Promo Buy 1 Get 1 Free pada Kategori Minuman", W)).append("\n");
            sb.append(center("Tunjukkan struk ini saat pembelian selanjutnya", W)).append("\n");
            sb.append(center("Berlaku di seluruh Outlet Nikmat Lezat seluruh Indonesia", W)).append("\n");
            sb.append(center("Syarat dan Ketentuan Berlaku :", W)).append("\n");
            sb.append(center("Berlaku untuk item dengan harga yang sama atau lebih rendah", W)).append("\n");
            sb.append(center("Promo tidak dapat digabungkan dengan promo lain", W)).append("\n");
            sb.append(center("Promo berlaku sampai dengan 7 hari setelah tanggal pembelian", W)).append("\n");
            sb.append(center("Promo tidak berlaku untuk kelipatan pembelian", W)).append("\n");
        }
        return sb.toString();
    }

    private String center(String text, int width) {
        if (text.length() >= width)
            return text;
        int pad = (width - text.length()) / 2;
        return " ".repeat(pad) + text;
    }

    private String formatRupiah(double amount) {
        long val = Math.round(amount);
        String s = String.valueOf(val);
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (count > 0 && count % 3 == 0)
                result.insert(0, ".");
            result.insert(0, s.charAt(i));
            count++;
        }
        return result.toString();
    }

    public void simpanStruk(String namaFile) {
        simpanStruk(namaFile, 0);
    }

    public void simpanStruk(String namaFile, double pembayaranCash) {
        String struk = generateStruk(pembayaranCash);
        try (PrintWriter writer = new PrintWriter(new FileWriter(namaFile, true))) {
            writer.println(struk);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan struk: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public String muatRiwayat(String namaFile) {
        StringBuilder riwayat = new StringBuilder();
        File file = new File(namaFile);
        if (!file.exists()) {
            return "Belum ada riwayat pesanan.";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = reader.readLine()) != null) {
                riwayat.append(baris).append("\n");
            }
        } catch (IOException e) {
            return "Gagal memuat riwayat: " + e.getMessage();
        }
        return riwayat.toString();
    }
}