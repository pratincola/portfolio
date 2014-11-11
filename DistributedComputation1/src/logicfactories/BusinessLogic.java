/**
 * Created with IntelliJ IDEA.
 * User: prateek
 * Date: 2/2/14
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
package logicfactories;


public class  BusinessLogic {
    int theater_cap;
    static int curr_cap ;
    String [] theater_map;
    final int EXIT_VAL = -1;

    private static final BusinessLogic singleton = new BusinessLogic();
    private BusinessLogic(){ }
    /* Static 'instance' method */
    public static BusinessLogic getInstance( ) {
        return singleton;
    }

    public void initBusinessLogic(int capacity) {
        this.theater_cap = capacity;
        this.theater_map  = new String[theater_cap];
        this.curr_cap = 0;

        for (int i = 0; i < theater_map.length; i++) {
            theater_map[i] = "empty";
        }
    }

    public byte[] makeResponse(String s) {
        String out = null;
        String[] command = s.split(" ");
        if(command.length < 2 || command.length > 3){
            out = "Invalid Format";
        }
        else {
            if(command[0].equalsIgnoreCase("reserve"))
                out = reserve_call(command[1]);
            else if (command[0].equalsIgnoreCase("bookSeat"))
                out = bookSeat_call(command[1], command[2]);
            else if (command[0].equalsIgnoreCase("search"))
                out = search_call(command[1]);
            else if (command[0].equalsIgnoreCase("delete"))
                out = delete_call(command[1]);
            else
                System.err.println("Invalid Command");
        }

        return out.getBytes();
    }

    public synchronized String reserve_call(String name){
        String res = "Sold out - No seat available";
        if(curr_cap == theater_cap){
            res = "Sold out - No seat available";
        }
        else if(searchName(name)!=EXIT_VAL) {
            res = "Seat already booked against the name provided";
        }
        else {
            for (int i =0; i < theater_map.length; i++){
                if(theater_map[i].equalsIgnoreCase("empty")){
                    curr_cap++;
                    theater_map[i] = name;
                    res = "Seat assigned to you is " + String.valueOf(i);
                    break;
                }
            }
        }
        return res;
    }

    public synchronized String bookSeat_call(String name, String seatNum){
        String res = String.valueOf(seatNum) + " is not available";
        if( !theater_map[Integer.valueOf(seatNum)].equalsIgnoreCase("empty")){}
        else if (curr_cap == theater_cap){
            res = "Sold out - No seat available";
        }
        else {
            theater_map[Integer.valueOf(seatNum)] = name;
            curr_cap++;
            res = "Seat assigned to you is " + Integer.valueOf(seatNum);
        }
        return res;
    }

    public synchronized String search_call(String name){
        String res = "No reservation found for" + name;
        int seat =0;
        seat = searchName(name) ;
        if(seat !=EXIT_VAL){
            res = name + "Seat already booked against the name provided: " + String.valueOf(seat);
        }
        return res;
    }

    public synchronized int searchName(String name){
        int seat = EXIT_VAL;
        for(int i = 0 ; i < theater_map.length; i++){
            if( !theater_map[i].equalsIgnoreCase("empty")){
                if(theater_map[i].equalsIgnoreCase(name)){
                seat = i;
                break;
                }
            }
        }
        return seat;
    }

    public synchronized String delete_call(String name){
        String res = "No reservation found for" + name;
        int seat = 0;
        seat = searchName(name);
        if(seat !=EXIT_VAL && curr_cap !=0){
            res = "Free-ing seat with number: " + String.valueOf(seat);
            curr_cap --;
            theater_map[seat] = "empty";
        }
        return res;
    }
}
