package com.spring.secu06.ex02;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class SecuredObjectDao {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String DEF_ROLES_AND_URL_QUERY = 
			"SELECT A.RESOURCE_PATTERN AS URL, B.AUTHORITY AS AUTHORITY "
			+ "FROM TB_SECURED_RESOURCES A, TB_SECURED_RESOURCES_ROLE B "
			+" WHERE A.RESOURCE_ID = B.RESOURCE_ID"
			+" AND A.RESOURCE_TYPE = 'url'"
			+ " ORDER BY A.SORT_ORDER";
	
	public static final String DEF_REGEX_MATCHED_REQUEST_MAPPING_QUERY_ORACLE10G =
			"SELECT A.RESOURCE_PATTERN URI, B.AUTHORITY AUTHORITY" 
		  + " FROM TB_SECURED_RESOURCES A, TB_SECURED_RESOURCES_ROLE B"
		  + " WHERE A.RESOURCE_ID = B.RESOURCE_ID"
		  + " AND A.RESOURCE_ID =  (SELECT RESOURCE_ID FROM" 
			                       + "(SELECT RESOURCE_ID, ROW_NUMBER() OVER (ORDER BY SORT_ORDER) RESOURCE_ORDER"
			                       + " FROM TB_SECURED_RESOURCES C"
			                       + " WHERE REGEXP_LIKE(:URL, C.RESOURCE_PATTERN)"
			                       + " AND C.RESOURCE_TYPE = 'url'"
			                       + " ORDER BY C.SORT_ORDER)"
			                       + " WHERE RESOURCE_ORDER = 1 )";
	
	
	
	public static final String DEF_HIERARCHICAL_ROLES_QUERY =
			"SELECT A.CHILD_ROLE CHILD, A.PARENT_ROLE PARENT"
			+ " FROM TB_ROLES_HIERARCHY A LEFT JOIN TB_ROLES_HIERARCHY B ON (A.CHILD_ROLE = B.PARENT_ROLE)";
	
	
	private String sqlRolesAndUrl;
	//private String sqlRolesAndMethod;
	//private String sqlRlesAndPointcut;
	private String sqlRegexMatchedRequestMapping;
	private String sqlHierarchicalRoles;
	
	public SecuredObjectDao() {
		this.sqlRolesAndUrl = DEF_ROLES_AND_URL_QUERY;
		//this.sqlRolesAndMethod = DEF_ROLES_AND_METHOD_QUERY;
		//this.sqlRolesAndPointcut = DEF_ROLES_AND_POINTCUT_QUERY;
		this.sqlRegexMatchedRequestMapping = DEF_REGEX_MATCHED_REQUEST_MAPPING_QUERY_ORACLE10G;
		this.sqlHierarchicalRoles = DEF_HIERARCHICAL_ROLES_QUERY;
	}
	
	private NamedParameterJdbcTemplate nameParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.nameParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public String getSqlRolesAndUrl() {
		return sqlRolesAndUrl;
	}

	public void setSqlRolesAndUrl(String sqlRolesAndUrl) {
		this.sqlRolesAndUrl = sqlRolesAndUrl;
	}

	public String getSqlRegexMatchedRequestMapping() {
		return sqlRegexMatchedRequestMapping;
	}

	public void setSqlRegexMatchedRequestMapping(String sqlRegexMatchedRequestMapping) {
		this.sqlRegexMatchedRequestMapping = sqlRegexMatchedRequestMapping;
	}

	public String getSqlHierarchicalRoles() {
		return sqlHierarchicalRoles;
	}

	public void setSqlHierarchicalRoles(String sqlHierarchicalRoles) {
		this.sqlHierarchicalRoles = sqlHierarchicalRoles;
	}

	public NamedParameterJdbcTemplate getNameParameterJdbcTemplate() {
		return nameParameterJdbcTemplate;
	}

	public void setNameParameterJdbcTemplate(NamedParameterJdbcTemplate nameParameterJdbcTemplate) {
		this.nameParameterJdbcTemplate = nameParameterJdbcTemplate;
	}
	
	public LinkedHashMap<Object, List<ConfigAttribute>> getRolesAndResources(String resourceType){
		LinkedHashMap<Object, List<ConfigAttribute>> resourcesMap = new LinkedHashMap<Object, List<ConfigAttribute>>();
		
		String sqlRolesAndResources = null;
		boolean isResourcesUrl = true;
		if("method".equals(resourceType)) {
			//sqlRolesAndResources = getSqlRolesAndMethod();
			//isResourcesUrl = false;
		}else if("pointcut".equals(resourceType)) {
			//sqlRolesAndResources = getSqlRolesAndPointcut();
			//isResourcesUrl = false;
		}else {
			sqlRolesAndResources = getSqlRolesAndUrl();
		}
		
		List<Map<String, Object>> resultList = 
							this.nameParameterJdbcTemplate.queryForList(sqlRolesAndResources,new HashMap<String, String>());
		
		Iterator<Map<String, Object>> itr = resultList.iterator();
		Map<String, Object> tempMap;
		String preResource = null;
		String presentResourceStr;
		Object presentResource;
		
		while(itr.hasNext()) {
			tempMap = itr.next();
			
			presentResourceStr = (String) tempMap.get(resourceType);
			presentResource = isResourcesUrl ? new AntPathRequestMatcher(presentResourceStr): presentResourceStr;
			List<ConfigAttribute> configList = new LinkedList<ConfigAttribute>();
			
			if(preResource != null && presentResourceStr.equals(presentResource)) {
				List<ConfigAttribute> preAuthList = resourcesMap.get(presentResource);
				Iterator<ConfigAttribute> preAuthItr = preAuthList.iterator();
				while(preAuthItr.hasNext()) {
					SecurityConfig tempConfig = (SecurityConfig) preAuthItr.next();
					configList.add(tempConfig);
				}
			}
			
			configList.add(new SecurityConfig((String) tempMap.get("authority")));
					
			resourcesMap.put(presentResource, configList);
			
			//이전 resource 비교 위해 저장
			preResource = presentResourceStr;
		}
		
		return resourcesMap;
	}
	
	public LinkedHashMap<Object, List<ConfigAttribute>> getRolesAndUrl() throws Exception{
		return getRolesAndResources("url");
	}
	
	public LinkedHashMap<Object, List<ConfigAttribute>> getRolesAndMethod() throws Exception{
		return getRolesAndResources("method");
	}
	
	public LinkedHashMap<Object, List<ConfigAttribute>> getRolesAndPointcut() throws Exception{
		return getRolesAndResources("pointcut");
	}

	public List<ConfigAttribute> getRegexMatchedRequestMapping(String url) throws Exception{
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("url", url);
		List<Map<String, Object>> resultList =
								this.nameParameterJdbcTemplate.queryForList(getSqlRegexMatchedRequestMapping(), paramMap);
		
		Iterator<Map<String, Object>> itr = resultList.iterator();
		Map<String, Object> tempMap;
		List<ConfigAttribute> configList = new LinkedList<ConfigAttribute>();
		
		//같은 Url에 대한 Role 매핑이므로 무조건 configList에 add함
		while(itr.hasNext()) {
			tempMap = itr.next();
			configList.add(new SecurityConfig((String)tempMap.get("authority")));
		}
		
		if(configList.size() > 0) {
			//logger.debug("Request Url : {}, matched Url: {}, mapping Roles: {}", url, resultList.get(0).get("url"), configList);
		}
		
		return configList;
	}
	
	public String getHierarchicalRoles() throws Exception{
		List<Map<String, Object>> resultList =
				this.nameParameterJdbcTemplate.queryForList(getSqlHierarchicalRoles(), new HashMap<String, String>());
		
		Iterator<Map<String, Object>> itr = resultList.iterator();
		StringBuffer concatedRoles = new StringBuffer();
		Map<String, Object> tempMap;
		
		while(itr.hasNext()) {
			tempMap = itr.next();
			concatedRoles.append(tempMap.get("child"));
			concatedRoles.append(">");
			concatedRoles.append(tempMap.get("parent"));
			concatedRoles.append("\n");
		}
		return concatedRoles.toString();
	}
	
	
	
	
	
	

}
