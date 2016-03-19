package mobcom.iacademy.thesis.routine.model;

public class RoutineBean {


    String id;
    String routineName;
    String routineAdmin;

    public RoutineBean(String id, String routineName, String routineAdmin) {
        this.id = id;
        this.routineName = routineName;
        this.routineAdmin = routineAdmin;
    }

    public RoutineBean() {
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public String getRoutineAdmin() {
        return routineAdmin;
    }

    public void setRoutineAdmin(String routineAdmin) {
        this.routineAdmin = routineAdmin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.getRoutineName();
    }
}
