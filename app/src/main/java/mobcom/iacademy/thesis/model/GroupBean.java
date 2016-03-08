package mobcom.iacademy.thesis.model;

public class GroupBean {


    String id;
    String groupName;
    String groupAdmin;
    String groupId;

    public GroupBean(String id, String groupName, String groupAdmin) {
        this.id = id;
        this.groupName = groupName;
        this.groupAdmin = groupAdmin;
    }

    public GroupBean(String id, String groupName, String groupAdmin, String groupId) {
        this.id = id;
        this.groupName = groupName;
        this.groupAdmin = groupAdmin;
        this.groupId = groupId;
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

    public String getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.getGroupName();
    }
}
