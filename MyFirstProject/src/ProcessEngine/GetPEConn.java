package ProcessEngine;



import org.apache.log4j.*;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWWorkObject;

public class GetPEConn {
	static Logger log = Logger.getLogger(GetPEConn.class.getName());
	 public static VWSession getPESession()
	    {
	      String strAppURI1="http://ibmdemo16:9080/wsi/FNCEWS40MTOM/";
	      log.info("[ENTER PEManager getPESession()]");
	      VWSession peSession = null;

	      System.setProperty("java.security.auth.login.config","C:\\opt\\jaas.conf.WSI");
	    

	      try
	      {
	        peSession = new VWSession();
	        peSession.setBootstrapCEURI(strAppURI1);
	    
	        peSession.logon("p8admin", "Password1", "WFCP");
	        String sn = peSession.getPEServerName();
	        log.info("++++++++++++++++"+ sn);

	        System.out.println("PE session established:"+peSession);
	   
	        
	      }
	      catch (VWException e) {
	    	  log.info("Exception occured while establishing PE session." );
	          e.printStackTrace();

	      }
	      log.info("[Exit PEManager getPESession()]");
	    return peSession;
	  }

	//Close VWSession

	public static void closePESession(VWSession peSession)
	  {
		log.info("[Enter closePESession]");
	    
	    try {
	      if (peSession != null)
	        peSession.logoff();
	    }
	    catch (VWException e) {
	    	log.info(e.getMessage());
	    }

	    log.info("[Exit : closePESession]");
	  } 


}

