
import java.util.*;

import javax.security.auth.Subject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

//import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;

import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Connection;
import com.filenet.api.util.UserContext;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.SiteSet;
import com.filenet.api.core.Folder;
import com.filenet.api.collection.FolderSet;

public class ObjStoreDetails {

	public static void main(String[] args) {

		String ceURI = "http://ibmdemo16:9080/wsi/FNCEWS40MTOM/"; // t3://servername:port/FileNet/Engine
		String userName = "p8admin"; // Specify the username to be authenticated
		String password = "Password1"; // Specify the user password
		String domainName = "FileNetP8"; // Specify the symbolic name of the class instance to retrieve
		String osName = "Case Design";
		// String myFolderPath = "/"; //root folder
		String myFolderPath = "/DattaSamples";
		// String myFolderPath = "/DattaSamples/dbcFolderOne";

		// Read connection info from XML file
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			System.out.println("Read xml file ConfigFiles/ConnConfig.xml");
			String fXmlFile = "ConfigFiles/ConnConfig.xml";
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			if (doc != null) {
				System.out.println("xml file doc found");
				NodeList envNodeList = doc.getElementsByTagName("ConnectionConfig");
				ceURI = (String) ((Element) envNodeList.item(0)).getElementsByTagName("ceURI").item(0).getChildNodes()
						.item(0).getNodeValue();
				userName = (String) ((Element) envNodeList.item(0)).getElementsByTagName("userName").item(0)
						.getChildNodes().item(0).getNodeValue();
				password = (String) ((Element) envNodeList.item(0)).getElementsByTagName("password").item(0)
						.getChildNodes().item(0).getNodeValue();
				domainName = (String) ((Element) envNodeList.item(0)).getElementsByTagName("domainName").item(0)
						.getChildNodes().item(0).getNodeValue();
				osName = (String) ((Element) envNodeList.item(0)).getElementsByTagName("osName").item(0).getChildNodes()
						.item(0).getNodeValue();
				myFolderPath = (String) ((Element) envNodeList.item(0)).getElementsByTagName("myFolderPath").item(0)
						.getChildNodes().item(0).getNodeValue();

				System.out.println("ceuri: " + ceURI);
				System.out.println("userName: " + userName);
				System.out.println("password: " + password);
				System.out.println("domainName: " + domainName);
				System.out.println("osName: " + osName);
				System.out.println("myFolderPath: " + myFolderPath);
			} else {
				System.out.println("xml file doc not found");
			}
		} catch (ParserConfigurationException e) {
			System.out.println(
					"ParserConfigurationException in building document - IPAM Configuration XML: " + e.getMessage());
		} catch (SAXException e) {
			System.out.println("SAXException in Parsing IPAM Configuration XML: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException in parsing IPAM Configuration XML: " + e.getMessage());
		}

		UserContext userContext = null;

		try {
			Connection connection = Factory.Connection.getConnection(ceURI);
			System.out.println("dbc1: connection created: " + connection); // new connection object
			Subject subject = UserContext.createSubject(connection, userName, password, null);
			System.out.println("dbc2: subject created: " + subject); // authenticate with application server
			userContext = UserContext.get(); // get UserContext object associated with current thread
			System.out.println("dbc3: userContext created: " + userContext);
			userContext.pushSubject(subject); // make the specified JAAS Subject active
			System.out.println("dbc4: pushSubject() called successfully");
			Domain domain = Factory.Domain.fetchInstance(connection, domainName, null); // Get the default domain
			System.out.println("dbc5: domain name: " + domain.get_Name());

			// list out site/s in the domain "FileNetP8"
			System.out.println("\ndbc6: site/s at domain : " + domain.get_Name() + ":");
			SiteSet siteSet = domain.get_Sites();
			if (siteSet.isEmpty())
				System.out.println("No site/s at domain : " + domain.get_Name() + ":");
			else {
				com.filenet.api.admin.Site vsite = null;
				Iterator itrSite = siteSet.iterator();
				Iterator<com.filenet.api.admin.Site> siteItr = itrSite;
				while (siteItr.hasNext()) {
					vsite = (com.filenet.api.admin.Site) siteItr.next();
					System.out.println("site:" + vsite.get_Name() + " (Display Name:" + vsite.get_DisplayName() + ")");
				}
			}

			// list out object_store/s in the domain "FileNetP8"
			System.out.println("\ndbc7: ObjectStore/s at domain " + domain.get_Name() + ":");
			ObjectStoreSet osSet = domain.get_ObjectStores();
			if (siteSet.isEmpty())
				System.out.println("No site/s at domain : " + domain.get_Name() + ":");
			else {
				ObjectStore objStore = null;
				Iterator itrOS = osSet.iterator();
				// Iterator<ObjectStore> osIter = itrOS; //osItr not required
				while (itrOS.hasNext()) {
					objStore = (ObjectStore) itrOS.next();
					if (objStore.getAccessAllowed().intValue() > 0)
						System.out.println("objstore:" + objStore.get_Name() + " (Display Name: "
								+ objStore.get_DisplayName() + ")");
				}
			}

			ObjectStore objectStore = null;
			objectStore = Factory.ObjectStore.fetchInstance(domain, osName, null);
			System.out.println("\ndbc8: object store (display) name: " + objectStore.get_DisplayName());

			Folder folder = Factory.Folder.fetchInstance(objectStore, myFolderPath, null);
			System.out.println("\ndbc9: Folder/s inside " + folder.get_PathName() + ": ");
			FolderSet subFolders = folder.get_SubFolders();
			if (subFolders.isEmpty())
				System.out.println("No Folder/s inside " + folder.get_PathName());
			else {
				Iterator itrFS = subFolders.iterator();
				while (itrFS.hasNext()) {
					Folder subFolder = (Folder) itrFS.next();
					// if(subFolder.getProperties().getBooleanValue("IsHiddenContainer"))
					System.out.println("FolderName: " + subFolder.get_FolderName() + "(Class Name: "
							+ subFolder.getClassName() + ")");
				}
			}

			DocumentSet docs = folder.get_ContainedDocuments();
			System.out.println("\ndbc10: Document/s inside " + folder.get_PathName() + ": ");
			if (docs.isEmpty())
				System.out.println("No Document/s inside " + folder.get_PathName());
			else {
				Iterator itrDS = docs.iterator();
				while (itrDS.hasNext()) {
					Document myDoc = (Document) itrDS.next();
					System.out.println(
							"DocumentName: " + myDoc.get_Name() + "(Class Name: " + myDoc.getClassName() + ")");
				}
			}

		} catch (Exception Ex) {
			Ex.printStackTrace();
		} finally {
			userContext.popSubject();
		}
	}
}
