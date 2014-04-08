package pt.utl.ist.util;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamReplicator extends OutputStream {
	OutputStream[] streams;
	
	OutputStreamReplicator(OutputStream... streams){
		this.streams = streams;
	}

	@Override
	public void write(int arg0) throws IOException {
		for(OutputStream o: streams)
			o.write(arg0);
	}

}
