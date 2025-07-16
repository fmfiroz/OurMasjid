package pnpmsjm.com.ourmasjid;

public class Member {
    private String Name;
    private String Designation;
    private String Mobile_No;
    private String Purl;

    public Member() {
        // Firestore এর জন্য ডিফল্ট কনস্ট্রাক্টর
    }

    public Member(String name, String description, String mobile_no, String photo) {
        this.Name = name;
        this.Designation = description;
        this.Mobile_No = mobile_no;
        this.Purl = photo;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Designation;
    }

    public String getMobile_no() {
        return Mobile_No;
    }

    public String getPhoto() {
        return Purl;
    }
}
