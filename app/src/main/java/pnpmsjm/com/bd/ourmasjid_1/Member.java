package pnpmsjm.com.bd.ourmasjid_1;

public class Member {
    private String name;
    private String description;
    private String mobile_no;
    private String photo;

    public Member() {
        // Firestore এর জন্য ডিফল্ট কনস্ট্রাক্টর
    }

    public Member(String name, String description, String mobile_no, String photo) {
        this.name = name;
        this.description = description;
        this.mobile_no = mobile_no;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getPhoto() {
        return photo;
    }
}
