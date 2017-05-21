package graduation.mo7adraty.models;

/**
 * Created by ahmed on 17/03/17.
 */

public class lectures {
    private String uid;
    private String name;
    private String place;
    private int time;
    private String inst;
    private String Section;
    private boolean isTemp;
    public String getName() {
        return name;
    }

    public String getSection() {
        return Section;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setSection(String section) {
        Section = section;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getInst() {
        return inst;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }
}
