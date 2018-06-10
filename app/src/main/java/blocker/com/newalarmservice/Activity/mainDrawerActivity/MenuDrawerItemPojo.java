package blocker.com.newalarmservice.Activity.mainDrawerActivity;


public class MenuDrawerItemPojo {
    private int imgId;
    private String name;
    private boolean clickedstatus;
    private int position;

    public MenuDrawerItemPojo(int imgId, String name, boolean clickedstatus, int pos) {
        this.imgId = imgId;
        this.name = name;
        this.clickedstatus = clickedstatus;
        this.position = pos;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClickedstatus() {
        return clickedstatus;
    }

    public void setClickedstatus(boolean clickedstatus) {
        this.clickedstatus = clickedstatus;
    }
}
