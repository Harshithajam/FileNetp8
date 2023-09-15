package ProcessEngine;



import org.apache.log4j.*;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterElement;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWWorkObject;

public class GetWorkItems {
	static Logger log = Logger.getLogger(GetWorkItems.class.getName());
	VWRosterElement rosterItem;
	
	public void getWorkItem(VWSession peSession,String rosterName){
	      VWWorkObject vwwObj=null;
	      try{
	     
	     
	          VWRoster roster=peSession.getRoster(rosterName);
	          int queryFlags=VWRoster.QUERY_NO_OPTIONS;
	          String abc ="DDE5AE99ED365346950C5545B7515C5A";
	          String filter="F_WobNum="+abc;

	          //Change filter based on requirement

	       
	          VWRosterQuery query=roster.createQuery(null, null, null, queryFlags,null, null, VWFetchType.FETCH_TYPE_ROSTER_ELEMENT);
	          log.info("Total records for Work ITem: "+query.fetchCount());
	         
	          while(query.hasNext()){
	        	  rosterItem= (VWRosterElement) query.next();
	               
	        	  log.info("WF  Number: "+rosterItem.getWorkflowNumber() );
	        	  log.info("WOB Number: "+rosterItem.getWorkObjectNumber());
	        	  log.info("F_StartTime: " + rosterItem.getFieldValue("F_StartTime") );
	        	  log.info("F_Subject: " + rosterItem.getFieldValue("F_Subject") );
	        	  
	          }
	         
	      }catch(VWException vwe){
	    	  log.info("Exception found at PEManager.getVWWorkObject():"+vwe);
	          vwe.printStackTrace();
	      }catch(Exception vwe){
	          System.out.println("Exception found at PEManager.getVWWorkObject():"+vwe);
	          vwe.printStackTrace();
	      }
	     
	 
	}

}

