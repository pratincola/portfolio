DistComp1
=========
### What?
- Distributed Computing setup 1. More info below.

### When?
- Delivery date: Feb 4, 2014

***

### What does this code do?
- The goal of this project is to display client server programming with TCP and UDP sockets. 

### Commands?
- The server accepts only the following calls from a client:
1. reserve <name> – inputs the name of a person and reserves a seat against this name. If the theater does not have enough seats(completely booked), no seat is assigned and the command responds with message: ‘Sold out - No seat available’. If a reservation has already been made under that name, then the command responds with message: ‘Seat already booked against the name provided’.
Otherwise, a seat is reserved against the name provided and the client is relayed a message: ‘Seat assigned to you is <seat-number>’.
2. bookSeat <name> <seatNum> – behaves similar to reserve command, but imposes an additional constraint that a seat is reserved if and only if there is no existing reservation against name and the seat having the number seatNum is available. If there is no existing reservation but seatNum is not available, the response is: ‘<seatNum> is not available’.
3. search <name> – returns the seat number reserved for name. If no reservation is found for name the system re- sponds with a message: ‘No reservation found for <name>’.
4. delete <name> – frees up the seat allocated to that person. The command returns the seat number that was released. If no existing reservation was found, responds with: ‘No reservation found for <name>’.

***

### How to run the server:
./RUN.jar (or .sh, or .java) -s <positive integer to be uses as the total number of seats in the theater> -p <port to listen on>

Example: `./RUN.jar -s 100  -p 2014`

Example: `./ReservationService.java -s 100  -p 2014`

### How to run the client:
./RUN.jar (or .sh, or .java) -c -t <for tcp>| -u <for udp> -ip <server-ip> -p <server-port>

Example: `./RUN.jar -c -u -ip 127.0.0.1 -p 2014` 

Example: `./ResurvationService.java -c -u -ip 127.0.0.1 -p 2014`  (start the client and connect to the server,
using the udp protocol, running at the ip address for the localhost and the port 2014).
