package ProcessEngine;



import com.filenet.api.core.Connection;

import filenet.vw.api.VWSession;

public class TestPE {

	public static void main(String[] args) {
		
		GetPEConn peConn= new GetPEConn();
		 VWSession pevw= peConn.getPESession();
		 GetWorkItems getWorkItem = new GetWorkItems();
		// DispatchWorkItem getWorkItem=new DispatchWorkItem();
		 getWorkItem.getWorkItem(pevw,"BankAccountOpening");
	}

}
