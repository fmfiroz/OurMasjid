package pnpmsjm.com.ourmasjid;

public class Members {
    private String Name;
    private String Designation;
    private String Mobile_No;
    private String Purl;

    public Members() {
        // Firebase-এর জন্য প্রয়োজনীয় ডিফল্ট কনস্ট্রাক্টর
    }

    public Members(String Name, String Designation, String Mobile_No, String Purl) {
        this.Name = Name;
        this.Designation = Designation;
        this.Mobile_No = Mobile_No;
        this.Purl = Purl;
    }

    public String getName() {
        return Name;
    }

    public String getDesignation() {
        return Designation;
    }

    public String getMobile_No() {
        return Mobile_No;
    }

    public String getPurl() {
        return Purl;
    }

}