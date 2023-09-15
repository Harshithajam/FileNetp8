
// Import.
import java.util.Iterator;
import java.util.Scanner;

import javax.security.auth.Subject;

import org.apache.logging.log4j.*;

import java.io.ByteArrayInputStream;

import com.filenet.api.admin.StorageArea;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Factory.ClassDefinition;
import com.filenet.api.core.Factory.ContentElement;
import com.filenet.api.core.Factory.ReferentialContainmentRelationship;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;

import ContentEngine.CeOperations;

public class CreateDocument {
	static Logger log1 = LogManager.getLogger(CeOperations.class.getName());

	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		// Set connection parameters; substitute for the placeholders.

		String uri = "http://ibmdemo16:9080/wsi/FNCEWS40MTOM/";
		String username = "p8admin";
		String password = "Password1";
		String objectStoreName = "Case Design";

		// Make connection.
		Connection conn = Factory.Connection.getConnection(uri);
		Subject subject = UserContext.createSubject(conn, username, password, null);
		UserContext.get().pushSubject(subject);

		try {
			// Get default domain.
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			System.out.println("Domain: " + domain.get_Name());

			// Get object stores for domain.
			ObjectStoreSet osSet = domain.get_ObjectStores();
			ObjectStore store = Factory.ObjectStore.fetchInstance(domain, objectStoreName, null);

			System.out.println("Connection to Content Platform Engine successful");
			// Create a document instance.
			Document doc = Factory.Document.createInstance(store, ClassNames.DOCUMENT);

			// Set document properties.
			doc.getProperties().putValue("DocumentTitle", "DemoDocument_2");
			doc.set_MimeType("text/plain");

			StorageArea sa = Factory.StorageArea.getInstance(store, new Id("{2054928A-0000-C621-9A34-5F9E6C15D708}"));
			doc.set_StorageArea(sa);

			doc.save(RefreshMode.NO_REFRESH);

			// Check in the document.
			doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
			doc.save(RefreshMode.NO_REFRESH);
			System.out.println("Document created successfully");
			
			//Create folder 
			Folder myFolder= Factory.Folder.createInstance(store,null);
			myFolder.set_Parent(store.get_RootFolder());
			myFolder.set_FolderName("DemoFolder_2");
			myFolder.save(RefreshMode.NO_REFRESH);
			System.out.println("Folder created succesfully");

			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			UserContext.get().popSubject();

		}

	}
}
