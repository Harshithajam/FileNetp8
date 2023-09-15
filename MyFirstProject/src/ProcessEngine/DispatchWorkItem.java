package ProcessEngine;

import org.apache.log4j.*;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterElement;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

public class DispatchWorkItem {
	static Logger log = Logger.getLogger(DispatchWorkItem.class.getName());
	VWWorkObject vwWorkObj;

	public void getWorkItem(VWSession peSession, String queueName) {

		try {

			VWQueue roster = peSession.getQueue(queueName);
			int queryFlags = VWRoster.QUERY_NO_OPTIONS;
			String abc = "DDE5AE99ED365346950C5545B7515C5A";
			String filter = "F_WobNum=" + abc;

			// Change filter based on requirement

			VWQueueQuery query = roster.createQuery(null, null, null, queryFlags, null, null,
					VWFetchType.FETCH_TYPE_WORKOBJECT);
			log.info("Total records for Work ITem: " + query.fetchCount());

			while (query.hasNext()) {
				vwWorkObj = (VWWorkObject) query.next();

			}

		}

		catch (VWException vwe) {
			log.info("Exception found at PEManager.getVWWorkObject():" + vwe);
			vwe.printStackTrace();
		} catch (Exception vwe) {
			log.info("Exception found at PEManager.getVWWorkObject():" + vwe);
			vwe.printStackTrace();
		}

		try {

			if (vwWorkObj != null) {

				// String Response = "cancel";
				vwWorkObj.doLock(true);
				vwWorkObj.doTerminate();
				// VWStepElement vwStepObj=vwWorkObj.fetchStepElement();
				// vwStepObj.doLock(true);
				// vwStepObj.setSelectedResponse(Response);

				// vwStepObj.doDispatch();
			} else {

				// log.info("VWWorkObject is NULL");
				log.info("VWWorkObject is NULL");

			}

		} catch (Exception ex) {

			ex.printStackTrace();

		}

	}
	

}
