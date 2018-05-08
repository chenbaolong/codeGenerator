import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CodeGenerator {
	private static List<String> noPrintColumnList;
	private static String path = "f:/codeCreatFile/com/";
	private static String url = "jdbc:mysql://192.168.120.8:3306/";
	private static String url2 = "jdbc:mysql://192.168.120.8:3306/mikemary";
	private static String user = "mikemary";
	private static String pass = "mikemary";
	static{
		String[] noPrintColumn={"ID","CREATED_DATE","CREATED_BY","UPDATED_DATE","UPDATED_BY ","DEL_FLAG","DELFLAG","UPDATE_ID","UPDATE_DATE","CREATE_ID","CREATE_DATE"};
		noPrintColumnList = new ArrayList<String>();
		for(String c:noPrintColumn){
			noPrintColumnList.add(c);
		}
	}
	
	public static void main(String[] args) {
		CodeGenerator test = new CodeGenerator();
		test.connDB(url, user, pass);
//		test.createFile("F:/", "Test.java", test.getJtpl());
	}
	/**
	 * 获取表
	* <p>Title: connDB</p>
	* <p>Description: </p>
	* @param url
	* @param user
	* @param pass
	* @author chenbl
	* @date Oct 14, 2016 4:21:00 PM
	 */
	public void connDB(String url, String user, String pass) { // 这里没有指定数据库
		Connection conn = null;
		Connection conn2 = null;
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = (Connection) DriverManager.getConnection(url, user, pass);
			conn2 = DriverManager.getConnection(url2, user, pass);
			DatabaseMetaData metadata = conn.getMetaData();
			System.out.println("数据库已知的用户: " + metadata.getUserName());
			System.out.println("数据库的系统函数的逗号分隔列表: "
					+ metadata.getSystemFunctions());
			System.out.println("数据库的时间和日期函数的逗号分隔列表: "
					+ metadata.getTimeDateFunctions());
			System.out.println("数据库的字符串函数的逗号分隔列表: "
					+ metadata.getStringFunctions());
			System.out.println("数据库供应商用于 'schema' 的首选术语: "
					+ metadata.getSchemaTerm());
			System.out.println("数据库URL: " + metadata.getURL());
			System.out.println("是否允许只读:" + metadata.isReadOnly());
			System.out.println("数据库的产品名称:" + metadata.getDatabaseProductName());
			System.out
					.println("数据库的版本:" + metadata.getDatabaseProductVersion());
			System.out.println("驱动程序的名称:" + metadata.getDriverName());
			System.out.println("驱动程序的版本:" + metadata.getDriverVersion());

			System.out.println();
			System.out.println("数据库中使用的表类型");
			ResultSet rs = metadata.getTableTypes();
			while (rs.next()) {
				System.out.println(rs.getString(1));
			}
			rs.close();

			System.out.println();
			/**
			 * 获取指定的数据库的所有表的类型,getTables()的第一个参数就是数据库名
			 * 因为与MySQL连接时没有指定,这里加上,剩下的参数就可以为null了
			 * 第二个参数是模式名称的模式,但是输出也是什么都没有。谁知道告诉我一声
			 */
			System.out.println("获取指定的数据库的所有表的类型");
			ResultSet rs1 = metadata.getTables("mikemary", null, null, null);
			List<String>listTable= new ArrayList<String>();
			while (rs1.next()) {
				System.out.println();
				System.out.println("表名: " + rs1.getString(3));
				getColumn(conn2, rs1.getString(3));
				listTable.add(rs1.getString(3));
			}
			//serviceXML
			printServiceXML(listTable);
			//struts
			printStruts(listTable);
			rs1.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				conn2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * action
	* <p>Title: printAction</p>
	* <p>Description: </p>
	* @param listTable
	* @author chenbl
	* @date Oct 17, 2016 1:52:36 PM
	 */
	private void printAction( String tableName) {
		String[]actions = {"ListAction.jtpl","EditAction.jtpl","DispAction.jtpl","SaveAction.jtpl","DeleAction.jtpl"};
		for(String tplName:actions){
			StringBuffer tpl = getTpl(tplName);
			replaceAll(tpl,"ClassName", UpperCase(tableName));
			replaceAll(tpl,"className", LowerCase(tableName));
//			System.out.println(tpl.toString());
			//生成文件
			createFile(path+"/action/", UpperCase(tableName)+tplName.replace("jtpl", "java"), tpl);
		}
	}

	/**
	 * struts
	* <p>Title: printStruts</p>
	* <p>Description: </p>
	* @param listTable
	* @author chenbl
	* @date Oct 17, 2016 1:27:07 PM
	 */
	private void printStruts(List<String> listTable) {
		StringBuffer tpl = getTpl("struts.xml");
		String form = getTplStr(tpl, "contentForm");
		StringBuffer formBF = new StringBuffer();

		String action = getTplStr(tpl, "contentAction");
		StringBuffer actionBF = new StringBuffer();
		
		for(String tableName:listTable){
			formBF.append(replaceAll(replaceAll(form, "className", LowerCase(tableName)), "ClassName", UpperCase(tableName)));
			actionBF.append(replaceAll(replaceAll(action, "className", LowerCase(tableName)), "ClassName", UpperCase(tableName)));
		}
		replace(tpl, "contentForm", formBF.toString());
		replace(tpl, "contentAction", actionBF.toString());
//		System.out.println(tpl.toString());

		//生成文件
		createFile(path, "struts.xml", tpl);
	}

	/**
	 * 获取表结构
	* <p>Title: getColumn</p>
	* <p>Description: </p>
	* @param conn
	* @param tableName
	* @author chenbl
	* @date Oct 14, 2016 4:20:49 PM
	 */
	public void getColumn(Connection conn, String tableName) {
		try {
			PreparedStatement ps = conn.prepareStatement(" select * from " + tableName);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsme = rs.getMetaData();
			if("mm_confused_problem".equals(tableName)){
				int aa=0;
			}
			int columnCount = rsme.getColumnCount();
			//entity
			printEDF("entity",tableName, columnCount, rsme);
			//DTO
			printEDF("DTO",tableName, columnCount, rsme);
			//Form
			printEDF("form",tableName, columnCount, rsme);
			//hbm
			printHBM(tableName, columnCount, rsme);
			//service
			printService(tableName);
			//serviceImpl
			printServiceImpl(tableName);
			//action
			printAction(tableName);
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(" SHOW full FIELDS from " + tableName);  
			// 列表页
        	printListJsp(tableName,rs);
			//编辑页
        	printEditJsp("edit",tableName,rs);
        	printEditJsp("disp",tableName,rs);
            rs.close(); 
            stmt.close(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void printEditJsp(String param ,String tableName, ResultSet rs) throws Exception {
		 StringBuffer tpl = getTpl(param+".jsp");
		 replaceAll(tpl, "className", LowerCase(tableName));
		 String contentRow = getTplStr(tpl, "contentRow");
		 StringBuffer contentRowtBF = new StringBuffer();

		 String contentField = getTplStr(new StringBuffer(contentRow), "contentInput");
		 StringBuffer contentFieldBF = new StringBuffer();
		 int i = 0;
		 if(rs != null && !rs.isFirst()){
			 rs.first();
		 }
		 while(rs != null && rs.next()) {  
			String field = rs.getString("Field");
			if(!noPrintColumnList.contains(field.toUpperCase())){
				i++;
				String pramStr = replaceAll(contentField, "commentTd",rs.getString("Comment"));
				pramStr = replaceAll(pramStr, "fieldTd",LowerCase(rs.getString("Field")));
				contentFieldBF.append(pramStr);
				
				if(i%3==0){
					contentRowtBF.append(replace(contentRow, "contentInput", contentFieldBF.toString()));
					contentFieldBF.setLength(0);
				}
			}
         }
		 if(i%3!=0){
			contentRowtBF.append(replace(contentRow, "contentInput", contentFieldBF.toString()));
			contentFieldBF.setLength(0);
		 }
		 replace(tpl, "contentRow", contentRowtBF.toString());
		 System.out.println(tpl.toString());

		//生成文件
		createFile(path+"/jsp/", LowerCase(tableName)+UpperCase(param)+".jsp", tpl);
	}
	
	/**
	 * 列表页
	* <p>Title: printListJsp</p>
	* <p>Description: </p>
	* @param tableName
	* @param rs
	* @throws Exception
	* @author chenbl
	* @date Oct 17, 2016 3:51:51 PM
	 */
	private void printListJsp(String tableName, ResultSet rs) throws Exception {
		 StringBuffer tpl = getTpl("list.jsp");
		 replaceAll(tpl, "className", LowerCase(tableName));
		 String contentComment = getTplStr(tpl, "contentComment");
		 StringBuffer contentCommentBF = new StringBuffer();

		 String contentField = getTplStr(tpl, "contentField");
		 StringBuffer contentFieldBF = new StringBuffer();
		 while(rs != null && rs.next()) {  
			String field = rs.getString("Field");
			if(!noPrintColumnList.contains(field.toUpperCase())){
				String pramStr = replace(contentComment, "commentTd",rs.getString("Comment"));
				contentCommentBF.append(pramStr);
				pramStr = replace(contentField, "fieldTd", LowerCase(field));
				contentFieldBF.append(pramStr);
			}
         }
		 replace(tpl, "contentComment", contentCommentBF.toString());
		 replace(tpl, "contentField", contentFieldBF.toString());
//		 System.out.println(tpl.toString());

		//生成文件
		createFile(path+"/jsp/", LowerCase(tableName)+"List.jsp", tpl);
	}
	/**
	 * 获取注释
	* <p>Title: parse</p>
	* <p>Description: </p>
	* @param all
	* @return
	* @author chenbl
	* @date Oct 17, 2016 2:18:32 PM
	 */
	  public static String getComment(String all) {
		String comment = null;
		int index = all.indexOf("COMMENT='");
		if (index < 0) {
			return "";
		}
		comment = all.substring(index + 9);
		comment = comment.substring(0, comment.length() - 1);
		return comment;
	}  
	
	/**
	 * serviceXML
	 * <p>
	 * Title: printServiceXML
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param tableName
	 * @author chenbl
	 * @date Oct 17, 2016 11:36:16 AM
	 */
	private void printServiceXML(List<String>listTable) {
		StringBuffer tpl = getTpl("service.xml");
		String content = getTplStr(tpl, "content");
		StringBuffer contentBF = new StringBuffer();
		for(String tableName:listTable){
			contentBF.append(replaceAll(replaceAll(content, "className", LowerCase(tableName)), "ClassName", UpperCase(tableName)));
		}
		replace(tpl, "content", contentBF.toString());
//		System.out.println(tpl.toString());
		//生成文件
		createFile(path, "service.xml", tpl);
	}

	/**
	 * serviceImpl
	* <p>Title: printServiceImpl</p>
	* <p>Description: </p>
	* @param tableName
	* @author chenbl
	* @date Oct 17, 2016 11:24:58 AM
	 */
	private void printServiceImpl(String tableName) {
		StringBuffer tpl = getTpl("serviceImpl.jtpl");
		replaceAll(tpl,"ClassName", UpperCase(tableName));
		replaceAll(tpl,"TableName", tableName);
//		System.out.println(tpl.toString());
		//生成文件
		createFile(path+"/service/impl/", UpperCase(tableName)+"ServiceImpl.java", tpl);
	}

	/**
	 * service
	* <p>Title: printService</p>
	* <p>Description: </p>
	* @param tableName
	* @author chenbl
	* @date Oct 17, 2016 11:06:31 AM
	 */
	private void printService(String tableName) {
		StringBuffer tpl = getTpl("service.jtpl");
		replaceAll(tpl,"ClassName", UpperCase(tableName));
//		System.out.println(tpl.toString());
		//生成文件
		createFile(path+"/service/", UpperCase(tableName)+"Service.java", tpl);
	}
	
	/**
	 * 替换所有
	 * <p>Title: replaceAll</p>
	 * <p>Description: </p>
	 * @param tplStr
	 * @param name
	 * @param str
	 * @return
	 * @author chenbl
	 * @date Oct 17, 2016 11:36:28 AM
	 */
	public String replaceAll(String tplStr,String name,String str){
		return tplStr.replaceAll("<"+name+"></"+name+">", str);
	}
	
	public void replaceAll(StringBuffer tpl,String name,String str){
		String tplStr = tpl.toString();
		tpl.setLength(0);
		tpl.append(replaceAll(tplStr,name, str));
	}

	/**
	 * hbm
	* <p>Title: printHBM</p>
	* <p>Description: </p>
	* @param tableName
	* @param columnCount
	* @param rsme
	* @author chenbl
	 * @throws Exception 
	* @date Oct 17, 2016 10:40:49 AM
	 */
	private void printHBM(String tableName, int columnCount,ResultSetMetaData rsme) throws Exception {

		StringBuffer tpl = getTpl("entity.hbm.xml");
		//替换类名
		replace(tpl, "ClassName", UpperCase(tableName));
		//替换表名
		replace(tpl, "TableName", tableName);
		
		String property = getTplStr(tpl, "content");
		StringBuffer propertyBF = new StringBuffer();
		for (int i = 1; i <= columnCount; i++) {
			String pramStr = replace(property, "name", LowerCase(rsme.getColumnName(i)));
			pramStr = replace(pramStr, "type", getClassType(rsme.getColumnTypeName(i)));
			pramStr = replace(pramStr, "column", rsme.getColumnName(i));
			propertyBF.append(pramStr);
		}
		replace(tpl, "content", propertyBF.toString());
//		System.out.println(tpl.toString());
		//生成文件
		createFile(path+"/entity/",UpperCase(tableName)+ ".hbm.xml", tpl);
	}

	/**
	 * entity,DTO,from
	* <p>Title: printEntity</p>
	* <p>Description: </p>
	* @param tableName
	* @param columnCount
	* @param rsme
	* @throws Exception
	* @author chenbl
	* @date Oct 17, 2016 10:33:15 AM
	 */
	public void printEDF(String param, String tableName,int columnCount,ResultSetMetaData rsme) throws Exception{
		StringBuffer tpl = getTpl(param+".jtpl");
		//替换类名
		replace(tpl, "ClassName", UpperCase(tableName));
		String property = getTplStr(tpl, "property");
		StringBuffer propertyBF = new StringBuffer();
		String get = getTplStr(tpl, "get");
		StringBuffer getBF = new StringBuffer();
		String set = getTplStr(tpl, "set");
		StringBuffer setBF = new StringBuffer();
		Set<String> classTypeSet = new HashSet<String>(); 
		for (int i = 1; i <= columnCount; i++) {
			String pramStr = replace(property, "name", LowerCase(rsme.getColumnName(i)));
			pramStr = replace(pramStr, "type", getType(rsme.getColumnTypeName(i)));
			propertyBF.append(pramStr);
			
			pramStr = replace(get, "name", LowerCase(rsme.getColumnName(i)));
			pramStr = replace(pramStr, "type", getType(rsme.getColumnTypeName(i)));
			pramStr = replace(pramStr, "Name", UpperCase(rsme.getColumnName(i)));
			getBF.append(pramStr);
			

			pramStr = replace(set, "name", LowerCase(rsme.getColumnName(i)));
			pramStr = replace(pramStr, "name", LowerCase(rsme.getColumnName(i)));
			pramStr = replace(pramStr, "name", LowerCase(rsme.getColumnName(i)));
			pramStr = replace(pramStr, "type", getType(rsme.getColumnTypeName(i)));
			pramStr = replace(pramStr, "Name", UpperCase(rsme.getColumnName(i)));
			setBF.append(pramStr);
			
			classTypeSet.add(getClassType(rsme.getColumnTypeName(i)));
		}
		StringBuffer classTypeBF = new StringBuffer();
		for(Iterator<String> iterator = classTypeSet.iterator();iterator.hasNext();){  
			classTypeBF.append("import "+iterator.next()+";\r\n");  
        }  
		replace(tpl, "import", classTypeBF.toString());
		replace(tpl, "content", propertyBF.toString()+getBF.toString()+setBF.toString());
//		System.out.println(tpl.toString());
		
		//生成文件
		if("entity".equals(param)){
			createFile(path+"/entity/",UpperCase(tableName)+ ".java", tpl);
		}else if("DTO".equals(param)){
			createFile(path+"/dto/",UpperCase(tableName)+ "DTO.java", tpl);
		}else if("form".equals(param)){
			createFile(path+"/form/",UpperCase(tableName)+ "Form.java", tpl);
		}
	}
	
	
	
	/**
	 * 获取类型
	* <p>Title: getType</p>
	* <p>Description: </p>
	* @param type
	* @return
	* @author chenbl
	* @date Oct 17, 2016 10:21:39 AM
	 */
	public String getType(String type){
		if ("VARCHAR".equals(type)) {
			return "String";
		}else if ("VARCHAR2".equals(type)) {
			return "String";
		} else if ("CHAR".equals(type)) {
			return "String";
		} else if ("BIGINT".equals(type)) {
			return "Long";
		} else if ("SMALLINT".equals(type)) {
			return "int";
		} else if ("INTEGER".equals(type)) {
			return "int";
		} else if ("DECIMAL".equals(type)) {
			return "double";
		} else if ("TIMESTMP".equals(type)) {
			return "Date";
		}else if ("DATETIME".equals(type)) {
			return "Date";
		}
		
		return "";
	}
	
	/**
	 * 获取类型的类
	* <p>Title: getClassType</p>
	* <p>Description: </p>
	* @param type
	* @return
	* @author chenbl
	* @date Oct 17, 2016 10:21:50 AM
	 */
	public String getClassType(String type){
		if ("VARCHAR".equals(type)) {
			return "java.lang.String";
		}else if ("VARCHAR2".equals(type)) {
			return "java.lang.String";
		} else if ("CHAR".equals(type)) {
			return "java.lang.String";
		} else if ("BIGINT".equals(type)) {
			return "java.lang.Long";
		} else if ("SMALLINT".equals(type)) {
			return "java.lang.int";
		} else if ("INTEGER".equals(type)) {
			return "java.lang.int";
		} else if ("DECIMAL".equals(type)) {
			return "java.lang.double";
		} else if ("TIMESTMP".equals(type)) {
			return "java.util.Date";
		}else if ("DATETIME".equals(type)) {
			return "java.util.Date";
		}
		return "";
	}
	
	/**
	 * 替换
	* <p>Title: replace</p>
	* <p>Description: </p>
	* @param jtpl
	* @param name
	* @param str
	* @return
	* @author chenbl
	* @date Oct 17, 2016 10:22:00 AM
	 */
	public String replace(String jtpl,String name,String str){
		StringBuffer sb= new StringBuffer();
		sb.append(jtpl);
		sb.replace(sb.indexOf("<"+name+">"), sb.indexOf("</"+name+">")+("</"+name+">").length(), str);
		return sb.toString();
	}
	
	public void replace(StringBuffer jtpl,String name,String str){
		jtpl.replace(jtpl.indexOf("<"+name+">"), jtpl.indexOf("</"+name+">")+("</"+name+">").length(), str);
	}
	/**
	 * 获取模板
	* <p>Title: getTplStr</p>
	* <p>Description: </p>
	* @param jtpl
	* @param name
	* @return
	* @author chenbl
	* @date Oct 17, 2016 10:22:12 AM
	 */
	public String getTplStr(StringBuffer tpl,String name){
		return tpl.substring(tpl.indexOf("<"+name+">")+("<"+name+">").length(), tpl.indexOf("</"+name+">"));
	}
	
	/**
	 * 获取模板
	* <p>Title: getJtpl</p>
	* <p>Description: </p>
	* @return
	* @author chenbl
	* @date Oct 14, 2016 4:19:55 PM
	 */
	public StringBuffer getTpl(String fileName){
		try {
			StringBuffer text = new StringBuffer();
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader( this.getClass().getResourceAsStream("/"+fileName),"utf-8"));
			String line = null;
			for (; (line = reader.readLine()) != null;) {
				text.append(line+"\r\n");
			}
//			System.out.print(text.toString());
			reader.close();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 创建文件
	* <p>Title: createFile</p>
	* <p>Description: </p>
	* @param path
	* @param fileName
	* @author chenbl
	* @date Oct 14, 2016 4:18:53 PM
	 */
	
	public void createFile(String path, String fileName,StringBuffer text) {
		// path表示你所创建文件的路径
		try {
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			// fileName表示你创建的文件名；
			File file = new File(f, fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text.toString());
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 首字母转小写
	* <p>Title: toLowerCaseFirstOne</p>
	* <p>Description: </p>
	* @param s
	* @return
	* @author chenbl
	* @date Oct 14, 2016 4:36:57 PM
	 */
    public static String toLowerCaseFirstOne(String s)
    {
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    /**
     * 首字母转大写
    * <p>Title: toUpperCaseFirstOne</p>
    * <p>Description: </p>
    * @param s
    * @return
    * @author chenbl
    * @date Oct 14, 2016 4:37:03 PM
     */
    public static String toUpperCaseFirstOne(String s)
    {
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    /**
     * 转化为小写
    * <p>Title: toLowerCase</p>
    * <p>Description: </p>
    * @param s
    * @return
    * @author chenbl
    * @date Oct 14, 2016 4:38:09 PM
     */
    public static String toLowerCase(String s){
    	return s.toLowerCase();
    }
    
    /**
     * 转化 pay_order To payOrder
    * <p>Title: str1_str2ToStr1Str2</p>
    * <p>Description: </p>
    * @param s
    * @return
    * @author chenbl
    * @date Oct 14, 2016 4:46:56 PM
     */
    public static String LowerCase(String s){
    	String[] ss = toLowerCase(s).split("_");
    	s="";
    	for(int i=0;i<ss.length;i++){
    		if(i==0){
    			s+=ss[i];
    		}else{
    			s+=toUpperCaseFirstOne(ss[i]);
    		}
    	}
    	return s;
    }
    
    public static String UpperCase(String s){
    	String[] ss = toLowerCase(s).split("_");
    	s="";
    	for(int i=0;i<ss.length;i++){
			s+=toUpperCaseFirstOne(ss[i]);
    	}
    	return s;
    }
}
