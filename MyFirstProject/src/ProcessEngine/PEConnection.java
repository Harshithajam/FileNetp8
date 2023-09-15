package ProcessEngine;

import java.io.IOException;

import com.filenet.api.core.Factory;
import com.filenet.api.core.Factory.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.Properties;

import filenet.vw.api.VWSession;

public class PEConnection {

	static VWSession mySession;

	public static VWSession getPEConnection() throws IOException {
		

		String uri = "http://ibmdemo16:9080/wsi/FNCEWS40MTOM/";
		String username = "p8admin";
		String password = "Password1";
		String connectionPointName = "WFCP";

		VWSession mysession = new VWSession();

		mysession.setBootstrapCEURI(uri);

		mysession.logon(username, password, connectionPointName);

		System.out.println("isLogOn():" + mysession.isLoggedOn());
		// mySession.logoff();
		return mysession;
	}

	public static void main(String[] args) {
		PEConnection conn = new PEConnection();
		try {
			conn.getPEConnection();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
