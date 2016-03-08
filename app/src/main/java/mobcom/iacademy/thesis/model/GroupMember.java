package mobcom.iacademy.thesis.model;

public class GroupMember {
    private String id;
    private String name = "";
    private String group;
    private String groupId;
    private String groupName;
    private boolean checked = false;

    public GroupMember() {
    }

    public GroupMember(String id, String name , String group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public GroupMember(String userId, String groupName, boolean checked) {
        this.id = userId;
        this.groupName = groupName;
        this.checked = checked;
    }

    public GroupMember(String objectId, String groupName, String groupId, boolean checked) {
        this.id = objectId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.checked = checked;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String toString() {
        return name;
    }

    public void toggleChecked() {
        checked = !checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
