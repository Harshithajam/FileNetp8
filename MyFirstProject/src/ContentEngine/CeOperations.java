package ContentEngine;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.util.Iterator;

import java.util.Properties;

import java.util.Scanner;

import javax.security.auth.Subject;

import com.filenet.api.query.SearchSQL;

import com.filenet.api.query.SearchScope;

//import org.apache.logging.log4j.*;
import org.apache.log4j.*;

import com.filenet.api.admin.ClassDefinition;

import com.filenet.api.admin.LocalizedString;

import com.filenet.api.collection.ContentElementList;

import com.filenet.api.collection.IndependentObjectSet;

import com.filenet.api.collection.LocalizedStringList;

import com.filenet.api.collection.ObjectStoreSet;

import com.filenet.api.collection.RepositoryRowSet;

import com.filenet.api.constants.AutoClassify;

import com.filenet.api.constants.CheckinType;

import com.filenet.api.constants.ClassNames;

import com.filenet.api.constants.RefreshMode;

import com.filenet.api.core.Connection;

import com.filenet.api.core.ContentTransfer;

import com.filenet.api.core.Document;

import com.filenet.api.core.Domain;

import com.filenet.api.core.Factory;

import com.filenet.api.core.ObjectStore;

import com.filenet.api.exception.EngineRuntimeException;

import com.filenet.api.exception.ExceptionCode;

import com.filenet.api.property.PropertyFilter;

import com.filenet.api.util.Id;

import com.filenet.api.util.UserContext;

public class CeOperations {

	static Logger log1 = Logger.getLogger(CeOperations.class.getName());

	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws IOException {

		Connection conn = getCEConn();

		Domain dom = Factory.Domain.fetchInstance(conn, null, null);

		log1.info(dom.get_Name());

		System.out.println("Enter the option");
		int op = sc.nextInt();
		switch (op) {
		case 1:
			getAllDoc(dom);
			break;
		case 2:
			 addDocument(dom);
			break;
		case 3:
			getObjectStores(dom);
			break;
		case 4:
			getDocById(dom);
			break;
		case 5:
			createPropertiesFile();
			break;
		case 6:
			readPropertiesFile();
			break;

		default:
			break;
		}


	}

	public static void getAllDoc(Domain dom) {

		Document doc = null;

		try {

			System.out.println("Enter OS name");

			String os = sc.nextLine();

			System.out.println("Enter Name of Doc needs to be searched");

			String docName = sc.nextLine();

			System.out.println("Enter Class Name");

			String className = sc.nextLine();

			ObjectStore osName = Factory.ObjectStore.fetchInstance(dom, os, null);

			String mySQLString = "select * from " + className + " where DocumentTitle = '" + docName + "'";

			SearchSQL sqlObject = new SearchSQL();

			sqlObject.setQueryString(mySQLString);

			Integer myPageSize = new Integer(500);

			SearchScope searchScope = new SearchScope(osName);

			Boolean continuable = new Boolean(true);

			IndependentObjectSet docSet = searchScope.fetchObjects(sqlObject, myPageSize, null, continuable);

			Iterator itr = docSet.iterator();

			log1.info(mySQLString);

			int count = 0;

			while (itr.hasNext()) {

				doc = (Document) itr.next();

				count++;

				log1.info(count + " : " + doc.get_Name() + " : " + doc.get_DateCreated() + " : " + doc.get_Id());

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static void addDocument(Domain dom) {

		try {

			System.out.println("Enter OS name");

			String os = sc.nextLine();

			System.out.println("Enter Class Name of Doc");

			String className = sc.nextLine();
			System.out.println("Enter Name of Doc");

			String DocName = sc.nextLine();

			System.out.println("Enter File Path");

			String path = sc.nextLine();

			ObjectStore osName = Factory.ObjectStore.fetchInstance(dom, os, null);

			Document doc = Factory.Document.createInstance(osName, className, null);

			log1.info(doc);

			PropertyFilter pf = new PropertyFilter();

			File file = new File(path);

			InputStream ins = new FileInputStream(file);

			ContentTransfer ct = Factory.ContentTransfer.createInstance();

			ct.setCaptureSource(ins);

			ct.set_RetrievalName(file.getName());

			ContentElementList cel = Factory.ContentElement.createList();

			cel.add(ct);

			doc.set_ContentElements(cel);

			doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);

			doc.getProperties().putValue("DocumentTitle", DocName);

			doc.save(RefreshMode.NO_REFRESH);

			// doc.fetchProperties(pf);

			log1.info("Document Created successfully ");

		} catch (Exception e) {

			log1.error(e.getMessage());

		}

	}

	public static void getObjectStores(Domain dom) {

		log1.info(dom.get_Name());

		ObjectStoreSet osColl = dom.get_ObjectStores();

		// Get each object store.

		Iterator iterator = osColl.iterator();

		while (iterator.hasNext())

		{

			// Get next object store.

			ObjectStore objStore = (ObjectStore) iterator.next();

			// Get the display name of the object store.

			String objStoreName = objStore.get_DisplayName();

			log1.info("Object store name = " + objStoreName);

		}

	}

	public static void getDocById(Domain dom) throws IOException {

		System.out.println("Enter OS name");

		String osName = sc.nextLine();

		System.out.println("Enter GUID of Doc");

		String docId = sc.nextLine();

		ObjectStore os = Factory.ObjectStore.fetchInstance(dom, osName, null);

		// System.out.println("ObjectStore Name : " + os.get_Name());

		Document doc = Factory.Document.fetchInstance(os, new Id(docId), null);

		log1.info("document name :" + doc.get_Name());

		// properties of a document

		com.filenet.api.property.Properties props = doc.getProperties();

		log1.info(props);

		// Download content

		ContentElementList docContentList = doc.get_ContentElements();

		Iterator iter = docContentList.iterator();

		while (iter.hasNext()) {

			ContentTransfer ct;

			InputStream stream;

			ct = (ContentTransfer) iter.next();

			stream = ct.accessContentStream();

			FileOutputStream fos = new FileOutputStream(doc.get_Id() + "_" + ct.get_RetrievalName());

			byte[] buffer = new byte[4096000];

			int bytesRead = 0;

			while ((bytesRead = stream.read(buffer)) != -1) {

				fos.write(buffer, 0, bytesRead);

			}

			fos.close();

			stream.close();

		}

	}

	public static Connection getCEConn() {

		Connection conn = null;

		Properties properties = new Properties();

		FileInputStream inputStream = null;

		Logger log = Logger.getLogger(CeOperations.class.getName());

		try {

			inputStream = new FileInputStream("config.properties");

			// Load the properties from the file

			properties.load(inputStream);

			// Read the properties

			String url = properties.getProperty("url");

			String username = properties.getProperty("username");

			String password = properties.getProperty("password");

			conn = Factory.Connection.getConnection(url);

			Subject subject = UserContext.createSubject(conn, username, password, null);

			UserContext uc = UserContext.get();

			uc.pushSubject(subject);

		} catch (Exception e1) {

			// TODO Auto-generated catch block

			log.error(e1.getMessage());

		}

		log.info("CE Conn :: " + conn);

		return conn;

	}

	public static void createPropertiesFile() {

		Properties properties = new Properties();

		FileOutputStream outputStream = null;

		try {

			outputStream = new FileOutputStream("config.properties");

			// Set the properties values

			properties.setProperty("url", "http://ibmdemo16:9080/wsi/FNCEWS40MTOM/");

			properties.setProperty("username", "p8admin");

			properties.setProperty("password", "Password1");

			// Save the properties to the file

			properties.store(outputStream, "Database Configuration");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (outputStream != null) {

				try {

					outputStream.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}

	}

	public static void readPropertiesFile() {

		Properties properties = new Properties();

		FileInputStream inputStream = null;

		Logger log = Logger.getLogger(CeOperations.class.getName());

		try {

			inputStream = new FileInputStream("config.properties");

			// Load the properties from the file

			properties.load(inputStream);

			// Read the properties

			String url = properties.getProperty("url");

			String username = properties.getProperty("username");

			String password = properties.getProperty("password");

			log.info(password);

			log.info(username);

			log.info(url);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (inputStream != null) {

				try {

					inputStream.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}

	}

}
