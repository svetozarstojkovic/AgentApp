package zeromq;

import org.zeromq.ZMQ;

public class ZeroMQSubscriber {
	
	public ZeroMQSubscriber() {
		ZMQ.Context context = ZMQ.context(1);
		 
        // Socket to talk to server
        System.out.println("Connecting to hello world server");
 
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.connect ("tcp://localhost:5555");
 
        for(int requestNbr = 0; requestNbr != 10; requestNbr++) {
            String request = "Hello" ;
            System.out.println("Sending Hello " + requestNbr );
            socket.send(request.getBytes (), 0);
 
            byte[] reply = socket.recv(0);
            System.out.println("Received " + new String (reply) + " " + requestNbr);
        }
 
        socket.close();
        context.term();
	}
	
	public static void protocolBuffers() {
		final ZMQ.Context context = ZMQ.context(1);

		// Connect our subscriber socket
		final ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.setIdentity("hello".getBytes());

		// Synchronize with the publisher
		final ZMQ.Socket sync = context.socket(ZMQ.PUSH);

		subscriber.subscribe("".getBytes());
		subscriber.connect("tcp://localhost:5562");
		sync.connect("tcp://localhost:5561");

		sync.send("".getBytes(), 0);
	
		final long start = System.currentTimeMillis();
		int i;
		for (i = 0; i != 10; i++) {
			final byte[] rawBytes = subscriber.recv(0);
			try {
				System.out.println(rawBytes);
				System.out.println(rawBytes.length);
				//final Test data = Protos.Test.parseFrom(rawBytes); // deserialization
				//System.out.println(data);
			} catch (final Exception e) {
				System.err.println(e);
			}
		}
		final long end = System.currentTimeMillis();
		final long diff = (end - start);
		System.out.println("time taken to receive messages " + i + " is :"
				+ diff);
	}

	public static void characters() {

		// Prepare our context and subscriber
		final ZMQ.Context context = ZMQ.context(1);
		final ZMQ.Socket subscriber = context.socket(ZMQ.SUB);

		subscriber.connect("tcp://localhost:5563");
		subscriber.subscribe("B".getBytes());
		while (true) {
			// Read envelope with address
			final String address = new String(subscriber.recv(0));
			// Read message contents
			final String contents = new String(subscriber.recv(0));
			System.out.println(address + " : " + contents);
		}
	}

}
