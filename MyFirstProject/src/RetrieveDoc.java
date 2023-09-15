import java.util.Iterator;

import javax.security.auth.Subject;

import com.filenet.api.admin.StorageArea;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;

public class RetrieveDoc {
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
			// Get document and populate property cache.
			PropertyFilter pf = new PropertyFilter();
			pf.addIncludeProperty(new FilterElement(null, null, null, "DocumentTitle", null) );
			pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.MIME_TYPE, null) );
			Document doc = Factory.Document.getInstance(store,"MyDocument", new Id("{F4DD983C-B845-4255-AC7A-257202B557EC}") );
		
			// Fetch selected properties from the server.
			doc.fetchProperties(pf);

			// Return document properties.
			com.filenet.api.property.Properties props = doc.getProperties();

			// Iterate the set and print property values.
			Iterator iter = props.iterator();
			System.out.println("Property" +"\t" + "Value");
			System.out.println("------------------------");
			while (iter.hasNext() )
			{
			    Property prop = (Property)iter.next();
			    if (prop.getPropertyName().equals("DocumentTitle") )
			      System.out.println(prop.getPropertyName() + "\t" + prop.getStringValue() );
			    else if (prop.getPropertyName().equals(PropertyNames.MIME_TYPE) )
			      System.out.println(prop.getPropertyName() + "\t" + prop.getStringValue() );
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			UserContext.get().popSubject();

		}

	}

}
