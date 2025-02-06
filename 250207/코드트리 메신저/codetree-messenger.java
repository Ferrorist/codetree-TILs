import java.io.*;
import java.util.*;

public class Main {
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static StringBuilder out = new StringBuilder();

    private static int roomCount = 0, cmdCount = 0;
    private static int alarmCount = 0;
    private static int[] parentsNum; // 각 채팅방의 부모를 저장함.
    private static List<ChatRoom> roomList = new ArrayList<>();

    private static final int INIT = 100, ALARM = 200, POWER = 300, CHANGE = 400, LOOKUP = 500;

    public static void main(String[] args) throws Exception {
        initArguments();
        startProgram();
        System.out.println(out);
    }


    private static void initArguments() throws Exception {
        StringTokenizer st = new StringTokenizer(in.readLine());
        roomCount = Integer.parseInt(st.nextToken());
        cmdCount = Integer.parseInt(st.nextToken());

        parentsNum = new int[roomCount + 1];
    }


    private static void startProgram() throws Exception {
        for(int i = 0; i < cmdCount; i++) {
            processCommand(in.readLine());
        }
    }

    private static void processCommand(String command) {
        String[] cmdValues = command.split(" ");
        int cmd = Integer.parseInt(cmdValues[0]);
        switch(cmd) {
            case INIT:
                initRooms(cmdValues);
                break;
            case ALARM:
                changeAlarm(cmdValues);
                break;
            case POWER:
                changePower(cmdValues);
                break;
            case CHANGE:
                changeParents(cmdValues);
                break;
            case LOOKUP:
                lookupAlarmCount(cmdValues);
                break;
        }
    }

    private static void initRooms(String[] cmdValues) {
        roomList.clear();
        for(int i = 0; i <= roomCount; i++) {
            roomList.add(new ChatRoom());
        }

        for(int i = 1; i <= roomCount; i++) {
            int parents = Integer.parseInt(cmdValues[i]);
            int power = Integer.parseInt(cmdValues[i + roomCount]);

            parentsNum[i] = parents;
            roomList.get(i).setPower(power);
            roomList.get(parents).addChild(i);
        }
    }

    private static void changeAlarm(String[] cmdValues) {
        int target = Integer.parseInt(cmdValues[1]);
        roomList.get(target).changeAlarm();
    }

    private static void changePower(String[] cmdValues) {
        int target = Integer.parseInt(cmdValues[1]);
        int power = Integer.parseInt(cmdValues[2]);

        roomList.get(target).setPower(power);
    }

    private static void changeParents(String[] cmdValues) {
        int target1 = Integer.parseInt(cmdValues[1]);
        int target2 = Integer.parseInt(cmdValues[2]);
        changeParents(target1, target2);
    }

    private static void changeParents(int target1, int target2) {
        int target1_parent = parentsNum[target1];
        int target2_parent = parentsNum[target2];

        roomList.get(target1_parent).removeChild(target1);
        roomList.get(target2_parent).removeChild(target2);

        roomList.get(target1_parent).addChild(target2);
        roomList.get(target2_parent).addChild(target1);

        int temp = parentsNum[target1];
        parentsNum[target1] = parentsNum[target2];
        parentsNum[target2] = temp;
    }

    private static void lookupAlarmCount(String[] cmdValues) {
        alarmCount = 0;
        int target = Integer.parseInt(cmdValues[1]);

        for(Integer childRoom : roomList.get(target).getChildren()) {
            checkAlarm(childRoom, 1);
        }

        out.append(alarmCount).append("\n");
    }

    private static void checkAlarm(int roomNum, int distance) {
        ChatRoom room = roomList.get(roomNum);
        if(!room.getAlarm()) return;

        if(room.getPower() >= distance) alarmCount += 1;

        for(Integer childRoom : room.getChildren()) {
            checkAlarm(childRoom, distance + 1);
        }
    }

}

class ChatRoom {
    private boolean alarm;
    private int authorityPower;
    private List<Integer> children;

    ChatRoom() {
        alarm = true;
        authorityPower = 0;
        children = new ArrayList<>();
    }

    ChatRoom(int authorityPower) {
        alarm = true;
        this.authorityPower = authorityPower;
        children = new ArrayList<>();
    }

    public boolean getAlarm() {
        return alarm;
    }

    public void changeAlarm() {
        alarm = !(alarm);
    }

    public int getPower() {
        return authorityPower;
    }

    public void setPower(int authorityPower) {
        this.authorityPower = authorityPower;
    }

    public List<Integer> getChildren() {
        return this.children;
    }

    public void addChild(int roomNum) {
        children.add(roomNum);
    }

    public void removeChild(int roomNum) {
        children.remove(new Integer(roomNum));
    }
}